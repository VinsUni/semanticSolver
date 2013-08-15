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
 */
public class EntityRecogniserImpl {
	private final int LANGUAGE_TAG_LENGTH = 3; // DUPLICATED IN SIMPLESOLUTION CLASS - REFACTOR OUT AS A STATIC CONSTANT?
	private final String LANGUAGE_TAG = "@"; // DUPLICATED IN SIMPLESOLUTION CLASS - REFACTOR OUT AS A STATIC CONSTANT?
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) Clue clue;
	@Getter(AccessLevel.PUBLIC) ArrayList<String> clueFragments;

	public EntityRecogniserImpl(Clue clue) {
		this.setClue(clue);
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
}
