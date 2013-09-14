package app;

import java.util.ArrayList;

import exception.NoSolutionsException;
import framework.Clue;
import framework.ClueSolver;
import framework.Solution;

/**
 * @author Ben Griffiths
 * ClueSolverImpl
 * An implementation of the ClueSolver interface, which removes proposed solutions that contain a language tag other than @en
 * @implements ClueSolver
 */
public class ClueSolverImpl implements ClueSolver {
	private final int LANGUAGE_TAG_LENGTH = 3;
	private final String LANGUAGE_TAG = "@";
	private final String LANG = "@en";
	
	/**
	 * filterOutNonEnglishSolutions
	 * @param proposedSolutions - an ArrayList of Solution objects to be filtered
	 * @return the ArrayList of Solution objects with any Solutions whose solution text contains a non-English language tag removed
	 */
	private ArrayList<Solution> filterOutNonEnglishSolutions(ArrayList<Solution> proposedSolutions) {
		ArrayList<Solution> filteredSolutions = new ArrayList<Solution>();
		for(int i = 0; i < proposedSolutions.size(); i++) {
			Solution thisSolution = proposedSolutions.get(i);
			String solutionText = thisSolution.getSolutionText();
			int positionOfLanguageTag = solutionText.length() - LANGUAGE_TAG_LENGTH;
			if(solutionText.length() > LANGUAGE_TAG_LENGTH) {
				if(solutionText.substring(positionOfLanguageTag, positionOfLanguageTag + 1).equals(LANGUAGE_TAG) 
					&& !solutionText.substring(positionOfLanguageTag + 1, solutionText.length()).equals(this.LANG))
						continue; // non-English language, so filter it out
			}
			filteredSolutions.add(thisSolution);
		}
		return filteredSolutions;
	}
	
	/**
	 * getSolutions
	 * @Override framework.ClueSolver.getSolutions
	 */
	@Override
	public ArrayList<Solution> getSolutions(Clue clue, ArrayList<Solution> proposedSolutions) throws NoSolutionsException {
		if(proposedSolutions == null)
			throw new NoSolutionsException("No solutions found for this clue");
		proposedSolutions = filterOutNonEnglishSolutions(proposedSolutions);
		
		ArrayList<Solution> acceptedSolutions = new ArrayList<Solution>();
		for(Solution proposedSolution : proposedSolutions) {
			if(!acceptedSolutions.contains(proposedSolution) && clue.matchesStructure(proposedSolution))
				acceptedSolutions.add(proposedSolution);
		}
		return acceptedSolutions;
	}
}
