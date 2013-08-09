/**
 * 
 */
package prototype;

import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Selector;

/**
 * @author Ben Griffiths
 *
 */
public interface Clue {
	public String getSourceClue(); // returns the original text String used to initialise the Clue object
	public ArrayList<String> getClueVariations(); // returns an arrayList of Strings representing variations of the original text String
	public ArrayList<Selector> getSelectorVariations(); // returns an arrayList of Selectors representing parsed versions of the clueVariations
	public void setSelectorVariations(ArrayList<Selector> selectorVariations);
}
