/**
 * 
 */
package exception;

/**
 * @author Ben Griffiths
 *
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
