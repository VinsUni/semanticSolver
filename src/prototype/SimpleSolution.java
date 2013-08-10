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
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) String solutionText;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) int[] solutionStructure;
	
	public SimpleSolution(String solutionText) {
		this.setSolutionText(solutionText);
		this.setSolutionStructure(this.deriveSolutionStructure(solutionText));
	}

	private int[] deriveSolutionStructure(String solutionText) {
		String[] decomposedSolution = solutionText.split(" "); // words in the clue are separated by spaces
		int[] structure = new int[decomposedSolution.length];
		for(int i = 0; i < decomposedSolution.length; i++)
			structure[i] = decomposedSolution[i].length();
		return structure;
	}
	
	
}
