/**
 * 
 */
package app;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFrame;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import exception.InvalidClueException;
import framework.Clue;
import framework.SemanticSolver;
import framework.UserInterface;

/**
 * @author Ben Griffiths
 *
 */
@SuppressWarnings("serial")
public class GraphicalUserInterface extends JFrame implements UserInterface {
	private final String EXIT_REQUEST = "EXIT";
	private final Dimension FRAME_DIMENSION = new Dimension(550, 700); // width and height of the GUI frame
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private String userResponse;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private DisplayPanel displayPanel;
	
	@Override
	public void createAndShow() {
		this.setTitle("Semantic Crossword Solver");
		
		this.setDisplayPanel(new DisplayPanel(this));
		
		this.setContentPane(this.getDisplayPanel());
		this.getDisplayPanel().setOpaque(true);
		
		this.setPreferredSize(this.FRAME_DIMENSION);
		this.setMinimumSize(this.FRAME_DIMENSION);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		this.start();
	}
	
	public void solveClue(String userResponse) {
		this.userResponse = userResponse;
		final GraphicalUserInterface THIS_UI = this;
		Thread thread = new Thread(new Runnable() {
    		@Override
			public void run() {		
    			SemanticSolver semanticSolver = new SemanticSolverImpl(THIS_UI);
    			Clue clue;
    			try {
					clue = new ClueImpl(THIS_UI.getUserResponse());
					semanticSolver.solve(clue);
				} catch (InvalidClueException e) {
					System.out.println("The clue you entered was invalid: " + e.getMessage());
				}
				catch(QueryExceptionHTTP e) {
					System.out.println("DBpedia is unavailable at this time. Please try again");
				}
    		}	

		});
		thread.start();
	}
	
	private void start() {
		final UserInterface THIS_UI = this;
		Thread thread = new Thread(new Runnable() {
    		@Override
			public void run() {
    			String userResponse = "";
    			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    			SemanticSolver semanticSolver = new SemanticSolverImpl(THIS_UI);
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
    						clue = new ClueImpl(userResponse);
    					} catch (InvalidClueException e) {
    						System.out.println("The clue you entered was invalid: " + e.getMessage());
    						continue;
    					}
    					try {
    						semanticSolver.solve(clue);
    					}
    					catch(QueryExceptionHTTP e) {
    						System.out.println("DBpedia is unavailable at this time. Please try again");
    					}
    				}
    			}	
    			
    			
			}
		});
		thread.start();
	}
	
	@Override
	public void updateResults(String resultsMessage) {
		this.getDisplayPanel().getMessageArea().append(resultsMessage + "\n");
		this.repaint();
	}
	
}
