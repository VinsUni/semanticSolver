/**
 * 
 */
package framework;

import java.util.ArrayList;

import exception.NoSolutionsException;

/**
 * @author Ben Griffiths
 *
 */
public interface ClueSolver {
	/**
	 * 
	 * @param clue
	 * @param proposedSolutions - an ArrayList of Strings resulting from a query of the model for an answer to the clue
	 * @return a subset of the list of proposedSolutions, each member of which matches the requirements of the clue
	 */

	public String getBestSolution(Clue clue);
	public ArrayList<Solution> getSolutions(Clue clue, ArrayList<Solution> proposedSolutions) throws NoSolutionsException;
}
