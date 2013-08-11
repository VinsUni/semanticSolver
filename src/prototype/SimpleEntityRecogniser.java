/**
 * 
 */
package prototype;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleEntityRecogniser implements EntityRecogniser {
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) Model model;
	@Setter(AccessLevel.PRIVATE) ArrayList<Resource> recognisedSubjects;
	@Setter(AccessLevel.PRIVATE) ArrayList<Property> recognisedProperties;
	@Setter(AccessLevel.PRIVATE) ArrayList<Resource> recognisedObjects;
	
	@Getter(AccessLevel.PUBLIC) ArrayList<String> clueFragments;

	public SimpleEntityRecogniser(Clue clue, Model model) {
		this.setClue(clue);
		this.setModel(model);
		this.setClueFragments();
		this.setRecognisedSubjects(new ArrayList<Resource>());
		this.setRecognisedProperties(new ArrayList<Property>());
		this.setRecognisedObjects(new ArrayList<Resource>());
	}
	
	private void setClueFragments() {
		this.clueFragments = new ArrayList<String>();
		String clueText = this.getClue().getSourceClue();
		String[] wordsInClueText = clueText.split(" ");
		for(int i = 0; i < wordsInClueText.length; i++) {
			String thisWord = wordsInClueText[i];
			this.getClueFragments().add(thisWord);
			for(int j = i + 1; j < wordsInClueText.length; j++) {
				thisWord = thisWord + " " + wordsInClueText[j];
				this.getClueFragments().add(thisWord);
			}
		}
	}

	@Override
	public ArrayList<Resource> getRecognisedSubjects() {
		ResIterator resourceIterator = this.getModel().listSubjects();
		return this.recognisedSubjects;
	}

	@Override
	public ArrayList<Property> getRecognisedProperties() {
		// TODO Auto-generated method stub
		return this.recognisedProperties;
	}

	@Override
	public ArrayList<Resource> getRecognisedObjects() {
		// TODO Auto-generated method stub
		return this.recognisedObjects;
	}

}
