/**
 * 
 */
package framework;

import java.util.ArrayList;

/**
 * @author Ben Griffiths
 * Queries a model to find an answer to a clue
 */
public interface Query {
	public ArrayList<String> getCandidateSolutions();
	public Clue getClue();
}
