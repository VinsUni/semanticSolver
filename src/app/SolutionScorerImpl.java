package app;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import framework.Clue;
import framework.Pop;
import framework.Solution;
import framework.SolutionScorer;

/**
 * @author Ben Griffiths
 * SolutionScorerImpl
 * @implements framework.SolutionScorer
 */
public class SolutionScorerImpl implements SolutionScorer {
	private static Logger log = Logger.getLogger(SolutionScorerImpl.class);
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Solution solution;
	
	/**
	 * getSolutionTypes
	 * @param solution - an instance of framework.Solution representing a solution
	 * @return an ArrayList of objects of type com.hp.hpl.jena.rdf.model.Resource, each of which represents an object type asserted
	 * for the solutionResource member of the solution argument in the solution's associated inference model
	 */
	private ArrayList<Resource> getSolutionTypes(Solution solution) {
		ArrayList<Resource> solutionTypes = new ArrayList<Resource>();
		InfModel infModel = solution.getInfModel();
		ArrayList<String> clueFragments = this.getClue().getClueFragments();
		/* Find the types of the solutionResource */
		Selector selector = new SimpleSelector(solution.getSolutionResource(), RDF.type, (RDFNode) null);
		StmtIterator solutionTypeStatements = infModel.listStatements(selector);
		/* add labels of the types */
		while(solutionTypeStatements.hasNext()) {
			Statement thisStatement = solutionTypeStatements.nextStatement();
			Resource thisType = thisStatement.getObject().asResource();
			StmtIterator typeLabels = thisType.listProperties(RDFS.label);
			while(typeLabels.hasNext()) {
				Statement thisTypeLabelStatement = typeLabels.nextStatement();
				String thisLabel = thisTypeLabelStatement.getString();
				thisLabel = solution.stripLanguageTag(thisLabel);
				String nameSpace = thisType.getNameSpace();
				if(nameSpace != null && nameSpace.equals(Pop.POP_URI)) {
					/* Types in the pop namespace are not found in the wild.
					 * Find equivalent types of this type that may be present in the DBpedia dataset
					 */
					StmtIterator equivalentTypeStatements = thisType.listProperties(OWL.equivalentClass);
					while(equivalentTypeStatements.hasNext()) {
						Statement equivalentTypeStatement = equivalentTypeStatements.nextStatement();
						Resource equivalentType = equivalentTypeStatement.getObject().asResource();
						String equivalentTypeNameSpace = equivalentType.getNameSpace();
						/* If not a type in the pop namespace, add the equivalent type as a solution type 
						 * together with the original type's label
						 */
						if( (equivalentTypeNameSpace != null) && (!equivalentTypeNameSpace.equals(Pop.POP_URI)) &&
							(!solutionTypes.contains(equivalentType)) && (clueFragments.contains(this.getClue().toProperCase(thisLabel))) )
							solutionTypes.add(equivalentType);
					}
				}
				else { // the type in question is one that is present in the DBpedia dataset
					if( (!solutionTypes.contains(thisType)) && (clueFragments.contains(this.getClue().toProperCase(thisLabel))) )
						solutionTypes.add(thisType);
				}
			}
		}
		infModel = null; // allow the model to be garbage-collected
		return solutionTypes;
	}
	
