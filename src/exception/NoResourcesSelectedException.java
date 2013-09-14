package exception;

/**
 * @author Ben Griffiths
 * NoResourcesSelectedException
 * Represents an exception caused by no entities being recognised in the text of a clue
 * @extends java.lang.Exception
 */
@SuppressWarnings("serial")
public class NoResourcesSelectedException extends Exception {
	public NoResourcesSelectedException() {
		super();
	}
	public NoResourcesSelectedException(String message) {
		super(message);
	}
}
