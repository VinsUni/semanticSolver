package prototype;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleQuery implements Query {
	@Setter(AccessLevel.PRIVATE) ArrayList<String> candidateSolutions;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) String clue;
	
	public SimpleQuery(String clue) {
		this.setClue(clue);
	}

	@Override
	public ArrayList<String> getCandidateSolutions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBestSolution() {
		// TODO Auto-generated method stub
		return null;
	}
}
