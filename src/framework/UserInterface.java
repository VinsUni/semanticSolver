/**
 * 
 */
package framework;

import app.DisplayPanel;

/**
 * @author Ben Griffiths
 *
 */
public interface UserInterface {
	public void createAndShow();
	public void updateResults(String resultsMessage);
	public DisplayPanel getDisplayPanel();
}
