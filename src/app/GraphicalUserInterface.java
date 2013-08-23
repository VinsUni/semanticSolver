/**
 * 
 */
package app;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
public class GraphicalUserInterface extends JFrame implements UserInterface, ActionListener, PropertyChangeListener {
	private final String EXIT_REQUEST = "EXIT";
	private final String ENTITY_RECOGNITION_IN_PROGRESS_MESSAGE = "Searching for known entities in the clue";
	private final Dimension FRAME_DIMENSION = new Dimension(550, 600); // width and height of the GUI frame
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private String userResponse;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private DisplayPanel displayPanel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private SemanticSolver semanticSolver;
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniserTask entityRecogniserTask;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Clue clue;
	
	@Override
	public void createAndShow() {
		this.setSemanticSolver(new SemanticSolverImpl(this));
		this.setTitle("Semantic Crossword Solver");
		
		this.setDisplayPanel(new DisplayPanel());
		this.getDisplayPanel().getSubmitClueButton().addActionListener(this);
		this.getDisplayPanel().setOpaque(true);
		
		this.setContentPane(this.getDisplayPanel());
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				requestFocusInWindow();
			}
		});
		
		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent keyEvent) {
				if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
					solveClue();
			}
			
		});
		
		this.setPreferredSize(this.FRAME_DIMENSION);
		this.setMinimumSize(this.FRAME_DIMENSION);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		this.start();
	}
	
	public void solveClue(String userResponse) {
		
		/*this.userResponse = userResponse;
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
		thread.start(); */
	}
	
	private void start() {
		
		Thread thread = new Thread(new Runnable() {
    		@Override
			public void run() {
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
    					
    					try {
    						setClue(new ClueImplMarkA(userResponse));
    					} catch (InvalidClueException e) {
    						System.out.println("The clue you entered was invalid: " + e.getMessage());
    						continue;
    					}
    					try {
    						getSemanticSolver().solve(getClue());
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

	/**
     * Invoked when task's progress property changes.
     */
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("progress")) {
            int progress = (Integer) propertyChangeEvent.getNewValue();
            this.getDisplayPanel().getProgressBar().setValue(progress);
            if(this.getDisplayPanel().getProgressBar().getValue() == 100)
            	this.getDisplayPanel().getProgressBar().setStringPainted(false);
        } 
    }
    
    public void solveClue() {
    	this.getDisplayPanel().getSubmitClueButton().setEnabled(false);
        this.getDisplayPanel().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        this.getDisplayPanel().getProgressBar().setString(ENTITY_RECOGNITION_IN_PROGRESS_MESSAGE);
        this.getDisplayPanel().getProgressBar().setStringPainted(true);
        
       
        
        String clueText = this.getDisplayPanel().getClueInputField().getText();
        int[] solutionStructure = new int[(Integer)this.getDisplayPanel().getWordNumberSpinnerModel().getValue()];
        
        ArrayList<JTextField> solutionStructureInputFields = this.getDisplayPanel().getSolutionStructureInputFields();
        for(int i = 0; i < solutionStructureInputFields.size(); i++) {
        	solutionStructure[i] = Integer.parseInt(solutionStructureInputFields.get(i).getText()); // NEED TO ADD EXCEPTION CHECK
        }
        
        Clue clue = null;
		try {
			clue = new ClueImpl(clueText, solutionStructure);
		} catch (InvalidClueException e) {
			this.getDisplayPanel().getMessageArea().append(e.getMessage() + "\n");
			this.getDisplayPanel().getSubmitClueButton().setEnabled(true);
	        this.getDisplayPanel().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}
		
		this.setClue(clue);
        
		this.getDisplayPanel().getProgressBar().setString(this.ENTITY_RECOGNITION_IN_PROGRESS_MESSAGE);
        this.getDisplayPanel().getProgressBar().setStringPainted(true);
		
	    Thread solverThread = new Thread(new Runnable() {
		        public void run() {
		        	getSemanticSolver().solve(getClue());
		        }
		    });
	    solverThread.start();
        
        this.getDisplayPanel().getSubmitClueButton().setEnabled(true);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Invoked when the user presses the "Submit clue" button.
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.solveClue();
    }
    
    /**
     * Provided for convenience - used by SemanticSolverImpl
     * @return
     */
    public JButton getSubmitClueButton() {
    	return this.getDisplayPanel().getSubmitClueButton();
    }

	@Override
	public NewDisplayPanel getMainDisplayPanel() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
