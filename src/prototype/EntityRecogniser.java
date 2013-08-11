/**
 * 
 */
package prototype;

import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;

/**
 * @author Ben Griffiths
 *
 */
public interface EntityRecogniser {
	public ArrayList<Resource> getRecognisedResources(String clueText);
	public ArrayList<Property> getRecognisedProperties(String clueText);
}
