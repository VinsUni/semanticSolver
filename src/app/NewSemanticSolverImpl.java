/**
 * 
 */
package app;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;


import framework.remotePrototype.Clue;
import framework.remotePrototype.ClueSolver;
import framework.remotePrototype.SemanticSolver;
import framework.remotePrototype.Solution;
import framework.remotePrototype.UserInterface;

/**
 * @author Ben Griffiths	
 *
 */
public class NewSemanticSolverImpl implements SemanticSolver {
	private final String CLUE_QUERY_IN_PROGRESS_MESSAGE = "Extracting data from DBpedia";
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private NewGraphicalUserInterface userInterface;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private NewEntityRecogniserTask entityRecogniserTask;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ClueQueryTask clueQueryTask;

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ClueQueryManager clueQueryManager;

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ClueSolver clueSolver;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private String results;

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<String> clueFragments;

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<RecognisedResource> recognisedResources;

	public NewSemanticSolverImpl(NewGraphicalUserInterface userInterface) {
		this.setUserInterface(userInterface);
	}

	

	
	public void findEntities(Clue clue) throws QueryExceptionHTTP {
         	this.setClue(clue);
        	
        	this.setEntityRecogniserTask(new NewEntityRecogniserTask(getClue()));
        	this.setClueFragments(this.getEntityRecogniserTask().getClueFragments());
	
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
                 	getUserInterface().getMainDisplayPanel().getSubmitClueButton().setEnabled(true);
                 	getUserInterface().getMainDisplayPanel().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                 	getUserInterface().getChosenEntitiesFromUser(getRecognisedResources());
                 	}
        	});      
	}

	public  void findSolutions(ArrayList<String> recognisedResourceUris) {        
                
        	this.setClueQueryTask(new ClueQueryTask(this.getClue(), this.getClueFragments(), recognisedResourceUris));
        
        	Thread cqThread = new Thread(new Runnable() {
                	public void run() {
                    	getClueQueryTask().addPropertyChangeListener(getUserInterface());
                    	getClueQueryTask().execute();
                	}
            	});
        	cqThread.start();

        	/* update the String painted on the Progress bar */
		
		SwingUtilities.invokeLater(new Runnable() {
        		@Override
                 	public void run() {
        			getUserInterface().getMainDisplayPanel().getProgressBar().setString(CLUE_QUERY_IN_PROGRESS_MESSAGE);
        			getUserInterface().getMainDisplayPanel().getProgressBar().setStringPainted(true);
                        }
        	});

        	ArrayList<Solution> proposedSolutions = null;
                 try {
                          proposedSolutions = this.getClueQueryTask().get(); // will block until CQTask is finished
                 } catch (InterruptedException e) {
                          // TODO Auto-generated catch block
                          e.printStackTrace();
                 } catch (ExecutionException e) {
                          // TODO Auto-generated catch block
                          e.printStackTrace();
                 }
        
        	this.setClueSolver(new ClueSolverImpl());
        
        	ArrayList<Solution> solutions = this.getClueSolver().getSolutions(clue, proposedSolutions);

        	String resultsBuffer = "Candidate solutions:\n";

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
	}




	@Override
	public void solve(Clue clue) {
		// TODO Auto-generated method stub
		
	}
}
