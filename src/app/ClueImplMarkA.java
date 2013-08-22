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
public class ClueImplMarkA implements Clue {
	private final String SOLUTION_STRUCTURE_OPEN_TAG = "[";
	private final char SOLUTION_STRUCTURE_CLOSE_TAG = ']';
	private final String FILL_IN_THE_BLANK_MARKER = "_";
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private String sourceClue;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private ArrayList<String> clueVariations;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private ArrayList<Selector> selectorVariations;
	// solutionStructure of e.g. {2, 3} means the answer consists of a 2-letter word followed by a 3-letter word
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private int[] SolutionStructure;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private int numberOfWords;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private boolean fillInTheBlank; // true if the clue is a 'Fill in the blank' style clue
	
	
	public ClueImplMarkA() {
		
	}
	
	/**
	 * Construct a clue using a String that contains both source clue text and solution structure, in the form:
	 * words_making_up_clue [num_letters_in_first_word, ... , num_letters_in_last_word]
	 * @param clueAsString
	 * @throws InvalidClueException
	 */
	public ClueImplMarkA(String clueAsString) throws InvalidClueException {
		if(clueAsString == null || clueAsString.length() == 0)
			throw new InvalidClueException("Empty clue");
		if( (!clueAsString.contains(SOLUTION_STRUCTURE_OPEN_TAG)) 
			|| (clueAsString.charAt(clueAsString.length() - 1) != SOLUTION_STRUCTURE_CLOSE_TAG) )
			throw new InvalidClueException("Invalid specification of solution structure");
		// Raw clue is in form "words making up clue [String representing solution structure]"
		
		String[] decomposedClue = clueAsString.split("\\["); // split the raw clue into the 2 Strings either side of the '[' character
		
		this.setSourceClue(decomposedClue[0]);
		if(this.getSourceClue().contains(FILL_IN_THE_BLANK_MARKER))
			this.setFillInTheBlank(true);
		System.err.println("Clue text = " + this.getSourceClue()); // DEBUGGING
		
		String solutionStructureAsString = decomposedClue[1].substring(0, decomposedClue[1].length() - 1);
		System.err.println("Solution structure = " + solutionStructureAsString); // DEBUGGING
		
		this.parseSolutionStructure(solutionStructureAsString);
		this.setNumberOfWords(this.getSolutionStructure().length);
	}
	
	/**
	 * parseSolutionStructureAsString - creates an array of ints representing the number of words in the solution and the
	 * number of letter in each of those words
	 * @param solutionStructureAsString - the fragment of the raw clue containing the solution structure in String form, with 
	 * the square brackets removed. Each number is separated by a comma followed by a space.
	 */
	private void parseSolutionStructure(String solutionStructureAsString) throws InvalidClueException {
		String[] parsedSolutionStructureAsString = solutionStructureAsString.split(", ");
		this.setSolutionStructure(new int[parsedSolutionStructureAsString.length]);
		for(int i = 0; i < parsedSolutionStructureAsString.length; i++)
			try {
				this.getSolutionStructure()[i] = Integer.parseInt(parsedSolutionStructureAsString[i]);
			}
			catch(NumberFormatException e) {
				throw new InvalidClueException(parsedSolutionStructureAsString[i] + " is not a valid solution structure");
			}
	}

	@Override
	public boolean matchesStructure(Solution solution) {
		return (Arrays.equals(solution.getSolutionStructure(), this.getSolutionStructure())); // requires comparison of deep equality
	}
}
