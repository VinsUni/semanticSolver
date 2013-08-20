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

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

/**
 * @author Ben Griffiths
 *
 */
@SuppressWarnings("serial")
public class DisplayPanel extends JPanel implements ActionListener, PropertyChangeListener {
	private final int PANEL_INSET = 5;
	private final int MESSAGE_AREA_ROWS = 5;
	private final int MESSAGE_AREA_COLUMNS = 20;
	private final int PROGRESS_BAR_MAXIMUM = 100;
	private final int BORDER_LEFT = 20;
	private final int BORDER_TOP = 20;
	private final int BORDER_BOTTOM = 20;
	private final int BORDER_RIGHT = 20;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private JTextArea messageArea;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JPanel panel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JProgressBar progressBar;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JButton submitClueButton;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniserTask task;
	
    public DisplayPanel() {
        super(new BorderLayout());

        this.setSubmitClueButton(new JButton("Submit clue"));
        this.getSubmitClueButton().setActionCommand("start");
        this.getSubmitClueButton().addActionListener(this);

        this.setProgressBar(new JProgressBar(0, this.PROGRESS_BAR_MAXIMUM));
        this.getProgressBar().setValue(0);
        this.getProgressBar().setStringPainted(true);
        
        this.setMessageArea(new JTextArea(this.MESSAGE_AREA_ROWS, this.MESSAGE_AREA_COLUMNS));
        this.getMessageArea().setMargin(new Insets(this.PANEL_INSET, this.PANEL_INSET, this.PANEL_INSET, this.PANEL_INSET));
        this.getMessageArea().setEditable(false);

        this.setPanel(new JPanel());
        this.getPanel().add(this.getSubmitClueButton());
        this.getPanel().add(this.getProgressBar());

        this.add(panel, BorderLayout.PAGE_START);
        this.add(new JScrollPane(getMessageArea()), BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(this.BORDER_TOP, this.BORDER_LEFT, this.BORDER_BOTTOM, this.BORDER_RIGHT));
    }

    /**
     * Invoked when the user presses the start button.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        this.getSubmitClueButton().setEnabled(false);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        EntityRecogniserTask erTask = new EntityRecogniserTask(null, null, this);
        erTask.addPropertyChangeListener(this);
        erTask.execute();
    }

    /**
     * Invoked when task's progress property changes.
     */
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if ("progress" == propertyChangeEvent.getPropertyName()) {
            int progress = (Integer) propertyChangeEvent.getNewValue();
            this.getProgressBar().setValue(progress);
            this.getMessageArea().append(String.format(
                    "Completed %d%% of task.\n", getTask().getProgress()));
        } 
    }
}
