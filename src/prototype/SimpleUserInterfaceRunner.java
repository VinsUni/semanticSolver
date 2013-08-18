package prototype;

import framework.prototype.UserInterface;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleUserInterfaceRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UserInterface ui = new SimpleUserInterface();
		ui.createAndShow();
	}

}
