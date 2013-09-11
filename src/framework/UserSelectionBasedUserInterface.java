/**
 * 
 */
package framework;

import java.util.ArrayList;

import java.beans.PropertyChangeListener;

import remotePrototype.RecognisedResource;

import app.DisplayPanel;

/**
 * @author Ben Griffiths
 *
 */
public interface UserSelectionBasedUserInterface extends PropertyChangeListener {
	public void createAndShow();
	public void updateResults(String resultsMessage);
	public void updateProgressBarMessage(String message);
	public void solveClue();
	public void findSolutions();
	public DisplayPanel getDisplayPanel();
	public void getChosenEntitiesFromUser(ArrayList<RecognisedResource> recognisedResources);
	public void showNewClueOptions();
}
