/**
 * 
 */
package remotePrototype;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import app.CandidateSelector;
import app.SolutionImpl;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.LiteralRequiredException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.FileManager;

import com.hp.hpl.jena.vocabulary.RDFS;

import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import experiments.NsPrefixLoader;
import framework.remotePrototype.Clue;
import framework.remotePrototype.ClueQuery;
import framework.remotePrototype.EntityRecogniser;
import framework.remotePrototype.Pop;
import framework.remotePrototype.Solution;

/**
 * @author Ben Griffiths
 *
 */
public class ClueQueryImpl implements ClueQuery {
	private final String SCHEMA_FILE_NAME = "popv7.owl";
	private final String ENDPOINT_URI = "http://dbpedia.org/sparql";
	private final String DBPEDIA_PREFIX_DECLARATION = "PREFIX dbpedia: <http://dbpedia.org/resource/>";
	private final String DBPEDIA_OWL_PREFIX_DECLARATION = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>";
	private final String DBPEDIA_PROPERTY_PREFIX_DECLARATION = "PREFIX dbpprop: <http://dbpedia.org/property/>"; // the 'old' property ontology
	private final String RDFS_PREFIX_DECLARATION = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
	private final int LANGUAGE_TAG_LENGTH = 3;
	private final String LANGUAGE_TAG = "@";
	private final String ENG_LANG = "en";
	
