package prototype;

import java.util.ArrayList;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
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
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) InfModel model;
	
	public SimpleQuery(Clue clue, InfModel model) {
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
			OntModel ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF, this.getModel());
			StmtIterator iterator = ontologyModel.listStatements(selector);
			while(iterator.hasNext()) {
				Statement statement = iterator.nextStatement();
				Resource subject = statement.getSubject();
				StmtIterator sols = ontologyModel.listStatements(new SimpleSelector(subject, RDFS.label, (RDFNode) null));
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
