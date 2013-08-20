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
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JProgressBar progressBar;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private JButton submitClueButton;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniserTask task;
	
    public DisplayPanel() {
        super(new BorderLayout());

        //Create the demo's UI.
        setSubmitClueButton(new JButton("Submit clue"));
        getSubmitClueButton().setActionCommand("start");
        getSubmitClueButton().addActionListener(this);

        setProgressBar(new JProgressBar(0, 100));
        getProgressBar().setValue(0);
        getProgressBar().setStringPainted(true);

        setMessageArea(new JTextArea(5, 20));
        getMessageArea().setMargin(new Insets(5,5,5,5));
        getMessageArea().setEditable(false);

        JPanel panel = new JPanel();
        panel.add(getSubmitClueButton());
        panel.add(getProgressBar());

        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(getMessageArea()), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }

    /**
     * Invoked when the user presses the start button.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        getSubmitClueButton().setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        EntityRecogniserTask erTask = new EntityRecogniserTask(null, null, this);
        erTask.addPropertyChangeListener(this);
        erTask.execute();
    }

    /**
     * Invoked when task's progress property changes.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            getProgressBar().setValue(progress);
            getMessageArea().append(String.format(
                    "Completed %d%% of task.\n", getTask().getProgress()));
        } 
    }
}
