package app;

import java.util.ArrayList;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Literal;
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

import com.hp.hpl.jena.vocabulary.RDFS;

import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import exception.NoResourcesSelectedException;
import framework.Clue;
import framework.Pop;
import framework.Solution;

/**
 * @author Ben Griffiths
 * ClueQueryTask
 * For a given clue and list of URIs of named entities in that clue, the ClueQueryTask queries DBpedia's SPARQL endpoint to retrieve
 * a graph of RDF triples around each named entity. It then searches within each knowledge graph for resources that are semantically 
 * related to the recognised entity around which the graph is constructed, and builds a list of Solution objects representing potential
 * solutions to the clue, based on relationships defined in the pop ontology.
 * @extends javax.swing.SwingWorker
 */

public class ClueQueryTask extends SwingWorker<ArrayList<Solution>, Void> {
	private static Logger log = Logger.getLogger(ClueQueryTask.class);
	private final int LANGUAGE_TAG_LENGTH = 3;
	private final String LANGUAGE_TAG = "@";
	private final String ENG_LANG = "en";
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Model schema;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Reasoner reasoner;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private InfModel infModel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<String> recognisedResourceUris;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private  ArrayList<Solution> solutions;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Clue clue;
	
	/**
	 * constructModelFromRemoteStore
	 * @param resourceUri - the URI of a resource around which to construct an RDF graph by performing a SPARQL query against
	 * DBpedia's SPARQL endpoint
	 * @return an instance of com.hp.hpl.jena.rdf.model.Model representing the constructed RDF graph
	 * @throws com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP - if either of two SPARQL queries generated result in this 
	 * exception being thrown
	 */
	private Model constructModelFromRemoteStore(String resourceUri) throws QueryExceptionHTTP {
		String sparqlQuery = Pop.RDFS_PREFIX_DECLARATION + " " +
				Pop.RDF_PREFIX_DECLARATION + " " +
				Pop.DBPEDIA_PROPERTY_PREFIX_DECLARATION +
				" construct {<" + resourceUri + "> ?predicate ?object." +
				" 			?object rdfs:label ?label." +
				" 			?object dbpprop:name ?name." +
				"			?object rdf:type ?objectType." +
				"			?objectType rdfs:label ?objectTypeLabel." +
				" 			?subject ?anotherPredicate <" + resourceUri + ">." +
				"			?subject rdfs:label ?anotherLabel." +
				"			?subject dbpprop:name ?anotherName." +
				"			?subject rdf:type ?subjectType." +
				"			?subjectType rdfs:label ?subjectTypeLabel." +
				"}" +
				" where {" +
				" {<" + resourceUri + "> ?predicate ?object." +
				"  ?object rdfs:label ?label." +
				"  ?object rdf:type ?objectType." +
				"  ?objectType rdfs:label ?objectTypeLabel.}" +
				" UNION" +
				" {<" + resourceUri + "> ?predicate ?object." +
				"  ?object dbpprop:name ?name." +
				"  ?object rdf:type ?objectType." +
				"  ?objectType rdfs:label ?objectTypeLabel.}" +
				" UNION" +
				" {?subject ?anotherPredicate <" + resourceUri + ">." +
				" ?subject rdfs:label ?anotherLabel. " +
				" ?subject rdf:type ?subjectType." +
				" ?subjectType rdfs:label ?subjectTypeLabel.}" +
				" UNION" +
				" {?subject ?anotherPredicate <" + resourceUri + ">." +
				" ?subject dbpprop:name ?anotherName. " +
				" ?subject rdf:type ?subjectType." +
				" ?subjectType rdfs:label ?subjectTypeLabel.}" +
				"}" +
				"LIMIT 50000";
		 
		log.debug("Constructing model around " + resourceUri);
		
		Query query = QueryFactory.create(sparqlQuery);
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(Pop.ENDPOINT_URI, query);
		
		Model model = queryExecution.execConstruct();
		
		queryExecution.close();
		
		/* Construct a second model to gather labels of the recognised resource */
		String secondSparqlQuery = Pop.FOAF_PREFIX_DECLARATION + " " +
				Pop.RDFS_PREFIX_DECLARATION + " " +
				Pop.DBPEDIA_PROPERTY_PREFIX_DECLARATION +
				" construct {<" + resourceUri + "> foaf:givenName ?givenName." +
				" 		<" + resourceUri + "> foaf:surname ?surname." +
				" 		<" + resourceUri + "> rdfs:label ?label." +
				" 		<" + resourceUri + "> dbpprop:name ?name." +
				" }" +
				" where {" +
				"	 {<" + resourceUri + "> foaf:givenName ?givenName.}" +
				"	 UNION" +
				"	 {<" + resourceUri + "> foaf:surname ?surname.}" +
				"	 UNION" +
				"	 {<" + resourceUri + "> rdfs:label ?label.}" +
				"	 UNION" +
				"	 {<" + resourceUri + "> dbpprop:name ?name.}" +
				"}";
		
		Query secondQuery = QueryFactory.create(secondSparqlQuery);
		QueryExecution secondQueryExecution = QueryExecutionFactory.sparqlService(Pop.ENDPOINT_URI, secondQuery);
		
		log.debug("Constructing second model around " + resourceUri);
		
		Model secondModel = secondQueryExecution.execConstruct();
		
		secondQueryExecution.close();
		
		Model mergedModel = model.union(secondModel);
		model = null;
		secondModel = null;
		return mergedModel;
	}
	
