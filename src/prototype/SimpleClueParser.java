/**
 * 
 */
package prototype;

import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.vocabulary.RDF;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 *
 */
public class SimpleClueParser implements ClueParser {
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) Model model;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) Clue clue;
	
	
	public SimpleClueParser(Clue clue, Model model) {
		this.setClue(clue);
		this.setModel(model);
	}
	

	@Override
	public void parse() {
		ArrayList<Selector> selectorVariations = new ArrayList<Selector>();
		
		Property predicate = this.getModel().getProperty("http://www.griffithsben.com/ontologies/pop.owl#memberOf");
		Resource object = this.getModel().getResource("http://schema.org/CreativeWork");
		
		Selector selector = new SimpleSelector(null, RDF.type, object);
		selectorVariations.add(selector);
		this.getClue().setSelectorVariations(selectorVariations);
	}
}
