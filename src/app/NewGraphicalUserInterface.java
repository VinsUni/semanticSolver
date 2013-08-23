/**
 * 
 */
package app;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import javax.swing.JCheckBox;
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
public class NewGraphicalUserInterface extends JFrame implements UserInterface, ActionListener, PropertyChangeListener {
	private final String EXIT_REQUEST = "EXIT";
	private final String ENTITY_RECOGNITION_IN_PROGRESS_MESSAGE = "Searching for known entities in the clue";
	private final Dimension FRAME_DIMENSION = new Dimension(550, 600); // width and height of the GUI frame
	
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private String userResponse;
	
	
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private NewDisplayPanel mainDisplayPanel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private NewSemanticSolverImpl semanticSolver;

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private NewEntityRecogniserTask entityRecogniserTask;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Clue clue;
	
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private ArrayList<JCheckBox> checkBoxes;

	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private ArrayList<String> recognisedResourceUris;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private ArrayList<String> chosenResourceUris;

	@Override
	public void createAndShow() {
		this.setSemanticSolver(new NewSemanticSolverImpl(this));
		this.setTitle("Semantic Crossword Solver");

		this.setMainDisplayPanel(new NewDisplayPanel());

		this.getMainDisplayPanel().getSubmitClueButton().addActionListener(this);


		this.getMainDisplayPanel().getSubmitChosenResourcesButton().addActionListener(this);



		this.getMainDisplayPanel().setOpaque(true);

		this.setContentPane(this.getMainDisplayPanel());

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

	@Override
	public void updateResults(String resultsMessage) {
		this.getMainDisplayPanel().getMessageArea().append(resultsMessage + "\n");
		this.repaint();
	}

	/**
     * Invoked when task's progress property changes.
     */
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("progress")) {
            int progress = (Integer) propertyChangeEvent.getNewValue();
            this.getMainDisplayPanel().getProgressBar().setValue(progress);
            if(this.getMainDisplayPanel().getProgressBar().getValue() == 100)
            	this.getMainDisplayPanel().getProgressBar().setStringPainted(false);
        } 
    }
    
    public void solveClue() {
    	this.getMainDisplayPanel().getSubmitClueButton().setEnabled(false);
        this.getMainDisplayPanel().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        this.getMainDisplayPanel().getProgressBar().setString(ENTITY_RECOGNITION_IN_PROGRESS_MESSAGE);
        this.getMainDisplayPanel().getProgressBar().setStringPainted(true);
        
       
        
        String clueText = this.getMainDisplayPanel().getClueInputField().getText();
        int[] solutionStructure = new int[(Integer)this.getMainDisplayPanel().getWordNumberSpinnerModel().getValue()];
        
        ArrayList<JTextField> solutionStructureInputFields = this.getMainDisplayPanel().getSolutionStructureInputFields();
        for(int i = 0; i < solutionStructureInputFields.size(); i++) {
        	solutionStructure[i] = Integer.parseInt(solutionStructureInputFields.get(i).getText()); // NEED TO ADD EXCEPTION CHECK
        }
        
        Clue clue = null;
		try {
			clue = new ClueImpl(clueText, solutionStructure);
		} catch (InvalidClueException e) {
			this.getMainDisplayPanel().getMessageArea().append(e.getMessage() + "\n");
			this.getMainDisplayPanel().getSubmitClueButton().setEnabled(true);
	        this.getMainDisplayPanel().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}

		this.setClue(clue);
        
		this.getMainDisplayPanel().getProgressBar().setString(this.ENTITY_RECOGNITION_IN_PROGRESS_MESSAGE);
        this.getMainDisplayPanel().getProgressBar().setStringPainted(true);

	    Thread solverThread = new Thread(new Runnable() {
		        public void run() {
		        	getSemanticSolver().findEntities(getClue());
		        }
		    });
	    solverThread.start();
        
        this.getMainDisplayPanel().getSubmitClueButton().setEnabled(true);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void getChosenEntitiesFromUser(ArrayList<RecognisedResource> recognisedResources) {
    	
    	this.getMainDisplayPanel().getSubmitClueButton().setActionCommand("submitChosenResources");
   	 	this.getMainDisplayPanel().getSubmitClueButton().setText("Solve clue");
   	 
   	 
        this.setCheckBoxes(new ArrayList<JCheckBox>());
        this.setRecognisedResourceUris(new ArrayList<String>());
        this.setChosenResourceUris(new ArrayList<String>());

        /* Remove solutionStructurePanel and add resourceSelectorPanel in its place */
        
        GridBagConstraints constraints = this.getMainDisplayPanel().getGridBagConstraints(); // I WILL NEED TO MAKE THE CONSTRAINTS A MEMBER INSTEAD OF JUST A LOCAL VARIABLE
        constraints.gridy = 1; // Add to second row of DisplayPanel (which I think is where the solutionStructurePanel was)
        this.getMainDisplayPanel().getPanelScrollPane().setViewportView(this.getMainDisplayPanel().getResourceSelectorPanel());
        this.getMainDisplayPanel().getPanelScrollPane().revalidate();
        this.getMainDisplayPanel().getPanelScrollPane().repaint();

        for(RecognisedResource thisResource : recognisedResources) {
                 String resourceLabel = thisResource.getResourceLabel();
                 String typeLabel = thisResource.getTypeLabel();
                 String uri = thisResource.getUri();
                 this.getRecognisedResourceUris().add(uri);
                 JCheckBox checkBox = new JCheckBox(resourceLabel + " (type: " + typeLabel + ")");
                 this.getCheckBoxes().add(checkBox);
                 
                 checkBox.addItemListener(new ItemListener() {
                          @Override
                          public void itemStateChanged(ItemEvent itemEvent) {
                               JCheckBox thisCheckBox = (JCheckBox)itemEvent.getSource();
                               int index = getCheckBoxes().indexOf(thisCheckBox);
                               String thisUri = getRecognisedResourceUris().get(index);
                               if(itemEvent.getStateChange() == ItemEvent.SELECTED)
                            	   getChosenResourceUris().add(thisUri);                     
                               else getChosenResourceUris().remove(thisUri);
                       }
               });
         }
        
        for(JCheckBox checkBox : this.getCheckBoxes())
            this.getMainDisplayPanel().getResourceSelectorPanel().add(checkBox); 
        
         /* Revalidate and repaint – do I need to call this on the displayPanel? Hopefully it will propagate to all children of the GraphicalUserInterface */
         this.getMainDisplayPanel().getPanelScrollPane().revalidate();
         this.getMainDisplayPanel().getPanelScrollPane().repaint();
    }



	public void findSolutions() {
		Thread findSolutionsThread = new Thread(new Runnable() {
                         public void run() {
                          getSemanticSolver().findSolutions(getChosenResourceUris());
                         }
                     });
		findSolutionsThread.start();
	}

    /**
     * Invoked when the user presses the "Submit clue" button.
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getActionCommand().equals("submitClue"))
        	this.solveClue();
	else if(actionEvent.getActionCommand().equals("submitChosenResources"))
	      	this.findSolutions();
    		
    }
    
    /**
     * Provided for convenience - used by SemanticSolverImpl
     * @return
     */
    public JButton getSubmitClueButton() {
    	return this.getMainDisplayPanel().getSubmitClueButton();
    }

	@Override
	public DisplayPanel getDisplayPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	public void showNewClueOptions() {
		this.getMainDisplayPanel().getSubmitClueButton().setEnabled(true);
		this.getMainDisplayPanel().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		this.getMainDisplayPanel().getSubmitClueButton().setActionCommand("submitClue");
		this.getMainDisplayPanel().getSubmitClueButton().setText("Submit clue");
 
     	/* Remove checkboxes from resourceSelectorPanel */
     	for(JCheckBox thisCheckBox : this.getCheckBoxes())
     		this.getMainDisplayPanel().getResourceSelectorPanel().remove(thisCheckBox);
     	
     	/* Remove resourceSelectorPanel and add solutionStructurePanel in its place */
     	
        this.getMainDisplayPanel().getPanelScrollPane().setViewportView(this.getMainDisplayPanel().getSolutionStructurePanel());
        
        this.getMainDisplayPanel().getPanelScrollPane().revalidate();
     	this.getMainDisplayPanel().getPanelScrollPane().repaint();
	}
}