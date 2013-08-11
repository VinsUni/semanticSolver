/**
 * 
 */
package exception;

/**
 * @author Ben Griffiths
 *
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
