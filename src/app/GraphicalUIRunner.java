package app;

import javax.swing.SwingUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import framework.UserInterface;

/**
 * @author Ben Griffiths
 * GraphicalUIRunner
 * Contains the application's main method, which configures the logging system and then creates the graphical user interface
 * on the Event Dispatch Thread
 */
public class GraphicalUIRunner {
	public static void main(String[] args) {
		/* log4j logging configuration */
		Logger.getRootLogger().setLevel(Level.INFO);
		PropertyConfigurator.configure("log4j.properties");
		/* Create the graphical user interface on the Event Dispatch thread */
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UserInterface ui = new GraphicalUserInterface();
				ui.createAndShow();
			}
		});
	}
}