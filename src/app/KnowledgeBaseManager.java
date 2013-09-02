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
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
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
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<SolvedClue> solvedClues;
	
	/**
	 * The only constructor is private
	 */
	private KnowledgeBaseManager() {
		this.setKnowledgeBase(ModelLoader.getKnowledgeBase());
		this.setSolvedClues(new ArrayList<SolvedClue>());
		this.gatherPreviouslySolvedClues();
	}
	
	private void gatherPreviouslySolvedClues() {

		Selector selector = new SimpleSelector(null, CrosswordKB.solvedBy, (RDFNode) null);
		StmtIterator statements = this.getKnowledgeBase().listStatements(selector);
		
		while(statements.hasNext()) {
			Statement thisStatement = statements.nextStatement();
			System.err.println(thisStatement.toString());
			Resource thisClue = thisStatement.getSubject();
			Resource thisSolution = thisStatement.getObject().asResource();

			Statement clueTextStatement = thisClue.getProperty(CrosswordKB.hasClueText);
			String clueText = clueTextStatement.getObject().toString();
			
			Statement solutionStructureStatement = thisClue.getProperty(CrosswordKB.hasSolutionStructure);
			String solutionStructure = solutionStructureStatement.getObject().toString();
			
			Statement solutionTextStatement = thisSolution.getProperty(CrosswordKB.hasSolutionText);
			String solutionText = solutionTextStatement.getObject().toString();
			
			solvedClues.add(new SolvedClue(clueText, solutionStructure, solutionText));
		}
	}
	
	public static KnowledgeBaseManager getInstance() {
		if(instance == null)
			instance = new KnowledgeBaseManager();
		return instance;
	}
	
	public void addToKnowledgeBase(Clue clue, ArrayList<Solution> solutions) {
		for(Solution solution : solutions) {
			if(solution.getConfidence() > 0) {
				SolvedClue solvedClue = new SolvedClue(clue.getSourceClue(), clue.getSolutionStructureAsString(), 
						solution.getSolutionText());
				if(!this.getSolvedClues().contains(solvedClue)) {
					this.getSolvedClues().add(solvedClue); // add the solvedClue to the list in memory
					this.addToKnowledgeBase(clue, solution); // add the new triples to the knowledge base
				}
			}
		}
	}
	
	public void addToKnowledgeBase(Clue clue, Solution solution) {
		String clueText = clue.getSourceClue();
		String solutionStructure = clue.getSolutionStructureAsString();
		String solutionText = solution.getSolutionText();
		
		UUID clueUID = UUID.randomUUID();
		UUID solutionUID = UUID.randomUUID();
		
		String clueUri = CrosswordKB.CROSSWORD_KB_URI + clueUID.toString();
		String solutionUri = CrosswordKB.CROSSWORD_KB_URI + solutionUID.toString();

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
