/**
 * 
 */
package framework;

import app.DisplayPanel;
import app.NewDisplayPanel;

/**
 * @author Ben Griffiths
 *
 */
public interface UserInterface {
	public void createAndShow();
	public void updateResults(String resultsMessage);
	public DisplayPanel getDisplayPanel();
	public NewDisplayPanel getMainDisplayPanel();
}
