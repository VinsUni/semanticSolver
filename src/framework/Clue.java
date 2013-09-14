package framework;

import java.util.ArrayList;

/**
 * @author Ben Griffiths
 * Clue
 * Represents a clue to be solved, including the text that makes up the clue, and a representation of the structure of
 * the solution to the clue
 */
public interface Clue {
	/**
	 * getSourceClue
	 * @return the original text String used to initialise the Clue object
	 */
	public String getSourceClue();
	
	/**
	 * getSolutionStructure
	 * @return - an integer array representing the structure of the solution to a clue. Each element represents the number of letters in 
	 * an individual word in the solution. A solutionStructure of e.g. {2, 3} means the answer consists of a 2-letter word followed by 
	 * a 3-letter word
	 */
	public int[] getSolutionStructure();
	
	/**
	 * getSolutionStructureAsString
	 * @return a String representing the structure of the solution to this clue. For example, if the solution consists of two words,
	 * each containing 5 letters, the return value should be the String "[5, 5]"
	 */
	public String getSolutionStructureAsString();
	
	/**
	 * getClueFragments
	 * @return an arrayList of Strings representing parsed fragments of the original text String
	 */
	public ArrayList<String> getClueFragments();
	
	/**
	 * matchesStructure
	 * @param solution - a solution whose structure is to be compared to the solutionStructure member of this clue
	 * @return - true if the getSolutionStructure returns an array of integers equal to that returned by the solution argument's
	 * getSolutionStructure method.
	 */
	public boolean matchesStructure(Solution solution); // compares the structure of this clue with the Solution argument
	
	/**
	 * isFillInTheBlank
	 * @return - true if the clue is a Fill-In-The-Blank clue - i.e. the text of the clue contains a marker of one or more missing words
	 * whose contents provides the clue's solution
	 */
	public boolean isFillInTheBlank();
	
	/**
	 * toProperCase
     * @param thisWord the String to be converted to proper case
     * @return thisWord, converted to proper case
	 */
	public String toProperCase(String text);
}
