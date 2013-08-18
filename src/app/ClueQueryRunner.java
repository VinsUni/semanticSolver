/**
 * 
 */
package app;


import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;

import com.hp.hpl.jena.rdf.model.InfModel;
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

import com.hp.hpl.jena.vocabulary.RDFS;

import framework.ClueQuery;
import framework.Pop;

/**
 * @author Ben Griffiths
 *
 */
public class ClueQueryRunner implements Runnable {

		private final String ENDPOINT_URI = "http://dbpedia.org/sparql";
		private final String RDFS_PREFIX_DECLARATION = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
		private final int LANGUAGE_TAG_LENGTH = 3;
		private final String LANGUAGE_TAG = "@";
		private final String ENG_LANG = "en";
		
		@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<Resource> extractedResources; // Resources whose labels have been extracted from DBpedia
		@Setter(AccessLevel.PRIVATE) private ArrayList<String> candidateSolutions;
		@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Query query;
		@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private QueryExecution queryExecution;
		@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PUBLIC) private String whereClause;
		@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private String resourceUriToQuery;
		@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<String> clueFragments;
		@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Model schema;
		@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private MultiThreadedClueQueryImpl clueQuery;
		
		
		public ClueQueryRunner(String resourceUriToQuery, ArrayList<String> clueFragments, ArrayList<String> candidateSolutions,
								ArrayList<Resource> extractedResources, Model schema, MultiThreadedClueQueryImpl clueQuery) {
			
			this.setResourceUriToQuery(resourceUriToQuery);
			this.setClueFragments(clueFragments);
			this.setCandidateSolutions(candidateSolutions);
			this.setExtractedResources(extractedResources);
			this.setSchema(schema);
			this.setClueQuery(clueQuery);
		}
		
		@Override
		public void run() {
			addSolutions();
			this.getClueQuery().notifyThreadComplete();
		}
		
		public void addSolutions() {
			

			Reasoner reasoner = ReasonerRegistry.getOWLMiniReasoner();
		    reasoner = reasoner.bindSchema(this.getSchema());

				
		    Model data = this.constructModelFromRemoteStore(this.getResourceUriToQuery(), true); // Query DBpedia using resource as subject
		    InfModel infModel = ModelFactory.createInfModel(reasoner, data);
		    this.extractCandidates(infModel);
		    
		    
		    data = this.constructModelFromRemoteStore(this.getResourceUriToQuery(), false); // .. and using resource as object
		    infModel = ModelFactory.createInfModel(reasoner, data);
		    this.extractCandidates(infModel);
		}
		
		private Model constructModelFromRemoteStore(String resourceUri, boolean resourceAsSubject) {
			String sparqlQuery;
			if(resourceAsSubject) {
				sparqlQuery = RDFS_PREFIX_DECLARATION +
							" construct {<" + resourceUri + "> ?predicate ?object." +
							" 			?object rdfs:label ?label.}" +
							" where {<" + resourceUri + "> ?predicate ?object." +
							" 			?object rdfs:label ?label.}";
			}
			else {
				sparqlQuery = RDFS_PREFIX_DECLARATION +
							" construct { ?subject ?predicate <" + resourceUri + ">." +
							" 			?subject rdfs:label ?label.}" +
							" where {?subject ?predicate <" + resourceUri + ">." +
							" 			?subject rdfs:label ?label.}";
			}
			Query query = QueryFactory.create(sparqlQuery);
			QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ENDPOINT_URI, query);
			String subOrOb = resourceAsSubject ? "subject" : "object";						 // DEBUGGING ******************************
			System.out.println("Constructing model with " + resourceUri + " as " + subOrOb); // DEBUGGING ******************************
			Model model = queryExecution.execConstruct();
			
			/*
			// DEBUGGING ***************************************************************
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
			*/
			
			
			
			queryExecution.close();
			return model;
		}
		
		private void extractCandidates(InfModel infModel) {
			
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
				
				StmtIterator statementsOfInterest = infModel.listStatements(selector);
				
				while(statementsOfInterest.hasNext()) {
					Statement statementOfInterest = statementsOfInterest.nextStatement();
					Property thisPredicate = statementOfInterest.getPredicate();
					
					Resource thisPredicateInModel = infModel.getResource(thisPredicate.getURI());
					
					StmtIterator labelProperties = thisPredicateInModel.listProperties(RDFS.label);
					
					if(labelProperties != null) {
						System.err.println("Found some properties... for statement: " + statementOfInterest.toString()); // DEBUGGING ****************************
						while(labelProperties.hasNext()) {
							RDFNode predicateLabelValue = labelProperties.nextStatement().getObject();
							String rawPredicateLabel = predicateLabelValue.toString();
							System.err.println("Found pop:relationalProperty with label " + rawPredicateLabel); // DEBUGGING **************
							String predicateLabel = stripLanguageTag(rawPredicateLabel);
							if(clueFragments.contains(toProperCase(predicateLabel))) {
								RDFNode objectOfInterest = thisStatement.getObject();
								if(objectOfInterest.isLiteral()) { // a string has been identified which may be a solution
										this.addCandidateSolution(objectOfInterest.toString());
								}
									
								else {  // a resource has been identified whose label may represent a solution
										Resource object = objectOfStatement.asResource();
										
										if(!extractedResources.contains(object)) { // check if we have already tested this resource
											extractedResources.add(object);
											StmtIterator candidateLabels = object.listProperties(RDFS.label);
											while(candidateLabels.hasNext()) {
												Statement s = candidateLabels.nextStatement();
												
												String lang = s.getLanguage(); // we only want English-language labels
												if(lang == null || lang.equals(this.ENG_LANG)) {
													RDFNode candidateLabelValue = s.getObject();
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
