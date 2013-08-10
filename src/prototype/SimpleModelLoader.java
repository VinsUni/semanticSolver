/**
 * 
 */
package prototype;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import experiments.NsPrefixLoader;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleModelLoader implements ModelLoader {
	private final String DATA_URI = "popv7.owl";
	private final String ONTOLOGY_URI = "testDatasetExtended.xml";
	@Setter(AccessLevel.PRIVATE) Model model;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) Model data;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) Model ontology;
	
	@Override
	public Model getModel() {
		/* log4j logging configuration */
		Logger.getRootLogger().setLevel(Level.INFO);
		PropertyConfigurator.configure("log4j.properties");
		
		this.setModel(ModelFactory.createDefaultModel());
		
		data = FileManager.get().loadModel(DATA_URI); // Read the data from DBPedia into a model
		ontology = FileManager.get().loadModel(ONTOLOGY_URI); // read my pop ontology into another model
		
		model = data.union(ontology); // create a third model which is the union of the two models
		
		// load standard prefixes into the model
		NsPrefixLoader prefixLoader = new NsPrefixLoader(model);
		prefixLoader.loadStandardPrefixes();
		
		/*
		// Now, write the model out to a file in RDF/XML-ABBREV format:
		String fileName = "data\\ontologyMergedWithTestDataSet.xml";
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
		*/
		
		return this.model;
	}
}