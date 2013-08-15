package app;

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
		UserInterface ui = new UserInterfaceImpl();
		ui.createAndShow();
	}

}
