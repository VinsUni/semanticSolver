/**
 * 
 */
package app;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.log4j.Logger;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import framework.Clue;
import framework.CrosswordKB;
import framework.Solution;

/**
 * @author Ben Griffiths
 *
 */
public class KnowledgeBaseManager {
	private static KnowledgeBaseManager instance;
	private static Logger log = Logger.getLogger(SemanticSolverImpl.class);
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Model knowledgeBase;
	
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
	
	public void addToKnowledgeBase(Clue clue, ArrayList<Solution> solutions) {
		for(Solution solution : solutions) {
			if(solution.getConfidence() > 0)
				this.addToKnowledgeBase(clue, solution);
		}
	}
	
	public void addToKnowledgeBase(Clue clue, Solution solution) {
		UUID clueUID = UUID.randomUUID();
		UUID solutionUID = UUID.randomUUID();
		
		String clueUri = CrosswordKB.CROSSWORD_KB_URI + clueUID.toString();
		String solutionUri = CrosswordKB.CROSSWORD_KB_URI + solutionUID.toString();
		
		String clueText = clue.getSourceClue();
		String solutionStructure = clue.getSolutionStructureAsString();
		String solutionText = solution.getSolutionText();
		
		Resource clueResource = this.getKnowledgeBase().createResource(clueUri);
		Resource solutionResource = this.getKnowledgeBase().createResource(solutionUri);
		
		this.getKnowledgeBase().add(clueResource, RDF.type, CrosswordKB.clue);
		this.getKnowledgeBase().add(clueResource, CrosswordKB.hasClueText, clueText);
		this.getKnowledgeBase().add(clueResource, CrosswordKB.hasSolutionStructure, solutionStructure);
		
		this.getKnowledgeBase().add(solutionResource, RDF.type, CrosswordKB.solution);
		this.getKnowledgeBase().add(solutionResource, CrosswordKB.hasSolutionText, solutionText);
		
		this.getKnowledgeBase().add(clueResource, CrosswordKB.solvedBy, solutionResource);
	}
	
	public void persistKnowledgeBase() {
		try {
			String fileName = "data\\" + CrosswordKB.LOCAL_KNOWLEDGE_BASE_URI;
			FileOutputStream outFile = new FileOutputStream(fileName);
			log.debug("Writing out crosswordKB to disk");
			this.getKnowledgeBase().write(outFile, "RDF/XML-ABBREV");
			outFile.close();
			log.debug("CrosswordKB written to disk");
		}
		catch(FileNotFoundException e) {
			log.debug("Failed to write crosswordKB out to disk");
			log.debug(e.getMessage());
		} 
		catch (IOException e) {
			log.debug("Failed to write crosswordKB out to disk");
			log.debug(e.getMessage());
		}
	}
}
