/**
 * 
 */
package prototype;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import framework.prototype.Clue;
import framework.prototype.EntityRecogniser;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleEntityRecogniser implements EntityRecogniser {
	private final int LANGUAGE_TAG_LENGTH = 3; // DUPLICATED IN SIMPLESOLUTION CLASS - REFACTOR OUT AS A STATIC CONSTANT?
	private final String LANGUAGE_TAG = "@"; // DUPLICATED IN SIMPLESOLUTION CLASS - REFACTOR OUT AS A STATIC CONSTANT?
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Model model;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private OntModel ontModel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ResIterator subjectsIterator;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private StmtIterator statementsIterator; // possibly don't need the other two?
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private NodeIterator objectsIterator;
	@Setter(AccessLevel.PUBLIC) private ArrayList<Resource> recognisedSubjects;
	@Setter(AccessLevel.PRIVATE) private ArrayList<Property> recognisedProperties;
	@Setter(AccessLevel.PRIVATE) private ArrayList<Resource> recognisedObjects;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<Resource> allSubjects;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<Property> allProperties;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<Resource> allObjects;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private ArrayList<String> clueFragments;

	public SimpleEntityRecogniser(Clue clue, Model model) {
		this.setClue(clue);
		this.setModel(model);
		this.setOntModel(ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM,this.getModel()));
		this.setClueFragments(new ArrayList<String>());
		this.addClueFragments(this.getClue().getSourceClue());
		this.setRecognisedSubjects(new ArrayList<Resource>());
		this.setRecognisedProperties(new ArrayList<Property>());
		this.setRecognisedObjects(new ArrayList<Resource>());
	}
	
	private void addClueFragments(String clueText) {
		String[] wordsInClueText = clueText.split(" ");
		for(int i = 0; i < wordsInClueText.length; i++) {
			String thisWord = wordsInClueText[i].toLowerCase();
			if(!this.getClueFragments().contains(thisWord))
				this.getClueFragments().add(thisWord);
			for(int j = i + 1; j < wordsInClueText.length; j++) {
				thisWord = thisWord + " " + wordsInClueText[j].toLowerCase();
				if(!this.getClueFragments().contains(thisWord))
					this.getClueFragments().add(thisWord);
			}
		}
	}
	
	/*
	private void setClueFragments() {
		this.clueFragments = new ArrayList<String>();
		String clueText = this.getClue().getSourceClue();
		String[] wordsInClueText = clueText.split(" ");
		for(int i = 0; i < wordsInClueText.length; i++) {
			String thisWord = wordsInClueText[i];
			this.getClueFragments().add(thisWord.toLowerCase());
			for(int j = i + 1; j < wordsInClueText.length; j++) {
				thisWord = thisWord + " " + wordsInClueText[j];
				this.getClueFragments().add(thisWord.toLowerCase());
			}
		}
	} */
	
	/*
	 * THIS CODE IS DUPLICATED IN THE SIMPLESOLUTION CLASS - REFACTOR IT OUT SOMEWHERE?
	 */
	private String stripLanguageTag(String text) {
		int positionOfLanguageTag = text.length() - LANGUAGE_TAG_LENGTH;
		if(text.length() > LANGUAGE_TAG_LENGTH) {
			if(text.substring(positionOfLanguageTag, positionOfLanguageTag + 1).equals(LANGUAGE_TAG))
				return text.substring(0, positionOfLanguageTag);
		}
		return text;
	}

	@Override
	public ArrayList<Resource> getRecognisedSubjects() {
		
		/* Outline of multithreading idea (doesn't actually work; just exceeds heap space and crashes!
		if(this.recognisedSubjects == null) {
			this.setRecognisedSubjects(new ArrayList<Resource>()); // take this line out of constructor
			ResIterator subjectsInModel = this.getModel().listSubjects();
			while(subjectsInModel.hasNext()) {
				Thread erThread = new Thread(new EntityRecogniserRunnable(this));
				erThread.start(); // start a new thread to query the model for this subject
			}
		}
		return this.recognisedSubjects; */
		
		
		
		if(this.getSubjectsIterator() == null)
			this.setSubjectsIterator(this.getModel().listSubjects());
		
		while(this.getSubjectsIterator().hasNext()) {
			Resource thisSubject = this.getSubjectsIterator().nextResource();
			if(this.recognisedSubjects.contains(thisSubject))
				continue;
			OntResource subject = this.getOntModel().getOntResource(thisSubject); // create an OntResource from the Resource object
			ExtendedIterator<RDFNode> labels = subject.listLabels(null); // list all values of RDFS:label for this resource
			while(labels.hasNext()) {
				String thisLabel = stripLanguageTag(labels.next().toString());
				if(this.getClueFragments().contains(thisLabel.toLowerCase()))
					this.recognisedSubjects.add(thisSubject);
			}
		}
		return this.recognisedSubjects;
	}
	
	/*
	 * CODE DUPLICATED FROM getRecognisedSubjects() method - TO BE REFACTORED SOMEHOW
	 */
	@Override
	public ArrayList<Property> getRecognisedProperties() {
		if(this.getStatementsIterator() == null)
			this.setStatementsIterator(this.getModel().listStatements());
		
		while(this.getStatementsIterator().hasNext()) {
			Property thisProperty = this.getStatementsIterator().nextStatement().getPredicate();
			if(this.recognisedProperties.contains(thisProperty))
				continue;
			String uri = thisProperty.getURI();
			if(uri == null)
				continue;
			OntProperty property = this.getOntModel().getOntProperty(uri); // create an OntProperty from the Property object
			if(property == null)
				continue;
			ExtendedIterator<RDFNode> labels = property.listLabels(null); // list all values of RDFS:label for this resource
			if(labels == null)
				continue;
			while(labels.hasNext()) {
				String thisLabel = stripLanguageTag(labels.next().toString());
				if(this.getClueFragments().contains(thisLabel))
					this.recognisedProperties.add(thisProperty);
			}
		}
		return this.recognisedProperties;
	}
	
	/*
	 * CODE DUPLICATED FROM getRecognisedSubjects() method - TO BE REFACTORED SOMEHOW
	 */
	@Override
	public ArrayList<Resource> getRecognisedObjects() {
		if(this.getObjectsIterator() == null)
			this.setObjectsIterator(this.getModel().listObjects());
		
		while(this.getObjectsIterator().hasNext()) {
			RDFNode thisObject = this.getObjectsIterator().nextNode();
			if(thisObject.isResource()) { // We are only interested in objects that are resources
				if(this.recognisedObjects.contains((Resource)thisObject))
					continue;
				OntResource object = this.getOntModel().getOntResource((Resource)thisObject); // create an OntResource from the RDFNode object
				ExtendedIterator<RDFNode> labels = object.listLabels(null); // list all values of RDFS:label for this resource
				while(labels.hasNext()) {
					String thisLabel = stripLanguageTag(labels.next().toString());
					if(this.getClueFragments().contains(thisLabel))
						this.recognisedObjects.add((Resource)thisObject);
				}
			}
		}
		return this.recognisedObjects;
	}

	@Override
	public ArrayList<Resource> getRecognisedResources() {
		ArrayList<Resource> subjectsAndObjects = this.getRecognisedSubjects();
		subjectsAndObjects.addAll(this.getRecognisedObjects());
		return subjectsAndObjects;
	}

	@Override
	public ArrayList<String> getRecognisedResourceURIs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getRecognisedPropertyURIs() {
		// TODO Auto-generated method stub
		return null;
	}

}
