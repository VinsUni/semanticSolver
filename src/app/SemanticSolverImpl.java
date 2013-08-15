/**
 * 
 */
package app;

import java.util.ArrayList;

import framework.Clue;
import framework.ClueSolver;
import framework.Query;
import framework.SemanticSolver;

/**
 * @author Ben Griffiths
 *
 */
public class SemanticSolverImpl implements SemanticSolver {

	@Override
	public void solve(Clue clue) {
		Query query = new QueryImpl(clue);
		ClueSolver solver = new ClueSolverImpl();
		
		ArrayList<String> candidateSolutions = solver.getSolutions(clue, query.getCandidateSolutions());
		System.out.println("Candidate solutions:");
		for(String candidateSolution : candidateSolutions)
			System.out.println(candidateSolution);
		
	}

}
