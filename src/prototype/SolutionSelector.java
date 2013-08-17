/**
 * 
 */
package prototype;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * @author Ben Griffiths
 * See http://jena.apache.org/documentation/javadoc/jena/com/hp/hpl/jena/rdf/model/SimpleSelector.html
 */
public class SolutionSelector extends SimpleSelector {
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private ArrayList<String> labels;
	
	public SolutionSelector(Resource subject, Property predicate, RDFNode object, ArrayList<String> labels) {
		super(subject, predicate, object);
		this.setLabels(labels);
	}
	
	/**
	 * The idea here is that perhaps I can set the labels member with the values of
	 * all of the labels of the matched resource, during this visit to the model, rather than having to query
	 * the model again to get the labels?
	 */
	@Override
	public boolean selects(Statement s) {
		if(super.selects(s)) {
			// set the labels somehow
			return true;
		}
		return false;
	}
}
