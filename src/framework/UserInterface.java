package framework;

import java.beans.PropertyChangeListener;

import app.DisplayPanel;

/**
 * @author Ben Griffiths
 * UserInterface
 * Interface through which to communicate with a graphical user interface, to gather input from and present results to the user
 * @extends java.beans.PropertyChangeListener
 */
public interface UserInterface extends PropertyChangeListener {
	/**
	 * createAndShow - create the user interface and display it to the user. Must be called from the Event Dispatch Thread
	 */
	public void createAndShow();
	
	/**
	 * updateResults
	 * @param resultsMessage - a String to be presented to the user as the result of an operation
	 */
	public void updateResults(String resultsMessage);
	
	/**
	 * getDisplayPanel
	 * @return a reference to an instance of app.DisplayPanel, used as the main content pane of the graphical user interface
	 */
	public DisplayPanel getDisplayPanel();
	
	/**
	 * showNewClueOptions - presents the user with the display options necessary to input a new clue to be solved
	 */
	public void showNewClueOptions();
}
