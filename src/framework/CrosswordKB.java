/**
 * 
 */
package framework;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Ben Griffiths
 *
 */
public class CrosswordKB {
    private static Model model = ModelFactory.createDefaultModel();

    public static final String LOCAL_KNOWLEDGE_BASE_URI = "crosswordKB.rdf";
    
    public static final String CROSSWORD_KB_URI = "http://www.griffithsben.com/ontologies/crosswordKBontology.owl#";
    
    /**
     * Resource representing the crosswordKB namespace
     */
    public static final Resource CROSSWORD_KB_NS = model.createResource(CROSSWORD_KB_URI);
    
    /**
     * Properties in the crosswordKB vocabulary
     */
    public static final Property solves = model.createProperty(CROSSWORD_KB_URI + "solves");
    public static final Property solvedBy = model.createProperty(CROSSWORD_KB_URI + "solvedBy");
    public static final Property hasClueText = model.createProperty(CROSSWORD_KB_URI + "hasClueText");
    public static final Property hasSolutionStructure = model.createProperty(CROSSWORD_KB_URI + "hasSolutionStructure");
    public static final Property hasSolutionText = model.createProperty(CROSSWORD_KB_URI + "hasSolutionText");
    
    
    /**
     * Classes in the crosswordKB vocabulary
     */
    public static final Resource clue = model.createResource(CROSSWORD_KB_URI + "clue");
    public static final Resource solution = model.createResource(CROSSWORD_KB_URI + "solution");
   
}
