package app;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;

import framework.remotePrototype.Pop;

/**
 * CandidateSelector
 * A simple extension of Jena's built-in implementation of the Selector interface, this class is used as a convenient method
 * of selecting all Statements in a model in which the predicate is Pop.relationalProperty
 * @author Ben Griffiths
 * @extends com.hp.hpl.jena.rdf.model.SimpleSelector
 */
public class CandidateSelector extends SimpleSelector {
	
	/**
	 * Default constructor. Constructs a new CandidateSelector with the specified arguments
	 * @param subject - the subject to be matched by the CandidateSelector
	 * @param predicate - the predicate to be matched by the CandidateSelector
	 * @param object - the object to be matched by the CandidateSelector
	 */
	public CandidateSelector(Resource subject, Property predicate, RDFNode object) {
		super(subject, predicate, object);
	}
	
	/**
	 * selects - returns true if the statement should be selected by this CandidateSelector, otherwise false.
	 * See com.hp.hpl.jena.rdf.model.SimpleSelecto;.
	 * @argument statement - the statement to be tested
	 * @return false for all statements whose predicate is a sub-property of pop:relationalProperty, and true for all others
	 */
	@Override
	public boolean selects(Statement statement) {
		Property predicate = statement.getPredicate();

		if(predicate.equals(Pop.relationalProperty))
			return false;
		return true;
	}
	
	/**
	 * isSimple - used by the Jena framework to ascertain whether or not the selects statement has been overriden for this 
	 * implementation of the Selector interface. See com.hp.hpl.jena.rdf.model.SimpleSelector.
	 * @return false, since the selects method has been overwritten.
	 * 
	 */
	@Override
	public boolean isSimple() {
		return false;
	}
}
