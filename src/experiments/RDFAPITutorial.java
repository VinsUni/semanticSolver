package experiments;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.VCARD;

public class RDFAPITutorial {
	
	/*I am following the tutorial at http://jena.apache.org/tutorials/rdf_api.html#ch-Introduction, but substituting
	 * examples from my own domain of interest for their examples, which are about people and use an RDF representation 
	 * of VCARDS.
	 */
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
		 
		 
		 
		 
		 
		 /* I'm now going to add some more properties to my johnLennon resource. These properties, FN, Given and Family, are
		  * part of the VCARD schema. We will also make use of the property VCARD:N to illustrate the use of a blank node, which
		  * is a common RDF technique
		  */
		 
		 String givenName = "John";
		 String familyName = "Lennon";
		 String fullName = givenName + " " + familyName;
		 
		 // This time we will add properties to the resource using the cascading style
		 johnLennon
		 	.addProperty(VCARD.FN, fullName)
		 	.addProperty(VCARD.N, model.createResource()
		 								.addProperty(VCARD.Given, givenName)
		 								.addProperty(VCARD.Family, familyName));
		 
		 /* Now retrieve the value of the Given and Family properties of the blank node of type VCARD:N that is a property of our
		  * johnLennon resource
		  */
		 System.out.println(johnLennon.getProperty(VCARD.N).getProperty(VCARD.Given).getString()); // getProperty returns a Statement
		 System.out.println(johnLennon.getProperty(VCARD.N).getProperty(VCARD.Family).getString());
		 // Alternatively, using Jena's StmtIterator interface, which is a subtype of Java's Iterator:
		 Resource nameProperty = johnLennon.getPropertyResourceValue(VCARD.N); // getPropertyResourceValue returns a Resource
		 StmtIterator iterator = nameProperty.listProperties();
		 while(iterator.hasNext())
			 System.out.println(iterator.nextStatement().getString());
		 
		 
		 
		 
		 // Exploring the Statement interface further:
		 
		 /* The Statement interface provides accessor methods to the subject, predicate and object of a statement. We will now get
		  * a StmtIterator over all the statements in the model:
		  */
		 iterator = model.listStatements();
		 while(iterator.hasNext()) {
			 Statement statement = iterator.nextStatement(); // get the next statement
			 Resource subject = statement.getSubject(); // get the subject of the statement
			 Property predicate = statement.getPredicate(); // get the predicate
			 RDFNode object = statement.getObject(); // get the object
			 /*Since the object of a statement can be either a resource or a literal, the getObject() method returns 
			  * an object typed as RDFNode, which is a common superclass of both Resource and Literal.
			  */
			 
			 System.out.print(subject.toString());
			 System.out.print(" " + predicate.toString() + " ");
			 if(object instanceof Resource)
				 System.out.print(object.toString());
			 else System.out.print(" \"" + object.toString() + "\""); // the object is a literal, so surround it with quotes
			 
			 System.out.println(" .");
		 }
		 
		 /* Writing RDF */
		 
		 
		 
	}

}
