/**
 * 
 */
package app;

import java.util.ArrayList;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import framework.Clue;
import framework.Solution;
import framework.SolutionScorer;

/**
 * @author Ben Griffiths
 *
 */
public class SolutionScorerImpl implements SolutionScorer {
	private final String ENDPOINT_URI = "http://dbpedia.org/sparql";
	private final int LANGUAGE_TAG_LENGTH = 3;
	private final String LANGUAGE_TAG = "@";
	
	private final String DBPEDIA_PROPERTY_PREFIX_DECLARATION = "PREFIX dbpprop: <http://dbpedia.org/property/>"; // the 'old' property ontology
	private final String RDFS_PREFIX_DECLARATION = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
	private final String RDF_PREFIX_DECLARATION = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Solution solution;
	
	@Override
	public double score(Solution solution) {
		this.setSolution(solution);
		
		double distanceBetweenClueAndSolution = distance(solution.getSolutionResource(), solution.getClueResource());
		
		ArrayList<Resource> solutionTypes = this.getSolutionTypes(solution);
		ArrayList<Resource> solutionProperties = this.getSolutionProperties(solution);
		
		System.out.println(); //DEBUGGING **********************************
		System.out.println("Recognised the following types in the solutionResource " + solution.getSolutionResource().getURI() + ":"); //DEBUGGING **********************************
		for(Resource solutionType : solutionTypes) //DEBUGGING **********************************
			System.out.println(solutionType.getURI()); //DEBUGGING **********************************
		System.out.println(); //DEBUGGING **********************************
		
		System.out.println(); //DEBUGGING **********************************
		System.out.println("Recognised the following properties of the solutionResource " + solution.getSolutionResource().getURI() + ":"); //DEBUGGING **********************************
		for(Resource solutionProperty : solutionProperties) //DEBUGGING **********************************
			System.out.println(solutionProperty.getURI()); //DEBUGGING **********************************
		System.out.println(); //DEBUGGING **********************************
		
		
		
		double distanceBetweenClueFragmentsAndSolution = distance(solution.getSolutionResource(), solutionTypes, solutionProperties);
		
		return distanceBetweenClueAndSolution * distanceBetweenClueFragmentsAndSolution;
	}
	
	private ArrayList<Resource> getSolutionTypes(Solution solution) {
		ArrayList<Resource> solutionTypes = new ArrayList<Resource>();

		InfModel infModel = solution.getInfModel();
		this.setClue(solution.getClue());
		
		ArrayList<String> clueFragments = this.getClue().getClueFragments();
		
		/* Find the types of the clueResource */
		Selector selector = new SimpleSelector(solution.getSolutionResource(), RDF.type, (RDFNode) null);
		
		StmtIterator solutionTypeStatements = infModel.listStatements(selector);
		
		/* add labels to the types */
		while(solutionTypeStatements.hasNext()) {
			Statement thisStatement = solutionTypeStatements.nextStatement();
			
			System.err.println(thisStatement.toString()); // DEBUGGING ***********************************************
			
			Resource thisType = thisStatement.getObject().asResource();
			
			StmtIterator typeLabels = thisType.listProperties(RDFS.label);
			
			while(typeLabels.hasNext()) {
				Statement thisTypeLabelStatement = typeLabels.nextStatement();
				
				String thisLabel = thisTypeLabelStatement.getString();
				thisLabel = stripLanguageTag(thisLabel);
				
				System.out.println("Found label: " + thisLabel); // DEBUGGING *****************************************
				
				if(clueFragments.contains(toProperCase(thisLabel)))
					solutionTypes.add(thisType);
			}
		}
		return solutionTypes;
	}
	
	private ArrayList<Resource> getSolutionProperties(Solution solution) {
		ArrayList<Resource> solutionProperties = new ArrayList<Resource>();

		InfModel infModel = solution.getInfModel();
		Clue clue = solution.getClue();
		
		ArrayList<String> clueFragments = clue.getClueFragments();
		
		Resource clueResource = solution.getClueResource();
		Selector predicateSelector = new SimpleSelector(solution.getSolutionResource(), null, (RDFNode) clueResource);
		
		StmtIterator solutionPropertyStatements = infModel.listStatements(predicateSelector);
		
		/* add labels to the types */
		while(solutionPropertyStatements.hasNext()) {
			Statement thisStatement = solutionPropertyStatements.nextStatement();
			
			System.err.println(thisStatement.toString()); // DEBUGGING ***********************************************
			
			Resource thisPredicate = thisStatement.getPredicate().asResource();
			
			StmtIterator predicateLabels = thisPredicate.listProperties(RDFS.label);
			
			while(predicateLabels.hasNext()) {
				Statement thisPredicateLabelStatement = predicateLabels.nextStatement();
				
				String thisPredicateLabel = thisPredicateLabelStatement.getString();
				thisPredicateLabel = stripLanguageTag(thisPredicateLabel);
				
				System.out.println("Found label: " + thisPredicateLabel); // DEBUGGING *****************************************
				
				if(clueFragments.contains(toProperCase(thisPredicateLabel)))
					solutionProperties.add(thisPredicate);
			}
		}
		return solutionProperties;
	}
	
