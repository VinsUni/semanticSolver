package experiments;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DatasetMerger {
	
	/**
	 * From <http://jena.apache.org/tutorials/rdf_api.html#ch-Operations on Models>:
	 * Jena provides three operations for manipulating Models as a whole. These are the common set operations of 
	 * union, intersection and difference. The union of two Models is the union of the sets of statements which 
	 * represent each Model. This is one of the key operations that the design of RDF supports. It enables data from 
	 * disparate data sources to be merged.
	 * 
	 * I am going to use the union operation to merge my two (abridged) datasets, triplesWhereObjectIsAMusicalWorkExtract.xml and
	 * triplesWhereSubjectIsAMusicalWorkExtract.xml
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		/* log4j logging configuration. For now, we are just writing logs to standard out */
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO); // set threshold of logging messages to write
		
		// create two empty models into which to read my two RDF/XML files
		Model model1 = ModelFactory.createDefaultModel();
		Model model2 = ModelFactory.createDefaultModel();
		
		String model1FileName = "triplesWhereObjectIsAMusicalWorkExtract.xml";
		String model2FileName = "triplesWhereSubjectIsAMusicalWorkExtract.xml";
		
		// instantiate two input streams and read the two datasets into the two models
		InputStream in1, in2;
		try {
			in1 = new FileInputStream(model1FileName);
			in2 = new FileInputStream(model2FileName);
			
			String lang = "RDF/XML";
			
			model1.read(new InputStreamReader(in1), "", lang);
			model2.read(new InputStreamReader(in2), "", lang);
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	
		Model mergedModel = model1.union(model2); // create a third model which is the union of the two models
		
		// Define a prefix for one of the many namespaces used in the merged model
		mergedModel.setNsPrefix("dbpedia-owl","http://dbpedia.org/ontology/");
		
		// Now, write the model out to a file in RDF/XML-ABBREV format:
		String fileName = "mergedTestDataSet.xml";
		try {
			FileOutputStream outFile = new FileOutputStream(fileName);
			mergedModel.write(outFile, "RDF/XML-ABBREV");
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
