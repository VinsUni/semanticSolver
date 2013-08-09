package prototype;

import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleQuery implements Query {
	@Setter(AccessLevel.PRIVATE) ArrayList<String> candidateSolutions;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) ClueParser clueParser;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) Model model;
	
	public SimpleQuery(Clue clue, Model model) {
		this.setClue(clue);
		this.setClueParser(new SimpleClueParser(clue, model));
		this.setModel(model);
	}

	@Override
	public ArrayList<String> getCandidateSolutions() {
		ArrayList<String> candidateSolutions = new ArrayList<String>();
		clueParser.parse();
		ArrayList<Selector> selectorVariations = clue.getSelectorVariations();
		
		
		
		
		/* At this point, I need to use a reasoner to infer additional triples from my dataset
		 * See http://jena.apache.org/documentation/inference/
		 */
		
		
		
		for(Selector selector : selectorVariations) {
			StmtIterator iterator = this.getModel().listStatements(selector);
			while(iterator.hasNext()) {
				Statement statement = iterator.nextStatement();
				Resource subject = statement.getSubject();
				StmtIterator sols = model.listStatements(new SimpleSelector(subject, RDFS.label, (RDFNode) null));
				while(sols.hasNext()) {
					Statement stmt = sols.nextStatement();
					candidateSolutions.add(stmt.getObject().toString());
				}
			}
		}
		return candidateSolutions;
	}

	@Override
	public String getBestSolution() {
		// TODO Auto-generated method stub
		return null;
	}
}
