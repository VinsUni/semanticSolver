/**
 * 
 */
package framework.remotePrototype;

import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;

/**
 * @author Ben Griffiths
 *
 */
public interface EntityRecogniser {
	public ArrayList<String> getRecognisedResourceURIs();
	public ArrayList<String> getRecognisedPropertyURIs(); // TO BE REMOVED - USED BY CLUEQUERYIMPLMARKA ONLY
	public ArrayList<String> getClueFragments();
}
