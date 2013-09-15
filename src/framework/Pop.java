package framework;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Ben Griffiths
 * Pop
 * Contains a series of static constants providing easy access to terms used in the pop ontology and important URIs and prefix declarations
 * used in the DBpedia dataset
 */
public class Pop {
    private static Model model = ModelFactory.createDefaultModel();
    public static final String LOCAL_VOCAB_URI = "pop.owl";
    public static final String POP_URI = "http://www.griffithsben.com/ontologies/pop.owl#";
    public static final String DBPEDIA_OWL_URI = "http://dbpedia.org/ontology/";
    public static final String ENDPOINT_URI = "http://dbpedia-live.openlinksw.com/sparql"; // URL of the DBpedia Live SPARQL endpoint
    public static final String DBPEDIA_RESOURCE_NS = "http://dbpedia.org/resource/";
    public static final String RDFS_PREFIX_DECLARATION = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
    public static final String DBPPROP_PREFIX_DECLARATION = "PREFIX dbpprop: <http://dbpedia.org/property/>";
    public static final String DB_OWL_PREFIX_DECLARATION = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>";
    public static final String XPATH_FUNCTIONS_PREFIX_DECLARATION = "PREFIX fn: <http://www.w3.org/2005/xpath-functions#>";
    public static final String FOAF_PREFIX_DECLARATION = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>";
	public static final String DBPEDIA_PROPERTY_PREFIX_DECLARATION = "PREFIX dbpprop: <http://dbpedia.org/property/>";
	public static final String RDF_PREFIX_DECLARATION = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
	
    /**
     * Resource representing the pop namespace
     */
    public static final Resource POP_NS = model.createResource(POP_URI);
    
    /**
     * Properties of special interest in the dbpedia-owl vocabulary
     */
    public static final Property wikiPageRedirects = model.createProperty(DBPEDIA_OWL_URI + "wikiPageRedirects");
    
    /**
     * Properties in the pop vocabulary
     */
    public static final Property propertyToCheck = model.createProperty(POP_URI + "propertyToCheck");
    public static final Property relationalProperty = model.createProperty(POP_URI + "relationalProperty");
    public static final Property artistOf = model.createProperty(POP_URI + "artistOf");
    public static final Property hasArtist = model.createProperty(POP_URI + "hasArtist");
    public static final Property albumOf = model.createProperty(POP_URI + "albumOf");
    public static final Property hasAlbum = model.createProperty(POP_URI + "hasAlbum");
    public static final Property composerOf = model.createProperty(POP_URI + "composerOf");
    public static final Property hasComposer = model.createProperty(POP_URI + "hasComposer");
    public static final Property compositionOf = model.createProperty(POP_URI + "compositionOf");
    public static final Property hasComposition = model.createProperty(POP_URI + "hasComposition");
    public static final Property genreOf = model.createProperty(POP_URI + "genreOf");
    public static final Property hasGenre = model.createProperty(POP_URI + "hasGenre");
    public static final Property memberOf = model.createProperty(POP_URI + "memberOf");
    public static final Property hasMember = model.createProperty(POP_URI + "hasMember");
    public static final Property producerOf = model.createProperty(POP_URI + "producerOf");
    public static final Property hasProducer = model.createProperty(POP_URI + "hasProducer");
    public static final Property recordLabelOf = model.createProperty(POP_URI + "recordLabelOf");
    public static final Property hasRecordLabel = model.createProperty(POP_URI + "hasRecordLabel");
    
    /**
     * Classes in the pop vocabulary
     */
    public static final Resource popClass = model.createResource(POP_URI + "popClass");
    public static final Resource album = model.createResource(POP_URI + "album");
    public static final Resource artist = model.createResource(POP_URI + "artist");
    public static final Resource composer = model.createResource(POP_URI + "composer");
    public static final Resource composition = model.createResource(POP_URI + "composition");
    public static final Resource genre = model.createResource(POP_URI + "genre");
    public static final Resource group = model.createResource(POP_URI + "genre");
    public static final Resource member = model.createResource(POP_URI + "member");
    public static final Resource producer = model.createResource(POP_URI + "producer");
    public static final Resource recordLabel = model.createResource(POP_URI + "recordLabel");
}