	/**
	 * getSolutionProperties
	 * @param solution - an instance of framework.Solution representing a solution
	 * @return an ArrayList of objects of type com.hp.hpl.jena.rdf.model.Resource, each of which represents an object property asserted
	 * as a link between the solutionResource and clueResource members of the solution argument in the solution's associated inference 
	 * model
	 */
	private ArrayList<Resource> getSolutionProperties(Solution solution) {
		ArrayList<Resource> solutionProperties = new ArrayList<Resource>();
		InfModel infModel = solution.getInfModel();
		ArrayList<String> clueFragments = this.getClue().getClueFragments();
		Resource clueResource = solution.getClueResource();
		Selector predicateSelector = new SimpleSelector(solution.getSolutionResource(), null, (RDFNode) clueResource);
		StmtIterator solutionPropertyStatements = infModel.listStatements(predicateSelector);
		/* add labels of the predicates */
		while(solutionPropertyStatements.hasNext()) {
			Statement thisStatement = solutionPropertyStatements.nextStatement();
			Resource thisPredicate = thisStatement.getPredicate().asResource();
			StmtIterator predicateLabels = thisPredicate.listProperties(RDFS.label);
			while(predicateLabels.hasNext()) {
				Statement thisPredicateLabelStatement = predicateLabels.nextStatement();
				String thisPredicateLabel = thisPredicateLabelStatement.getString();
				thisPredicateLabel = solution.stripLanguageTag(thisPredicateLabel);
				String nameSpace = thisPredicate.getNameSpace();
				if(nameSpace != null && nameSpace.equals(Pop.POP_URI)) {
					/* Properties in the pop namespace are not found in the wild.
					 * Find equivalent properties of this property that may be present in the DBpedia dataset
					 */
					StmtIterator equivalentPropertyStatements = thisPredicate.listProperties(OWL.equivalentProperty);
					while(equivalentPropertyStatements.hasNext()) {
						Statement equivalentPropertyStatement = equivalentPropertyStatements.nextStatement();
						Resource equivalentProperty = equivalentPropertyStatement.getObject().asResource();
						String equivalentPropertyNameSpace = equivalentProperty.getNameSpace();
						/* If not a property in the pop namespace, add the equivalent property as a solution property 
						 * together with the original property's label
						 */
						if( (equivalentPropertyNameSpace != null) && (!equivalentPropertyNameSpace.equals(Pop.POP_URI)) &&
							(!solutionProperties.contains(equivalentProperty)) && 
							(clueFragments.contains(this.getClue().toProperCase(thisPredicateLabel))) )
							solutionProperties.add(equivalentProperty);
					}
				}
				else { // the property in question is one that is present in the DBpedia dataset
					if( (!solutionProperties.contains(thisPredicate)) && (clueFragments.contains(this.getClue().toProperCase(
							thisPredicateLabel))) )
						solutionProperties.add(thisPredicate);
				}
			}
		}
		return solutionProperties;
	}
	
	/**
	 * distance
	 * @param firstResource - an instance of com.hp.hpl.jena.rdf.model.Resource
	 * @param secondResource - an instance of com.hp.hpl.jena.rdf.model.Resource
	 * @return a double representing the semantic distance in the DBpedia knowledge base between the first and second resource arguments
	 */
	private double distance(Resource firstResource, Resource secondResource) {
		double numberOfLinks = this.countLinks(firstResource, secondResource);
		double distance = (1.0 / (1.0 + numberOfLinks));
		return distance;
	}
	
	/**
	 * distance - formulates a SPARQL query to measure the number of links between the solutionResource and the union of recognised 
	 * solution types and solution properties. Executes that query and returns a measure of the semantic distance between the
	 * solutionResource and that union of types and properties.
	 * @param solutionResource - an instance of com.hp.hpl.jena.rdf.model.Resource that has a label from which a solution has been
	 * derived
	 * @param recognisedSolutionTypes - an ArrayList of objects of type com.hp.hpl.jena.rdf.model.Resource, each of which represents 
	 * an object type asserted for the solutionResource member of the solution argument in the solution's associated inference model
	 * @param recognisedSolutionProperties - an ArrayList of objects of type com.hp.hpl.jena.rdf.model.Resource, each of which represents
	 *  an object property asserted as a link between the solutionResource and clueResource members of the solution argument in the 
	 *  solution's associated inference model
	 * @return a double representing the semantic distance between the solutionResource and the union of recognised solution types and
	 * solution properties
	 */
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
		String sparqlQueryStart = Pop.RDF_PREFIX_DECLARATION +
							" select (count(*) as ?count) where {";
		String sparqlQueryEnd = " }";
		String sparqlQuery = sparqlQueryStart + queryBuffer + sparqlQueryEnd;
		
