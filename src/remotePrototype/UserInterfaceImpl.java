package remotePrototype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;

import app.DisplayPanel;
import app.NewDisplayPanel;

import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import exception.InvalidClueException;

import framework.Clue;
import framework.SemanticSolver;
import framework.UserInterface;

/**
 * @author Ben Griffiths
 *
 */
public class UserInterfaceImpl implements UserInterface {
	private final String EXIT_REQUEST = "EXIT";

	@Override
	public void createAndShow() {
		String userResponse = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		//SemanticSolver semanticSolver = new SemanticSolverImpl(this);
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
					clue = new ClueImplMarkA(userResponse);
				} catch (InvalidClueException e) {
					System.out.println("The clue you entered was invalid: " + e.getMessage());
					continue;
				}
				try {
					//semanticSolver.solve(clue);
				}
				catch(QueryExceptionHTTP e) {
					System.out.println("DBpedia is unavailable at this time. Please try again");
				}
			}
		}	
	}

	@Override
	public void updateResults(String resultsMessage) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public DisplayPanel getDisplayPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NewDisplayPanel getMainDisplayPanel() {
		// TODO Auto-generated method stub
		return null;
	}
}