	/*
	 * DUPLICATED IN CLUEIMPL CLASS
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
	

	private double distance(Resource firstResource, Resource secondResource) {
		
		double numberOfLinks = this.countLinks(firstResource, secondResource);
		
		double distance = (1.0 / (1.0 + numberOfLinks));
		
		return distance;
	}
	
	private double distance(Resource solutionResource, ArrayList<Resource> recognisedSolutionTypes, 
			ArrayList<Resource> recognisedSolutionProperties) {
		
		if(recognisedSolutionTypes.size() == 0 && recognisedSolutionProperties.size() == 0)
			return 1.0;
		
		String solutionResourceUri = solutionResource.getURI();
		String clueResourceUri = this.getSolution().getClueResource().getURI();
		
		String queryBuffer = "";
		
		for(int i = 0; i < recognisedSolutionTypes.size(); i++) {
			if(i > 0)
				queryBuffer += " UNION";
			
			String typeUri = recognisedSolutionTypes.get(i).getURI();
			queryBuffer += " {<" + solutionResourceUri + "> rdf:type <" + typeUri + ">." + " }";
		}
		
		if(recognisedSolutionTypes.size() > 0 && recognisedSolutionProperties.size() > 0)
			queryBuffer += " UNION";
		
		for(int i = 0; i < recognisedSolutionProperties.size(); i++) {
			if(i > 0)
				queryBuffer += " UNION";
			
			String predicateUri = recognisedSolutionProperties.get(i).getURI();
			queryBuffer += " {<" + solutionResourceUri + "> <" + predicateUri + "> <" + clueResourceUri + ">}" +
						" UNION" +
						" {<" + clueResourceUri + "> <" + predicateUri + "> <" + solutionResourceUri + ">}";
		}
		
		
		String sparqlQueryStart = this.RDF_PREFIX_DECLARATION +
							" select (count(*) as ?count) where {";
		
		String sparqlQueryEnd = " }";
		
		String sparqlQuery = sparqlQueryStart + queryBuffer + sparqlQueryEnd;
		
		double numberOfLinks = this.executeCountQuery(sparqlQuery);
		
		System.err.println("Second count query for solutionResource " + solutionResourceUri + " - " + sparqlQuery + " - has result: " +
				numberOfLinks); // DEBUGGING ***********************************************************************
		
		double distance = (1.0 / (1.0 + numberOfLinks));
		
		return distance;
	}

	private double countLinks(Resource firstResource, Resource secondResource) {
		String firstResourceUri = firstResource.getURI();
		String secondResourceUri = secondResource.getURI();
		
		String sparqlQuery = " select (count(*) as ?count) where {" +
							 	" {<" + firstResourceUri + "> ?predicate <" + secondResourceUri + ">." +
							 	" }" +
							 " UNION" +
							 	" {<" + secondResourceUri + "> ?predicate <" + firstResourceUri + ">." +
							 	" }" +
							 " }";
		Query query = QueryFactory.create(sparqlQuery);
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(this.ENDPOINT_URI, query);
		
		ResultSet resultSet = null;
		try {
			resultSet = queryExecution.execSelect();
		}
		catch (QueryExceptionHTTP e) {
			System.err.println("DBpedia failed to return a result for the scoring query: " + sparqlQuery);
			return 0;
		}
		
        QuerySolution querySolution = resultSet.nextSolution();
        
        Literal numberOfLinksAsLiteral = querySolution.getLiteral("?count");
        double numberOfLinks = numberOfLinksAsLiteral.getDouble();
        
        System.out.println("Number of links found: " + numberOfLinks); // DEBUGGING ****************

		queryExecution.close();
		return numberOfLinks;
	}
	
	private double executeCountQuery(String countQuery) {
		Query query = QueryFactory.create(countQuery);
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(this.ENDPOINT_URI, query);
		ResultSet resultSet = null;
		try {
			resultSet = queryExecution.execSelect();
		}
		catch (QueryExceptionHTTP e) {
			System.err.println("DBpedia failed to return a result for the scoring query: " + countQuery);
			return 0;
		}
        QuerySolution querySolution = resultSet.nextSolution();
        
        Literal numberOfLinksAsLiteral = querySolution.getLiteral("?count");
        double numberOfLinks = numberOfLinksAsLiteral.getDouble();
        
        System.out.println("Number of links found: " + numberOfLinks); // DEBUGGING ****************

		queryExecution.close();
		return numberOfLinks;	
	}
	
	private double countTypeLinks(Resource solutionResource, ArrayList<Resource> clueTypes) {
		
		return 1.0;
	}
	
	private void outLineOfScoringAlgorithm(Solution solution) {
		/* Start by getting the values of RDF:type for the solutionResource
		 * Then check my local ontology for class types with labels that match fragments of the solution text
		 * 
		 * Then...
		 * 
		 */
		
		/* Find values of RDF:type for the solutionResource and add these resources to a list */
		ArrayList<Resource> solutionResourceTypes = new ArrayList<Resource>();
		StmtIterator statementIterator = solution.getSolutionResource().listProperties(RDF.type);
		while(statementIterator.hasNext()) {
			Resource r = (Resource) statementIterator.nextStatement().getObject();
			solutionResourceTypes.add(r);
		}
		
		/* Find types within my ontology that match fragments of the clue text.
		 * I will need to add these to a list of objects that allow me to store together the particular fragment of the
		 * text and the type from my ontology that it has matched. For now, I will lazily keep these in two parallel lists,
		 * but I REALLY MUST REFACTOR THIS
		 */
		ArrayList<String> recognisedClueFragments = new ArrayList<String>(); // TWO PARALLEL LISTS
		ArrayList<Resource> recognisedClueResourceTypes = new ArrayList<Resource>();
		
		/* Next, I need to get hold of the complete list of clue fragments... */
	}
}
