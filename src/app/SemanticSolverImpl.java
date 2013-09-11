/**
 * 
 */
package app;

import java.awt.Cursor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import exception.NoResourcesSelectedException;
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
	private static Logger log = Logger.getLogger(SemanticSolverImpl.class);
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private UserInterface userInterface;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniserTask entityRecogniserTask;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ClueQueryTask clueQueryTask;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ClueSolver clueSolver;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private String results;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<String> clueFragments;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<String> recognisedResourceUris;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<Solution> solutions;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private KnowledgeBaseManager knowledgeBaseManager;

	public SemanticSolverImpl(UserInterface userInterface) {
		this.setUserInterface(userInterface);
		Thread instantiateKBManagerThread = new Thread(new Runnable() {
        	public void run() {
        		setKnowledgeBaseManager(KnowledgeBaseManager.getInstance());
        	}
    	});
		instantiateKBManagerThread.start();
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
         	
         	this.setRecognisedResourceUris(null);
        	try {
                        this.setRecognisedResourceUris(this.getEntityRecogniserTask().get()); // will block until ERTask has finished
        	} 
        	catch (QueryExceptionHTTP e) {
         	throw e;
        	}
        	catch (InterruptedException e) {
        		log.debug(e.getMessage());
                 } 
        	catch (ExecutionException e) {
        		log.debug(e.getMessage());
        	}
        	this.setEntityRecogniserTask(null); // allow EntityRecogniserTask to be garbage-collected

        	if(this.getRecognisedResourceUris() == null) {
        		/* Notify the user that no solutions were found and then return*/
				this.setResults("No solutions found");
	        	SwingUtilities.invokeLater(new Runnable() {
	        	@Override
	                 	public void run() {
	        				getUserInterface().getDisplayPanel().getProgressBar().setStringPainted(false);
	        				getUserInterface().updateResults(getResults());
	                 		
	                 		getUserInterface().getDisplayPanel().getSubmitClueButton().setEnabled(true);
	                     	getUserInterface().getDisplayPanel().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	                 		getUserInterface().showNewClueOptions();
	                 	}
	        	});
	        	return;
        	}
        	this.findSolutions(this.getRecognisedResourceUris());
	}
	
	@Override
	public  void findSolutions(ArrayList<String> recognisedResourceUris) {
			final long NANOSECONDS_IN_ONE_SECOND = 1000000000;
			long startTime = System.nanoTime();
		
			/* Update the progress bar to reflect the fact that Entity Recognition phase is over */
	     	SwingUtilities.invokeLater(new Runnable() {
	    		@Override
	             	public void run() {
	    			String clueQueryInProgressMessage = "Searching for solutions on DBpedia";
	    			getUserInterface().getDisplayPanel().getProgressBar().setValue(0);
	    			getUserInterface().getDisplayPanel().getProgressBar().setString(clueQueryInProgressMessage);
	    			getUserInterface().getDisplayPanel().getProgressBar().setStringPainted(true);
	             	}
	    	});      
                
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
                 catch (Exception e) {
                	 try {
                		 NoResourcesSelectedException castE = (NoResourcesSelectedException)e;
                		 System.out.println("No entities were recognised in the clue " + this.getClue().getSourceClue());
                		 log.debug(castE.getMessage());
                	 }
                	 catch(ClassCastException cce) {
                		 log.debug(e.getMessage());
                	 }
                 }
            this.setClueQueryTask(null); // allow ClueQueryTask to be garbage-collected
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
	        				getUserInterface().getDisplayPanel().getProgressBar().setStringPainted(false);
	        				
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
			
			/* Update the GUI on the EDT */
        	SwingUtilities.invokeLater(new Runnable() {
        	@Override
                 	public void run() {
                 		getUserInterface().updateProgressBarMessage("Calculating confidence levels for solutions");
                 	}
        	});
			
        	String resultsBuffer = "Solutions to the clue \"" + this.getClue().getSourceClue() + " " +
        							this.getClue().getSolutionStructureAsString() + "\":\n";

        	SolutionScorer solutionScorer = new SolutionScorerImpl();
        	/* Score each solution */
        	for(Solution solution: solutions) {
             	solution.setScore(solutionScorer.score(solution));
        	}
        	
        	this.addSolutionsToKnowledgeBase(solutions);
        	
        	/* Filter out any solutions that duplicate a solution with a higher confidence level, and sort in order of confidence */
        	ArrayList<Solution> filteredSolutions = this.sortAndFilterSolutions(solutions);
        	
        	for(Solution solution : filteredSolutions)
        		resultsBuffer += solution.getSolutionText() + " (confidence level: " + 
        					solution.getConfidence() + "%)\n";
        	solutions = null; // allow the garbage collector to remove solutions from memory immediately
        	long endTime = System.nanoTime();
			long durationInSecs = (endTime - startTime) / NANOSECONDS_IN_ONE_SECOND;
			resultsBuffer += "Time taken to process this clue: " + durationInSecs + "s\n";
        	
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
	
	@Override
	public void persistKnowledgeBase() {
		this.getKnowledgeBaseManager().persistKnowledgeBase();
	}
	
	private ArrayList<Solution> sortAndFilterSolutions(ArrayList<Solution> solutions) {
		ArrayList<Solution> sortedSolutions = this.sortByConfidenceLevel(solutions);
		ArrayList<Solution> filteredSolutions = new ArrayList<Solution>();
		ArrayList<String> filteredSolutionTexts = new ArrayList<String>();
		for(int i = 0; i < sortedSolutions.size(); i++) {
			Solution thisSolution = sortedSolutions.get(i);
			if(!filteredSolutionTexts.contains(thisSolution.getSolutionText())) {
				filteredSolutions.add(thisSolution);
				filteredSolutionTexts.add(thisSolution.getSolutionText());
			}
		}
		return filteredSolutions;
	}
	
	private ArrayList<Solution> sortByConfidenceLevel(ArrayList<Solution> solutions) {
		
		class SolutionConfidenceComparator implements Comparator<Solution> {
		    @Override
		    public int compare(Solution firstSolution, Solution secondSolution) {
		        return secondSolution.getConfidence() - firstSolution.getConfidence();
		    }
		}
		Collections.sort(solutions, new SolutionConfidenceComparator());
		return solutions;
	}
	
	private void addSolutionsToKnowledgeBase(ArrayList<Solution> solutions) {
		while(!this.getKnowledgeBaseManager().isFinished())
    		; // wait for the KnowledgeBaseManager to finish any ongoing updates
		this.setSolutions(solutions);
		Thread kbManagerThread = new Thread(new Runnable() {
        	public void run() {
                 	getKnowledgeBaseManager().addToKnowledgeBase(getClue(), getSolutions());
        	}
    	});
		kbManagerThread.start();
	}
}
