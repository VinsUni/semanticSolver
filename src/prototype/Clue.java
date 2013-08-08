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
public class Clue {
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) String stringRepresentation;
	
	public Clue(String clueAsString) {
		this.setStringRepresentation(clueAsString);
	}

}
