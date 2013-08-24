/**
 * 
 */
package exception;

/**
 * @author Ben Griffiths
 *
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
