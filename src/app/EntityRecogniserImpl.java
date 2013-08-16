/**
 * 
 */
package app;

import java.util.ArrayList;

import org.openjena.atlas.web.HttpException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import framework.Clue;
import framework.EntityRecogniser;

/**
 * @author Ben Griffiths
 *  ************************************************************************************************************!!!!!!!!!!!!!
 * I NEED TO USE THIS: http://wiki.dbpedia.org/lookup/
 *  ************************************************************************************************************!!!!!!!!!!!!!
 */
public class EntityRecogniserImpl implements EntityRecogniser {
	private final String LANG = "@en";
	private final String ENDPOINT_URI = "http://dbpedia.org/sparql"; // DUPLICATED IN QUERYIMPL
	//private final String DBPEDIA_PREFIX_DECLARATION = "PREFIX dbpedia: <http://dbpedia.org/resource/>"; // DUPLICATED IN QUERYIMPL
	//private final String DBPEDIA_OWL_PREFIX_DECLARATION = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>"; // DUPLICATED IN QUERYIMPL
	private final String RDFS_PREFIX_DECLARATION = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"; // DUPLICATED IN QUERYIMPL
	private final String RDF_PREFIX_DECLARATION = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) Clue clue;
	@Getter(AccessLevel.PUBLIC) ArrayList<String> clueFragments;
	@Setter(AccessLevel.PRIVATE) ArrayList<Property> recognisedProperties;
	private final String[] WORDS_TO_EXCLUDE = {"the", "of"}; // a list of common words to exclude from consideration

	public EntityRecogniserImpl(Clue clue) {
		this.setClue(clue);
		this.setClueFragments();
	}
	
	public ArrayList<String> getRecognisedResourceLabels() throws QueryExceptionHTTP {
		ArrayList<String> recognisedResources = new ArrayList<String>();
		
		for(String clueFragment : clueFragments) {
			
			String wrappedClueFragment = "\"" + clueFragment + "\"" + LANG; // wrap with escaped quotes and append a language tag
			
			String SPARQLquery = RDFS_PREFIX_DECLARATION +
								 " " + RDF_PREFIX_DECLARATION +
								 " select distinct ?resource" +
								 " where { ?resource rdfs:label " + wrappedClueFragment + "}";
			Query query = QueryFactory.create(SPARQLquery);
			QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ENDPOINT_URI, query);
			try {
				ResultSet resultSet = queryExecution.execSelect();
				while(resultSet.hasNext()) {
					QuerySolution querySolution = resultSet.nextSolution();
					Resource thisResource = querySolution.getResource("?resource");
					String resourceURI = thisResource.getURI();
					
					if(!recognisedResources.contains(resourceURI))
						recognisedResources.add(resourceURI);
				}
				queryExecution.close();	
			}
			catch(QueryExceptionHTTP e) {
				throw e;
			}
			
		}
		return recognisedResources;
	}
	
	private void setClueFragments() {
		this.clueFragments = new ArrayList<String>();
		String clueText = this.getClue().getSourceClue();
		String[] wordsInClueText = clueText.split(" ");
		for(int i = 0; i < wordsInClueText.length; i++) {
			String thisWord = this.toProperCase(wordsInClueText[i]);
			if(!excludedWord(thisWord))
				this.getClueFragments().add(thisWord);
			for(int j = i + 1; j < wordsInClueText.length; j++) {
				thisWord = thisWord + " " + this.toProperCase(wordsInClueText[j]);
				if(!excludedWord(thisWord))
					this.getClueFragments().add(thisWord);
			}
		}
		for(String s : clueFragments) // DEBUGGING *****************************************
			System.out.println(s);
	}

	private String toProperCase(String thisWord) {
		String thisWordInProperCase = thisWord.substring(0, 1).toUpperCase();
		if(thisWord.length() > 1)
			thisWordInProperCase += thisWord.substring(1,thisWord.length());
		return thisWordInProperCase;
	}
	
	/**
	 * 
	 * @param wordToCheck
	 * @return true if wordToCheck is in the list of common words to be excluded
	 */
	private boolean excludedWord(String wordToCheck) {
		for(int i = 0; i < this.WORDS_TO_EXCLUDE.length; i++)
			if(toProperCase(WORDS_TO_EXCLUDE[i]).equals(wordToCheck))
				return true;
		return false;
	}
	
	
	@Override
	public ArrayList<Resource> getRecognisedResources() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<Property> getRecognisedProperties() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	@Override
	public ArrayList<Resource> getRecognisedSubjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Resource> getRecognisedObjects() {
		// TODO Auto-generated method stub
		return null;
	}
}
