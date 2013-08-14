/**
 * 
 */
package framework;

import java.util.ArrayList;

/**
 * @author Ben Griffiths
 *
 */
public interface Solver {
	/**
	 * 
	 * @param clue
	 * @param proposedSolutions - an ArrayList of Strings resulting from a query of the model for an answer to the clue
	 * @return a subset of the list of proposedSolutions, each member of which matches the requirements of the clue
	 */
	public ArrayList<String> getSolutions(Clue clue, ArrayList<String> proposedSolutions);
	public String getBestSolution(Clue clue);
}
