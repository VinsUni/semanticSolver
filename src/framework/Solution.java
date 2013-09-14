package framework;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Ben Griffiths
 * Solution
 * An interface for manipulating a potential solution to a clue, comprising text of one or more words and a solution structure that 
 * describes how many words are in the text of the solution and how many letters are in each of those words. Also provides access to 
 * the knowledge graph in which the solution was found
 */
public interface Solution {
	/**
	 * getSolutionText
	 * @return a String containing the text of the solution
	 */
	public String getSolutionText();
	
	/**
	 * getSolutionStructure
	 * @return an array of integer representing the number of words in the solution, and the number of letters in each word
	 */
	public int[] getSolutionStructure();
	
	/**
	 * getSolutionResource
	 * @return an instance of com.hp.hpl.jena.rdf.model.Resource representing the resource whose label property contains the text of the
	 * solution
	 */
	public Resource getSolutionResource();
	
	/**
	 * getClueResource
	 * @return an instance of com.hp.hpl.jena.rdf.model.Resource representing the resource around which the knowledge graph containing 
	 * the solutionResource was built
	 */
	public Resource getClueResource();
	
	/**
	 * getClue
	 * @return a Clue object representing the Clue to which this solution applies
	 */
	public Clue getClue();
	
	/**
	 * getInfModel
	 * @return an instance of com.hp.hpl.jena.rdf.model.InfModel representing the knowledge graph from which the solution was obtained
	 */
	public InfModel getInfModel();
	
	/**
	 * setScore
	 * @param score a value of type double to be assigned as the score of this solution, representing the semantic distance between 
	 * the solution and clue resources. Scores should range from 0 (the closest possible match between a clue and a solution) 
	 * and 1 (the weakest possible match)
	 */
	public void setScore(double score);
	
	/**
	 * getScore
	 * @return a value of type double representing the semantic distance between the solution and clue resources.
	 */
	public double getScore();
	
	/**
	 * getConfidence
	 * @return an integer representing the level of confidence in this solution as a correct solution to its associated clue. Confidence
	 * levels should range from 0 (representing a score of 1) to 100 (representing a score of 0)
	 */
	public int getConfidence();
}
