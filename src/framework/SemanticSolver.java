package framework;

import java.util.ArrayList;

import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

/**
 * @author Ben Griffiths
 * SemanticSolver
 * Interface for a controlling object, through which the GraphicalUserInterface communicates with the core logic of the system
 */
public interface SemanticSolver {
	/**
	 * solve - used to instruct the SemanticSolver object to begin solving a new clue
	 * @param clue - a Clue object representing the clue to be solved
	 * @throws com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP - if any queries made to remote data sources during entity recognition
	 * themselves throw a QueryExceptionHTTP
	 */
	public void solve(Clue clue) throws QueryExceptionHTTP;
	
	/**
	 * findSolutions - used to instruct the SemanticSolver object to begin solving a new clue after Entity Recognition has taken place.
	 * This method relies on solve having been called first, but is provided to allow the UserSelectionBasedUserInterface (a prototype GUI
	 * class) to gather user feedback after Entity Recognition has taken place
	 * @param recognisedResourceUris - the list of recognised entities in the text of the clue to be solved
	 */
	public void findSolutions(ArrayList<String> recognisedResourceUris);
	
	/**
	 * persistKnowledgeBase - used to instruct the SemanticSolver object to save newly gathered solutions to clues to disk
	 */
	public void persistKnowledgeBase();
}
