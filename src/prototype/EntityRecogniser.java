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
	public ArrayList<Resource> getRecognisedSubjects();
	public ArrayList<Property> getRecognisedProperties();
	public ArrayList<Resource> getRecognisedObjects();
}
