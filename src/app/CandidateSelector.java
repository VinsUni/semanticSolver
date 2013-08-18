/**
 * 
 */
package app;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;

import framework.Pop;

/**
 * @author Ben Griffiths
 *
 */
public class CandidateSelector extends SimpleSelector {
	private final Property[] SUB_PROPERTIES_TO_TEST = 
		{Pop.artistOf, Pop.hasArtist, Pop.albumOf, Pop.hasAlbum, Pop.composerOf, Pop.hasComposer, Pop.compositionOf, 
		 Pop.hasComposition, Pop.genreOf, Pop.hasGenre, Pop.memberOf, Pop.hasMember, Pop.producerOf, Pop.hasProducer, 
		 Pop.recordLabelOf, Pop.hasRecordLabel};
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private InfModel infModel;
	
	public CandidateSelector(Resource subject, Property predicate, RDFNode object) {
		super(subject, predicate, object);
	}
	
	
	@Override
	public boolean selects(Statement s) {
		Property predicate = s.getPredicate();
		
		for(int i = 0; i < SUB_PROPERTIES_TO_TEST.length; i++) {
			System.err.println("Comparing property " + predicate.toString() + " with property " + SUB_PROPERTIES_TO_TEST[i].toString());
			if(predicate.equals(SUB_PROPERTIES_TO_TEST[i]))
				return true;
		}
		return false;
	}
	/*
	@Override
	public boolean test(Statement s) {
		return this.selects(s);
		
		Resource subject = s.getSubject();
		Property predicate = s.getPredicate();
		RDFNode object = s.getObject();
		
		for(int i = 0; i < SUB_PROPERTIES_TO_TEST.length; i++) {
			System.err.println("Comparing property " + predicate.toString() + " with property " + SUB_PROPERTIES_TO_TEST[i].toString());
			if(predicate.equals(SUB_PROPERTIES_TO_TEST[i])) {
				return true;
				
				Statement alternativeStatement = this.getInfModel().createStatement(subject, Pop.relationalProperty, object);
				if(super.test(alternativeStatement)) {
					System.err.println("Alternative statement returns true... for property: " + predicate.toString()); // *************DEBUGGING ****************
					return true;
				}
				
			}
		}
		return false;
		
	}
*/
	
	
	@Override
	public boolean isSimple() {
		return false;
	}
}
