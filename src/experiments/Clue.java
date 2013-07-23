package experiments;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class Clue {
	private Resource subject;
	private Property predicate;
	private RDFNode object;
	
	public Clue(Resource subject, Property predicate, RDFNode object) {
		this.setSubject(subject);
		this.setPredicate(predicate);
		this.setObject(object);
	}

	public Resource getSubject() {
		return subject;
	}

	public void setSubject(Resource subject) {
		this.subject = subject;
	}

	public Property getPredicate() {
		return predicate;
	}

	public void setPredicate(Property predicate) {
		this.predicate = predicate;
	}

	public RDFNode getObject() {
		return object;
	}

	public void setObject(RDFNode object) {
		this.object = object;
	}
	
	

}
