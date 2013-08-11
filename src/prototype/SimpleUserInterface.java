package prototype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleUserInterface implements UserInterface {
	private final String EXIT_REQUEST = "EXIT";

	@Override
	public void createAndShow() {
		String userResponse = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while(!userResponse.equals(EXIT_REQUEST)) {
			System.out.println("Please enter a clue: ");
			try {
				userResponse = in.readLine();
			}
			catch(IOException e) {
				e.printStackTrace();
				continue;
			}
			if(!userResponse.equals(EXIT_REQUEST)) {
				
				ModelLoader modelLoader = new SimpleModelLoader();
				InfModel model = modelLoader.getModel();
				
				Clue clue = new SimpleClue(userResponse);
				Query query = new SimpleQuery(clue, model);
				Solver solver = new SimpleSolver();
				
				ArrayList<String> candidateSolutions = solver.getSolutions(clue, query.getCandidateSolutions());
				System.out.println("Candidate solutions:");
				for(String candidateSolution : candidateSolutions)
					System.out.println(candidateSolution);
			}
		}	
	}
}
