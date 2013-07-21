package experiments;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.RDF;
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
		
		
		/* log4j logging configuration. For now, we are just writing logs to standard out */
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO); // set threshold of logging messages to write
		

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
		 String fileName = "output.xml";
		 try {
			 FileOutputStream outFile = new FileOutputStream(fileName);
			 model.write(outFile, "RDF/XML-ABBREV");
			 outFile.close();
		 }
		 catch(FileNotFoundException e) {
			 e.printStackTrace();
		 } catch (IOException e) {
			e.printStackTrace();
		}
		 
		/* We will now read back in our persisted Model before writing it to standard out*/
		// create an empty model
		Model persistedModel = ModelFactory.createDefaultModel();

		// use the FileManager to find the input file
		InputStream inFile = FileManager.get().open(fileName);
		if (inFile == null) {
		   throw new IllegalArgumentException("File: " + fileName + " not found");
		}

		// read the RDF/XML file
		persistedModel.read(inFile, null);
		//The second argument to the read() method call is the URI which will be used for resolving relative URIs

		// write it to standard out
		System.out.println("The persisted model:");
		persistedModel.write(System.out);
		
		
		/* Controlling Prefixes
		 * 1. Explicit prefix definitions:
		 * Jena provides ways of controlling the namespaces used on output with its prefix mappings:
		 * The method setNsPrefix(String prefix, String URI) declares that the namespace URI may be abbreviated by prefix. 
		 * Jena requires that prefix be a legal XML namespace name, and that URI ends with a non-name character. 
		 * The RDF/XML writer will turn these prefix declarations into XML namespace declarations and use them in its output
		 */
		
		// create a new artist resource and a new memberOf property:
		String griffithsBenNS = "http://www.griffithsben.com/ontology#";
		Resource artist  = persistedModel.createResource(griffithsBenNS + "artist");
		Property memberOf = persistedModel.createProperty(griffithsBenNS + "memberOf");
		
		// Add two new statements to the model, asserting that johnLennon a artist and johnLennon memberOf "The Beatles"
		persistedModel.add(johnLennon, RDF.type, artist);
		persistedModel.add(johnLennon, memberOf, "The Beatles");
		
		
		System.out.println("Writing the persisted model out with no special prefixes defined:");
		persistedModel.write(System.out);
		
		// Now provide a prefix for the namespace of my ontology
		persistedModel.setNsPrefix("clue", griffithsBenNS);
		System.out.println("Writing the persisted model out with a prefix defined for my namespace:");
		persistedModel.write(System.out);
		
		
		/*Navigating a model:
		 * Given the URI of a resource, the resource object can be retrieved from a model using the Model.getResource(String uri) method. 
		 * This method is defined to return a Resource object if one exists in the model, 
		 * or otherwise to create a new one. For example, to retrieve the JohnLennon resource from our persisted model:
		 */
		Resource lennon = persistedModel.getResource(DBPEDIA_NS + "John_Lennon");
		System.out.println("The namespace of the JohnLennon resource is " + lennon.getNameSpace());
		System.out.println("The name of the resource is " + lennon.getLocalName());
		System.out.println("The entire URI of the resource is " + lennon.getURI());
		
		// Retrieve the value of John Lennon's memberOf property:
		String band = lennon.getProperty(memberOf).getString(); //getProperty() returns a Statement, not a Property!
		System.out.println("John Lennon is a member of " + band);
		
		// Add Paul McCartney to the model, who is a member of two bands...
		Resource paulMcCartney = persistedModel.createResource(DBPEDIA_NS + "Paul_McCartney");
		persistedModel.add(paulMcCartney, memberOf, "The Beatles");
		persistedModel.add(paulMcCartney, memberOf, "Wings");
		
		// Since paulMcCartney has TWO memberOf properties, we must retrieve a list of statements instead of using getProperty:
		System.out.println("Paul McCartney is a member of:");
		iterator = paulMcCartney.listProperties(memberOf); // iterator is of type StmtIterator
		while(iterator.hasNext())
			System.out.println(" - " + iterator.nextStatement().getObject());
		/* NB: All the properties of a resource can be listed by using the listProperties() method without an argument. */
		
		// Now, write the model back out to the file in RDF/XML-ABBREV format:
		try {
			 FileOutputStream outFile = new FileOutputStream(fileName);
			 persistedModel.write(outFile, "RDF/XML-ABBREV");
			 outFile.close();
		}
		catch(FileNotFoundException e) {
			 e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*Querying a model
		 * The previous section dealt with the case of navigating a model from a resource with a known URI. 
		 * This section deals with searching a model. The core Jena API supports only a limited query primitive. 
		 * The more powerful query facilities of RDQL are not described in this tutorial.
		 */
		
		/* Model.listSubjectsWithProperty(Property p, RDFNode o) will return an iterator over all the resources 
		 * which have property p with value o.
		*/
		
		//retrieve an iterator over all resources that are members of the Beatles
		ResIterator resourceIterator = persistedModel.listSubjectsWithProperty(memberOf, "The Beatles");
		
		System.out.println();
		System.out.println("Querying the model: ");
		System.out.println("The members of the Beatles are: ");
		while(resourceIterator.hasNext())
			System.out.println(" - " + resourceIterator.nextResource().getLocalName());
		
		resourceIterator = persistedModel.listSubjectsWithProperty(memberOf, "Wings");
		System.out.println("The members of Wings are: ");
		while(resourceIterator.hasNext())
			System.out.println(" - " + resourceIterator.nextResource().getLocalName());
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
