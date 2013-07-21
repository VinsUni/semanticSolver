package experiments;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
		 writeModel(iterator);
		 
		 
		 /* Writing RDF:
		  * This time, instead of writing each statement in the model out to standard out as a triple, we will write it out
		  * (i) to standard out
		  * (ii) to a file
		  * in RDF/XML form
		  */
		 
		 // Write each statement out in XML form to standard out:
		 model.write(System.out);
		 /* Note that there is an error in the RDF XML written out by the above statement; it does not exactly represent 
		  * the Model we created. The blank node in the Model has been given a URI reference. It is no longer blank. 
		  * The RDF/XML syntax is not capable of representing all RDF Models; for example it cannot represent a blank node 
		  * which is the object of two statements. The 'dumb' writer we used to write this RDF/XML makes no attempt to 
		  * write correctly the subset of Models which can be written correctly. It gives a URI to each blank node, 
		  * making it no longer blank.
		  */
		 
		 /*Jena has an extensible interface which allows new writers for different serialization languages for RDF to be easily 
		  * plugged in. The above call invoked the standard 'dumb' writer. Jena also includes a more sophisticated RDF/XML 
		  * writer which can be invoked by specifying another argument to the write() method call:
		  */

		 model.write(System.out, "RDF/XML-ABBREV");
 
		 /* This writer, the so called PrettyWriter, takes advantage of features of the RDF/XML abbreviated syntax 
		  * to write a Model more compactly. It is also able to preserve blank nodes where that is possible. 
		  * It is however, not suitable for writing very large Models, as its performance is unlikely to be acceptable. 
		  * To write large files and preserve blank nodes, write in N-Triples format:
		  */
		 
		 model.write(System.out, "N-TRIPLES");
		 
		 // Now, write the model out to a file in RDF/XML-ABBREV format:
		 try {
			 FileOutputStream outFile = new FileOutputStream("output.xml");
			 model.write(outFile, "RDF/XML-ABBREV");
			 outFile.close();
		 }
		 catch(FileNotFoundException e) {
			 e.printStackTrace();
		 } catch (IOException e) {
			e.printStackTrace();
		}
		 
	}
	
	public static void writeModel(StmtIterator iterator) {
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
	}

}
