/**
 * 
 */
package app;

import framework.Solution;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 * Represents a solution to a clue - at present, it is just used to parse the solution by removing language tags and other
 * unwanted characters
 */
public class SolutionImpl implements Solution {
	private final int LANGUAGE_TAG_LENGTH = 3;
	private final String LANGUAGE_TAG = "@";
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private String solutionText;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private int[] solutionStructure;
	
	public SolutionImpl(String solutionText) {
		String solutionWithoutLanguageTag = this.stripLanguageTag(solutionText);
		this.setSolutionText(this.removeIllegalCharacters(solutionWithoutLanguageTag));
		this.setSolutionStructure(this.deriveSolutionStructure(this.getSolutionText()));
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
	
	/**
	 * 
	 * @param solutionWithoutLanguageTag
	 * @return the solution after removing all characters that are not alphabetic or spaces or hyphens
	 */
	private String removeIllegalCharacters(String solutionWithoutLanguageTag) {
		String parsedSolution = solutionWithoutLanguageTag.replaceAll("[^a-zA-Z -]", "");
		return parsedSolution;
	}

	private int[] deriveSolutionStructure(String solutionText) {
		String[] decomposedSolution = solutionText.split(" "); // words in the clue are separated by spaces
		int[] structure = new int[decomposedSolution.length];
		for(int i = 0; i < decomposedSolution.length; i++)
			structure[i] = decomposedSolution[i].length();
		return structure;
	}
	
	
}