	/**
	 * extractCandidateSolutions - searches the inference model most recently constructed for candidate solutions to the clue with which
	 * the ClueQueryTask was initialised
	 * @param rootResourceUri - the URI of the resource around which the most recent RDF graph was constructed 
	 */
	private void extractCandidateSolutions(String rootResourceUri) {
		/* First, check the labels of the resource around which the model was constructed */
		this.extractSolutionsFromRootResource(rootResourceUri);
		
		/* Now list statements from the model in which the predicate is a pop:relationalProperty */
		Selector propertiesOfInterestSelector = new SimpleSelector(null, Pop.relationalProperty, (RDFNode)null);
		StmtIterator statements = this.getInfModel().listStatements(propertiesOfInterestSelector);
		
		while(statements.hasNext()) {
			Statement thisStatement = statements.nextStatement();
			Resource subjectOfStatement = thisStatement.getSubject();
			RDFNode objectOfStatement = thisStatement.getObject();

			Selector selector = new CandidateSelector(subjectOfStatement, null, objectOfStatement);
			
			StmtIterator statementsOfInterest = this.getInfModel().listStatements(selector);
			
			while(statementsOfInterest.hasNext()) {
				Statement statementOfInterest = statementsOfInterest.nextStatement();
				Property thisPredicate = statementOfInterest.getPredicate();
				
				Resource thisPredicateInModel = this.getInfModel().getResource(thisPredicate.getURI());
				
				StmtIterator labelProperties = thisPredicateInModel.listProperties(RDFS.label);
				
				if(labelProperties != null) {
					while(labelProperties.hasNext()) {
						RDFNode predicateLabelValue = labelProperties.nextStatement().getObject();
						String rawPredicateLabel = predicateLabelValue.toString();
						String predicateLabel = stripLanguageTag(rawPredicateLabel);
						if(this.getClue().getClueFragments().contains(this.getClue().toProperCase(predicateLabel))) {
							Resource r = thisStatement.getResource();
							RDFNode objectOfInterest = thisStatement.getObject();
							/*
							 * Either a string has been identified which may be a solution or a resource has been identified
							 * whose label may represent a solution
							 */
							if(objectOfInterest.isLiteral())// a string has been identified which may be a solution
								this.extractSolutionFromLiteral(r, objectOfInterest.asResource());								
							else this.extractSolutionsFromSubjectAndObject(subjectOfStatement, objectOfStatement.asResource()); 
						}
					}
				}
			}
		}
	}
	
