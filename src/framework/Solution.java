/**
 * 
 */
package framework;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Ben Griffiths
 * Solution - represents a solution to a clue, comprising text of one or more words and a solution structure that describes
 * how many words are in the text of the solution and how many letters are in each of those words
 */
public interface Solution {
	public String getSolutionText();
	public int[] getSolutionStructure();
	public Resource getSolutionResource();
}
