/**
 * 
 */
package framework.prototype;

import java.util.ArrayList;

/**
 * @author Ben Griffiths
 * Queries a model to find an answer to a clue
 */
public interface ClueQuery {
	public ArrayList<String> getCandidateSolutions();
	public Clue getClue();
}
