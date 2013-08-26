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

import exception.NoSolutionsException;
import framework.Clue;
import framework.ClueSolver;
import framework.SemanticSolver;
import framework.Solution;
import framework.SolutionScorer;
import framework.UserInterface;

/**
 * @author Ben Griffiths	
 *
 */
public class SemanticSolverImpl implements SemanticSolver {
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private UserInterface userInterface;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniserTask entityRecogniserTask;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ClueQueryTask clueQueryTask;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ClueSolver clueSolver;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private String results;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<String> clueFragments;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<RecognisedResource> recognisedResources;

	public SemanticSolverImpl(UserInterface userInterface) {
		this.setUserInterface(userInterface);
	}

	@Override
	public void findEntities(Clue clue) throws QueryExceptionHTTP {
         	this.setClue(clue);
        	
         	this.setClueFragments(this.getClue().getClueFragments());
        	this.setEntityRecogniserTask(new EntityRecogniserTask(getClue()));
	
        	Thread erThread = new Thread(new Runnable() {
                	public void run() {
                         	getEntityRecogniserTask().addPropertyChangeListener(getUserInterface());
                         	getEntityRecogniserTask().execute();
                	}
            	});
         	erThread.start();
         	
         	this.setRecognisedResources(null);
        	try {
                        this.setRecognisedResources(this.getEntityRecogniserTask().get()); // will block until ERTask has finished
        	} 
        	catch (QueryExceptionHTTP e) {
         	throw e;
        	}
        	catch (InterruptedException e) {
                          // TODO Auto-generated catch block
                          e.printStackTrace();
                 } 
        	catch (ExecutionException e) {
         		// TODO Auto-generated catch block
         		e.printStackTrace();
        	}
        
         	/* End by calling a function in GraphicalUserInterface which will find out which resources the user wants to proceed with */
         	SwingUtilities.invokeLater(new Runnable() {
        		@Override
                 	public void run() {
                 	getUserInterface().getDisplayPanel().getSubmitClueButton().setEnabled(true);
                 	getUserInterface().getDisplayPanel().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                 	getUserInterface().getChosenEntitiesFromUser(getRecognisedResources());
                 	}
        	});      
	}
	
	@Override
	public  void findSolutions(ArrayList<String> recognisedResourceUris) {        
                
        	this.setClueQueryTask(new ClueQueryTask(this.getClue(), this.getClueFragments(), recognisedResourceUris));
        
        	Thread cqThread = new Thread(new Runnable() {
                	public void run() {
                    	getClueQueryTask().addPropertyChangeListener(getUserInterface());
                    	getClueQueryTask().execute();
                	}
            	});
        	cqThread.start();

        	ArrayList<Solution> proposedSolutions = null;
                 try {
                	 proposedSolutions = this.getClueQueryTask().get(); // will block until CQTask is finished
                 }
                 catch (InterruptedException e) {
                          // TODO Auto-generated catch block
                          e.printStackTrace();
                 } catch (ExecutionException e) {
                          // TODO Auto-generated catch block
                          e.printStackTrace();
                 }
        
        	this.setClueSolver(new ClueSolverImpl());
        
        	ArrayList<Solution> solutions = null;
			try {
				solutions = this.getClueSolver().getSolutions(this.getClue(), proposedSolutions);
			} catch (NoSolutionsException e) {
				/* Notify the user that no solutions were found and then return*/
				this.setResults(e.getMessage());
	        	SwingUtilities.invokeLater(new Runnable() {
	        	@Override
	                 	public void run() {
	        				String exceptionMessage = getResults();
	        				String solutionStructure = getClue().getSolutionStructureAsString();
	        				setResults(exceptionMessage + ": \"" + getClue().getSourceClue() + "\" " + solutionStructure);
	                 		getUserInterface().updateResults(getResults());
	                 		
	                 		getUserInterface().getDisplayPanel().getSubmitClueButton().setEnabled(true);
	                     	getUserInterface().getDisplayPanel().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	                 		
	                 		getUserInterface().showNewClueOptions();
	                 	}
	        	});
	        	return;
			}
			
        	String resultsBuffer = "Solutions to the clue \"" + this.getClue().getSourceClue() + " " +
        							this.getClue().getSolutionStructureAsString() + "\":\n";
        	
        	for(Solution solution: solutions) {
                 	String solutionText = solution.getSolutionText();
                 	resultsBuffer += solutionText + "\n";
        	}
       
        	this.setResults(resultsBuffer);
        	
        	/* Update the GUI on the EDT */
        	SwingUtilities.invokeLater(new Runnable() {
        	@Override
                 	public void run() {
                 		getUserInterface().updateResults(getResults());
                 		getUserInterface().showNewClueOptions();
                 	}
        	});
        	
        	/* Get solution scores */
        	SolutionScorer solutionScorer = new SolutionScorerImpl();
        	resultsBuffer = "Scores for these solutions:\n";
        	for(Solution solution: solutions) {
             	double thisScore = solutionScorer.score(solution);
             	resultsBuffer += thisScore + "\n";
        	}
        	
        	this.setResults(resultsBuffer);
        	
        	/* Update the GUI on the EDT to show the scores */
        	SwingUtilities.invokeLater(new Runnable() {
        	@Override
                 	public void run() {
                 		getUserInterface().updateResults(getResults());
                 		getUserInterface().showNewClueOptions();
                 	}
        	});
        	
        	
	}
}
