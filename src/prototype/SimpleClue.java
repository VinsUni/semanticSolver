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
	// solutionStructure of e.g. {2, 3} means the answer consists of a 2-letter word followed by a 3-letter word
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) int[] SolutionStructure;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) int numberOfWords;
	
	public SimpleClue(String clueAsString) {
		
		// Raw clue is in form "words making up clue [String representing solution structure]"
		String[] decomposedClue = clueAsString.split("[");
		
		this.setSourceClue(decomposedClue[0]);
		System.err.println("Clue text = " + this.getSourceClue()); // DEBUGGING
		
		String solutionStructureAsString = decomposedClue[1].substring(0, decomposedClue[1].length() - 1);
		System.err.println("Solution structure = " + solutionStructureAsString); // DEBUGGING
		
		this.parseSolutionStructure(solutionStructureAsString);
		this.setNumberOfWords(this.getSolutionStructure().length);
	}

	private void parseSolutionStructure(String solutionStructureAsString) {
		String[] parsedSolutionStructureAsString = solutionStructureAsString.split(", ");
		this.setSolutionStructure(new int[parsedSolutionStructureAsString.length]);
		for(int i = 0; i < parsedSolutionStructureAsString.length; i++)
			this.getSolutionStructure()[i] = Integer.parseInt(parsedSolutionStructureAsString[i]);	
	}
}
