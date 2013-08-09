/**
 * 
 */
package prototype;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 * Represents a clue
 */
public class SimpleClue {
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) String stringRepresentation;
	
	public SimpleClue(String clueAsString) {
		this.setStringRepresentation(clueAsString);
	}

}
