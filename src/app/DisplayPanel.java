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

import framework.Clue;
import framework.EntityRecogniser;

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

        this.setProgressBar(new JProgressBar(0, 100));
        this.getProgressBar().setValue(0);
        this.getProgressBar().setStringPainted(true);

        this.setMessageArea(new JTextArea(5, 20));
        this.getMessageArea().setMargin(new Insets(5,5,5,5));
        this.getMessageArea().setEditable(false);

        this.setPanel(new JPanel());
        this.getPanel().add(this.getSubmitClueButton());
        this.getPanel().add(this.getProgressBar());

        this.add(panel, BorderLayout.PAGE_START);
        this.add(new JScrollPane(getMessageArea()), BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
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
