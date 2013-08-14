/**
 * 
 */
package prototype;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.rdf.model.InfModel;

import framework.Clue;
import framework.ClueParser;
import framework.Query;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleSparqlQuery implements Query {
	@Setter(AccessLevel.PRIVATE) ArrayList<String> candidateSolutions;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) ClueParser clueParser;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) InfModel model;
	
	public SimpleSparqlQuery(Clue clue, InfModel model) {
		this.setClue(clue);
		this.setClueParser(new SimpleClueParser(clue, model));
		this.setModel(model);
	}

	@Override
	public ArrayList<String> getCandidateSolutions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Clue getClue() {
		// TODO Auto-generated method stub
		return null;
	}

}
