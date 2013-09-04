/**
 * 
 */
package app;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.shared.JenaException;
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
		/* the only constructor is private */
	}
	
	/**
	 * getModel - when first called, this method instantiates an InfModel, by loading the Pop ontology from a file and binding it to
	 * an OWL Mini reasoner. It also loads a set of standard namespace prefixes into the InfModel, from another file, and then returns
	 * the model. The InfModel member is treated as a Singleton and thus subsequent calls to the method result in the InfModel simply being
	 * returned. 
	 * @return - the unique static instance of an InfModel that is a member of the ModelLoader class
	 */
	public static InfModel getModel() {
		if(model == null) {
			Model baseModel = FileManager.get().loadModel(Pop.LOCAL_VOCAB_URI);
			/* Create an inference model using the ontology and an OWL Mini reasoner */
			setModel(ModelFactory.createInfModel(ReasonerRegistry.getOWLMiniReasoner(), baseModel));
			/* load standard prefixes into the model */
			NsPrefixLoader prefixLoader = new NsPrefixLoader(model);
			prefixLoader.loadStandardPrefixes();
		}
		return model;
	}
	
	/**
	 * getKnowledgeBase - when first called, this method instantiates a basic Model with the content of the RDF file that stores the 
	 * crossword knowledge base on disk, and returns the model. The Model member is treated as a Singleton and thus subsequent calls 
	 * to the method result in the Model simply being returned. 
	 * @return - the unique static instance of a Model that is a member of the ModelLoader class
	 */
	public static Model getKnowledgeBase() {
		if(knowledgeBase == null) {
			try {
				setKnowledgeBase(FileManager.get().loadModel(CrosswordKB.LOCAL_KNOWLEDGE_BASE_URI)); // Read the KB into a model
			}
			catch(JenaException e) {
				log.debug("Failed to load " + CrosswordKB.LOCAL_KNOWLEDGE_BASE_URI);
				throw e;
			}
		}
		return knowledgeBase;
	}
	
	public static Map<String, Boolean> getCommonClueFragments() {
		if(commonClueFragments == null) {
			setCommonClueFragments(new HashMap<String, Boolean>());
			/* Load the file containing common clue fragments into memory and use it to instantiate the Map member */
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
				log.debug(COMMON_CLUE_FRAGMENTS_FILE_LOCATION + " successfully loaded");
			}
		}
		return commonClueFragments;
	}
}