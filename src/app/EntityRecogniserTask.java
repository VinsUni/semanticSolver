/**
 * 
 */
package app;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import framework.Clue;

/**
 * @author Ben Griffiths
 *
 */
public class EntityRecogniserTask extends SwingWorker<ArrayList<String>, Void> {
	private static Logger log = Logger.getLogger(EntityRecogniserTask.class);
	private final String LANG = "@en";
	private final String ENDPOINT_URI = "http://dbpedia-live.openlinksw.com/sparql"; // http://dbpedia.org/sparql // DUPLICATED IN QUERYIMPL
	private final String RDFS_PREFIX_DECLARATION = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"; // DUPLICATED IN QUERYIMPL
	private final String DBPPROP_PREFIX_DECLARATION = "PREFIX dbpprop: <http://dbpedia.org/property/>";
	private final String DB_OWL_PREFIX_DECLARATION = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>";
	private final String RDF_PREFIX_DECLARATION = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
	private final String XPATH_FUNCTIONS_PREFIX_DECLARATION = "PREFIX fn: <http://www.w3.org/2005/xpath-functions#>";
	private final String FOAF_PREFIX_DECLARATION = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>";
	private final int RESULT_LIMIT = 200;
	private final int FITB_RESULT_LIMIT = 100;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private StmtIterator statementsIterator; // used to iterate over the statements in my local ontology
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ResIterator propertiesIterator;

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) ArrayList<String> recognisedResourceUris;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) Map<String, Boolean> commonClueFragments;
	

	/* This constructor will be used to instantiate a task that recognises entities in a clue */
	public EntityRecogniserTask(Clue clue) {
		super(); // call SwingWorker Default constructor
		this.setClue(clue);
		this.setRecognisedResourceUris(new ArrayList<String>());
		this.setCommonClueFragments(ModelLoader.getCommonClueFragments());
	}
    
	/*
     * Main task. Executed in background thread. 
     */
    @Override
    public ArrayList<String> doInBackground() {
        int progress = 0;
        this.setProgress(progress); // Initialise progress property of SwingWorker
        
        
        int combinedLengthOfQueries = this.getClue().getClueFragments().size();
        int taskLength = (100 / combinedLengthOfQueries);
        
        for(String clueFragment : this.getClue().getClueFragments()) {
        	try {
        		if(this.getClue().isFillInTheBlank())
        			this.extractFITBEntities(clueFragment);
        		else this.extractEntities(clueFragment);
        	}
        	catch (QueryExceptionHTTP e) {
        		log.debug("DBpedia connection dropped. Entity recognition for clue fragment " + clueFragment + " failed");
        		log.debug(e.getResponseMessage());
        	}
        	progress += taskLength;
            this.setProgress(progress); // one query has been completed
        }
        return this.getRecognisedResourceUris();
    }
    
    /*
     * Executed on EDT
     */
    @Override
    public void done() {
    	this.setProgress(100);
    }
    
    private void extractEntities(String clueFragment) throws QueryExceptionHTTP {
    	if(this.getCommonClueFragments().containsKey(clueFragment.toLowerCase()))
    		return;	
	     String wrappedClueFragment = "\"" + clueFragment + "\"" + LANG; // wrap with escaped quotes and append a language tag
	
	     String SPARQLquery = RDFS_PREFIX_DECLARATION + " " +
	                                                  RDF_PREFIX_DECLARATION + " " +
	                                                  DBPPROP_PREFIX_DECLARATION + " " +
	                                                  DB_OWL_PREFIX_DECLARATION + " " +
	                                                  FOAF_PREFIX_DECLARATION +
	                                " select distinct ?resource ?typeLabel {" +
	                               " {" +
	                                        "{ select distinct ?resource ?typeLabel" +
	                                        " where {?resource rdfs:label " + wrappedClueFragment + "." +
	                                                 " ?resource rdf:type ?type." +
	                                                 " ?type rdfs:label ?typeLabel.}" +
	                                        " }" +
	                                        " UNION" +
	                                        " { select distinct ?resource ?typeLabel" +
	                                        "  where {?resource dbpprop:name " + wrappedClueFragment + "." +
	                                                 " ?resource rdf:type ?type." +
	                                                 " ?type rdfs:label ?typeLabel.}" +
	                                        " }" +
	                                        " UNION" +
	                                        " { select distinct ?resource ?typeLabel" +
	                                        "  where {?resource foaf:givenName " + wrappedClueFragment + "." +
	                                                 " ?resource rdf:type ?type." +
	                                                 " ?type rdfs:label ?typeLabel.}" +
	                                        " }" +
	                                        " UNION" +
	                                        " { select distinct ?resource ?typeLabel" +
	                                        "  where {?resource foaf:surname " + wrappedClueFragment + "." +
	                                                 " ?resource rdf:type ?type." +
	                                                 " ?type rdfs:label ?typeLabel.}" +
	                                        " }" +
	                                        " UNION" +
	                                        " { select distinct ?resource ?typeLabel" +
	                                        "  where {?redirectingResource rdfs:label " + wrappedClueFragment + "." +
	                                        "         ?redirectingResource dbpedia-owl:wikiPageRedirects ?resource." +                                                             
	                                                 " ?resource rdf:type ?type." +
	                                                 " ?type rdfs:label ?typeLabel.}" +
	
	                                        " }" +
	                                        " UNION" +
	                                        " { select distinct ?resource ?typeLabel" +
	                                        "  where {?redirectingResource dbpprop:name " + wrappedClueFragment + "." +
	                                        "         ?redirectingResource dbpedia-owl:wikiPageRedirects ?resource." +
	                                                 " ?resource rdf:type ?type." +
	                                                 " ?type rdfs:label ?typeLabel.}" +
	                                        " }" +
	                                        " UNION" +
	                                        " { select distinct ?resource ?typeLabel" +
	                                        "  where {?redirectingResource foaf:givenName " + wrappedClueFragment + "." +
	                                        "         ?redirectingResource dbpedia-owl:wikiPageRedirects ?resource." +                                                             
	                                                 " ?resource rdf:type ?type." +
	                                                 " ?type rdfs:label ?typeLabel.}" +
	                                        " }" +
	                                        " UNION" +
	                                        " { select distinct ?resource ?typeLabel" +
	                                        "  where {?redirectingResource foaf:surname " + wrappedClueFragment + "." +
	                                        "         ?redirectingResource dbpedia-owl:wikiPageRedirects ?resource." +                                                             
	                                                 " ?resource rdf:type ?type." +
	                                                 " ?type rdfs:label ?typeLabel.}" +
	
	                                        " }" +
	                               " }" + 
	                     " }" +
	                     " LIMIT " + this.RESULT_LIMIT;
	
	     Query query = QueryFactory.create(SPARQLquery);
	     QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ENDPOINT_URI, query);
	     try {
	
	              ResultSet resultSet = queryExecution.execSelect();
	              while(resultSet.hasNext()) {
	                       QuerySolution querySolution = resultSet.nextSolution();
	                       Resource thisResource = querySolution.getResource("?resource");
	
	                       String nameSpace = thisResource.getNameSpace();
	                       
	                       /* We only want to consider resources in the BDpedia namespace */
	                       if(!nameSpace.contains("http://dbpedia.org/resource/")) {
	                    	   continue;
	                       }
	
	                       String resourceUri = thisResource.getURI();
	                       
	                       if(!this.getRecognisedResourceUris().contains(resourceUri)) {
	                    	   this.getRecognisedResourceUris().add(resourceUri);
	                    	   log.debug("Recognised resource: " + resourceUri);
	                       }
	              }
	              queryExecution.close();   
	     }
	     catch(QueryExceptionHTTP e) {
	              throw e;
	     }
	}
	
	private void extractFITBEntities(String clueFragment) throws QueryExceptionHTTP {
		if(this.getCommonClueFragments().containsKey(clueFragment.toLowerCase()))
    		return;
		
	    String wrappedClueFragment = "'\"" + clueFragment + "\"'";
	    
	    log.debug("Attempting to extract resources whose labels contain " + wrappedClueFragment);
	    
	    String SPARQLquery = XPATH_FUNCTIONS_PREFIX_DECLARATION + " " +
				RDFS_PREFIX_DECLARATION + " " +
	            RDF_PREFIX_DECLARATION + " " +
	            DBPPROP_PREFIX_DECLARATION + " " +
	            DB_OWL_PREFIX_DECLARATION + " " +
	            FOAF_PREFIX_DECLARATION +
	                   " select distinct ?resource ?typeLabel {" +
	                  " {" +
	                           "{ select distinct ?resource ?typeLabel" +
	                           " where {?resource rdfs:label ?label." +
	                           			" ?label <bif:contains> " + wrappedClueFragment + "." +
	                                    " ?resource rdf:type ?type." +
	                                    " ?type rdfs:label ?typeLabel." +
	                                    "}" +
	                           " }" +
	                           " UNION" +
	                           " { select distinct ?resource ?typeLabel" +
	                           "  where {?resource dbpprop:name ?label." +
	                           			" ?label <bif:contains> " + wrappedClueFragment + "." +
	                                    " ?resource rdf:type ?type." +
	                                    " ?type rdfs:label ?typeLabel." +
	                                    "}" +
	                           " }" +
	                           " UNION" +
	                           " { select distinct ?resource ?typeLabel" +
	                           "  where {?resource foaf:givenName ?label." +
	                           			" ?label <bif:contains> " + wrappedClueFragment + "." +
	                                    " ?resource rdf:type ?type." +
	                                    " ?type rdfs:label ?typeLabel." +
	                                    "}" +
	                           " }" +
	                           " UNION" +
	                           " { select distinct ?resource ?typeLabel" +
	                           "  where {?resource foaf:surname ?label." +
	                           			" ?label <bif:contains> " + wrappedClueFragment + "." +
	                                    " ?resource rdf:type ?type." +
	                                    " ?type rdfs:label ?typeLabel." +
	                                    "}" +
	                           " }" +
	                           " UNION" +
	                           " { select distinct ?resource ?typeLabel" +
	                           "  where {?redirectingResource rdfs:label ?label." +
	                           			" ?label <bif:contains> " + wrappedClueFragment + "." +
	                           "         ?redirectingResource dbpedia-owl:wikiPageRedirects ?resource." +                                                             
	                                    " ?resource rdf:type ?type." +
	                                    " ?type rdfs:label ?typeLabel." +
	                                    "}" +
	
	                           " }" +
	                           " UNION" +
	                           " { select distinct ?resource ?typeLabel" +
	                           "  where {?redirectingResource dbpprop:name ?label." +
	                           			" ?label <bif:contains> " + wrappedClueFragment + "." +
	                           "         ?redirectingResource dbpedia-owl:wikiPageRedirects ?resource." +
	                                    " ?resource rdf:type ?type." +
	                                    " ?type rdfs:label ?typeLabel." +
	                                    "}" +
	                           " }" +
	                           " UNION" +
	                           " { select distinct ?resource ?typeLabel" +
	                           "  where {?redirectingResource foaf:givenName ?label." +
	                           			" ?label <bif:contains> " + wrappedClueFragment + "." +
	                           "         ?redirectingResource dbpedia-owl:wikiPageRedirects ?resource." +                                                             
	                                    " ?resource rdf:type ?type." +
	                                    " ?type rdfs:label ?typeLabel." +
	                                    "}" +
	                           " }" +
	                           " UNION" +
	                           " { select distinct ?resource ?typeLabel" +
	                           "  where {?redirectingResource foaf:surname ?label." +
	                           			" ?label <bif:contains> " + wrappedClueFragment + "." +
	                           "         ?redirectingResource dbpedia-owl:wikiPageRedirects ?resource." +                                                             
	                                    " ?resource rdf:type ?type." +
	                                    " ?type rdfs:label ?typeLabel." +
	                                    "}" +
	                           " }" +
	                  " }" + 
	        " }" +
	        " LIMIT " + this.FITB_RESULT_LIMIT;
	
	    Query query = QueryFactory.create(SPARQLquery);
	    QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ENDPOINT_URI, query);
	    try {
	
	             ResultSet resultSet = queryExecution.execSelect();
	             while(resultSet.hasNext()) {
	                      QuerySolution querySolution = resultSet.nextSolution();
	                      Resource thisResource = querySolution.getResource("?resource");
	
	                      String nameSpace = thisResource.getNameSpace();
	                      
	                      /* We only want to consider resources in the BDpedia namespace */
	                      if(!nameSpace.contains("http://dbpedia.org/resource/")) {
	                   	   continue;
	                      }

	                      String resourceUri = thisResource.getURI();
	                      
	                      if(!this.getRecognisedResourceUris().contains(resourceUri)) {
	                    	   this.getRecognisedResourceUris().add(resourceUri);
	                           log.debug("Recognised resource: " + resourceUri);
	                      }
	             }
	             queryExecution.close();   
	    }
	    catch(QueryExceptionHTTP e) {
	             throw e;
	    }
	}
}
