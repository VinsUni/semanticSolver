/**
 * 
 */
package app;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 *
 */
public class SolvedClue {
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private String clueText;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private String solutionStructure;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private String solutionText;
	
	public SolvedClue(String clueText, String solutionStructure, String solutionText) {
		this.setClueText(clueText);
		this.setSolutionStructure(solutionStructure);
		this.setSolutionText(solutionText);
	}
	
	/**
	 * equals - see java.lang.Object
	 * Two SolvedClue objects are equal if their clueText members are equal, their solutionStructure members are equal, 
	 * and their solutionText members are equal
	 */
	@Override
	public boolean equals(Object anotherObject) {
		SolvedClue anotherSolvedClue;
		try {
			anotherSolvedClue = (SolvedClue)anotherObject;
		}
		catch(ClassCastException e) {
			return false;
		}
		return this.getClueText().equals(anotherSolvedClue.getClueText()) &&
				this.getSolutionStructure().equals(anotherSolvedClue.getSolutionStructure()) &&
				this.getSolutionText().equals(anotherSolvedClue.getSolutionText());
	}

}
