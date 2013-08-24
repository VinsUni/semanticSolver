/**
 * 
 */
package app;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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

import framework.Clue;

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
	
	private final int DEFAULT_NUMBER_OF_WORDS_IN_SOLUTION = 1;

	private final int DEFAULT_WORD_NUMBER = 1;
	private final String CLUE_HINT_MESSAGE = "Please enter a clue here: ";
	private final String WORD_NUMBER_HINT_MESSAGE = "Number of words in the solution: ";

	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private GridBagConstraints gridBagConstraints;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JPanel userInputPanel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JLabel clueHintLabel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JLabel wordNumberHintLabel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private int numberOfWordsInSolution;
	
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JScrollPane panelScrollPane;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JPanel solutionStructurePanel;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JPanel resourceSelectorPanel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private ArrayList<JLabel> solutionStructureLabels;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private ArrayList<JTextField> solutionStructureInputFields;

	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JTextField clueInputField;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JSpinner wordNumberSpinner;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private SpinnerListModel wordNumberSpinnerModel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JScrollPane messageAreaScrollPane;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JTextArea messageArea;

	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JProgressBar progressBar;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JButton submitClueButton;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JButton submitChosenResourcesButton;

	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Clue clue;

    public DisplayPanel() {
        super();
    	GridBagLayout gridBagLayout = new GridBagLayout();
    	this.setGridBagConstraints(new GridBagConstraints());
    	this.setLayout(gridBagLayout);
   
        this.setSubmitClueButton(new JButton("Submit clue"));
        this.getSubmitClueButton().setActionCommand("submitClue");

        this.setSubmitChosenResourcesButton(new JButton("Solve clue"));
        this.getSubmitChosenResourcesButton().setActionCommand("submitChosenResources");

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
		
		/* create solutionStructurePanel and resourceSelectorPanels, both with grid layout of one column and 
		 * variable number of rows
		 */
		this.setSolutionStructurePanel(new JPanel(new GridLayout(0, 1)));
		this.setResourceSelectorPanel(new JPanel(new GridLayout(0, 1)));
		
		this.setPanelScrollPane(new JScrollPane(this.getSolutionStructurePanel(), 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

		/* Add components to userInputPanel */
		this.getUserInputPanel().setLayout(new FlowLayout(FlowLayout.LEFT));
		this.getUserInputPanel().add(this.getClueHintLabel());
		this.getUserInputPanel().add(this.getClueInputField());
		this.getUserInputPanel().add(this.getWordNumberHintLabel());
		this.getUserInputPanel().add(this.getWordNumberSpinner());
		this.getUserInputPanel().add(this.getSubmitClueButton());

		/* Add userInputPanel to first row of grid bag */
		this.getGridBagConstraints().fill = GridBagConstraints.HORIZONTAL;
		this.getGridBagConstraints().anchor = GridBagConstraints.NORTHWEST;
		this.getGridBagConstraints().ipady = 100;
		this.getGridBagConstraints().weighty = 1;
		this.getGridBagConstraints().gridx = 0;
		this.getGridBagConstraints().gridy = 0;
		gridBagLayout.setConstraints(this.getUserInputPanel(), this.getGridBagConstraints());
		this.add(this.getUserInputPanel(), this.getGridBagConstraints());

		/* Add panelScrollPane to second row of grid bag */
		this.getGridBagConstraints().anchor = GridBagConstraints.NORTHWEST;
		this.getGridBagConstraints().weighty = 0;
		this.getGridBagConstraints().gridx = 0;
		this.getGridBagConstraints().gridy = 1;
		gridBagLayout.setConstraints(this.getPanelScrollPane(), this.getGridBagConstraints());
		this.add(this.getPanelScrollPane(), this.getGridBagConstraints());

		/* Add messageAreaScrollPane to third row of grid bag */
		this.getGridBagConstraints().weighty = 1;
		this.getGridBagConstraints().ipady = 250;
		this.getGridBagConstraints().gridx = 0;
		this.getGridBagConstraints().gridy = 2;
		gridBagLayout.setConstraints(this.getMessageAreaScrollPane(), this.getGridBagConstraints());
		this.add(this.getMessageAreaScrollPane(), this.getGridBagConstraints());

		/* Add progressBar to fourth row of grid bag */
		this.getGridBagConstraints().weighty = 0;
		this.getGridBagConstraints().anchor = GridBagConstraints.NORTHWEST;
		this.getGridBagConstraints().ipady = 25;
		this.getGridBagConstraints().weighty = 1;
		this.getGridBagConstraints().gridx = 0;
		this.getGridBagConstraints().gridy = 3;
		gridBagLayout.setConstraints(this.getProgressBar(), this.getGridBagConstraints());
		this.add(this.getProgressBar(), this.getGridBagConstraints()); // this.add(this.getProgressBar(), BorderLayout.SOUTH);
		
		/* Draw the solutionStructurePanel using default number of words in solution */
		this.setNumberOfWordsInSolution(this.DEFAULT_NUMBER_OF_WORDS_IN_SOLUTION);
		this.drawSolutionStructurePanel();
    }

	private void drawSolutionStructurePanel() {
		this.getSolutionStructurePanel().removeAll();
		this.setSolutionStructureLabels(new ArrayList<JLabel>());
		this.setSolutionStructureInputFields(new ArrayList<JTextField>());

		for(int i = 1; i <= this.getNumberOfWordsInSolution(); i++) {
			this.getSolutionStructureLabels().add(new JLabel("Letters in word " + i + ": "));
			this.getSolutionStructureInputFields().add(new JTextField(1));
			/* add the newly created label and textfield to a panel */
			JPanel solutionStructureSubPanel = new JPanel();
			solutionStructureSubPanel.add(this.getSolutionStructureLabels().get(i - 1));
			solutionStructureSubPanel.add(this.getSolutionStructureInputFields().get(i - 1));
			/* add the panel to the next row of the solutionStructurePanel */
			this.getSolutionStructurePanel().add(solutionStructureSubPanel);
		}
		this.revalidate();
		this.repaint();
	}
}
