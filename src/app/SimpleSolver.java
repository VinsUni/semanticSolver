/**
 * 
 */
package app;

import java.util.ArrayList;

import framework.Clue;
import framework.Solution;
import framework.Solver;

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
	 * For now, it just returns all of the proposedSolutions and sets the first one as the best solution,
	 * but it will need to screen out those that do not solve the clue
	 * (e.g. due to being the wrong number of words/wrong length 
	 */
	@Override
	public ArrayList<String> getSolutions(Clue clue, ArrayList<String> proposedSolutions) {
		ArrayList<String> solutions = new ArrayList<String>();
		for(String proposedSolution : proposedSolutions) {
			Solution parsedSolution = new SimpleSolution(proposedSolution);
			String solutionText = parsedSolution.getSolutionText();
			if(clue.matchesStructure(parsedSolution) && !solutions.contains(solutionText))
				solutions.add(solutionText);
		}
		
		if(proposedSolutions.size() > 0)
			this.setBestSolution(proposedSolutions.get(0));
		
		return solutions;
	}

	@Override
	public String getBestSolution(Clue clue) {
		return this.getBestSolution();
	}

}
