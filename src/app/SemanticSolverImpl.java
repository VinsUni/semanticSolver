/**
 * 
 */
package app;

import java.util.ArrayList;

import org.openjena.atlas.web.HttpException;

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
		ClueQuery query = new QueryImpl(clue);
		ClueSolver solver = new ClueSolverImpl();
		
		EntityRecogniserImpl entityRecogniser = new EntityRecogniserImpl(clue);
		System.out.println("Recognised the following resources:");
		try {
			ArrayList<String> recognisedResources = entityRecogniser.getRecognisedResourceLabels();
			for(String uri : recognisedResources)
				System.out.println(uri);
		}
		catch(QueryExceptionHTTP e) {
			throw e;
		}
		
		ArrayList<String> candidateSolutions = solver.getSolutions(clue, query.getCandidateSolutions());
		System.out.println("Candidate solutions:");
		for(String candidateSolution : candidateSolutions)
			System.out.println(candidateSolution);
		
	}

}
