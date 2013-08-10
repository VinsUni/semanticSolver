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
	public ArrayList<String> getSolutions(Clue clue);
	public String getBestSolution(Clue clue);
}
