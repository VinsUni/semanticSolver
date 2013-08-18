/**
 * 
 */
package app;

import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;

import framework.Clue;
import framework.ClueParser;
import framework.EntityRecogniser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 * THIS IS CURRENTLY UNUSED
 */
public class ClueParserImpl implements ClueParser {
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Model model;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniser entityRecogniser;
	
	
	public ClueParserImpl(Clue clue) {
		this.setClue(clue);
		this.setModel(model);
		this.setStringVariations();
	}
	

	@Override
	public void parse() {
		ArrayList<Selector> selectorVariations = new ArrayList<Selector>();
		this.getClue().setSelectorVariations(selectorVariations);
	}

	/**
	 * CURRENTLY DOES NOTHING
	 * setStringVariations - breaks down the source clue into variations of the original String, and sets
	 * the Clue objects string variations
	 */
	private void setStringVariations() {
		String sourceClue = this.getClue().getSourceClue();
		String[] sourceClueFragments = sourceClue.split(" ");
	}
	
	/*
	 * FOR DEBUGGING - TO BE REMOVED
	 */
	private void printRecognisedEntities() {
		ArrayList<Resource> subjects = this.getEntityRecogniser().getRecognisedSubjects();
		ArrayList<Property> predicates = this.getEntityRecogniser().getRecognisedProperties();
		ArrayList<Resource> objects = this.getEntityRecogniser().getRecognisedObjects();
		
		System.out.println("Recognised subjects:");
		for(Resource subject : subjects)
			System.out.println(subject.toString());
		
		System.out.println("Recognised properties:");
		for(Property predicate: predicates)
			System.out.println(predicate.toString());
		
		System.out.println("Recognised objects:");
		for(Resource object : objects)
			System.out.println(object.toString());
	}
}