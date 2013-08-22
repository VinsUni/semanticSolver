/**
 * 
 */
package app;

import java.util.ArrayList;
import java.util.Arrays;

import com.hp.hpl.jena.rdf.model.Selector;

import exception.InvalidClueException;
import framework.Clue;
import framework.Solution;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 * Represents a clue
 */
public class ClueImpl implements Clue {
	private final String FILL_IN_THE_BLANK_MARKER = "_";
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private String sourceClue;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private ArrayList<String> clueVariations;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private ArrayList<Selector> selectorVariations;
	/* solutionStructure of e.g. {2, 3} means the answer consists of a 2-letter word followed by a 3-letter word */
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private int[] SolutionStructure;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private boolean fillInTheBlank; // true if the clue is a 'Fill in the blank' style clue

	/**
	 * Constructor - constructs a clue from a String representing the clue text and an int[] representing the solution structure
	 * @param clueText
	 * @param solutionStructure
	 * @throws InvalidClueException
	 */
	public ClueImpl(String clueText, int[] solutionStructure) throws InvalidClueException {
		if(clueText == null || clueText.length() == 0)
			throw new InvalidClueException("Empty clue");
		if(solutionStructure == null || solutionStructure.length == 0)
			throw new InvalidClueException("Invalid specification of solution structure");
		for(int i = 0; i < solutionStructure.length; i++) {
			if(solutionStructure[i] < 1)
				throw new InvalidClueException("Invalid specification of solution structure");
		}
		
		this.setSourceClue(clueText);
		if(this.getSourceClue().contains(FILL_IN_THE_BLANK_MARKER))
			this.setFillInTheBlank(true);
		System.err.println("Clue text = " + this.getSourceClue()); // DEBUGGING
		
		this.setSolutionStructure(solutionStructure);
	}

	@Override
	public boolean matchesStructure(Solution solution) {
		return (Arrays.equals(solution.getSolutionStructure(), this.getSolutionStructure())); // requires comparison of deep equality
	}
}
