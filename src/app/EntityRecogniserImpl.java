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

import framework.Clue;

/**
 * @author Ben Griffiths
 *  ************************************************************************************************************!!!!!!!!!!!!!
 * I NEED TO USE THIS: http://wiki.dbpedia.org/lookup/
 *  ************************************************************************************************************!!!!!!!!!!!!!
 */
public class EntityRecogniserImpl {
	private final String LANG = "@en";
	private final String ENDPOINT_URI = "http://dbpedia.org/sparql"; // DUPLICATED IN QUERYIMPL
	//private final String DBPEDIA_PREFIX_DECLARATION = "PREFIX dbpedia: <http://dbpedia.org/resource/>"; // DUPLICATED IN QUERYIMPL
	//private final String DBPEDIA_OWL_PREFIX_DECLARATION = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>"; // DUPLICATED IN QUERYIMPL
	private final String RDFS_PREFIX_DECLARATION = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"; // DUPLICATED IN QUERYIMPL
	private final String RDF_PREFIX_DECLARATION = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) Clue clue;
	@Getter(AccessLevel.PUBLIC) ArrayList<String> clueFragments;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) ArrayList<Property> recognisedProperties;

	public EntityRecogniserImpl(Clue clue) {
		this.setClue(clue);
		this.setClueFragments();
	}
	
	public ArrayList<String> getRecognisedResources() throws HttpException {
		ArrayList<String> recognisedResources = new ArrayList<String>();
		
		for(String clueFragment : clueFragments) {
			
			String SPARQLquery = RDFS_PREFIX_DECLARATION +
								 " " + RDF_PREFIX_DECLARATION +
								 " select distinct ?resource" +
								 " where { ?resource rdfs:label " + clueFragment + ".}";
			
			Query query = QueryFactory.create(SPARQLquery);
			QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ENDPOINT_URI, query);
			try {
				ResultSet resultSet = queryExecution.execSelect();
				while(resultSet.hasNext()) {
					QuerySolution querySolution = resultSet.nextSolution();
					Resource thisResource = querySolution.getResource("?resource");
					String resourceURI = thisResource.getURI();
					
					/* I need to do this manipulation of the Strings BEFORE querying! */
					resourceURI = "\"" + resourceURI + "\""; // surround the raw String with quotes
					if(!recognisedResources.contains(resourceURI)) {
						recognisedResources.add(resourceURI); // add the quoted String
						recognisedResources.add(resourceURI + LANG); // add the quoted String with a language specification appended
					}
				}
				queryExecution.close();	
			}
			catch(HttpException e) {
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
			this.getClueFragments().add(thisWord);
			for(int j = i + 1; j < wordsInClueText.length; j++) {
				thisWord = thisWord + " " + this.toProperCase(wordsInClueText[j]);
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
}
