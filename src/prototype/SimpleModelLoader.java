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

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import experiments.NsPrefixLoader;
import framework.ModelLoader;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleModelLoader implements ModelLoader {
	private final String ONTOLOGY_URI = "popv7.owl";
	private final String DATA_URI = "newTestDataset.xml";
	private final String OWL_FULL_URI = "http://www.w3.org/2002/07/owl#";
	@Setter(AccessLevel.PRIVATE) InfModel model;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) OntModelSpec ontologyModelSpec;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) OntModel ontologyModel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) Model data;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) Model ontology;
	
	@Override
	public InfModel getModel() {
		if(this.model != null)
			return this.model; // if the model has already been loaded, simply return it
		
		/* log4j logging configuration */
		Logger.getRootLogger().setLevel(Level.INFO);
		PropertyConfigurator.configure("log4j.properties");
		
		
		Model baseModel = ModelFactory.createDefaultModel();
		
		data = FileManager.get().loadModel(DATA_URI); // Read the data from DBPedia into a model
		ontology = FileManager.get().loadModel(ONTOLOGY_URI); // read my pop ontology into another model
		
		baseModel = data.union(ontology); // create a third model which is the union of the two models
		
		
		this.setModel(ModelFactory.createInfModel(OntModelSpec.RDFS_MEM_RDFS_INF.getReasoner(), baseModel));
		
		// load standard prefixes into the model
		NsPrefixLoader prefixLoader = new NsPrefixLoader(this.model);
		prefixLoader.loadStandardPrefixes();
		
		return this.model;
	}
}