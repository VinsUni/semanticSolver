/**
 * 
 */
package prototype;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Ben Griffiths
 *
 */
public class Pop {
	private static final String POP_URI = "http://www.griffithsben.com/ontologies/pop.owl#";  
    private static Model model = ModelFactory.createDefaultModel();
    
    /**
     * Resource representing the pop namespace
     */
    public static final Resource POP_NS = model.createResource(POP_URI);
    
    /**
     * Properties in the pop vocabulary
     */
    public static final Property albumOf = model.createProperty(POP_URI + "albumOf");
    
    /**
     * Classes in the pop vocabulary
     */
    public static final Resource artist = model.createResource(POP_URI + "artist");
    
}
