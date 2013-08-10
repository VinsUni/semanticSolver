/**
 * 
 */
package prototype;

import java.util.ArrayList;

/**
 * @author Ben Griffiths
 *
 */
public interface Solver {
	public ArrayList<String> getSolutions(Clue clue, ArrayList<String> proposedSolutions);
	public String getBestSolution(Clue clue);
}
