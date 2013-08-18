/**
 * 
 */
package prototype;

import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.InfModel;

import framework.prototype.Clue;
import framework.prototype.ClueQuery;
import framework.prototype.ClueSolver;
import framework.prototype.ModelLoader;
import framework.prototype.SemanticSolver;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleSemanticSolver implements SemanticSolver {
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ModelLoader modelLoader; 
	
	@Override
	public void solve(Clue clue) {
		if(modelLoader == null)
			modelLoader = new SimpleModelLoader();
		InfModel model = modelLoader.getModel();
		
		ClueQuery query = new SimpleClueQuery(clue, model);
		ClueSolver solver = new SimpleClueSolver();
		
		ArrayList<String> candidateSolutions = solver.getSolutions(clue, query.getCandidateSolutions());
		System.out.println("Candidate solutions:");
		for(String candidateSolution : candidateSolutions)
			System.out.println(candidateSolution);
		
	}

}
