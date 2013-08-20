/**
 * 
 */
package app;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import framework.Clue;
import framework.ClueSolver;
import framework.EntityRecogniser;
import framework.ClueQuery;
import framework.SemanticSolver;
import framework.Solution;
import framework.UserInterface;

/**
 * @author Ben Griffiths
 *
 */
public class SemanticSolverImpl implements SemanticSolver {
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) UserInterface ui;
	
	public SemanticSolverImpl(UserInterface ui) {
		this.setUi(ui);
	}

	@Override
	public void solve(Clue clue) throws QueryExceptionHTTP {
		EntityRecogniser entityRecogniser = new EntityRecogniserImpl(clue);
		ClueQuery query = new ClueQueryImpl(clue, entityRecogniser);
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
		
		// ArrayList<String> candidateSolutions = solver.getCandidateSolutions(clue, query.getCandidateSolutions());
		
		ArrayList<Solution> solutions = solver.getSolutions(clue, query.getSolutions());
		
		
		System.out.println("Candidate solutions:");
		String results = "Candidate solutions:\n";
		
		for(Solution solution: solutions) {
			String solutionText = solution.getSolutionText();
			System.out.println(solutionText);
			results += solutionText + "\n";
		}
		
		
		
		/*
		for(String candidateSolution : candidateSolutions) {
			System.out.println(candidateSolution);
			results += "[" + candidateSolution + "] ";
		}
		*/
		
		final String FINAL_RESULTS = results;
		final UserInterface UI = this.getUi();
		SwingUtilities.invokeLater(new Runnable() {
    		@Override
			public void run() {
    			UI.updateResults(FINAL_RESULTS);
			}
		});
		System.out.println("SemanticSolverImpl is running on the thread: " + Thread.currentThread().getName()); // DEBUGGING ********
	}

}
