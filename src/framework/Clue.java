/**
 * 
 */
package framework;

import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Selector;

/**
 * @author Ben Griffiths
 * Clue - represents a clue to be solved, including the text that makes up the clue, and a representation of the structure of
 * the solution to the clue
 */
public interface Clue {
	public String getSourceClue(); // returns the original text String used to initialise the Clue object
	public void setSourceClue(String sourceClue);
	public ArrayList<String> getClueVariations(); // returns an arrayList of Strings representing variations of the original text String
	public ArrayList<Selector> getSelectorVariations(); // returns an arrayList of Selectors representing parsed versions of the clueVariations
	public void setSelectorVariations(ArrayList<Selector> selectorVariations);
	public int[] getSolutionStructure();
	public void setSolutionStructure(int[] solutionStructure);
	/**
	 * getSolutionStructureAsString
	 * @return a String representing the structure of the solution to this clue. For example, if the solution consists of two words,
	 * each containing 5 letters, the return value will be the String "[5, 5]"
	 */
	public String getSolutionStructureAsString();
	public boolean matchesStructure(Solution solution); // compares the structure of this clue with the Solution argument
}
