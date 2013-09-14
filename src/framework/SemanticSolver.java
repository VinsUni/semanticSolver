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
	 * 
	 * @param recognisedResourceUris
	 */
	public void findSolutions(ArrayList<String> recognisedResourceUris);
	
	/**
	 * 
	 */
	public void persistKnowledgeBase();
}
