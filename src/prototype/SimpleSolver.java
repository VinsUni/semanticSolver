/**
 * 
 */
package prototype;

import java.util.ArrayList;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleSolver implements Solver {

	@Override
	public ArrayList<String> getSolutions(Clue clue, ArrayList<String> proposedSolutions) {
		return proposedSolutions;
	}

	@Override
	public String getBestSolution(Clue clue) {
		// TODO Auto-generated method stub
		return null;
	}

}
