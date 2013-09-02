/**
 * 
 */
package app;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Resource;

import framework.Clue;
import framework.Pop;
import framework.Solution;

/**
 * @author Ben Griffiths
 *
 */
public class KnowledgeBaseManager {
	private static KnowledgeBaseManager instance;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private InfModel knowledgeBase;
	
	/**
	 * The only constructor is private
	 */
	private KnowledgeBaseManager() {
		this.setKnowledgeBase(ModelLoader.getKnowledgeBase());
	}
	
	public static KnowledgeBaseManager getInstance() {
		if(instance == null)
			instance = new KnowledgeBaseManager();
		return instance;
	}
	
	public void addToKnowledgeBase(Clue clue, Solution solution) {
		UUID clueUID = UUID.randomUUID();
		UUID solutionUID = UUID.randomUUID();
		
		String clueUri = Pop.CROSSWORD_KB_URI + clueUID.toString();
		String solutionUri = Pop.CROSSWORD_KB_URI + solutionUID.toString();
		
		Resource clueResource = this.getKnowledgeBase().createResource(clueUri);
		Resource solutionResource = this.getKnowledgeBase().createResource(solutionUri);
		
		//this.getKnowledgeBase().add(clueResource, Pop.)
	}
}
