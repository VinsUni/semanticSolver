/**
 * 
 */
package prototype;

import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.RDFSRuleReasonerFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

import experiments.NsPrefixLoader;
import framework.prototype.ModelLoader;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 *
 */
public class NewSimpleModelLoader implements ModelLoader {
	public static Map<String, Resource> subjectsInModel; // should NOT be static - refactor!
	public static Map<String, Property> propertiesInModel; // should NOT be static - refactor!
	public static Map<String, Resource> objectsInModel; // should NOT be static - refactor!
	
	private final int LANGUAGE_TAG_LENGTH = 3; // DUPLICATED IN SIMPLESOLUTION CLASS - REFACTOR OUT AS A STATIC CONSTANT?
	private final String LANGUAGE_TAG = "@"; // DUPLICATED IN SIMPLESOLUTION CLASS - REFACTOR OUT AS A STATIC CONSTANT?
	
	private final String ONTOLOGY_URI = "popv7.owl";
	private final String DATA_URI = "newTestDataset.xml";
	private final String OWL_FULL_URI = "http://www.w3.org/2002/07/owl#";
	@Setter(AccessLevel.PRIVATE) private InfModel infModel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private OntModelSpec ontologyModelSpec;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private OntModel ontModel;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Model data;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Model ontology;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Reasoner boundReasoner;
	
	
	private void setSubjects() {
			ResIterator subjects = this.getModel().listSubjects();
			while(subjects.hasNext()) {
				Resource thisSubject = subjects.nextResource();
				if(subjectsInModel.containsValue(thisSubject))
					continue;
				OntResource subject = this.getOntModel().getOntResource(thisSubject); // create an OntResource from the Resource object
				ExtendedIterator<RDFNode> labels = subject.listLabels(null); // list all values of RDFS:label for this resource
				while(labels.hasNext()) {
					String thisLabel = stripLanguageTag(labels.next().toString());
					subjectsInModel.put(thisLabel, thisSubject);
				}
			}
	}
	
	private void setProperties() {
		
		StmtIterator statementsInModel = this.getModel().listStatements();
		
		while(statementsInModel.hasNext()) {
			Property thisProperty = statementsInModel.nextStatement().getPredicate();
			if(propertiesInModel.containsValue(thisProperty))
				continue;
			String uri = thisProperty.getURI();
			if(uri == null)
				continue;
			OntProperty property = this.getOntModel().getOntProperty(uri); // create an OntProperty from the Property object
			if(property == null)
				continue;
			ExtendedIterator<RDFNode> labels = property.listLabels(null); // list all values of RDFS:label for this resource
			if(labels == null)
				continue;
			while(labels.hasNext()) {
				String thisLabel = stripLanguageTag(labels.next().toString());
				propertiesInModel.put(thisLabel,  thisProperty);
			}
		}
		
		
		ResIterator subjects = this.getModel().listSubjects();
		while(subjects.hasNext()) {
			Resource thisSubject = subjects.nextResource();
			if(subjectsInModel.containsKey(thisSubject))
				continue;
			OntResource subject = this.getOntModel().getOntResource(thisSubject); // create an OntResource from the Resource object
			ExtendedIterator<RDFNode> labels = subject.listLabels(null); // list all values of RDFS:label for this resource
			while(labels.hasNext()) {
				String thisLabel = stripLanguageTag(labels.next().toString());
				subjectsInModel.put(thisLabel, thisSubject);
			}
		}
	}
	
	private void setObjects() {
		
		NodeIterator objects = this.getModel().listObjects();
		while(objects.hasNext()) {
			RDFNode thisObject = objects.nextNode();
			if(thisObject.isResource()) { // We are only interested in objects that are resources
				Resource resource = (Resource)thisObject;
				if(objectsInModel.containsValue(resource))
					continue;
				OntResource object = this.getOntModel().getOntResource((Resource)thisObject); // create an OntResource from the RDFNode object
				ExtendedIterator<RDFNode> labels = object.listLabels(null); // list all values of RDFS:label for this resource
				while(labels.hasNext()) {
					String thisLabel = stripLanguageTag(labels.next().toString());
					objectsInModel.put(thisLabel,  object);
				}
			}
		}
	}
	
	/*
	 * THIS CODE IS DUPLICATED IN THE SIMPLESOLUTION CLASS - REFACTOR IT OUT SOMEWHERE?
	 */
	private String stripLanguageTag(String text) {
		int positionOfLanguageTag = text.length() - LANGUAGE_TAG_LENGTH;
		if(text.length() > LANGUAGE_TAG_LENGTH) {
			if(text.substring(positionOfLanguageTag, positionOfLanguageTag + 1).equals(LANGUAGE_TAG))
				return text.substring(0, positionOfLanguageTag);
		}
		return text;
	}
	
	
	@Override
	public InfModel getModel() {
		if(this.infModel != null)
			return this.infModel; // if the model has already been loaded, simply return it
		
		/* log4j logging configuration */
		Logger.getRootLogger().setLevel(Level.INFO);
		PropertyConfigurator.configure("log4j.properties");
		
		this.setOntologyModelSpec(OntModelSpec.RDFS_MEM_RDFS_INF);
		
		this.setData(FileManager.get().loadModel(DATA_URI)); // Read the data from DBPedia into a model
		this.setInfModel(ModelFactory.createInfModel(this.getOntologyModelSpec().getReasoner(), this.getData()));
		this.setOntology(FileManager.get().loadModel(ONTOLOGY_URI)); // read my pop ontology into another model
		
		Reasoner reasoner = RDFSRuleReasonerFactory.theInstance().create(null);
	    reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_FULL);
		
		
		this.setBoundReasoner(reasoner.bindSchema(this.getOntology()));
		
		this.infModel = ModelFactory.createInfModel(this.getBoundReasoner(), this.getData());
		
		// load standard prefixes into the model
		NsPrefixLoader prefixLoader = new NsPrefixLoader(this.infModel);
		prefixLoader.loadStandardPrefixes();
		
		this.setSubjects();
		this.setProperties();
		this.setObjects();
		
		return this.infModel;
	}
	
	
}