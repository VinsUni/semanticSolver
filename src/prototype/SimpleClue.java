/**
 * 
 */
package prototype;

import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Selector;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 * Represents a clue
 */
public class SimpleClue implements Clue {
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) String sourceClue;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) ArrayList<String> clueVariations;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) ArrayList<Selector> selectorVariations;
	
	public SimpleClue(String clueAsString) {
		this.setSourceClue(clueAsString);
	}


}
