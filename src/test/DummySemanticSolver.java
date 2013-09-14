/**
 * 
 */
package test;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import app.ClueQueryTask;
import app.ClueSolverImpl;
import app.EntityRecogniserTask;

import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import exception.NoResourcesSelectedException;
import exception.NoSolutionsException;
import framework.Clue;
import framework.ClueSolver;
import framework.Solution;

/**
 * @author Ben Griffiths	
 *
 */
public class DummySemanticSolver {
	private static Logger log = Logger.getLogger(DummySemanticSolver.class);
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniserTask entityRecogniserTask;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ClueQueryTask clueQueryTask;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ClueSolver clueSolver;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private String results;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<String> recognisedResourceUris;

	public ArrayList<Solution> solve(Clue clue) throws QueryExceptionHTTP {
         	this.setClue(clue);
        	this.setEntityRecogniserTask(new EntityRecogniserTask(getClue()));
	
        	Thread erThread = new Thread(new Runnable() {
                	public void run() {
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

        	if(this.getRecognisedResourceUris() == null) {
        		/* Notify the user that no solutions were found and then return*/
				this.setResults("No solutions found");
	        	return null;
        	}
        	return this.findSolutions(this.getRecognisedResourceUris());
	}

	public  ArrayList<Solution> findSolutions(ArrayList<String> recognisedResourceUris) {
                
        	this.setClueQueryTask(new ClueQueryTask(this.getClue(), recognisedResourceUris));
        
        	Thread cqThread = new Thread(new Runnable() {
                	public void run() {
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
        
        	this.setClueSolver(new ClueSolverImpl());
        
        	ArrayList<Solution> solutions = null;
			try {
				solutions = this.getClueSolver().getSolutions(this.getClue(), proposedSolutions);
			} catch (NoSolutionsException e) {
				/* Notify the user that no solutions were found and then return*/
				this.setResults(e.getMessage());
	        	return null;
			}
			
			return solutions;
	}
}
