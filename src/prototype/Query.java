/**
 * 
 */
package prototype;

import java.util.ArrayList;

/**
 * @author Ben Griffiths
 * Queries the loaded model to find an answer to the clue that is passed as an argument to the Query constructor
 */
public interface Query {
	public ArrayList<String> getCandidateSolutions();
	public String getBestSolution();
	public String getClue();
}
