/**
 * 
 */
package app;

import java.util.ArrayList;

import exception.NoSolutionsException;
import framework.Clue;
import framework.ClueSolver;
import framework.Solution;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 *
 */
public class ClueSolverImpl implements ClueSolver {
	private final int LANGUAGE_TAG_LENGTH = 3;
	private final String LANGUAGE_TAG = "@";
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private String bestSolutionText;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Solution bestSolution;
	
	/**
	 * getSolutions - see framework.ClueSolver
	 */
	@Override
	public ArrayList<Solution> getSolutions(Clue clue, ArrayList<Solution> proposedSolutions) throws NoSolutionsException {
		if(proposedSolutions == null)
			throw new NoSolutionsException("No solutions found for this clue");
		proposedSolutions = filterOutNonEnglishSolutions(proposedSolutions);
		
		ArrayList<Solution> acceptedSolutions = new ArrayList<Solution>();
		for(Solution proposedSolution : proposedSolutions) {
			String solutionText = proposedSolution.getSolutionText();
			if(!acceptedSolutions.contains(solutionText) && isWellFormedSolution(solutionText) 
					&& clue.matchesStructure(proposedSolution))
				acceptedSolutions.add(proposedSolution);
		}
		
		if(acceptedSolutions.size() > 0)
			this.setBestSolution(acceptedSolutions.get(0));
		
		return acceptedSolutions;
	}
	
	/**
	 * For now, it just returns all of the proposedSolutions and sets the first one as the best solution,
	 * but it will need to screen out those that do not solve the clue
	 * (e.g. due to being the wrong number of words/wrong length 
	 */
	@Override
	public ArrayList<String> getCandidateSolutions(Clue clue, ArrayList<String> proposedSolutions) {
		proposedSolutions = this.filterByLanguage(proposedSolutions);
		ArrayList<String> solutions = new ArrayList<String>();
		for(String proposedSolution : proposedSolutions) {
			Solution parsedSolution = new SolutionImpl(proposedSolution);
			String solutionText = parsedSolution.getSolutionText();
			if(!solutions.contains(solutionText) && isWellFormedSolution(solutionText) && clue.matchesStructure(parsedSolution))
				solutions.add(solutionText);
		}
		
		if(proposedSolutions.size() > 0)
			this.setBestSolutionText(proposedSolutions.get(0));
		
		return solutions;
	}
	
	
	/**
	 * PRETTY MUCH DUPLICATED IN THE filterByLanguage METHOD BELOW
	 * @param proposedSolutions
	 * @return
	 */
	private ArrayList<Solution> filterOutNonEnglishSolutions(ArrayList<Solution> proposedSolutions) {
		ArrayList<Solution> filteredSolutions = new ArrayList<Solution>();
		for(int i = 0; i < proposedSolutions.size(); i++) {
			Solution thisSolution = proposedSolutions.get(i);
			String solutionText = thisSolution.getSolutionText();
			int positionOfLanguageTag = solutionText.length() - LANGUAGE_TAG_LENGTH;
			if(solutionText.length() > LANGUAGE_TAG_LENGTH) {
				if(solutionText.substring(positionOfLanguageTag, positionOfLanguageTag + 1).equals(LANGUAGE_TAG) 
					&& !solutionText.substring(positionOfLanguageTag + 1, solutionText.length()).equals("en"))
						continue; // non-English language, so filter it out
			}
			filteredSolutions.add(thisSolution);
		}
		return filteredSolutions;
	}

	
	
	/*
	 * THIS CODE IS DUPLICATED IN THE SIMPLEENTITYRECOGNISER CLASS - REFACTOR IT OUT SOMEWHERE?
	 */
	private ArrayList<String> filterByLanguage(ArrayList<String> proposedSolutions) {
		ArrayList<String> filteredSolutions = new ArrayList<String>();
		for(int i = 0; i < proposedSolutions.size(); i++) {
			String solutionText = proposedSolutions.get(i);
			int positionOfLanguageTag = solutionText.length() - LANGUAGE_TAG_LENGTH;
			if(solutionText.length() > LANGUAGE_TAG_LENGTH) {
				if(solutionText.substring(positionOfLanguageTag, positionOfLanguageTag + 1).equals(LANGUAGE_TAG) 
					&& !solutionText.substring(positionOfLanguageTag + 1, solutionText.length()).equals("en"))
						continue; // non-English language, so filter it out
			}
			filteredSolutions.add(solutionText);
		}
		return filteredSolutions;
	}

	/**
	 * Need to make this much more sophisticated... (!)
	 * @param solutionText
	 * @return
	 */
	private boolean isWellFormedSolution(String solutionText) {
		return true;
	}

	@Override
	public String getBestSolution(Clue clue) {
		return this.getBestSolutionText();
	}
}