	/**
	 * extractSolutionsFromRootResource - checks the labels of the root resource of the most recently constructed RDF graph for 
	 * potential solutions to the clue, and constructs a Solution object for each label with a literal value
	 * @param rootResourceUri - the URI of the resource around which the most recent RDF graph was constructed
	 */
	private void extractSolutionsFromRootResource(String rootResourceUri) {
		Resource rootResource = this.getInfModel().getResource(rootResourceUri);
		Selector rootResourceLabelSelector = new SimpleSelector(rootResource, RDFS.label, (RDFNode)null);
		StmtIterator rootLabels = this.getInfModel().listStatements(rootResourceLabelSelector);

		while(rootLabels.hasNext()) {
			Statement stmnt = rootLabels.nextStatement();
			Literal rootLabelLiteral;
			try {
				rootLabelLiteral = stmnt.getLiteral();
			}
			catch(LiteralRequiredException e) {
				continue;
			}
			this.constructSolution(rootLabelLiteral.toString(), rootResource, rootResource);
		}
	}
	
	/**
	 * extractSolutionFromLiteral - constructs a single Solution from two provided resources, one of which must be known 
	 * to reference a literal resource
	 * @param resource - an instance of com.hp.hpl.jena.rdf.model.Resource
	 * @param literalResource - an instance of com.hp.hpl.jena.rdf.model.Resource that is assumed to reference a literal resource
	 */
	private void extractSolutionFromLiteral(Resource resource, Resource literalResource) {
		Resource clueResource, solutionResource;
		if(this.getRecognisedResourceUris().contains(resource.getURI())) {
			clueResource = resource;
			solutionResource = literalResource;
		}
		else {
			clueResource = literalResource;
			solutionResource = resource;
		}
		/* Only construct a Solution if both resources are in the DBpedia namespace  */
		String solutionResourceNameSpace = solutionResource.getNameSpace();
		String clueResourceNameSpace = clueResource.getNameSpace();
		if(!solutionResourceNameSpace.contains(Pop.DBPEDIA_RESOURCE_NS))
			return;
		if(!clueResourceNameSpace.contains(Pop.DBPEDIA_RESOURCE_NS))
			return;
		this.constructSolution(literalResource.toString(), solutionResource, clueResource);
	}
	
	/**
	 * extractSolutionsFromSubjectAndObject - constructs one or more Solutions from two provided resources that have been found in 
	 * a single triple within the most recently constructed RDF graph
	 * @param subject - the first resource, present as the subject in the identified triple
	 * @param object - the second resource, present as the object in the identified triple
	 */
	private void extractSolutionsFromSubjectAndObject(Resource subject, Resource object) {
		Resource clueResource, solutionResource;
		StmtIterator candidateLabels = object.listProperties(RDFS.label);
		while(candidateLabels.hasNext()) {
			Statement s = candidateLabels.nextStatement();
			RDFNode candidateLabelValue = null;
			
			String lang = "LITERAL_REQUIRED_EXCEPTION"; // will remain with this value if a LiteralRequiredException is thrown 
			try {
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
				
				if(this.getRecognisedResourceUris().contains(res.getURI())) {
					clueResource = res;
					solutionResource = subject;
				}
				else {
					clueResource = subject;
					solutionResource = res;
				}
				/* We are only interested in resources in the namespace http://dbpedia.org/resource/ */
				String solutionResourceNameSpace = solutionResource.getNameSpace();
				String clueResourceNameSpace = clueResource.getNameSpace();
				if(!solutionResourceNameSpace.contains(Pop.DBPEDIA_RESOURCE_NS))
					return;
				if(!clueResourceNameSpace.contains(Pop.DBPEDIA_RESOURCE_NS))
					return;
				log.debug("Constructing solution with label " + candidateLabel + " and solutionResource " + solutionResource.getURI());
				this.constructSolution(candidateLabel, solutionResource, clueResource);
			}
	}
}
	/**
	 * constructSolution - instantiates a Solution object with the solutionText argument and, if it is not already contained 
	 * in the solutions list, adds it to that list. If the solutionText contains any spaces, then it is fragmented into every
	 * possible combination of sequences of sequential words within the text, and each fragment is used to create a further 
	 * Solution object
	 * @param solutionText - the text with which to construct one or more Solutions
	 * @param solutionResource - the resource whose label provides the solution text
	 * @param clueResource - the named entity recognised in the clue, around which the most recent RDF graph was constructed
	 */
	private void constructSolution(String solutionText, Resource solutionResource, Resource clueResource) {
		ArrayList<String> potentialSolutions = new ArrayList<String>();
		potentialSolutions.add(solutionText);
		String[] solutionTextFragments = solutionText.split(" ");
		for(int i = 0; i < solutionTextFragments.length; i++) {
			String thisFragment = solutionTextFragments[i];
			potentialSolutions.add(thisFragment);
			for(int j = i + 1; j < solutionTextFragments.length; j++) {
				thisFragment = thisFragment + " " + solutionTextFragments[j];
				potentialSolutions.add(thisFragment);
			}
		}
		for(String potentialSolution : potentialSolutions) {
			Solution solution = new SolutionImpl(potentialSolution, solutionResource, clueResource,
					this.getInfModel(), this.getClue());
			if(!(this.getSolutions().contains(solution))) {
				this.getSolutions().add(solution);
			}
		}
	}

