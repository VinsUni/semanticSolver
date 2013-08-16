/**
 * 
 */
package framework;

import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;

/**
 * @author Ben Griffiths
 *
 */
public interface EntityRecogniser {
	public ArrayList<String> getRecognisedResourceURIs();
	public ArrayList<Resource> getRecognisedResources();
	public ArrayList<Property> getRecognisedProperties();
	public ArrayList<Resource> getRecognisedSubjects();
	public ArrayList<Resource> getRecognisedObjects();
	public ArrayList<String> getClueFragments();
}
