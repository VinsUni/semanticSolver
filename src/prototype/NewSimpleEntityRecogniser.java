/**
 * 
 */
package prototype;

import java.util.ArrayList;
import java.util.Map;

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

import framework.Clue;
import framework.EntityRecogniser;

/**
 * @author Ben Griffiths
 * 
 * Two ideas:
 * 1. Do I have to list every subject, predicate, and object in the model every time and go through them one by one to see if their
 * labels match? Couldn't I either (a) just do this once, the first time, and store the labels either in memory or in a file, or
 * (b) query the model in some way to pick likely subjects, predicates and objects rather than listing every one?
 * 
 * 2. THE IDEA IS TO SIMPLY RECOGNISE ALL RESOURCES at once (instead of doing subjects, predicates, and objects separately)
 * AND THEN ALLOCATE ONES THAT ARE SUBCLASSES OF OBJECTPROPERTY TO PROPERTIES LIST
 * AND THE OTHERS TO BOTH SUBJECT AND OBJECT LISTS. ONE PROBLEM AT THE MOMENT IS THAT PROPERTIES - E.G. ALBUMOF AND HASALBUM ARE
 * RECOGNISED AS OBJECTS OR SUBJECTS BUT NOT AS PROPERTIES
 */
public class NewSimpleEntityRecogniser implements EntityRecogniser {
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) Clue clue;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) Model model;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) OntModel ontModel;
	@Setter(AccessLevel.PUBLIC) ArrayList<Resource> recognisedSubjects;
	@Setter(AccessLevel.PRIVATE) ArrayList<Property> recognisedProperties;
	@Setter(AccessLevel.PRIVATE) ArrayList<Resource> recognisedObjects;
	
	@Getter(AccessLevel.PUBLIC) ArrayList<String> clueFragments;

	public NewSimpleEntityRecogniser(Clue clue, Model model) {
		this.setClue(clue);
		this.setModel(model);
		this.setOntModel(ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM,this.getModel()));
		this.setClueFragments();
	}
	
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
	}
	
	@Override
	public ArrayList<Resource> getRecognisedSubjects() {
		this.setRecognisedSubjects(new ArrayList<Resource>());
		Map<String, Resource> subjects = NewSimpleModelLoader.subjectsInModel;
		
		for(String clueFragment : this.getClueFragments()) {
			if(subjects.containsKey(clueFragment));
				this.recognisedSubjects.add(subjects.get(clueFragment)); // add the resource with a matching a String value
		}
		return this.recognisedSubjects;
	}
	
	/*
	 * CODE DUPLICATED FROM getRecognisedSubjects() method - TO BE REFACTORED SOMEHOW
	 */
	@Override
	public ArrayList<Property> getRecognisedProperties() {
		this.setRecognisedProperties(new ArrayList<Property>());
		Map<String, Property> properties = NewSimpleModelLoader.propertiesInModel;
		
		for(String clueFragment : this.getClueFragments()) {
			if(properties.containsKey(clueFragment));
				this.recognisedProperties.add(properties.get(clueFragment)); // add the resource with a matching a String value
		}
		return this.recognisedProperties;
	}
	
	@Override
	public ArrayList<Resource> getRecognisedObjects() {
		this.setRecognisedObjects(new ArrayList<Resource>());
		Map<String, Resource> objects = NewSimpleModelLoader.objectsInModel;
		
		for(String clueFragment : this.getClueFragments()) {
			if(objects.containsKey(clueFragment));
				this.recognisedObjects.add(objects.get(clueFragment)); // add the resource with a matching a String value
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

}
