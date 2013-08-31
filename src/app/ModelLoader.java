/**
 * 
 */
package app;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.FileManager;

import experiments.NsPrefixLoader;

import framework.Pop;

import lombok.AccessLevel;
import lombok.Setter;

/**
 * @author Ben Griffiths
 *
 */
public class ModelLoader {
	@Setter(AccessLevel.PRIVATE) private static InfModel model;
	
	private ModelLoader() {
		/* ModelLoader is a Singleton, so the onyl constructor is private */
	}
	
	public static InfModel getModel() {
		if(model == null) {
			/* log4j logging configuration */
			Logger.getRootLogger().setLevel(Level.INFO);
			PropertyConfigurator.configure("log4j.properties");
			
			Model baseModel = FileManager.get().loadModel(Pop.LOCAL_VOCAB_URI); // Read my ontology into a model
			
			/* Create an inference model using my ontology */
			setModel(ModelFactory.createInfModel(ReasonerRegistry.getOWLMiniReasoner(), baseModel));

			// load standard prefixes into the model
			NsPrefixLoader prefixLoader = new NsPrefixLoader(model);
			prefixLoader.loadStandardPrefixes();
		}
		return model;
	}
	
	
}