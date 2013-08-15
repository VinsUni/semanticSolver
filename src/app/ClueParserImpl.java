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
 * This implementation needs to query DBpedia to see if it can find resources that match any of the clue fragments identified.
 * It will need to return a list of identified resources to the Solver object, so that it can perform queries
 */
public class ClueParserImpl implements ClueParser {
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) Model model;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) EntityRecogniser entityRecogniser;
	
	
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