		double numberOfLinks = this.executeCountQuery(sparqlQuery);
		
		log.debug("Second count query for solutionResource " + solutionResourceUri + " - " + sparqlQuery + " - has result: " +
				numberOfLinks);
		
		double distance = (1.0 / (1.0 + numberOfLinks));
		return distance;
	}
	
	/**
	 * countLinks - formulates a SPARQL query to count the number of links between the two resources represented by the two resource
	 * arguments within the DBpedia knowledge base, and returns the result of executing that query
	 * @param firstResource - an instance of com.hp.hpl.jena.rdf.model.Resource
	 * @param secondResource - an instance of com.hp.hpl.jena.rdf.model.Resource
	 * @return a double representing the number of links in the DBpedia knowledge base between the two resources represented by the
	 * two resource arguments
	 */
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
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(Pop.ENDPOINT_URI, query);
		
		ResultSet resultSet = null;
		try {
			resultSet = queryExecution.execSelect();
		}
		catch (QueryExceptionHTTP e) {
			log.debug("DBpedia failed to return a result for the scoring query: " + sparqlQuery);
			return 0;
		}
        QuerySolution querySolution = resultSet.nextSolution();
        Literal numberOfLinksAsLiteral = querySolution.getLiteral("?count");
        double numberOfLinks = numberOfLinksAsLiteral.getDouble();

		queryExecution.close();
		return numberOfLinks;
	}
	
	/**
	 * countQuery - executes the SPARQL count query passed in as an argument, and returns the result as a double.
	 * The query must be a valid SPARQL query returning a single literal value bound to a variable, ?count, as its result.
	 * @param countQuery - a String representing the SPARQL query to be executed
	 * @return the result of the countQuery, as a value of type double
	 */
	private double executeCountQuery(String countQuery) {
		Query query = QueryFactory.create(countQuery);
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(Pop.ENDPOINT_URI, query);
		ResultSet resultSet = null;
		try {
			resultSet = queryExecution.execSelect();
		}
		catch (QueryExceptionHTTP e) {
			log.debug("DBpedia failed to return a result for the scoring query: " + countQuery);
			return 0;
		}
        QuerySolution querySolution = resultSet.nextSolution();
        Literal numberOfLinksAsLiteral = querySolution.getLiteral("?count");
        double numberOfLinks = numberOfLinksAsLiteral.getDouble();
        
		queryExecution.close();
		return numberOfLinks;
	}
	
	/**
	 * score - returns a score for the provided solution calculated by multiplying together:
	 * (a) the semantic distance in the DBpedia dataset between the two resources represented by the solution's solutionResource and 
	 * clueResource members, and
	 * (b) the semantic distance in the DBpedia dataset between the resource represented by the solution's solutionResource member and
	 * the union of [i] the resources representing the recognised object types asserted for the solutionResource in its associated 
	 * inference model and [ii] the resources representing properties that link the solution's solutionResource member with its 
	 * clueResource member in the associated inference model
	 * @Override framework.SolutionScorer.score
	 */
	@Override
	public double score(Solution solution) {
		this.setSolution(solution);
		this.setClue(solution.getClue());
		double distanceBetweenClueAndSolution = distance(solution.getSolutionResource(), solution.getClueResource());
		
		ArrayList<Resource> solutionTypes = this.getSolutionTypes(solution);
		ArrayList<Resource> solutionProperties = this.getSolutionProperties(solution);
		double distanceBetweenClueFragmentsAndSolution = distance(solution.getSolutionResource(), solutionTypes, solutionProperties);
		/* solution and clue can now both be garbage-collected */
		this.setSolution(null);
		this.setClue(null);
		
		log.debug("Solution with text " + solution.getSolutionText() + " and solutionResource " +
				solution.getSolutionResource().getURI() + " scores " + 
				distanceBetweenClueAndSolution * distanceBetweenClueFragmentsAndSolution);
		
		return distanceBetweenClueAndSolution * distanceBetweenClueFragmentsAndSolution;
	}
}
