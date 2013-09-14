package framework;

/**
 * @author Ben Griffiths
 * SolutionScorer
 * Interface through which a score is calculated for a solution by the system's controller.
 */
public interface SolutionScorer {
	/**
	 * score - calculates a score to the solution argument, representing the semantic distance between the solution and clue resources 
	 * held as members of that object. Scores should range from 0 (the closest possible match between a clue and a solution) 
	 * and 1 (the weakest possible match)
	 * @param solution - the solution for which a score is to be calculated
	 * @return - the calculated score as a value of type double, ranging from 0.0 to 1.0
	 */
	public double score(Solution solution);
}
