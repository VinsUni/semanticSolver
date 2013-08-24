/**
 * 
 */
package remotePrototype;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFrame;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import app.DisplayPanel;
import app.DisplayPanelMarkA;
import app.NewDisplayPanel;

import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import exception.InvalidClueException;
import framework.remotePrototype.Clue;
import framework.remotePrototype.SemanticSolver;
import framework.remotePrototype.UserInterface;

/**
 * @author Ben Griffiths
 *
 */
@SuppressWarnings("serial")
public class GUIMarkA extends JFrame implements UserInterface {
	private final String EXIT_REQUEST = "EXIT";
	private final Dimension FRAME_DIMENSION = new Dimension(550, 700); // width and height of the GUI frame
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private String userResponse;
	@Setter(AccessLevel.PRIVATE) private DisplayPanelMarkA displayPanel;
	
	@Override
	public void createAndShow() {
		this.setTitle("Semantic Crossword Solver");
		
		this.setDisplayPanel(new DisplayPanelMarkA(this));
		
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
		final GUIMarkA THIS_UI = this;
		Thread thread = new Thread(new Runnable() {
    		@Override
			public void run() {		
    			//SemanticSolver semanticSolver = new SemanticSolverImpl(THIS_UI);
    			Clue clue;
    			try {
					clue = new ClueImplMarkA(THIS_UI.getUserResponse());
					//semanticSolver.solve(clue);
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
    			//SemanticSolver semanticSolver = new SemanticSolverImpl(THIS_UI);
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
		});
		thread.start();
	}
	
	@Override
	public void updateResults(String resultsMessage) {
		this.getDisplayPanel().getMessageArea().append(resultsMessage + "\n");
		this.repaint();
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
