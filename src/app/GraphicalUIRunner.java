/**
 * 
 */
package app;

import javax.swing.SwingUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import framework.UserInterface;


/**
 * @author Ben Griffiths
 *
 */
public class GraphicalUIRunner {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				/* log4j logging configuration */
				Logger.getRootLogger().setLevel(Level.INFO);
				PropertyConfigurator.configure("log4j.properties");
				
				UserInterface ui = new GraphicalUserInterface();
				ui.createAndShow();
			}
		});
	}
}