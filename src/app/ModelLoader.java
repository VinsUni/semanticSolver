/**
 * 
 */
package app;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.FileManager;

import framework.CrosswordKB;
import framework.Pop;

import lombok.AccessLevel;
import lombok.Setter;

/**
 * @author Ben Griffiths
 *
 */
public class ModelLoader {
	@Setter(AccessLevel.PRIVATE) private static InfModel model;
	@Setter(AccessLevel.PRIVATE) private static Model knowledgeBase;
	
	private ModelLoader() {
		/* ModelLoader is a Singleton, so the only constructor is private */
	}
	
	public static InfModel getModel() {
		if(model == null) {
			Model baseModel = FileManager.get().loadModel(Pop.LOCAL_VOCAB_URI); // Read my ontology into a model
			
			/* Create an inference model using my ontology */
			setModel(ModelFactory.createInfModel(ReasonerRegistry.getOWLMiniReasoner(), baseModel));

			// load standard prefixes into the model
			NsPrefixLoader prefixLoader = new NsPrefixLoader(model);
			prefixLoader.loadStandardPrefixes();
		}
		return model;
	}
	
	public static Model getKnowledgeBase() {
		if(knowledgeBase == null) {
			Model baseModel = FileManager.get().loadModel(CrosswordKB.LOCAL_KNOWLEDGE_BASE_URI); // Read the KB into a model
			
			/* Create an inference model using the knowledge base */
			setKnowledgeBase(ModelFactory.createInfModel(ReasonerRegistry.getOWLMiniReasoner(), baseModel));
		}
		return knowledgeBase;
	}
}