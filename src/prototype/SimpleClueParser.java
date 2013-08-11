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
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) EntityRecogniser entityRecogniser;
	
	
	public SimpleClueParser(Clue clue, Model model) {
		this.setClue(clue);
		this.setModel(model);
		this.setStringVariations();
		this.setEntityRecogniser(new SimpleEntityRecogniser(clue, model));
	}
	

	@Override
	public void parse() {
		ArrayList<Selector> selectorVariations = new ArrayList<Selector>();
		
		
		
		Property predicate = Pop.memberOf; // this.getModel().getProperty("http://www.griffithsben.com/ontologies/pop.owl#hasMember");
		Resource object = this.getModel().getResource("http://dbpedia.org/resource/The_Beatles");
		
		Selector selector = new SimpleSelector(null, predicate, object);
		selectorVariations.add(selector);
		this.getClue().setSelectorVariations(selectorVariations);
	}

	/**
	 * setStringVariations - breaks down the source clue into variations of the original String, and sets
	 * the Clue objects string variations
	 */
	private void setStringVariations() {
		String sourceClue = this.getClue().getSourceClue();
		String[] sourceClueFragments = sourceClue.split(" ");
	}
}
