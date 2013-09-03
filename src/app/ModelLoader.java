/**
 * 
 */
package app;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.openjena.atlas.logging.Log;

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
	private static Logger log = Logger.getLogger(SemanticSolverImpl.class);
	@Setter(AccessLevel.PRIVATE) private static InfModel model;
	@Setter(AccessLevel.PRIVATE) private static Model knowledgeBase;
	@Setter(AccessLevel.PRIVATE) private static Map<String, Boolean> commonClueFragments;
	private static final String COMMON_CLUE_FRAGMENTS_FILE_LOCATION = "commonClueFragments.txt";
	
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
			setKnowledgeBase(FileManager.get().loadModel(CrosswordKB.LOCAL_KNOWLEDGE_BASE_URI)); // Read the KB into a model
		}
		return knowledgeBase;
	}
	
	public static Map<String, Boolean> getCommonClueFragments() {
		if(commonClueFragments == null) {
			setCommonClueFragments(new HashMap<String, Boolean>());
			// Load the file containing common clue fragments into memory and use it to instantiate the Map member
			ModelLoader m = new ModelLoader();
	        ClassLoader classLoader = m.getClass().getClassLoader();
			InputStream inputStream = classLoader.getResourceAsStream(COMMON_CLUE_FRAGMENTS_FILE_LOCATION);
			if(inputStream == null)
				log.debug("Failed to load " + COMMON_CLUE_FRAGMENTS_FILE_LOCATION);
			else {
				Scanner scanner = new Scanner(inputStream);
				while (scanner.hasNext ()) {
					String commonFragment = scanner.nextLine();
					commonClueFragments.put(commonFragment, true);
				}
				scanner.close();
			}
		}
		return commonClueFragments;
	}
}