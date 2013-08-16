package prototype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.InfModel;

import exception.InvalidClueException;
import framework.Clue;
import framework.ModelLoader;
import framework.ClueQuery;
import framework.ClueSolver;
import framework.UserInterface;

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
			System.out.println("Please enter a clue: (e.g. \"member of The Beatles [4, 6]\") or EXIT to finish");
			try {
				userResponse = in.readLine();
			}
			catch(IOException e) {
				e.printStackTrace();
				continue;
			}
			if(!userResponse.equals(EXIT_REQUEST)) {
				
				Clue clue;
				try {
					clue = new SimpleClue(userResponse);
				} catch (InvalidClueException e) {
					System.out.println("The clue you entered was invalid: " + e.getMessage());
					continue;
				}
				
				ModelLoader modelLoader = new SimpleModelLoader();
				InfModel model = modelLoader.getModel();
				
				ClueQuery query = new SimpleQuery(clue, model);
				ClueSolver solver = new SimpleClueSolver();
				
				ArrayList<String> candidateSolutions = solver.getSolutions(clue, query.getCandidateSolutions());
				System.out.println("Candidate solutions:");
				for(String candidateSolution : candidateSolutions)
					System.out.println(candidateSolution);
			}
		}	
	}
}