	/**
	 * stripLanguageTag
	 * @param solutionText - the String to be stripped
	 * @return - the solutionText with any instances of LANGUAGE_TAG removed from it
	 */
	private String stripLanguageTag(String solutionText) {
		int positionOfLanguageTag = solutionText.length() - LANGUAGE_TAG_LENGTH;
		if(solutionText.length() > LANGUAGE_TAG_LENGTH) {
			if(solutionText.substring(positionOfLanguageTag, positionOfLanguageTag + 1).equals(LANGUAGE_TAG))
				return solutionText.substring(0, positionOfLanguageTag);
		}
		return solutionText;
	}
	
	/**
	 * Constructor - instantiates a ClueQueryTask object for the given clue and list of URIs of recognised entities in the clue text
	 * @param clue - the clue for which the ClueQueryTask will search for a solution
	 * @param recognisedResourceUris - an ArrayList<String> of URIs of recognised entities in the clue text
	 */
	public ClueQueryTask(Clue clue, ArrayList<String> recognisedResourceUris) {
		super();
		this.setClue(clue);
		this.setRecognisedResourceUris(recognisedResourceUris);
		this.setSolutions(new ArrayList<Solution>());
		this.setSchema(ModelLoader.getModel()); // retrieve a reference to the pop ontology
		Reasoner reasoner = ReasonerRegistry.getOWLMicroReasoner();
	    this.setReasoner(reasoner.bindSchema(this.getSchema()));
	}
	
	/**
	 * doInBackground - constucts an RDF graph around each resource specified in the recognisedResourceUris list held by this
	 * ClueQueryTask. Then constructs an instance of com.hp.hpl.jena.rdf.model.InfModel by binding the constructed graph to an instance
	 * of com.hp.hpl.jena.reasoner.Reasoner that has been instantiated with the pop ontology as its schema.
	 * A list of candidate solutions is then built by querying each such inference model.
	 * @override javax.swing.SwingWorker.doInBackground
	 * @throws exception.NoResourcesSelectedException if the list of recognisedResourceUris held by this ClueQueryTask is empty
	 */
	@Override
	protected ArrayList<Solution> doInBackground() throws Exception {
		int progress = 0;
        this.setProgress(progress); // Initialise progress property of SwingWorker
 
        int combinedLengthOfQueries = this.getRecognisedResourceUris().size();
        
        int taskLength = 0;
        try {
        	taskLength = (100 / combinedLengthOfQueries);
        }
        catch(ArithmeticException e) { // will be thrown if combinedLengthOfQueries is 0
        	throw new NoResourcesSelectedException("No entities were recognised in the clue");
        }
        
        for(String resourceUri : this.getRecognisedResourceUris()) {
        	
			Model data;
			try {
				data = this.constructModelFromRemoteStore(resourceUri); // Query DBpedia for triples that include this resource
				this.setInfModel(ModelFactory.createInfModel(reasoner, data));
			    this.extractCandidateSolutions(resourceUri); // adds any candidate solutions from the model to the solutions list
			    this.setInfModel(null); // allow the model to be garbage-collected
			}
			catch(QueryExceptionHTTP e) {
				log.debug("Extraction of recognised resource <" + resourceUri + "> from DBpedia failed.");
				log.debug(e.getResponseMessage());
			}
			
        	progress += taskLength;
            this.setProgress(progress); // one query has been completed
        }
        return this.getSolutions();
	}
	
	/**
	 * @override javax.swing.SwingWorker.done
	 */
    @Override
    public void done() {
    	this.setProgress(100);
    }
}
