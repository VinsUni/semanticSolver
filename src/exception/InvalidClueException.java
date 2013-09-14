package exception;

/**
 * @author Ben Griffiths
 * InvalidClueException - represents an exception caused by an improperly specified clue
 * @extends java.lang.Exception
 */
@SuppressWarnings("serial")
public class InvalidClueException extends Exception {
	public InvalidClueException() {
		super();
	}
	public InvalidClueException(String message) {
		super(message);
	}
}