	private final String[] EXCLUDED_VOCABS = {};
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<Resource> extractedResources; // Resources whose labels have been extracted from DBpedia
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<Selector> testedSelectors;
	@Setter(AccessLevel.PRIVATE) private ArrayList<String> candidateSolutions; // list of candidate solutions as raw Strings
	@Setter(AccessLevel.PRIVATE) private ArrayList<Solution> solutions; // list of candidate solutions wrapped in Solution objects
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniser entityRecogniser;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Query query;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private QueryExecution queryExecution;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PUBLIC) private String whereClause;
	
	public ClueQueryImpl(Clue clue, EntityRecogniser entityRecogniser) {
		this.setClue(clue);
		this.setEntityRecogniser(entityRecogniser);
		this.setCandidateSolutions(new ArrayList<String>());
		this.setSolutions(new ArrayList<Solution>());
		this.setExtractedResources(new ArrayList<Resource>());
		this.setTestedSelectors(new ArrayList<Selector>());
	}
	
	/**
	 * getSolutions - wraps each candidate solution String into a Solution object and returns a list of candidate Solutions
	 * @return ArrayList<Solution>
	 * THIS NEEDS TO ALSO EITHER PASS THE MODEL TO THE SOLUTION OBJECT SO IT CAN SCORE ITSELF, OR PASS THE SCORE TO THE SOLUTION
	 * OBJECT
	 */
	@Override
	public ArrayList<Solution> getSolutions() {
		ArrayList<String> recognisedResourceURIs = this.getEntityRecogniser().getRecognisedResourceURIs();
		Model schema = FileManager.get().loadModel(this.SCHEMA_FILE_NAME);
		Reasoner reasoner = ReasonerRegistry.getOWLMiniReasoner();
	    reasoner = reasoner.bindSchema(schema);
		for(String resourceUri : recognisedResourceURIs) {
			Model data;
			try {
				data = this.constructModelFromRemoteStore(resourceUri); // Query DBpedia for triples that include this resource
				InfModel infModel = ModelFactory.createInfModel(reasoner, data);
			    ArrayList<Solution> sols = this.extractCandidateSolutions(infModel); // adds any candidate solutions from the model to the candidateSolutions list
			    for(Solution sol : sols)
			    	this.solutions.add(sol);
			}
			catch(QueryExceptionHTTP e) {
				System.err.println("Extraction of recognised resource <" + resourceUri + "> from DBpedia failed.");
			}
			
		}
		return this.solutions;
	}
	
	private ArrayList<Solution> extractCandidateSolutions(InfModel infModel) {
		ArrayList<Solution> candidateSols = new ArrayList<Solution>();
		ArrayList<String> clueFragments = this.getEntityRecogniser().getClueFragments();
		
		Selector propertiesOfInterestSelector = new SimpleSelector(null, Pop.relationalProperty, (RDFNode)null);
		// List statements in which the predicate is a pop:relationalProperty
		StmtIterator statements = infModel.listStatements(propertiesOfInterestSelector);
		
		while(statements.hasNext()) {
			Statement thisStatement = statements.nextStatement();
			Resource subjectOfStatement = thisStatement.getSubject();
			RDFNode objectOfStatement = thisStatement.getObject();
			
			Selector selector = new CandidateSelector(subjectOfStatement, null, objectOfStatement);
			
			StmtIterator statementsOfInterest = infModel.listStatements(selector);
			
			while(statementsOfInterest.hasNext()) {
				Statement statementOfInterest = statementsOfInterest.nextStatement();
				Property thisPredicate = statementOfInterest.getPredicate();
				
				Resource thisPredicateInModel = infModel.getResource(thisPredicate.getURI());
				
				StmtIterator labelProperties = thisPredicateInModel.listProperties(RDFS.label);
				
				if(labelProperties != null) {
					while(labelProperties.hasNext()) {
						RDFNode predicateLabelValue = labelProperties.nextStatement().getObject();
						String rawPredicateLabel = predicateLabelValue.toString();
						String predicateLabel = stripLanguageTag(rawPredicateLabel);
						if(clueFragments.contains(toProperCase(predicateLabel))) {
							Resource r = thisStatement.getResource();
							RDFNode objectOfInterest = thisStatement.getObject();
							if(objectOfInterest.isLiteral()) { // a string has been identified which may be a solution
								Solution s = new SolutionImpl(objectOfInterest.toString(), r, infModel, this.getClue());
								if(!(candidateSols.contains(s)))
									candidateSols.add(s);
								
									
							}
								
							else {  // a resource has been identified whose label may represent a solution
								
									// *********** SHOULD I ALSO TEST THE LABELS OF SUBJECTS????????? ********************
									Resource object = objectOfStatement.asResource();
									
									if(!extractedResources.contains(object)) { // check if we have already tested this resource
										extractedResources.add(object);
										StmtIterator candidateLabels = object.listProperties(RDFS.label);
										while(candidateLabels.hasNext()) {
											Statement s = candidateLabels.nextStatement();
											RDFNode candidateLabelValue = null;
											
											String lang = "LITERAL_REQUIRED_EXCEPTION"; // will remain with this value if a 
											try {										// LiteralRequiredException is thrown	
												lang = s.getLanguage(); // we only want English-language labels
											}
											catch(LiteralRequiredException e) {
												/* The pop ontology treats some properties in the dbpprop namespace as subProperties
												 * of rdfs:label, because they are often used in place of rdfs:label in the DBpedia
												 * dataset. However, sometimes they are given resources as values. In such cases,
												 * we ignore the resource and move on to the next value of rdfs:label
												 */
											}
											if(lang == null || lang.equals(this.ENG_LANG)) {
												if(candidateLabelValue == null)
													candidateLabelValue = s.getObject();
												String rawCandidateLabel = candidateLabelValue.toString();
												String candidateLabel = stripLanguageTag(rawCandidateLabel);
												
												Resource res = s.getSubject();
												Solution so = new SolutionImpl(candidateLabel, res, infModel, this.getClue());
												if(!(candidateSols.contains(so)))
													candidateSols.add(so);
											}
										}
										
							
									}
							}
						}
					}
				}
			}
		}
		return candidateSols;
	}

	/**
	 * @return ArrayList<String>
	 */
	@Override
	public ArrayList<String> getCandidateSolutions() {
		ArrayList<String> recognisedResourceURIs = this.getEntityRecogniser().getRecognisedResourceURIs();
		Model schema = FileManager.get().loadModel(this.SCHEMA_FILE_NAME);
		Reasoner reasoner = ReasonerRegistry.getOWLMiniReasoner();
	    reasoner = reasoner.bindSchema(schema);
		for(String resourceUri : recognisedResourceURIs) {
			try {
				Model data = this.constructModelFromRemoteStore(resourceUri); // Query DBpedia for triples that include this resource
				InfModel infModel = ModelFactory.createInfModel(reasoner, data);
			    this.extractCandidates(infModel);
			}
			catch(QueryExceptionHTTP e) {
				System.err.println("Extraction of recognised resource <" + resourceUri + "> from DBpedia failed.");
			}
			
		}
		return this.candidateSolutions;
	}
	
	private Model constructModelFromRemoteStore(String resourceUri) throws QueryExceptionHTTP {
		
		String sparqlQuery = RDFS_PREFIX_DECLARATION + " " +
				DBPEDIA_PROPERTY_PREFIX_DECLARATION +
				" construct {<" + resourceUri + "> ?predicate ?object." +
				" 			?object rdfs:label ?label." +
				" 			?object dbpprop:name ?name." +
				" 			?subject ?anotherPredicate <" + resourceUri + ">." +
				"			?subject rdfs:label ?anotherLabel." +
				"			?subject dbpprop:name ?anotherName." +
				"}" +
				" where {" +
				" {<" + resourceUri + "> ?predicate ?object." +
				" 			?object rdfs:label ?label.}" +
				" UNION" +
				" {<" + resourceUri + "> ?predicate ?object." +
				" 			?object dbpprop:name ?name.}" +
				" UNION" +
				" {?subject ?anotherPredicate <" + resourceUri + ">." +
				" ?subject rdfs:label ?anotherLabel.} " +
				" UNION" +
				" {?subject ?anotherPredicate <" + resourceUri + ">." +
				" ?subject dbpprop:name ?anotherName.} " +
				"}";
		
		Query query = QueryFactory.create(sparqlQuery);
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ENDPOINT_URI, query);
		
		System.out.println("Constructing model around " + resourceUri); // DEBUGGING ******************************
		
		Model model = queryExecution.execConstruct();
		
		
		
		// DEBUGGING ***************************************************************
		if(resourceUri.equals("http://dbpedia.org/resource/Houses_Of_The_Holy")) {
			 // load standard prefixes into the model
		    NsPrefixLoader prefixLoader = new NsPrefixLoader(model);
			prefixLoader.loadStandardPrefixes();
			 
			// Now, write the model out to a file in RDF/XML-ABBREV format:
			try {
				Random rand = new Random();
				int randToAppend = rand.nextInt(1000);
				
				String fileName = "data\\extractedModel" + randToAppend + ".xml";
				FileOutputStream outFile = new FileOutputStream(fileName);
				System.out.println("Writing retrieved data to file...");
				model.write(outFile, "RDF/XML-ABBREV");
				outFile.close();
				System.out.println("Operation complete");
			}
			catch(FileNotFoundException e) {
				e.printStackTrace();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		
		
		queryExecution.close();
		return model;
	}
	
	private void extractCandidates(InfModel infModel) {
		ArrayList<String> clueFragments = this.getEntityRecogniser().getClueFragments();
		System.out.println("Extracting labels..."); // DEBUGGING ****************************************************
		
		Selector propertiesOfInterestSelector = new SimpleSelector(null, Pop.relationalProperty, (RDFNode)null);
		
		//Model posit = ModelFactory.createDefaultModel();
		//Resource desiredProperty = posit.createResource("http://www.griffithsben.com/ontologies/pop.owl#desiredProperty");
		
		
		
		// List statements in which the predicate is a pop:relationalProperty
		StmtIterator statements = infModel.listStatements(propertiesOfInterestSelector);
		
		while(statements.hasNext()) {
			Statement thisStatement = statements.nextStatement();
			Resource subjectOfStatement = thisStatement.getSubject();
			RDFNode objectOfStatement = thisStatement.getObject();
			
			Selector selector = new CandidateSelector(subjectOfStatement, null, objectOfStatement);
			
			
			
			
			if(this.getTestedSelectors().contains(selector)) { // DEBUGGING **************************************
				System.err.println("Already tested this selector"); // DEBUGGING **********************************
			}
			else this.getTestedSelectors().add(selector); // DEBUGGING **********************************
			
			StmtIterator statementsOfInterest = infModel.listStatements(selector);
			
			while(statementsOfInterest.hasNext()) {
				Statement statementOfInterest = statementsOfInterest.nextStatement();
				Property thisPredicate = statementOfInterest.getPredicate();
				
				Resource thisPredicateInModel = infModel.getResource(thisPredicate.getURI());
				
				StmtIterator labelProperties = thisPredicateInModel.listProperties(RDFS.label);
				
				if(labelProperties != null) {
					while(labelProperties.hasNext()) {
						RDFNode predicateLabelValue = labelProperties.nextStatement().getObject();
						String rawPredicateLabel = predicateLabelValue.toString();
						String predicateLabel = stripLanguageTag(rawPredicateLabel);
						if(clueFragments.contains(toProperCase(predicateLabel))) {
							RDFNode objectOfInterest = thisStatement.getObject();
							if(objectOfInterest.isLiteral()) { // a string has been identified which may be a solution
									this.addCandidateSolution(objectOfInterest.toString());
							}
								
							else {  // a resource has been identified whose label may represent a solution
								
									// *********** SHOULD I ALSO TEST THE LABELS OF SUBJECTS????????? ********************
									Resource object = objectOfStatement.asResource();
									
									if(!extractedResources.contains(object)) { // check if we have already tested this resource
										extractedResources.add(object);
										StmtIterator candidateLabels = object.listProperties(RDFS.label);
										while(candidateLabels.hasNext()) {
											Statement s = candidateLabels.nextStatement();
											RDFNode candidateLabelValue = null;
											
											String lang = "LITERAL_REQUIRED_EXCEPTION"; // will remain with this value if a 
											try {										// LiteralRequiredException is thrown	
												lang = s.getLanguage(); // we only want English-language labels
											}
											catch(LiteralRequiredException e) {
												/* The pop ontology treats some properties in the dbpprop namespace as subProperties
												 * of rdfs:label, because they are often used in place of rdfs:label in the DBpedia
												 * dataset. However, sometimes they are given resources as values. In such cases,
												 * we ignore the resource and move on to the next value of rdfs:label
												 */
											}
											if(lang == null || lang.equals(this.ENG_LANG)) {
												if(candidateLabelValue == null)
													candidateLabelValue = s.getObject();
												String rawCandidateLabel = candidateLabelValue.toString();
												String candidateLabel = stripLanguageTag(rawCandidateLabel);
												this.addCandidateSolution(candidateLabel);
											}
										}
										
							
									}
							}
						}
					}
				}
			}
		}
	}

	private void addCandidateSolution(String candidateSolution) {
		if(!(this.candidateSolutions.contains(candidateSolution)))
			this.candidateSolutions.add(candidateSolution);
		System.out.println("Found candidate solution: " + candidateSolution); // DEBUGGING ******************************
	}

	/*
	 * THIS CODE IS DUPLICATED IN THE SIMPLEENTITYRECOGNISER CLASS - REFACTOR IT OUT SOMEWHERE?
	 */
	private String stripLanguageTag(String solutionText) {
		int positionOfLanguageTag = solutionText.length() - LANGUAGE_TAG_LENGTH;
		if(solutionText.length() > LANGUAGE_TAG_LENGTH) {
			if(solutionText.substring(positionOfLanguageTag, positionOfLanguageTag + 1).equals(LANGUAGE_TAG))
				return solutionText.substring(0, positionOfLanguageTag);
		}
		return solutionText;
	}
	
	/*
	 * DUPLICATED FROM ENTITYRECOGNISERIMPL CLASS
	 */
	private String toProperCase(String thisWord) {
		String thisWordInProperCase = thisWord.substring(0, 1).toUpperCase();
		if(thisWord.length() > 1) {
			int index = 1; // start at the second letter of the word
			while(index < thisWord.length()) {
				String nextCharacter = thisWord.substring(index, index + 1);
				thisWordInProperCase += nextCharacter;
				if((nextCharacter.equals(" ")) && (index < (thisWord.length() - 1))) {
					 index++; // the next character needs to be capitalised
					 nextCharacter = thisWord.substring(index, index + 1);
					 thisWordInProperCase += nextCharacter.toUpperCase();
				}
				index++;
			}
		}
		return thisWordInProperCase;
	}
}
