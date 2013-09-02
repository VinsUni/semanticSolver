/**
 * 
 */
package app;

import java.util.ArrayList;

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
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private ArrayList<String> solutionTexts;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private String clueResourceUri;
	
	public SolvedClue(String clueText, String solutionStructure, String clueResourceUri) {
		this.setClueText(clueText);
		this.setSolutionStructure(solutionStructure);
		this.setClueResourceUri(clueResourceUri);
		this.setSolutionTexts(new ArrayList<String>());
	}
	
	public SolvedClue(String clueText, String solutionStructure, String clueResourceUri, String solutionText) {
		this(clueText, solutionStructure, clueResourceUri);
		this.getSolutionTexts().add(solutionText);
	}
	
	/**
	 * equals - see java.lang.Object
	 * Two SolvedClue objects are equal if their clueText members are equal and their solutionStructure members are equal
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
				this.getSolutionStructure().equals(anotherSolvedClue.getSolutionStructure());
	}

}
