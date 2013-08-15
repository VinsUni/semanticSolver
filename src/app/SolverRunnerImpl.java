package app;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import framework.UserInterface;

/**
 * @author Ben Griffiths
 *
 */
public class SolverRunnerImpl {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/* log4j logging configuration */
		Logger.getRootLogger().setLevel(Level.INFO);
		PropertyConfigurator.configure("log4j.properties");
		
		UserInterface ui = new UserInterfaceImpl();
		ui.createAndShow();
	}

}
