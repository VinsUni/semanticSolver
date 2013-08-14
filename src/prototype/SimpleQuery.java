package prototype;

import java.util.ArrayList;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

import framework.Clue;
import framework.ClueParser;
import framework.Query;

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
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) InfModel infModel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) OntModel ontModel;
	
	
	public SimpleQuery(Clue clue, InfModel model) {
		this.setClue(clue);
		this.setClueParser(new SimpleClueParser(clue, model));
		this.setInfModel(model);
		this.setOntModel(ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF, this.getInfModel()));
	}

	@Override
	public ArrayList<String> getCandidateSolutions() {
		ArrayList<String> candidateSolutions = new ArrayList<String>();
		clueParser.parse(); // THE CLUE PARSER SHOULD PERHAPS BE A FIELD IN THE CLUE CLASS RATHER THAN THIS ONE?
		ArrayList<Selector> selectorVariations = clue.getSelectorVariations();
		
		for(Selector selector : selectorVariations) {
			StmtIterator iterator = this.getOntModel().listStatements(selector);
			while(iterator.hasNext()) {
				Statement statement = iterator.nextStatement();
				Resource resource;
				if(selector.getSubject() == null)
					resource = statement.getSubject(); // the candidate Selector was intended to select subjects matching a pattern of p+o
				else {
					if(statement.getObject().isResource())
						resource = (Resource)statement.getObject(); // the candidate Selector was intended to select objects matching a pattern of s+p
					else { // the resource matched is a literal value
						String literal = statement.getObject().toString();
						if(!candidateSolutions.contains(literal))
							candidateSolutions.add(literal);
						continue;
					}
				}
				
				StmtIterator sols = this.getOntModel().listStatements(new SimpleSelector(resource, RDFS.label, (RDFNode) null));
				while(sols.hasNext()) {
					Statement stmt = sols.nextStatement();
					String label = stmt.getObject().toString();
					if(!candidateSolutions.contains(label))
						candidateSolutions.add(label);
				}
			}
		}
		return candidateSolutions;
	}
}
