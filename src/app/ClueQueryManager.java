/**
 * 
 */
package app;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.LiteralRequiredException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.FileManager;

import com.hp.hpl.jena.vocabulary.RDFS;

import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import experiments.NsPrefixLoader;
import framework.Clue;
import framework.ClueQuery;
import framework.EntityRecogniser;
import framework.Pop;
import framework.Solution;

/**
 * @author Ben Griffiths
 *
 */
public class ClueQueryManager implements ClueQuery {
	private final String SCHEMA_FILE_NAME = "popv7.owl";
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<String> recognisedResourceUris;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<Resource> extractedResources; // Resources whose labels have been extracted from DBpedia
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<Selector> testedSelectors;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private ArrayList<String> candidateSolutions; // list of candidate solutions as raw Strings
	@Setter(AccessLevel.PRIVATE) private ArrayList<Solution> solutions; // list of candidate solutions wrapped in Solution objects
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Clue clue;

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Query query;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private QueryExecution queryExecution;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PUBLIC) private String whereClause;
	
	public ClueQueryManager(Clue clue, ArrayList<String> recognisedResourceUris) {
		this.setClue(clue);
		this.setRecognisedResourceUris(recognisedResourceUris);
		this.setCandidateSolutions(new ArrayList<String>());
		this.setSolutions(new ArrayList<Solution>());
		this.setExtractedResources(new ArrayList<Resource>());
		this.setTestedSelectors(new ArrayList<Selector>());
	}
	
	/**
	 * getSolutions - wraps each candidate solution String into a Solution object and returns a list of candidate Solutions
	 * @return ArrayList<Solution>
	 * THIS NEEDS TO ALSO EITHER PASS THE MODEL TO THE SOLUTION OBJECT SO IT CAN SCORE ITSELF, OR PASS THE SCORE TO THE SOLUTION
	 * OBJECT
	 */
	@Override
	public ArrayList<Solution> getSolutions() {
		/*
		Model schema = FileManager.get().loadModel(this.SCHEMA_FILE_NAME);
		Reasoner reasoner = ReasonerRegistry.getOWLMiniReasoner();
	    reasoner = reasoner.bindSchema(schema);
		for(String resourceUri : recognisedResourceURIs) {
			Model data;
			try {
				data = this.constructModelFromRemoteStore(resourceUri); // Query DBpedia for triples that include this resource
				InfModel infModel = ModelFactory.createInfModel(reasoner, data);
			    ArrayList<Solution> sols = this.extractCandidateSolutions(infModel); // adds any candidate solutions from the model to the candidateSolutions list
			    for(Solution sol : sols)
			    	this.solutions.add(sol);
			}
			catch(QueryExceptionHTTP e) {
				System.err.println("Extraction of recognised resource <" + resourceUri + "> from DBpedia failed.");
			}
			
		}
		*/
		return this.solutions;
	}
}
