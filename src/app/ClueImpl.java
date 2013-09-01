/**
 * 
 */
package app;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Selector;

import exception.InvalidClueException;

import framework.Clue;
import framework.Solution;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 * Represents a clue
 */
public class ClueImpl implements Clue {
	private static Logger log = Logger.getLogger(ClueImpl.class);
	private final String[] WORDS_TO_EXCLUDE = {"the", "of", "that", "a"}; // a list of common words to exclude from consideration
	private final String APOSTROPHE_S_SEQUENCE = "'s"; // if present in a clue, requires further special transformation
	private final String FILL_IN_THE_BLANK_MARKER = "_";
	
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private ArrayList<String> clueFragments;
	
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private String sourceClue;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private ArrayList<String> clueVariations;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private ArrayList<Selector> selectorVariations;
	/* solutionStructure of e.g. {2, 3} means the answer consists of a 2-letter word followed by a 3-letter word */
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private int[] SolutionStructure;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private boolean fillInTheBlank; // true if the clue is a 'Fill in the blank' style clue

	/**
	 * Constructor - constructs a clue from a String representing the clue text and an int[] representing the solution structure
	 * @param clueText - the text of the clue
	 * @param solutionStructure - an array of integers representing the structure of the solution, with each element of the array
	 * representing a word, and the value of the element representing the number of letters in that array
	 * @throws InvalidClueException - if either the clue text or the structure of the solution is invalid
	 */
	public ClueImpl(String clueText, int[] solutionStructure) throws InvalidClueException {
		if(clueText == null || clueText.length() == 0)
			throw new InvalidClueException("Empty clue");
		if(solutionStructure == null || solutionStructure.length == 0)
			throw new InvalidClueException("Invalid specification of solution structure");
		for(int i = 0; i < solutionStructure.length; i++) {
			if(solutionStructure[i] < 1)
				throw new InvalidClueException("Invalid specification of solution structure");
		}
		
		if(clueText.contains(FILL_IN_THE_BLANK_MARKER))
			this.setFillInTheBlank(true); // fillInTheBlank flag must be set before calling stripQuotes
		
		clueText = stripQuotes(clueText);
		
		this.setSourceClue(clueText);
		
		
		this.setSolutionStructure(solutionStructure);
		
		this.setClueFragments(new ArrayList<String>());
		this.addClueFragments(clueText);
		
		/* Logging */
		log.debug("Clue text = " + this.getSourceClue());
		log.debug("Clue fragments generated:");
		for(String f : this.getClueFragments())
			log.debug(f);
	}
	
	private String stripQuotes(String clueText) {
		String clueTextWithoutQuotes = clueText.replace("\"", "");
		if(this.isFillInTheBlank()) {
			clueTextWithoutQuotes = clueTextWithoutQuotes.replace(this.APOSTROPHE_S_SEQUENCE, ""); // remove instances of " 's "
			clueTextWithoutQuotes = clueTextWithoutQuotes.replace("'", ""); // remove any remaining inverted commas
		}
		return clueTextWithoutQuotes;
	}

	/**
	 * matchesStructure - see framework.Clue
	 */
	@Override
	public boolean matchesStructure(Solution solution) {
		return (Arrays.equals(solution.getSolutionStructure(), this.getSolutionStructure())); // requires comparison of deep equality
	}
	
	/**
	 * getSolutionStructureAsString - see framework.Clue
	 */
	@Override
	public String getSolutionStructureAsString() {
		if(this.getSolutionStructure() == null || this.getSolutionStructure().length < 1)
			return "";
		String structure = "[" + this.getSolutionStructure()[0];
		for(int i = 1; i < this.getSolutionStructure().length; i++)
			structure += ", " + this.getSolutionStructure()[i];
		structure += "]";
		return structure;
	}
	
	
	
    private void addClueFragments(String clueText) {
		String[] wordsInClueText = clueText.split(" ");
		for(int i = 0; i < wordsInClueText.length; i++) {
			String thisWord = this.toProperCase(wordsInClueText[i]);
			if(!this.getClueFragments().contains(thisWord) && !excludedWord(thisWord)) {
				this.getClueFragments().add(thisWord);
				/* if the word ends with a comma or closing bracket, add the word without the comma/bracket as a fragment too */
				if(thisWord.length() > 1 && (thisWord.substring(thisWord.length() - 1, thisWord.length()).equals(",")
						|| thisWord.substring(thisWord.length() - 1, thisWord.length()).equals(")")))
					this.getClueFragments().add(thisWord.substring(0, thisWord.length() - 1));
				/* if the word begins with a (, add the word without the ( as a fragment too */
				if(thisWord.length() > 1 && thisWord.substring(0, 1).equals("("))
					this.getClueFragments().add(thisWord.substring(1, thisWord.length()));
			}
			for(int j = i + 1; j < wordsInClueText.length; j++) {
				thisWord = thisWord + " " + this.toProperCase(wordsInClueText[j]);
				if(!this.getClueFragments().contains(thisWord) && !excludedWord(thisWord)) {
					this.getClueFragments().add(thisWord);
					/* if the word ends with a comma or closing bracket, add the word without the comma/bracket as a fragment too */
					if(thisWord.length() > 1 && (thisWord.substring(thisWord.length() - 1, thisWord.length()).equals(",")
							|| thisWord.substring(thisWord.length() - 1, thisWord.length()).equals(")")))
						this.getClueFragments().add(thisWord.substring(0, thisWord.length() - 1));
					/* if the word begins with a (, add the word without the ( as a fragment too */
					if(thisWord.length() > 1 && thisWord.substring(0, 1).equals("("))
						this.getClueFragments().add(thisWord.substring(1, thisWord.length()));
				}
			}
		}

		if(clueText.contains(this.APOSTROPHE_S_SEQUENCE)) {
			String transformedClueText = clueText.replace(this.APOSTROPHE_S_SEQUENCE, "");
			this.addClueFragments(transformedClueText);
		}
	}

	private String toProperCase(String thisWord) {
		String thisWordInProperCase = thisWord.substring(0, 1).toUpperCase();
		if(thisWord.length() > 1) {
			int index = 1; // start at the second letter of the word
			while(index < thisWord.length()) {
				String nextCharacter = thisWord.substring(index, index + 1);
				thisWordInProperCase += nextCharacter;
				if((nextCharacter.equals(" ")) && (index < (thisWord.length() - 1))) {
					 index++; // the next character needs to be capitalised
					 nextCharacter = thisWord.substring(index, index + 1);
					 thisWordInProperCase += nextCharacter.toUpperCase();
				}
				index++;
			}
		}
		return thisWordInProperCase;
	}

	/**
	 * 
	 * @param wordToCheck
	 * @return true if wordToCheck is in the list of common words to be excluded
	 */
	private boolean excludedWord(String wordToCheck) {
		for(int i = 0; i < this.WORDS_TO_EXCLUDE.length; i++)
			if(toProperCase(WORDS_TO_EXCLUDE[i]).equals(wordToCheck))
				return true;
		return false;
	}
}
