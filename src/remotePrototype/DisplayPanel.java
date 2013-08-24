/**
 * 
 */
package remotePrototype;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.util.ArrayList;


import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import framework.remotePrototype.Clue;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

/**
 * @author Ben Griffiths
 *
 */
@SuppressWarnings("serial")
public class DisplayPanel extends JPanel {
	private final int PANEL_INSET = 5;
	private final int MESSAGE_AREA_ROWS = 5;
	private final int MESSAGE_AREA_COLUMNS = 20;
	private final int PROGRESS_BAR_MAXIMUM = 100;
	private final int DEFAULT_WORD_NUMBER = 1;
	private final String CLUE_HINT_MESSAGE = "Please enter a clue here: ";
	private final String WORD_NUMBER_HINT_MESSAGE = "Number of words in the solution: ";

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JPanel userInputPanel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JLabel clueHintLabel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JLabel wordNumberHintLabel;
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private int numberOfWordsInSolution;
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JPanel solutionStructurePanel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<JLabel> solutionStructureLabels;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private ArrayList<JTextField> solutionStructureInputFields;
	
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JTextField clueInputField;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JSpinner wordNumberSpinner;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private SpinnerListModel wordNumberSpinnerModel;
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JScrollPane messageAreaScrollPane;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JTextArea messageArea;

	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JProgressBar progressBar;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JButton submitClueButton;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniserTaskMarkA entityRecogniserTask;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Clue clue;
	
	
    public DisplayPanel() {
        super();
    	GridBagLayout gridBagLayout = new GridBagLayout();
    	GridBagConstraints gridBagConstraints = new GridBagConstraints();
    	this.setLayout(gridBagLayout);
   
        this.setSubmitClueButton(new JButton("Submit clue"));
        this.getSubmitClueButton().setActionCommand("submitClue");
        

        this.setProgressBar(new JProgressBar(0, this.PROGRESS_BAR_MAXIMUM));
        this.getProgressBar().setValue(0);
        
        this.setMessageArea(new JTextArea(this.MESSAGE_AREA_ROWS, this.MESSAGE_AREA_COLUMNS));
        this.getMessageArea().setMargin(new Insets(this.PANEL_INSET, this.PANEL_INSET, this.PANEL_INSET, this.PANEL_INSET));
        this.getMessageArea().setEditable(false);
        this.setMessageAreaScrollPane(new JScrollPane(this.getMessageArea()));
        
        this.setClueHintLabel(new JLabel(this.CLUE_HINT_MESSAGE));
        this.setWordNumberHintLabel(new JLabel(this.WORD_NUMBER_HINT_MESSAGE));
        
        
        ArrayList<Integer> wordNumberOptionsList = new ArrayList<Integer>();
        for(int i = 1; i <= 50; i++)
        	wordNumberOptionsList.add(i);
		
		wordNumberSpinnerModel = new SpinnerListModel(wordNumberOptionsList);
		wordNumberSpinnerModel.setValue(this.DEFAULT_WORD_NUMBER);
		wordNumberSpinner = new JSpinner((wordNumberSpinnerModel));
		
		wordNumberSpinnerModel.addChangeListener(new ChangeListener() {
								@Override
								public void stateChanged(ChangeEvent changeEvent) {
									setNumberOfWordsInSolution((Integer)getWordNumberSpinnerModel().getValue());
									drawSolutionStructurePanel();
								}
								
							});
        
        
        this.setClueInputField(new JTextField(20));
		this.getClueInputField().setText("Enter your clue");
		this.setUserInputPanel(new JPanel());
		this.setSolutionStructurePanel(new JPanel());
		
		/* Add components to userInputPanel */
		this.getUserInputPanel().setLayout(new FlowLayout(FlowLayout.LEFT));
		this.getUserInputPanel().add(this.getClueHintLabel());
		this.getUserInputPanel().add(this.getClueInputField());
		this.getUserInputPanel().add(this.getWordNumberHintLabel());
		this.getUserInputPanel().add(this.getWordNumberSpinner());
		this.getUserInputPanel().add(this.getSubmitClueButton());
		
		
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		/* Add userInputPanel to first row of grid bag */
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.ipady = 100;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagLayout.setConstraints(this.getUserInputPanel(), gridBagConstraints);
		this.add(this.getUserInputPanel(), gridBagConstraints);
		
		/* Add solutionStructurePanel to second row of grid bag */
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		
		gridBagConstraints.weighty = 0;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagLayout.setConstraints(this.getSolutionStructurePanel(), gridBagConstraints);
		this.add(this.getSolutionStructurePanel(), gridBagConstraints);
		
		/* Add messageAreaScrollPane to third row of grid bag */
		gridBagConstraints.weighty = 1;
		gridBagConstraints.ipady = 250;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagLayout.setConstraints(this.getMessageAreaScrollPane(), gridBagConstraints);
		this.add(this.getMessageAreaScrollPane(), gridBagConstraints);
		
		/* Add progressBar to fourth row of grid bag */
		gridBagConstraints.weighty = 0;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.ipady = 25;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagLayout.setConstraints(this.getProgressBar(), gridBagConstraints);
		this.add(this.getProgressBar(), gridBagConstraints); // this.add(this.getProgressBar(), BorderLayout.SOUTH);
    }


	private void drawSolutionStructurePanel() {
		this.getSolutionStructurePanel().removeAll();
		this.setSolutionStructureLabels(new ArrayList<JLabel>());
		this.setSolutionStructureInputFields(new ArrayList<JTextField>());
		
		for(int i = 1; i <= this.getNumberOfWordsInSolution(); i++) {
			
			this.getSolutionStructureLabels().add(new JLabel("Letters in word " + i + ": "));
			this.getSolutionStructureInputFields().add(new JTextField(1));
			/* Add the newly created label and textfield to the solutionStructurePanel */
			this.getSolutionStructurePanel().add(this.getSolutionStructureLabels().get(i - 1));
			this.getSolutionStructurePanel().add(this.getSolutionStructureInputFields().get(i - 1));
		}
		this.revalidate();
		this.repaint();
	}
}
