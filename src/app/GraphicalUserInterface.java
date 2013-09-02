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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import javax.swing.JTextField;

import org.apache.log4j.Logger;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

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
	private static Logger log = Logger.getLogger(ClueQueryTask.class);
	private final String ENTITY_RECOGNITION_IN_PROGRESS_MESSAGE = "Searching for known entities in the clue";
	public static final String CLUE_QUERY_IN_PROGRESS_MESSAGE = "Searching for solutions on DBpedia";
	private final Dimension FRAME_DIMENSION = new Dimension(1000, 600); // width and height of the GUI frame
	private final Dimension DISPLAY_PANEL_DIMENSION = new Dimension(950, 575);
	private final String HELP_FILE_LOCATION = "helpFile.txt";
	private final String ABOUT_FILE_LOCATION = "aboutFile.txt";
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private String helpText;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private String aboutText;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private String userResponse;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private DisplayPanel displayPanel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private SemanticSolver semanticSolver;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniserTask entityRecogniserTask;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private ArrayList<JCheckBox> checkBoxes;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private ArrayList<String> recognisedResourceUris;
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JMenuBar mainMenuBar;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JMenu menu;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JMenuItem aboutMenuItem;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JMenuItem helpMenuItem;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JMenuItem exitMenuItem;
	
	/**
	 * createAndShow - Creates the GUI. Must be called from the EDT. See framework.UserInterface
	 */
	@Override
	public void createAndShow() {
		this.setSemanticSolver(new SemanticSolverImpl(this));
		this.setTitle("Semantic Crossword Solver");
		
		// Load the file containing text for the help dialog and copy contents into helpText string
        ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(HELP_FILE_LOCATION);
		StringBuffer stringBuffer = new StringBuffer();
		if(inputStream == null)
			this.setHelpText("Help file not found");
		else {
			Scanner scanner = new Scanner(inputStream);
			while (scanner.hasNext ())
				stringBuffer.append (scanner.nextLine ());
			scanner.close();
			this.setHelpText(new String(stringBuffer));
		}
		
		// Load the file containing text for the about dialog and copy contents into aboutText string
		inputStream = classLoader.getResourceAsStream(ABOUT_FILE_LOCATION);
		stringBuffer = new StringBuffer();
		if(inputStream == null)
			this.setAboutText("About file not found");
		else {
			Scanner scanner = new Scanner(inputStream);
			while (scanner.hasNext ())
				stringBuffer.append (scanner.nextLine ());
			scanner.close();
			this.setAboutText(new String(stringBuffer));
		}
		

		/* Create menubar, menu and menu items */
		this.setMainMenuBar(new JMenuBar());
		this.setMenu(new JMenu("Help"));
		
		this.setExitMenuItem(new JMenuItem("Exit"));
		this.getExitMenuItem().addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent actionEvent) {
    			exitApplication();
    		}
    	});
    	this.getMenu().add(this.getExitMenuItem(), 0);
		
		this.setAboutMenuItem(new JMenuItem("About this application"));
    	this.getAboutMenuItem().addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent actionEvent) {
    			JOptionPane.showMessageDialog(getContentPane(), getAboutText(), "About this application",
    					JOptionPane.INFORMATION_MESSAGE);
    		}
    	});
    	this.getMenu().add(this.getAboutMenuItem(), 0);
    	this.setHelpMenuItem(new JMenuItem("How to use this application"));
    	this.getHelpMenuItem().addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent actionEvent) {
    			JOptionPane.showMessageDialog(getContentPane(), getHelpText(), "How to use this application",
    					JOptionPane.QUESTION_MESSAGE);
    		}
    	});
    	
    	this.getMenu().add(this.getHelpMenuItem(), 0);
    	
    	this.getMainMenuBar().add(this.getMenu());
    	this.setJMenuBar(this.getMainMenuBar());
    	
        
		this.setDisplayPanel(new DisplayPanel());
		this.getDisplayPanel().getSubmitClueButton().addActionListener(this);
		
		this.getDisplayPanel().setPreferredSize(this.DISPLAY_PANEL_DIMENSION);
		
		this.getDisplayPanel().setSize(this.DISPLAY_PANEL_DIMENSION);
		
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
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowListener() {
        	@Override
			public void windowClosing(WindowEvent e) {
				exitApplication();
			}
			@Override public void windowActivated(WindowEvent e) {}
			@Override public void windowClosed(WindowEvent e) {}
			@Override public void windowDeactivated(WindowEvent e) {}
			@Override public void windowDeiconified(WindowEvent e) {}
			@Override public void windowIconified(WindowEvent e) {}
			@Override public void windowOpened(WindowEvent e) {}
        });
		this.pack();
		this.setVisible(true);
	}
	
	/**
	 * updateResults - Adds the specified String to the message area. See framework.UserInterface
	 * @argument resultsMessage - the message to be displayed to the user
	 */
	@Override
	public void updateResults(String resultsMessage) {
		this.getDisplayPanel().getMessageArea().append(resultsMessage + "\n");
		this.repaint();
	}

	/**
     * propertyChange - Invoked when task's progress property changes. See java.beans.PropertyChangeListener
     * @argument propertyChangeEvent - the event to be handled
     */
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("progress")) {
            int progress = (Integer) propertyChangeEvent.getNewValue();
            if(progress == this.getDisplayPanel().PROGRESS_BAR_MAXIMUM) {
            	this.getDisplayPanel().getProgressBar().setValue(progress);
            	progress = 0;
            }
            this.getDisplayPanel().getProgressBar().setValue(progress);
        } 
    }
    
    /**
     * solveClue - updates the GUI to reflect to the user that a clue has been submitted. Retrieves the specification of a clue
     * entered by the user and instantiates a new Clue object to represent the clue. Then calls the SemanticSolver member's
     * findEntities method on a separate thread in order to begin extracting recognised entities from the clue.
     * See framework.UserInterface
     */
    @Override
    public void solveClue() {
    	this.getDisplayPanel().getSubmitClueButton().setEnabled(false);
        this.getDisplayPanel().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        String clueText = this.getDisplayPanel().getClueInputField().getText();
        int[] solutionStructure = new int[(Integer)this.getDisplayPanel().getWordNumberSpinnerModel().getValue()];
        
        ArrayList<JTextField> solutionStructureInputFields = this.getDisplayPanel().getSolutionStructureInputFields();
        for(int i = 0; i < solutionStructureInputFields.size(); i++) {
        	try {
        		solutionStructure[i] = Integer.parseInt(solutionStructureInputFields.get(i).getText());
        	}
        	catch(NumberFormatException e) {
        		this.updateResults("Please specify the complete solution structure, using only digits (0-9)");
        		this.getDisplayPanel().getSubmitClueButton().setEnabled(true);
    	        this.getDisplayPanel().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        		return;
        	}
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
		        	getSemanticSolver().findEntities(getClue());
		        }
		    });
	    solverThread.start();
    }

    /**
     * actionPerformed - invoked when the user presses the "Submit clue" button in order to submit a clue.
     * See java.awt.event.ActionListener.
     * @argument actionEvent - the event to be handled
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        	this.solveClue();
    }
    
    /**
     * showNewClueOptions - 
     * See framework.UserInterface
     */
    @Override
	public void showNewClueOptions() {
    	this.getDisplayPanel().getProgressBar().setStringPainted(false);
		this.getDisplayPanel().getSubmitClueButton().setEnabled(true);
		this.getDisplayPanel().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
    
    /**
     * 
     */
    @Override
    public void updateProgressBarMessage(String message) {
    	this.getDisplayPanel().getProgressBar().setString(message);
    	this.getDisplayPanel().getProgressBar().setStringPainted(true);
    }
    
    public void exitApplication() {
    	this.getDisplayPanel().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    	log.debug("System exit requested by user");
    	this.getSemanticSolver().persistKnowledgeBase(); // save the crossword knowledge base to disk before exit
		System.exit(EXIT_ON_CLOSE);
    }
}