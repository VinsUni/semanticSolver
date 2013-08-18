/**
 * 
 */
package app;

import java.util.ArrayList;

import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import framework.Clue;
import framework.ClueSolver;
import framework.EntityRecogniser;
import framework.ClueQuery;
import framework.SemanticSolver;

/**
 * @author Ben Griffiths
 *
 */
public class SemanticSolverImpl implements SemanticSolver {

	@Override
	public void solve(Clue clue) throws QueryExceptionHTTP {
		EntityRecogniser entityRecogniser = new EntityRecogniserImpl(clue);
		ClueQuery query = new AnotherNewClueQueryImpl(clue, entityRecogniser);
		ClueSolver solver = new ClueSolverImpl();
		
		/*
		System.out.println("Recognised the following resources:"); // DEBUGGING ***************************
		try {
			ArrayList<String> recognisedResources = entityRecogniser.getRecognisedResourceURIs();
			for(String uri : recognisedResources) // DEBUGGING ***************************
				System.out.println(uri); // DEBUGGING ***************************
		}
		catch(QueryExceptionHTTP e) {
			throw e;
		}
		System.out.println("Recognised the following properties in the local ontology:"); // DEBUGGING ***************************
		ArrayList<String> recognisedProperties = entityRecogniser.getRecognisedPropertyURIs();
		for(String uri : recognisedProperties) // DEBUGGING ***************************
			System.out.println(uri); // DEBUGGING ***************************
		*/
		
		ArrayList<String> candidateSolutions = solver.getSolutions(clue, query.getCandidateSolutions());
		System.out.println("Candidate solutions:");
		for(String candidateSolution : candidateSolutions)
			System.out.println(candidateSolution);
		
	}

}
