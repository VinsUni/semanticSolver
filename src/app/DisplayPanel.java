/**
 * 
 */
package app;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import exception.InvalidClueException;
import framework.Clue;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

/**
 * @author Ben Griffiths
 * I need to move the progress bars, listening functionality, etc to the GraphicalUserInterface class *********************
 * ****************************************************************************************************************
 * ****************************************************************************************************************
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
	
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JTextField inputField;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JTextArea messageArea;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JPanel userInputPanel;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JProgressBar progressBar;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JButton submitClueButton;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniserTaskMarkA entityRecogniserTask;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Clue clue;
	
    public DisplayPanel() {
        super(new BorderLayout());
        
        this.setSubmitClueButton(new JButton("Submit clue"));
        this.getSubmitClueButton().setActionCommand("submitClue");
        

        this.setProgressBar(new JProgressBar(0, this.PROGRESS_BAR_MAXIMUM));
        this.getProgressBar().setValue(0);
        
        this.setMessageArea(new JTextArea(this.MESSAGE_AREA_ROWS, this.MESSAGE_AREA_COLUMNS));
        this.getMessageArea().setMargin(new Insets(this.PANEL_INSET, this.PANEL_INSET, this.PANEL_INSET, this.PANEL_INSET));
        this.getMessageArea().setEditable(false);

        this.setUserInputPanel(new JPanel());
        this.add(new JScrollPane(this.getMessageArea()),BorderLayout.CENTER);
        
       
        
        

        
        
        
        this.setInputField(new JTextField(20));
		this.getInputField().setText("Enter your clue");
		
		this.getUserInputPanel().add(this.getInputField());
		this.getUserInputPanel().add(this.getSubmitClueButton());
		
		this.add(this.getUserInputPanel(), BorderLayout.NORTH);
		
		this.add(this.getProgressBar(), BorderLayout.SOUTH);
		
        
        this.setBorder(BorderFactory.createEmptyBorder(this.BORDER_TOP, this.BORDER_LEFT, this.BORDER_BOTTOM, this.BORDER_RIGHT));
    }
}
