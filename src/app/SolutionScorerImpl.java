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
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

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
	
	@Override
	public double score(Solution solution) {
		
		double distanceBetweenClueAndSolution = distance(solution.getSolutionResource(), solution.getClueResource());
		
		ArrayList<Resource> clueTypes = this.getClueTypes(solution);
		
		
		
		double distanceBetweenClueTypesAndSolution = 1.0;
		
		return distanceBetweenClueAndSolution * distanceBetweenClueTypesAndSolution;
	}
	
	private ArrayList<Resource> getClueTypes(Solution solution) {
		ArrayList clueTypes = new ArrayList<Resource>();
		
		InfModel infModel = solution.getInfModel();
		Clue clue = solution.getClue();
		
		//ArrayList<String> clueFragments = 
		
		//StmtIterator typeStatements = 
		
		// TODO Auto-generated method stub
		return null;
	}

	private double distance(Resource firstResource, Resource secondResource) {
		
		double numberOfLinks = this.countLinks(firstResource, secondResource);
		
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
		ResultSet resultSet = queryExecution.execSelect();
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
