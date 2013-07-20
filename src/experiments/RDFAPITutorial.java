package experiments;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

public class RDFAPITutorial {

	public static void main(String[] args) {
		// some definitions
		final String DBPEDIA_ONTOLOGY_NS = "http://dbpedia.org/ontology/";
		final String DBPEDIA_NS = "http://dbpedia.org/resource/";
		final String DBPEDIA_ARTIST = DBPEDIA_ONTOLOGY_NS + "artist"; // unused at present
		String artistURI = DBPEDIA_NS + "John_Lennon";
		String artistName = "John Lennon";

		// create an empty Model in memory
		Model model = ModelFactory.createDefaultModel();

		// create a resource
		Resource johnLennon = model.createResource(artistURI);

		/* add a property to the resource. The property is provided by a "constant" class 
		 * RDFS which holds objects representing all the definitions in the RDFS schema.
		 * Jena provides constant classes for other well known schemas, such as RDF, Dublin Core, VCARD, and OWL
		 */
		 johnLennon.addProperty(RDFS.label, artistName);
		 
		 // Retrieve the RDFS:label property of the resource that we created in our model
		 System.out.println(johnLennon.getProperty(RDFS.label));
		 
		 // Retrieve the value of the that property (i.e. the String that is the value of our artistName variable)
		 System.out.println(johnLennon.getProperty(RDFS.label).getString());
	}

}
