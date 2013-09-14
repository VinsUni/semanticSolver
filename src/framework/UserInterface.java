package framework;

import java.beans.PropertyChangeListener;

import app.DisplayPanel;

/**
 * @author Ben Griffiths
 *
 */
public interface UserInterface extends PropertyChangeListener {
	public void createAndShow();
	public void updateResults(String resultsMessage);
	public void updateProgressBarMessage(String message);
	
	public DisplayPanel getDisplayPanel();
	public void showNewClueOptions();
}
