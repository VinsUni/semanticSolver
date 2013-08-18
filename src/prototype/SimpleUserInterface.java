package prototype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import exception.InvalidClueException;

import framework.prototype.Clue;
import framework.prototype.SemanticSolver;
import framework.prototype.UserInterface;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleUserInterface implements UserInterface {
	private final String EXIT_REQUEST = "EXIT";

	@Override
	public void createAndShow() {
		SemanticSolver solver = new SimpleSemanticSolver();
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
				solver.solve(clue);
			}
		}	
	}
}
