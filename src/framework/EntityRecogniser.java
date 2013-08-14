/**
 * 
 */
package framework;

import java.util.ArrayList;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
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
	public Model getModel();
	public OntModel getOntModel();
	public ArrayList<String> getClueFragments();
}
