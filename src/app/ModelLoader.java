package app;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;

import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.FileManager;

import framework.CrosswordKB;
import framework.Pop;

import lombok.AccessLevel;
import lombok.Setter;

/**
 * @author Ben Griffiths
 * ModelLoader
 * Utility class providing static methods to load a single instance of each of the pop ontology, the crossword knowledge base, and a
 * list of the most common clue fragments, and provide references to the unique instances of each of these data sources
 */
public class ModelLoader {
	private static Logger log = Logger.getLogger(ModelLoader.class);
	@Setter(AccessLevel.PRIVATE) private static Model model;
	@Setter(AccessLevel.PRIVATE) private static Model knowledgeBase;
	@Setter(AccessLevel.PRIVATE) private static Map<String, Boolean> commonClueFragments;
	private static final String COMMON_CLUE_FRAGMENTS_FILE_LOCATION = "commonClueFragments.txt";
	
	/**
	 * The only constructor is private; the ModelLoader class is not designed to be instantiated
	 */
	private ModelLoader() {
		
	}
	
	/**
	 * getModel - when first called, this method instantiates a Model by loading the Pop ontology from a file. It also loads a set of 
	 * standard namespace prefixes into the Model, from another file, and then returns the model. The Model member is treated as a 
	 * Singleton and thus subsequent calls to the method result in the Model simply being returned. 
	 * @return - the unique static instance of com.hp.hpl.jena.rdf.model.Model that is the model member of the ModelLoader class
	 */
	public static Model getModel() {
		if(model == null) {
			setModel(FileManager.get().loadModel(Pop.LOCAL_VOCAB_URI));
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
	 * @return - the unique static instance of a com.hp.hpl.jena.rdf.model.Model that is the knowledgeBase member of the ModelLoader class
	 */
	public static Model getKnowledgeBase() {
		if(knowledgeBase == null) {
			try {
				setKnowledgeBase(FileManager.get().loadModel(CrosswordKB.LOCAL_KNOWLEDGE_BASE_URI));
			}
			catch(JenaException e) {
				log.debug("Failed to load " + CrosswordKB.LOCAL_KNOWLEDGE_BASE_URI);
				throw e;
			}
		}
		return knowledgeBase;
	}
	
	/**
	 * getCommonClueFragments - when first called, instantiates a map containing all of the most common clue fragments by loading the
	 * content of the commonClueFragments text file into memory. Each map entry is given a value of true; a map is used in order to
	 * provide fast random access
	 * @return the unique static instance of java.util.HashMap containing all of the most common clue fragments
	 */
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