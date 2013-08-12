/**
 * 
 */
package extraction;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import experiments.NsPrefixLoader;

/**
 * @author Ben
 * Extracts a subset of data from DBpedia. The data extracted is a set of triples in which the supplied resource is either the subject,
 * the predicate, or the object
 */
public class ExtractionRunner {

	public static void main(String[] args) {
		
		Logger.getRootLogger().setLevel(Level.INFO);
		PropertyConfigurator.configure("ExtractionRunnerlog4j.properties");
		
		
		String fileName = "data\\subjectIsABandOffset0.xml"; // <-- The name of the file the results will be saved in
		
		String SPARQLquery = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>" +
							" construct {?subject ?predicate ?object.}" +
		 					" where {?subject ?predicate ?object." +
		 					" 		 ?subject a dbpedia-owl:Band.}" +
		 					" ORDER BY desc(?subject)";
		
	    Query query = QueryFactory.create(SPARQLquery);
	    QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
	    Model model = ModelFactory.createDefaultModel();
	    queryExecution.execConstruct(model);  
	    queryExecution.close();
	     
	    // load standard prefixes into the model
	    NsPrefixLoader prefixLoader = new NsPrefixLoader(model);
		prefixLoader.loadStandardPrefixes();
		 
		// Now, write the model out to a file in RDF/XML-ABBREV format:
		try {
			FileOutputStream outFile = new FileOutputStream(fileName);
			model.write(outFile, "RDF/XML-ABBREV");
			outFile.close();
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
