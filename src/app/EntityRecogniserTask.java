/**
 * 
 */
package app;

import java.util.ArrayList;

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
public class EntityRecogniserTask extends SwingWorker<ArrayList<RecognisedResource>, Void> {
	private static Logger log = Logger.getLogger(EntityRecogniserTask.class);
	private final String LANG = "@en";
	private final String ENDPOINT_URI = "http://dbpedia-live.openlinksw.com/sparql"; // http://dbpedia.org/sparql // DUPLICATED IN QUERYIMPL
	private final String RDFS_PREFIX_DECLARATION = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"; // DUPLICATED IN QUERYIMPL
	private final String DBPPROP_PREFIX_DECLARATION = "PREFIX dbpprop: <http://dbpedia.org/property/>";
	private final String DB_OWL_PREFIX_DECLARATION = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>";
	private final String RDF_PREFIX_DECLARATION = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
	private final String FOAF_PREFIX_DECLARATION = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>";
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private StmtIterator statementsIterator; // used to iterate over the statements in my local ontology
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ResIterator propertiesIterator;
	private final String[] WORDS_TO_CONSIDER_AS_PREDICATES_ONLY = {"artist", "singer", "band", "album", "member", 
			"writer", "song", "group"};

	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) ArrayList<RecognisedResource> recognisedResources;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) ArrayList<String> recognisedResourceUris;

	/* This constructor will be used to instantiate a task that recognises entities in a clue */
	public EntityRecogniserTask(Clue clue) {
		super(); // call SwingWorker Default constructor
		this.setClue(clue);
		this.setRecognisedResources(new ArrayList<RecognisedResource>());
		this.setRecognisedResourceUris(new ArrayList<String>());
	}
    
	/*
     * Main task. Executed in background thread. 
     */
    @Override
    public ArrayList<RecognisedResource> doInBackground() {
        int progress = 0;
        this.setProgress(progress); // Initialise progress property of SwingWorker
        
        
        int combinedLengthOfQueries = this.getClue().getClueFragments().size();
        int taskLength = (100 / combinedLengthOfQueries);
        
        for(String clueFragment : this.getClue().getClueFragments()) {
        	try {
        		if(this.getClue().isFillInTheBlank())
        			this.extractEntities(clueFragment);
        		else this.extractEntities(clueFragment);
        	}
        	catch (QueryExceptionHTTP e) {
        		log.debug("DBpedia connection dropped. Entity recognition for clue fragment " + clueFragment + " failed");
        	}
        	progress += taskLength;
            this.setProgress(progress); // one query has been completed
        }
        return this.getRecognisedResources();
    }
    
    /*
     * Executed on EDT
     */
    @Override
    public void done() {
    	this.setProgress(100);
    }
    
    private void extractEntities(String clueFragment) throws QueryExceptionHTTP {
                 if(considerAsPredicateOnly(clueFragment))
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
                                 " LIMIT 100";

                 Query query = QueryFactory.create(SPARQLquery);
                 QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ENDPOINT_URI, query);
                 try {

                          ResultSet resultSet = queryExecution.execSelect();
                          while(resultSet.hasNext()) {
                                   QuerySolution querySolution = resultSet.nextSolution();
                                   Resource thisResource = querySolution.getResource("?resource");

                                   String nameSpace = thisResource.getNameSpace();
                                   
                                   /* Trialling dbpedia.org/resource only ... */
                                   if(!nameSpace.contains("http://dbpedia.org/resource/")) {
                                	   continue;
                                   }
                                   
                                   Literal thisTypeLabel = querySolution.getLiteral("?typeLabel");
                                   String typeLabel = thisTypeLabel.toString();

                                   String resourceUri = thisResource.getURI();
                                   
                                   boolean resourceAlreadyRecognised = false;
                                   int indexOfResource = 0;
                                   
                                   /* Check if a RecognisedResource has already been created for this resource */
                                   for(int i = 0; i < this.getRecognisedResources().size(); i++) {
                                	   if(this.getRecognisedResources().get(i).getUri().equals(resourceUri)) {
                                		   resourceAlreadyRecognised = true;
                                		   indexOfResource = i;
                                	   	   break;
                                	   }
                                   }
                                
                                   if(resourceAlreadyRecognised) {
                                	   this.getRecognisedResources().get(indexOfResource).addTypeLabel(typeLabel);
                                   }
                                   else {
		                                   RecognisedResource recognisedResource = new RecognisedResource(resourceUri, clueFragment);
		                                   recognisedResource.addTypeLabel(typeLabel);
		                                   this.getRecognisedResources().add(recognisedResource);
                                   }
                          }
                          queryExecution.close();   
                 }
                 catch(QueryExceptionHTTP e) {
                          throw e;
                 }
	}

	private String toProperCase(String thisWord) {
		String thisWordInProperCase = thisWord.substring(0, 1).toUpperCase();
		if(thisWord.length() > 1) {
			int index = 1; // start at the second letter of the word
			while(index < thisWord.length()) {
				String nextCharacter = thisWord.substring(index, index + 1);
				thisWordInProperCase += nextCharacter;
				if((nextCharacter.equals(" ")) && (index < (thisWord.length() - 1))) {
					 index++; // the next character needs to be capitalised
					 nextCharacter = thisWord.substring(index, index + 1);
					 thisWordInProperCase += nextCharacter.toUpperCase();
				}
				index++;
			}
		}
		return thisWordInProperCase;
	}


	private boolean considerAsPredicateOnly(String wordToCheck) {
		for(int i = 0; i < this.WORDS_TO_CONSIDER_AS_PREDICATES_ONLY.length; i++)
			if(toProperCase(WORDS_TO_CONSIDER_AS_PREDICATES_ONLY[i]).equals(wordToCheck))
				return true;
		return false;
	}
}
