/**
 * 
 */
package remotePrototype;

import java.util.ArrayList;

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
import framework.remotePrototype.ClueQuery;
import framework.remotePrototype.EntityRecogniser;
import framework.remotePrototype.Solution;

/**
 * @author Ben Griffiths
 *
 */
public class MultiThreadedClueQueryImpl implements ClueQuery {
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<Resource> extractedResources; // Resources whose labels have been extracted from DBpedia
	@Setter(AccessLevel.PRIVATE) private ArrayList<String> candidateSolutions;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniser entityRecogniser;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Query query;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private QueryExecution queryExecution;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PUBLIC) private String whereClause;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PUBLIC) volatile private int threadsCompleted;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PUBLIC) volatile private int threadCount;
	
	
	public MultiThreadedClueQueryImpl(Clue clue, EntityRecogniser entityRecogniser) {
		this.setClue(clue);
		this.setEntityRecogniser(entityRecogniser);
		this.setCandidateSolutions(new ArrayList<String>());
		this.setExtractedResources(new ArrayList<Resource>());
		this.setThreadsCompleted(0);
	}
	
	/**
	 * @return ArrayList<String>
	 */
	@Override
	public ArrayList<String> getCandidateSolutions() {
		ArrayList<String> recognisedResourceURIs = this.getEntityRecogniser().getRecognisedResourceURIs();
		ArrayList<String> clueFragments = this.getEntityRecogniser().getClueFragments();
		
		Model schema = FileManager.get().loadModel("popv7.owl");
		Reasoner reasoner = ReasonerRegistry.getOWLMiniReasoner();
	    reasoner = reasoner.bindSchema(schema);
	    
	    this.setThreadCount(recognisedResourceURIs.size());
	    
		for(String resourceUri : recognisedResourceURIs) {
			ClueQueryRunner clueQueryRunner = new ClueQueryRunner(resourceUri, clueFragments, this.getCandidateSolutions(),
												this.getExtractedResources(), schema, this);
			Thread t = new Thread(clueQueryRunner);
			t.start();
			
			/*
		    Model data = this.constructModelFromRemoteStore(resourceUri, true); // Query DBpedia using resource as subject
		    InfModel infModel = ModelFactory.createInfModel(reasoner, data);
		    this.extractCandidates(infModel);
		    
		    
		    data = this.constructModelFromRemoteStore(resourceUri, false); // .. and using resource as object
		    infModel = ModelFactory.createInfModel(reasoner, data);
		    this.extractCandidates(infModel);
		    */
		}

		return this.candidateSolutions;
	}
	
	synchronized public void notifyThreadComplete() {
	    this.threadsCompleted++;
	    if (threadsCompleted == threadCount) { // All threads have finished.
	       
	    }
	}

	@Override
	public ArrayList<Solution> getSolutions() {
		// TODO Auto-generated method stub
		return null;
	}
}
