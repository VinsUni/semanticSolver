/**
 * 
 */
package prototype;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleSolution implements Solution {
	private final int LANGUAGE_TAG_LENGTH = 3;
	private final String LANGUAGE_TAG = "@";
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) String solutionText;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) int[] solutionStructure;
	
	public SimpleSolution(String solutionText) {
		this.setSolutionText(stripLanguageTag(solutionText));
		this.setSolutionStructure(this.deriveSolutionStructure(this.getSolutionText()));
	}

	private String stripLanguageTag(String solutionText) {
		int positionOfLanguageTag = solutionText.length() - LANGUAGE_TAG_LENGTH;
		if(solutionText.length() > LANGUAGE_TAG_LENGTH) {
			if(solutionText.substring(positionOfLanguageTag, positionOfLanguageTag + 1).equals(LANGUAGE_TAG))
				return solutionText.substring(0, positionOfLanguageTag);
		}
		return solutionText;
	}

	private int[] deriveSolutionStructure(String solutionText) {
		String[] decomposedSolution = solutionText.split(" "); // words in the clue are separated by spaces
		int[] structure = new int[decomposedSolution.length];
		for(int i = 0; i < decomposedSolution.length; i++)
			structure[i] = decomposedSolution[i].length();
		return structure;
	}
	
	
}