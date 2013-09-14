package framework;

import java.util.ArrayList;

import exception.NoSolutionsException;

/**
 * @author Ben Griffiths
 * ClueSolver
 * This interface is used to retrieve a list of valid solutions to a given clue from a list of candidate solutions
 */
public interface ClueSolver {
	/**
	 * getSolutions - return a list of all the valid solutions found for the given clue
	 * @param clue - the clue whose solutions are to be returned
	 * @param proposedSolutions - an ArrayList of Solution objects
	 * @return a subset of the list of proposedSolutions, each member of which matches the solution structure of the clue. 
	 * Should return null if the proposedSolutions list is empty
	 * @throws NoSolutionsException - if the proposedSolutions argument is null
	 */
	public ArrayList<Solution> getSolutions(Clue clue, ArrayList<Solution> proposedSolutions) throws NoSolutionsException;
}
