package exception;

/**
 * @author Ben Griffiths
 * NoSolutionsException
 * Represents an exception caused by a failure to find any candidate solutions to a clue
 * @extends java.lang.Exception
 */
@SuppressWarnings("serial")
public class NoSolutionsException extends Exception {
	public NoSolutionsException() {
		super();
	}
	public NoSolutionsException(String message) {
		super(message);
	}
}
