/**
 * 
 */
package app;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import framework.Clue;
import framework.EntityRecogniser;
import framework.ModelLoader;

/**
 * @author Ben Griffiths
 *  ************************************************************************************************************!!!!!!!!!!!!!
 * I NEED TO USE THIS: http://wiki.dbpedia.org/lookup/
 *  ************************************************************************************************************!!!!!!!!!!!!!
 */
public class EntityRecogniserImpl implements EntityRecogniser {
	private final String ONTOLOGY_NAMESPACE = "http://www.griffithsben.com/ontologies/pop.owl#";
	private final int LANGUAGE_TAG_LENGTH = 3;
	private final String LANGUAGE_TAG = "@";
	private final String LANG = "@en";
	private final String ENDPOINT_URI = "http://dbpedia.org/sparql"; // DUPLICATED IN QUERYIMPL
	//private final String DBPEDIA_PREFIX_DECLARATION = "PREFIX dbpedia: <http://dbpedia.org/resource/>"; // DUPLICATED IN QUERYIMPL
	//private final String DBPEDIA_OWL_PREFIX_DECLARATION = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>"; // DUPLICATED IN QUERYIMPL
	private final String RDFS_PREFIX_DECLARATION = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"; // DUPLICATED IN QUERYIMPL
	private final String RDF_PREFIX_DECLARATION = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private ArrayList<String> clueFragments;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private StmtIterator statementsIterator; // used to iterate over the statements in my local ontology
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ResIterator propertiesIterator;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private OntModel ontModel; // to hold the ontology in memory
	private final String[] WORDS_TO_EXCLUDE = {"the", "of"}; // a list of common words to exclude from consideration

	public EntityRecogniserImpl(Clue clue) {
		this.setClue(clue);
		this.setClueFragments(new ArrayList<String>());
		this.addClueFragments(this.getClue().getSourceClue());
	}
	
	public ArrayList<String> getRecognisedResourceURIs() throws QueryExceptionHTTP {
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
	
	/**
	 * Queries the locally stored ontology for properties matching fragments of the clue
	 */
	@Override
	public ArrayList<String> getRecognisedPropertyURIs() {
		ArrayList<String> recognisedPropertyURIs = new ArrayList<String>();
		
		if(this.getStatementsIterator() == null) {
			ModelLoader modelLoader = new ModelLoaderImpl();
			InfModel model = modelLoader.getModel();
			this.setPropertiesIterator(model.listSubjects()); // NEED TO DO THE SAME WITH OBJECTS! ********************************
			this.setOntModel(ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM, model)); // initialise the OntModel member
		}
		
		while(this.getPropertiesIterator().hasNext()) {
			Resource thisResource = this.getPropertiesIterator().nextResource();
			String uri = thisResource.getURI();
			if(uri == null)
				continue;
			if(recognisedPropertyURIs.contains(uri))
				continue;
			if(thisResource.getNameSpace().equals(ONTOLOGY_NAMESPACE)) {
				continue; // properties in my pop namespace are not wanted as they aren't used in the wild
			}
			OntResource resource = this.getOntModel().getOntResource(uri); // create an OntResource from the Resource object
			if(resource == null)
				continue;
			ExtendedIterator<RDFNode> labels = resource.listLabels(null); // list all values of RDFS:label for this resource
			if(labels == null)
				continue;
			while(labels.hasNext()) {
				String thisLabel = stripLanguageTag(labels.next().toString());
				if(this.getClueFragments().contains(toProperCase(thisLabel)))
					recognisedPropertyURIs.add(uri);
			}
		}
		return recognisedPropertyURIs;
	}
	
	/*
	 * THIS CODE IS DUPLICATED IN THE SIMPLEENTITYRECOGNISER CLASS - REFACTOR IT OUT SOMEWHERE?
	 */
	private String stripLanguageTag(String solutionText) {
		int positionOfLanguageTag = solutionText.length() - LANGUAGE_TAG_LENGTH;
		if(solutionText.length() > LANGUAGE_TAG_LENGTH) {
			if(solutionText.substring(positionOfLanguageTag, positionOfLanguageTag + 1).equals(LANGUAGE_TAG))
				return solutionText.substring(0, positionOfLanguageTag);
		}
		return solutionText;
	}
	
	private void addClueFragments(String clueText) {
		String[] wordsInClueText = clueText.split(" ");
		for(int i = 0; i < wordsInClueText.length; i++) {
			String thisWord = this.toProperCase(wordsInClueText[i]);
			if(!this.getClueFragments().contains(thisWord) && !excludedWord(thisWord))
				this.getClueFragments().add(thisWord);
			for(int j = i + 1; j < wordsInClueText.length; j++) {
				thisWord = thisWord + " " + this.toProperCase(wordsInClueText[j]);
				if(!this.getClueFragments().contains(thisWord) && !excludedWord(thisWord))
					this.getClueFragments().add(thisWord);
			}
		}
		for(String s : clueFragments) // DEBUGGING *****************************************
			System.out.println(s);
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
