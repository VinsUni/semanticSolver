package prototype;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Ben Griffiths
 *
 */
public class SemanticSolverRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/* log4j logging configuration */
		Logger.getRootLogger().setLevel(Level.INFO);
		PropertyConfigurator.configure("log4j.properties");
		
		ModelLoader modelLoader = new SimpleModelLoader();
		Model model = modelLoader.getModel();
		
		Clue clue = new SimpleClue("member of The Beatles");
		Query query = new SimpleQuery(clue, model);
		
		
		System.out.println("Candidate solutions:");
		ArrayList<String> candidateSolutions = query.getCandidateSolutions();
		for(String candidateSolution : candidateSolutions)
			System.out.println(candidateSolution);

	}

}
