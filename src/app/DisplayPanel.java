/**
 * 
 */
package app;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;

import exception.InvalidClueException;
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
	private final int BORDER_LEFT = 20;
	private final int BORDER_TOP = 20;
	private final int BORDER_BOTTOM = 20;
	private final int BORDER_RIGHT = 20;
	private final int DEFAULT_WORD_NUMBER = 1;
	private final String CLUE_HINT_MESSAGE = "Please enter a clue here: ";
	private final String WORD_NUMBER_HINT_MESSAGE = "Number of words in the solution: ";

	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JPanel userInputPanel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JLabel clueHintLabel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JLabel wordNumberHintLabel;
	
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JTextField clueInputField;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JSpinner wordNumberSpinner;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private SpinnerListModel wordNumberSpinnerModel;
	
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JScrollPane messageAreaScrollPane;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JTextArea messageArea;

	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JProgressBar progressBar;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JButton submitClueButton;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniserTaskMarkA entityRecogniserTask;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Clue clue;
	
	
    public DisplayPanel() {
        //super(new BorderLayout());
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
        
        
        this.setClueInputField(new JTextField(20));
		this.getClueInputField().setText("Enter your clue");
		this.setUserInputPanel(new JPanel());
		
		
		
		/* Add components to userInputPanel */
		this.getUserInputPanel().add(this.getClueHintLabel());
		this.getUserInputPanel().add(this.getClueInputField());
		this.getUserInputPanel().add(this.getWordNumberHintLabel());
		this.getUserInputPanel().add(this.getWordNumberSpinner());
		this.getUserInputPanel().add(this.getSubmitClueButton());
		
		
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		/* Add userInputPanel to first row of grid bag */
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.ipady = 50;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagLayout.setConstraints(this.getUserInputPanel(), gridBagConstraints);
		this.add(this.getUserInputPanel(), gridBagConstraints); // this.add(this.getUserInputPanel(), BorderLayout.NORTH);
		
		
		/* Add messageAreaScrollPane to second row of grid bag */
		//gridBagConstraints.weighty = 2;
		gridBagConstraints.ipady = 250;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagLayout.setConstraints(this.getMessageAreaScrollPane(), gridBagConstraints);
		this.add(this.getMessageAreaScrollPane(), gridBagConstraints); // this.add(new JScrollPane(this.getMessageArea()),BorderLayout.CENTER);
		
		/* Add progressBar to third row of grid bag */
		//gridBagConstraints.weighty = 0;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.ipady = 50;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagLayout.setConstraints(this.getProgressBar(), gridBagConstraints);
		this.add(this.getProgressBar(), gridBagConstraints); // this.add(this.getProgressBar(), BorderLayout.SOUTH);
		
		
        
		
        
        //this.setBorder(BorderFactory.createEmptyBorder(this.BORDER_TOP, this.BORDER_LEFT, this.BORDER_BOTTOM, this.BORDER_RIGHT));
    }
}
