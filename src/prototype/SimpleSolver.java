/**
 * 
 */
package prototype;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleSolver implements Solver {
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) String bestSolution; 

	/**
	 * For now, it just returns all of the proposedSolutions, but it will need to screen out those that do not solve the clue
	 * (e.g. due to being the wrong number of words/wrong length 
	 */
	@Override
	public ArrayList<String> getSolutions(Clue clue, ArrayList<String> proposedSolutions) {
		if(proposedSolutions.size() > 0)
			this.setBestSolution(proposedSolutions.get(0));
		return proposedSolutions;
	}

	@Override
	public String getBestSolution(Clue clue) {
		return this.getBestSolution();
	}

}
