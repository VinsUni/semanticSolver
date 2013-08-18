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
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;

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
	private final String DBPPROP_PREFIX_DECLARATION = "PREFIX dbpprop: <http://dbpedia.org/property/>";
	private final String RDF_PREFIX_DECLARATION = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private ArrayList<String> clueFragments;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private StmtIterator statementsIterator; // used to iterate over the statements in my local ontology
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ResIterator propertiesIterator;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private OntModel ontModel; // to hold the ontology in memory
	private final String[] WORDS_TO_EXCLUDE = {"the", "of"}; // a list of common words to exclude from consideration
	private final String[] VOCABULARIES_TO_EXCLUDE = {"http://dbpedia.org/class/yago/"}; // a list of namespaces whose terms should be excluded from consideration
	private final String APOSTROPHE_S_SEQUENCE = "'s"; // if present in a clue, requires further special transformation
	
	
	public EntityRecogniserImpl(Clue clue) {
		this.setClue(clue);
		this.setClueFragments(new ArrayList<String>());
		this.addClueFragments(this.getClue().getSourceClue());
	}
	
	public ArrayList<String> getRecognisedResourceURIs() throws QueryExceptionHTTP {
		ArrayList<String> recognisedResources = new ArrayList<String>();
		
		for(String clueFragment : clueFragments) {
			
			String wrappedClueFragment = "\"" + clueFragment + "\"" + LANG; // wrap with escaped quotes and append a language tag
			
			String SPARQLquery = RDFS_PREFIX_DECLARATION + " " +
								 DBPPROP_PREFIX_DECLARATION +
						" select distinct ?resource {" +
				       		" {" +
					       		"{ select distinct ?resource" +
					       		" where {?resource rdfs:label " + wrappedClueFragment + "}" +
					       		" }" +
					       		" UNION" +
					       		" { select distinct ?resource" +
					       		"  where {?resource dbpprop:name " + wrappedClueFragment + "}" +
					       		" }" +
					       	" }" + 
				       " }";
			
			
			String s = RDFS_PREFIX_DECLARATION + // the query I was previously using
								 " " + RDF_PREFIX_DECLARATION + // this prefix isn't used in this query!
								 " select distinct ?resource" +
								 " where { ?resource rdfs:label " + wrappedClueFragment + "}" +
								 " UNION" +
								 " select distinct ?resource" +
								 " where { ?resource dbpprop:name " + wrappedClueFragment + "}";
			
			Query query = QueryFactory.create(SPARQLquery);
			QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ENDPOINT_URI, query);
			try {
				ResultSet resultSet = queryExecution.execSelect();
				while(resultSet.hasNext()) {
					QuerySolution querySolution = resultSet.nextSolution();
					Resource thisResource = querySolution.getResource("?resource");
					
					String nameSpace = thisResource.getNameSpace();
					if(excludedNameSpace(nameSpace))
						continue;
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
		
		if(this.getPropertiesIterator() == null) {
			ModelLoader modelLoader = new ModelLoaderImpl();
			InfModel model = modelLoader.getModel();
			this.setPropertiesIterator(model.listSubjects()); // NEED TO DO THE SAME WITH OBJECTS! ********************************
			this.setOntModel(ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MINI_RULE_INF, model)); // initialise the OntModel member
		}
		
		while(this.getPropertiesIterator().hasNext()) {
			Resource thisResource = this.getPropertiesIterator().nextResource();
			String uri = thisResource.getURI();
			if(uri == null)
				continue;
			if(recognisedPropertyURIs.contains(uri))
				continue;
			
			OntResource resource = this.getOntModel().getOntResource(uri); // create an OntResource from the Resource object
			if(resource == null)
				continue;
			
			
			
			/* Check to see if there are inverseProperties of this property that match a label 
			NodeIterator inverseProperties = resource.listPropertyValues(OWL.inverseOf);
			if(inverseProperties != null) {
				while(inverseProperties.hasNext()) {
					RDFNode propertyNode = inverseProperties.next();
					OntResource inverseProperty = (OntResource)propertyNode;
					
					String nodeURI;
					if(propertyNode.isURIResource())
						nodeURI = propertyNode.
					ExtendedIterator<RDFNode> labels = propertyNode.listLabels(null); // list all values of RDFS:label for this resource
					if(labels == null)
						continue;
					while(labels.hasNext()) {
						String thisLabel = stripLanguageTag(labels.next().toString());
						if(this.getClueFragments().contains(toProperCase(thisLabel)))
							recognisedPropertyURIs.add(uri);
					}
				}
			}
			*/
			
			if(thisResource.getNameSpace().equals(ONTOLOGY_NAMESPACE)) { // properties in my pop: namespace are not wanted as they aren't used in the wild
				//FIRST I NEED TO CHECK IF ANY OF THE LABELS OF THIS RESOURCE MATCH! SO I SHOULD DO THIS STUFF FURTHER BELOW...
				continue;
				/*
				ArrayList<RDFNode> nodeList = new ArrayList<RDFNode>();
				StmtIterator equivalentPropertyStatements = thisResource.listProperties();
				while(equivalentPropertyStatements.hasNext()) {
					Statement thisStatement = equivalentPropertyStatements.nextStatement();
					if(thisStatement.getPredicate().equals(OWL.equivalentProperty))
						nodeList.add(thisStatement.getObject());
				}
				for(RDFNode node : nodeList) { // instead, we want to check the labels of any equivalent properties in a different NS
					Resource equivalentProperty = (Resource)node;
					if(equivalentProperty.getNameSpace().equals(ONTOLOGY_NAMESPACE))
						continue; // the equivalent property is also in my pop: namespace, so ignore it and move on to next one
					String equivalentPropertyURI = equivalentProperty.getURI();
					
					System.out.println("Found equivalent property: " + equivalentPropertyURI); // DEBUGGING **********************
					OntResource thisEquivalentProperty = this.getOntModel().getOntResource(equivalentPropertyURI);
					// THIS CODE IS DUPLICATED BELOW
					ExtendedIterator<RDFNode> labels = thisEquivalentProperty.listLabels(null); // list all values of RDFS:label for this resource
					
					if(labels == null)
						continue;
					while(labels.hasNext()) {
						String thisLabel = stripLanguageTag(labels.next().toString());
						System.out.println("Found label of equivalent Property: " + thisLabel); // DEBUGGING *******************************************
						if(this.getClueFragments().contains(toProperCase(thisLabel))) // ****** I need to test the other label!
							recognisedPropertyURIs.add(equivalentPropertyURI);
					}
				}
				continue; // matched property was part of namespace - equivs have been checked, so move onto next found property
				*/
			}
			/* THIS CODE IS DUPLICATED ABOVE */
			ExtendedIterator<RDFNode> labels = resource.listLabels(null); // list all values of RDFS:label for this resource
			
			if(labels == null)
				continue;
			while(labels.hasNext()) {
				String thisLabel = stripLanguageTag(labels.next().toString());
				System.out.println("Found label: " + thisLabel); // DEBUGGING *******************************************
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
		
		if(clueText.contains(this.APOSTROPHE_S_SEQUENCE)) {
			String transformedClueText = clueText.replace(this.APOSTROPHE_S_SEQUENCE, "");
			this.addClueFragments(transformedClueText);
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
	
	/**
	 * excludedNameSpace - checks if any of the namespaces of the vocabularies that we want to exclude from consideration are
	 * substrings of the namespace under consideration. It is necessary to do it this way rather than simply checking if the NS 
	 * under consideration is in our list of namespaces to be excluded primarily because of the high number of resources on DBpedia
	 * that have been created under a badly-formed variant of the NS http://dbpedia.org/class/yago/, where a resource with local
	 * name local_name has been put into the DBpedia graph with the NS "http://dbpedia.org/class/yago/local_name"
	 * @param namespaceToCheck
	 * @return
	 */
	private boolean excludedNameSpace(String nameSpaceToCheck) {
		for(int i = 0; i < this.VOCABULARIES_TO_EXCLUDE.length; i++)
			if(nameSpaceToCheck.contains(VOCABULARIES_TO_EXCLUDE[i]))
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