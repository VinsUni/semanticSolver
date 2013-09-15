package app;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Resource;

import framework.Clue;
import framework.Solution;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 * SolutionImpl
 * An implementation of framework.Solution. Represents a solution to a clue. Provides access to the inference model from which the
 * solution was derived.
 */
public class SolutionImpl implements Solution {
	private final int LANGUAGE_TAG_LENGTH = 3;
	private final String LANGUAGE_TAG = "@";
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private String solutionText;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private int[] solutionStructure;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Resource solutionResource;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Resource clueResource;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private InfModel infModel;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private double score;
	
	/**
	 * stripLanguageTag
	 * @param solutionText - a String representing the text of a solution, which may or may not conclude with a language tag, "@XX"
	 * @return the solutionText with any trailing instance of "@XX" removed
	 */
	private String stripLanguageTag(String solutionText) {
		int positionOfLanguageTag = solutionText.length() - LANGUAGE_TAG_LENGTH;
		if(solutionText.length() > LANGUAGE_TAG_LENGTH) {
			if(solutionText.substring(positionOfLanguageTag, positionOfLanguageTag + 1).equals(LANGUAGE_TAG))
				return solutionText.substring(0, positionOfLanguageTag);
		}
		return solutionText;
	}
	
	/**
	 * removeIllegalCharacters - only letters, a-z or A-Z, spaces, and hyphens are considered valid characters in the text of a solution
	 * @param solutionText - the solution text to be parsed
	 * @return the solutionText after removing all characters that are not alphabetic or spaces or hyphens
	 */
	private String removeIllegalCharacters(String solutionText) {
		String parsedSolution = solutionText.replaceAll("[^a-zA-Z -]", "");
		return parsedSolution;
	}
	
	/**
	 * deriveSolutionStructure - parses the text of a solution to derive its structure, i.e. the number of words in the solution,
	 * and the number of letters in each word
	 * @param solutionText - the solution text of which to derive the structure
	 * @return an integer array representing the solution structure
	 */
	private int[] deriveSolutionStructure(String solutionText) {
		String[] decomposedSolution = solutionText.split(" "); // words in the clue are separated by spaces
		int[] structure = new int[decomposedSolution.length];
		for(int i = 0; i < decomposedSolution.length; i++)
			structure[i] = decomposedSolution[i].length();
		return structure;
	}
	
	/**
	 * Constructor - instantiates a new Solution to represent the given solutionText
	 * @param solutionText - the text of the Solution
	 * @param solutionResource - an instance of com.hp.hpl.jena.rdf.model.Resource representing the resource from whose label the 
	 * solution text was derived
	 * @param clueResource - an instance of com.hp.hpl.jena.rdf.model.Resource representing the resource, recognised as a named entity
	 * in the text of the clue, around which the infModel argument was created
	 * @param infModel - an instance of com.hp.hpl.jena.rdf.model.InfModel representing the RDF graph containing the solutionResource
	 * @param clue - an instance of framework.Clue representing the clue to which the solutionText provides a solution
	 */
	public SolutionImpl(String solutionText, Resource solutionResource, Resource clueResource, InfModel infModel, Clue clue) {
		String solutionWithoutLanguageTag = this.stripLanguageTag(solutionText);
		this.setSolutionText(this.removeIllegalCharacters(solutionWithoutLanguageTag));
		this.setSolutionStructure(this.deriveSolutionStructure(this.getSolutionText()));
		this.setSolutionResource(solutionResource);
		this.setClueResource(clueResource);
		this.setInfModel(infModel);
		this.setClue(clue);
	}
	
	/**
	 * equals - two Solution objects are considered equal if their respective solutionText members are equal, 
	 * the URLs of their respective solutionResource members are equal, and the URLs of their respective clueResource members are equal
	 * @override java.lang.Object.equals
	 */
	@Override
	public boolean equals(Object anotherObject) {
		SolutionImpl anotherSolution;
		try {
			anotherSolution = (SolutionImpl)anotherObject;
		}
		catch(ClassCastException e) {
			return false;
		}
		return this.getSolutionText().equals(anotherSolution.getSolutionText()) &&
				this.getSolutionResource().getURI().equals(anotherSolution.getSolutionResource().getURI()) &&
				this.getClueResource().getURI().equals(anotherSolution.getClueResource().getURI());
	}
	
	/**
	 * getConfidence
	 * @override framework.Solution.getConfidence
	 */
	@Override
	public int getConfidence() {
		int confidence = (int)((1 - this.getScore()) * 100);
		return confidence;
	}
	
	/**
	 * toString
	 * @override java.lang.Object.toString
	 */
	@Override
	public String toString() {
		return this.getSolutionText() + " - " + this.getSolutionResource().getURI() + " - " + this.getClueResource().getURI();
	}
}
