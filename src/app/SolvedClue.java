package app;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 * SolvedClue
 * Provides a wrapper for a clue-solution pair, used by the Knowledge Base Manager module to track previously solved clues
 * and their solutions. A solved clue may contain more than one solutionText which solves it.
 */
public class SolvedClue {
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private String clueText;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private String solutionStructure;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private ArrayList<String> solutionTexts;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private String clueResourceUri;
	
	/**
	 * Constructor - instantiates a new SolvedClue from the provided clueText, solutionStructure, and URI representing a named entity in
	 * the clue text, without specifying any solutions to the clue.
	 * @param clueText - the text of the clue
	 * @param solutionStructure - a String representing the structure of the solution of the clue
	 * @param clueResourceUri - the URI of the DBpedia resource recognised as a named entity in the clue for this particular solving
	 * instance
	 */
	public SolvedClue(String clueText, String solutionStructure, String clueResourceUri) {
		this.setClueText(clueText);
		this.setSolutionStructure(solutionStructure);
		this.setClueResourceUri(clueResourceUri);
		this.setSolutionTexts(new ArrayList<String>());
	}
	
	/**
	 * Constructor - instantiates a new SolvedClue from the provided clueText, solutionStructure, URI representing a named entity in
	 * the clue text, and solutionText
	 * @param clueText - the text of the clue
	 * @param solutionStructure - a String representing the structure of the solution of the clue
	 * @param clueResourceUri - the URI of the DBpedia resource recognised as a named entity in the clue for this particular solving
	 * instance
	 * @param solutionText - a String representing a solution to this clue
	 */
	public SolvedClue(String clueText, String solutionStructure, String clueResourceUri, String solutionText) {
		this(clueText, solutionStructure, clueResourceUri);
		this.getSolutionTexts().add(solutionText);
	}
	
	/**
	 * equals - two SolvedClue objects are equal if their clueText members are equal and their solutionStructure members are equal. Note
	 * that their clueResourceUri and solutionTexts members may differ.
	 * @Override java.lang.Object.equals
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
