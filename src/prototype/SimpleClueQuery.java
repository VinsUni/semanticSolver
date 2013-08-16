package prototype;

import java.util.ArrayList;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.PropertyNotFoundException;
import com.hp.hpl.jena.vocabulary.RDFS;

import framework.Clue;
import framework.ClueParser;
import framework.ClueQuery;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleClueQuery implements ClueQuery {
	@Setter(AccessLevel.PRIVATE) ArrayList<String> candidateSolutions;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) ClueParser clueParser;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) InfModel infModel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) OntModel ontModel;
	
	
	public SimpleClueQuery(Clue clue, InfModel model) {
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
			String label;
			StmtIterator iterator = this.getOntModel().listStatements(selector);
			while(iterator.hasNext()) {
				Statement statement = iterator.nextStatement();
				Resource resource;
				if(selector.getSubject() == null) {
					resource = statement.getSubject(); // the candidate Selector was intended to select subjects matching a pattern of p+o
					/* duplicated code */
					StmtIterator iter = resource.listProperties(RDFS.label);
					while(iter.hasNext()) {
						Statement s = iter.nextStatement();
						label = s.getObject().toString();
						if(!candidateSolutions.contains(label))
							candidateSolutions.add(label);
					}
				}
				else {
					if(statement.getObject().isResource()) {
						resource = (Resource)statement.getObject(); // the candidate Selector was intended to select objects matching a pattern of s+p
						try {
							/* duplicated code */
							StmtIterator iter = resource.listProperties(RDFS.label);
							while(iter.hasNext()) {
								Statement s = iter.nextStatement();
								label = s.getObject().toString();
								if(!candidateSolutions.contains(label))
									candidateSolutions.add(label);
							}
						}
						catch(PropertyNotFoundException e) {
							// this resource has no label, so ignore it.
						}
					}
					else { // the resource matched is a literal value
						String literal = statement.getObject().toString();
						if(!candidateSolutions.contains(literal))
							candidateSolutions.add(literal);
						continue;
					}
				}
			}
		}
		return candidateSolutions;
	}
}
