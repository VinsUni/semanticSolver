/**
 * 
 */
package app;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private GraphicalUserInterface userInterface;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniserTask entityRecogniserTask;
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ClueSolver clueSolver;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private String results;
	
	
	public SemanticSolverImpl(GraphicalUserInterface userInterface) {
		this.setUserInterface(userInterface);
	}

	@Override
	public void solve(Clue clue) throws QueryExceptionHTTP {
		this.setClue(clue);
        
        this.setEntityRecogniserTask(new EntityRecogniserTask(getClue()));

        Thread erThread = new Thread(new Runnable() {
                public void run() {
                         getEntityRecogniserTask().addPropertyChangeListener(getUserInterface());
                         getEntityRecogniserTask().execute();
                }
            });
         erThread.start();
        
        try {
			ArrayList<String> recognisedResourceUris = this.getEntityRecogniserTask().get(); // will block until ERTask has finished
			System.out.println("Recognised resources: ");
		     for(String recognisedResource : recognisedResourceUris)
		        	System.out.println("<" + recognisedResource + ">");
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        catch (ExecutionException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        

        /*              
        this.setClueQueryTask(new ClueQueryTask(this.getClue(), recognisedResourceUris));
        
        Thread cqThread = new Thread(new Runnable() {
                public void run() {
                          getClueQueryTask().addPropertyChangeListener(getUserInterface());
                         getClueQueryTask().execute();
                }
            });
         cqThread.start();
        ArrayList<Solution> proposedSolutions = this.getClueQueryTask().get(); // will block until CQTask is complete
        
        this.setClueSolver(ClueSolverImpl());
        
        ArrayList<Solution> solutions = solver.getSolutions(clue, proposedSolutions);

        String resultsBuffer = "Candidate solutions:\n";

        for(Solution solution: solutions) {
                 String solutionText = solution.getSolutionText();
                 resultsBuffer += solutionText + "\n";
        }
       
        this.setResults(resultsBuffer);
        */
       
        /* Update the GUI on the EDT */
        SwingUtilities.invokeLater(new Runnable() {
        @Override
                 public void run() {
                 // getUserInterface().updateResults(getResults());
                 getUserInterface().getDisplayPanel().getSubmitClueButton().setEnabled(true);
                 getUserInterface().getDisplayPanel().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                 }
        });
	}
	
	
	
	public void oldSolveMethod(Clue clue) throws QueryExceptionHTTP {
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
		//final UserInterface UI = this.getUi();
		SwingUtilities.invokeLater(new Runnable() {
    		@Override
			public void run() {
    			//UI.updateResults(FINAL_RESULTS);
			}
		});
		System.out.println("SemanticSolverImpl is running on the thread: " + Thread.currentThread().getName()); // DEBUGGING ********
	}

}
