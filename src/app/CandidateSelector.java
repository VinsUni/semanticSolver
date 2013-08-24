/**
 * 
 */
package app;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;

import framework.remotePrototype.Pop;

/**
 * @author Ben Griffiths
 *
 */
public class CandidateSelector extends SimpleSelector {
	
	public CandidateSelector(Resource subject, Property predicate, RDFNode object) {
		super(subject, predicate, object);
	}
	
	
	@Override
	public boolean selects(Statement s) {
		Property predicate = s.getPredicate();

		if(predicate.equals(Pop.relationalProperty))
			return false;
		return true;
	}
	
	@Override
	public boolean isSimple() {
		return false;
	}
}
