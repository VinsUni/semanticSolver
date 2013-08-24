/**
 * 
 */
package framework;

import remotePrototype.DisplayPanel;
import app.NewDisplayPanel;

/**
 * @author Ben Griffiths
 *
 */
public interface UserInterface {
	public void createAndShow();
	public void updateResults(String resultsMessage);
	public NewDisplayPanel getMainDisplayPanel();
}
