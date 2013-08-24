/**
 * 
 */
package remotePrototype;

import java.util.ArrayList;

import javax.swing.SwingWorker;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;

import com.hp.hpl.jena.rdf.model.Model;

import com.hp.hpl.jena.rdf.model.Resource;

import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.FileManager;

import framework.remotePrototype.Clue;
import framework.remotePrototype.Solution;

/**
 * @author Ben Griffiths
 *
 */
public class ClueQueryManager extends SwingWorker<ArrayList<Solution>, Void> {
	private final String SCHEMA_FILE_NAME = "popv7.owl";

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private int taskLength;
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<ClueQueryTaskMarkA> clueQueryTasks;
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Model schema;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Reasoner reasoner;
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private GraphicalUserInterface userInterface;
	
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<String> recognisedResourceUris;
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private String currentResourceUri; // to be accessed by anonymous inner Runnable
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ClueQueryManager instance; // to be accessed by anonymous inner Runnable
	
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<Resource> extractedResources; // Resources whose labels have been extracted from DBpedia
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private ArrayList<String> candidateSolutions; // list of candidate solutions as raw Strings
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private  ArrayList<Solution> solutions; // list of candidate solutions wrapped in Solution objects
	
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<String> clueFragments;
	
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Query query;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private QueryExecution queryExecution;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PUBLIC) private String whereClause;
	
	public ClueQueryManager(GraphicalUserInterface userInterface, Clue clue, ArrayList<String> clueFragments,
			ArrayList<String> recognisedResourceUris) {
		this.setUserInterface(userInterface);
		this.setClue(clue);
		this.setClueFragments(clueFragments);
		this.setRecognisedResourceUris(recognisedResourceUris);
		this.setCandidateSolutions(new ArrayList<String>());
		this.setSolutions(new ArrayList<Solution>());
		this.setExtractedResources(new ArrayList<Resource>());
		this.setSchema(FileManager.get().loadModel(this.SCHEMA_FILE_NAME));
		Reasoner reasoner = ReasonerRegistry.getOWLMiniReasoner();
	    this.setReasoner(reasoner.bindSchema(schema));
	}

	@Override
	protected ArrayList<Solution> doInBackground() throws Exception {
		
        this.setProgress(0); // Initialise progress property of SwingWorker

        int numberOfRecognisedResources = this.getRecognisedResourceUris().size();
        this.setTaskLength((100 / numberOfRecognisedResources));
        
        this.setInstance(this);
        
        this.setClueQueryTasks(new ArrayList<ClueQueryTaskMarkA>());
	    
		for(String resourceUri : this.getRecognisedResourceUris()) {
			this.setCurrentResourceUri(resourceUri);
			
			
			Thread clueQueryThread = new Thread(new Runnable() {
                public void run() {
                	ClueQueryTaskMarkA clueQueryTask = new ClueQueryTaskMarkA(getCurrentResourceUri(), getClue(), getClueFragments(),
                			getReasoner(), getInstance());
                	getClueQueryTasks().add(clueQueryTask);
                    
                    clueQueryTask.execute();
                }
            });
         	clueQueryThread.start();
			
		}
		
		for(ClueQueryTaskMarkA clueQueryTask : getClueQueryTasks()) {
			ArrayList<Solution> sols = clueQueryTask.get();
			this.getSolutions().addAll(sols);
		}
		return this.getSolutions();
	}
	
	public synchronized void updateProgress() {
		int newProgress = this.getProgress() + this.getTaskLength();
        this.setProgress(newProgress); // one query has been completed
	}
	
	 /*
     * Executed on EDT
     */
    @Override
    public void done() {
    	this.setProgress(100);
    }
}
