/**
 * 
 */
package framework;

import java.util.ArrayList;

import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

/**
 * @author Ben Griffiths
 *
 */
public interface SemanticSolver {
	public void findEntities(Clue clue) throws QueryExceptionHTTP;
	public  void findSolutions(ArrayList<String> recognisedResourceUris);
}
