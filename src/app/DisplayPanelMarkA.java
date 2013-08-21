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
public class DisplayPanelMarkA extends JPanel implements ActionListener, PropertyChangeListener {
	private final int PANEL_INSET = 5;
	private final int MESSAGE_AREA_ROWS = 5;
	private final int MESSAGE_AREA_COLUMNS = 20;
	private final int PROGRESS_BAR_MAXIMUM = 100;
	private final int BORDER_LEFT = 20;
	private final int BORDER_TOP = 20;
	private final int BORDER_BOTTOM = 20;
	private final int BORDER_RIGHT = 20;
	private final String ENTITY_RECOGNITION_IN_PROGRESS_MESSAGE = "Searching for known entities in the clue";
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private GUIMarkA uiFrame;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JTextField inputField;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JTextArea messageArea;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JPanel panel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JProgressBar progressBar;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JButton submitClueButton;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniserTask entityRecogniserTask;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Clue clue;
	
    public DisplayPanelMarkA(GUIMarkA uiFrame) {
        super(new BorderLayout());
        
        this.setUiFrame(uiFrame);

        this.setSubmitClueButton(new JButton("Submit clue"));
        this.getSubmitClueButton().setActionCommand("submitClue");
        this.getSubmitClueButton().addActionListener(this);

        this.setProgressBar(new JProgressBar(0, this.PROGRESS_BAR_MAXIMUM));
        this.getProgressBar().setValue(0);
        
        this.setMessageArea(new JTextArea(this.MESSAGE_AREA_ROWS, this.MESSAGE_AREA_COLUMNS));
        this.getMessageArea().setMargin(new Insets(this.PANEL_INSET, this.PANEL_INSET, this.PANEL_INSET, this.PANEL_INSET));
        this.getMessageArea().setEditable(false);

        this.setPanel(new JPanel());
        this.getPanel().add(this.getSubmitClueButton());
        this.getPanel().add(this.getProgressBar());

        this.add(panel, BorderLayout.PAGE_START);
        this.add(new JScrollPane(this.getMessageArea()), BorderLayout.CENTER);
        
        this.setInputField(new JTextField(20));
		this.getInputField().setText("Enter your clue");
		this.add(this.getInputField(), BorderLayout.SOUTH);
        
        this.setBorder(BorderFactory.createEmptyBorder(this.BORDER_TOP, this.BORDER_LEFT, this.BORDER_BOTTOM, this.BORDER_RIGHT));
    }

    /**
     * Invoked when the user presses the "Submit clue" button.
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.getSubmitClueButton().setEnabled(false);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
       
        
        String clueAsText = this.getInputField().getText();
        
        Clue clue = null;
		try {
			clue = new ClueImpl(clueAsText);
		} catch (InvalidClueException e) {
			this.getMessageArea().append("The clue \"" + clueAsText + "\" + " + " is invalid. Please try again\n");
			this.getSubmitClueButton().setEnabled(true);
	        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}
		
		this.setClue(clue);
        
		this.getProgressBar().setString(this.ENTITY_RECOGNITION_IN_PROGRESS_MESSAGE);
        this.getProgressBar().setStringPainted(true);
		
		
		final DisplayPanelMarkA THIS_PANEL = this;
	    Thread erThread = new Thread(new Runnable() {
		        public void run() {
		        	setEntityRecogniserTask(new EntityRecogniserTask(getClue(), THIS_PANEL));
		            getEntityRecogniserTask().addPropertyChangeListener(THIS_PANEL);
		            getEntityRecogniserTask().execute();
		        }
		    });
	    erThread.start();
        
        //this.getUiFrame().solveClue(clueAsText);
        
        this.getSubmitClueButton().setEnabled(true); // NEEDS TO BE DONE AFTER THE TASK IS FINISHED - i.e. in the GUI object, not here
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); // NEEDS TO BE DONE AFTER THE TASK IS FINISHED - i.e. in the GUI object, not here
    }

    /**
     * Invoked when task's progress property changes.
     */
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("progress")) {
            int progress = (Integer) propertyChangeEvent.getNewValue();
            this.getProgressBar().setValue(progress);
            if(this.getProgressBar().getValue() == 100)
            	this.getProgressBar().setStringPainted(false);
        } 
    }
}
