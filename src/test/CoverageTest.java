/**
 * 
 */
package test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import exception.InvalidClueException;

import app.ClueImpl;

import framework.Clue;
import framework.Solution;

/**
 * @author Ben Griffiths
 *
 */
public class CoverageTest {
	private final long NANOSECONDS_IN_ONE_SECOND = 1000000000;
	private static final String INVALID_CLUE_MARKER_TEXT = "INVALID_CLUE";
	private static ArrayList<String> cluesToSolve;
	private static ArrayList<int[]> solutionStructures;
	private static ArrayList<String> solutions;
	private Clue clue;
		
	@BeforeClass
	public static void Before() {
		
		cluesToSolve = new ArrayList<String>();
		solutions = new ArrayList<String>();
		solutionStructures = new ArrayList<int[]>();
		
		
		cluesToSolve.add("member of The Beatles");
		int[] structure1 = {4, 6};
		solutionStructures.add(structure1);
		solutions.add("John Lennon");
		
		cluesToSolve.add("Member of the beatles");
		int[] structure2 = {4, 9};
		solutionStructures.add(structure2);
		solutions.add("Paul McCartney");
		
		cluesToSolve.add("Firework singer");
		int[] structure3 = {4, 5};
		solutionStructures.add(structure3);
		solutions.add("Katy Perry");
		
		cluesToSolve.add("Singer Ives");
		int[] structure4 = {4};
		solutionStructures.add(structure4);
		solutions.add("Burl");
		
		cluesToSolve.add(INVALID_CLUE_MARKER_TEXT);
		int[] structure5 = {-1};
		solutionStructures.add(structure5);
		solutions.add("[clue is invalid]");
	}
	
	@Test
	public void basicCoverageTest() {	
				assertTrue(cluesToSolve.size() == solutions.size()); // check that a solution has been provided for each clue
				assertTrue(cluesToSolve.size() == solutionStructures.size()); // check that a solution structure has been provided too
				while(!cluesToSolve.isEmpty()) {
					long startTime = System.nanoTime();
					String clueAsString = cluesToSolve.get(0);
					int[] solutionStructure = solutionStructures.get(0);
					String solution = solutions.get(0);
					try {
						this.clue = new ClueImpl(clueAsString, solutionStructure);
					}
					catch(InvalidClueException e) {
						boolean clueIsInvalid = (clueAsString.equals(INVALID_CLUE_MARKER_TEXT));
						assertTrue("Invalid clue exception thrown for valid clue", clueIsInvalid);
						cluesToSolve.remove(clueAsString);
						solutionStructures.remove(solutionStructure);
						solutions.remove(solution);
						continue;
					}
					
					DummySemanticSolver semanticSolver = new DummySemanticSolver();
					
					ArrayList<Solution> foundSolutions = semanticSolver.solve(clue);
					
					ArrayList<String> solutionsAsStrings = new ArrayList<String>();
					
					for(Solution foundSolution : foundSolutions)
						solutionsAsStrings.add(foundSolution.getSolutionText());
					
					boolean correctSolutionInList = solutionsAsStrings.contains(solution);
					assertTrue("Failed to find correct solution (\"" + solution +
								"\") for the clue \"" + clueAsString +"\"", correctSolutionInList);
					long endTime = System.nanoTime();
					long durationInSecs = (endTime - startTime) / NANOSECONDS_IN_ONE_SECOND;
					System.err.println("Time taken for the clue \"" + clueAsString +"\": " + durationInSecs + " secs");
						
					cluesToSolve.remove(clueAsString);
					solutionStructures.remove(solutionStructure);
					solutions.remove(solution);
			}
	}	
}
