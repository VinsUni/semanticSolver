package prototype;

import java.io.Console;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleUserInterface implements UserInterface {
	private final String EXIT_REQUEST = "EXIT";

	@Override
	public void createAndShow() {
		String userResponse = "";
		Console console = System.console();
		while(!userResponse.equals(EXIT_REQUEST)) {
			console.readLine("Please enter a clue: ");
			System.out.println(userResponse);
		}
		
		
	}

}
