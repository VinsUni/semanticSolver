/**
 * 
 */
package framework;

import java.util.ArrayList;


/**
 * @author Ben Griffiths
 *
 */
public interface EntityRecogniser {
	public ArrayList<String> getRecognisedResourceURIs();
	public ArrayList<String> getRecognisedPropertyURIs(); // TO BE REMOVED - USED BY CLUEQUERYIMPLMARKA ONLY
	public ArrayList<String> getClueFragments();
}
