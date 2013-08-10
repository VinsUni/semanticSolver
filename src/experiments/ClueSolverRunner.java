/**
 * 
 */
package experiments;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

/**
 * @author Ben
 *
 */
public class ClueSolverRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/* log4j logging configuration */
		Logger.getRootLogger().setLevel(Level.INFO);
		PropertyConfigurator.configure("log4j.properties");
		
		ClueSolver clueSolver;
		Clue clue;
		
		Model model = ModelFactory.createDefaultModel();
		
		
		String modelFileName = "mergedTestDataSet.xml";
		
		// Read the file into the model
		model = FileManager.get().loadModel(modelFileName);
		
		// Find everything where the artist is Velvet Revolver
		Resource object = model.createResource("http://dbpedia.org/resource/Velvet_Revolver");
		Property predicate = model.createProperty("http://dbpedia.org/ontology/artist");
		
		clue = new Clue(null, predicate, object);
		
		clueSolver = new ClueSolver(clue, model);
		
		clueSolver.outputSolutionTriples();
		
		// Find everything that has genre Hip Hop
		System.out.println();
		predicate = model.createProperty("http://dbpedia.org/ontology/genre");
		object = model.createResource("http://dbpedia.org/resource/Hip_hop");
		clueSolver.setClue(new Clue(null, predicate, object));
		clueSolver.outputSolutionTriples();
		
		
		
		// Find all resources written by John Lennon in testDatasetExtended.xml
		System.out.println();
		Model newModel = ModelFactory.createDefaultModel();
		
		modelFileName = "testDatasetExtended.xml";
		
		// read the file into the model
		newModel = FileManager.get().loadModel(modelFileName);

		
		object = newModel.createResource("http://dbpedia.org/resource/John_Lennon");
		predicate = newModel.createProperty("http://dbpedia.org/ontology/writer");
		
		clueSolver.getClue().setSubject(null);
		clueSolver.getClue().setPredicate(predicate);
		clueSolver.getClue().setObject(object);
		clueSolver.setModel(newModel);
		clueSolver.outputSolutionTriples();
		
		
		//... and where The_Beatles is the artist in testDatasetExtended.xml:
		System.out.println();
		object = newModel.createResource("http://dbpedia.org/resource/The_Beatles");
		predicate = newModel.createProperty("http://dbpedia.org/ontology/artist");
		clueSolver.getClue().setPredicate(predicate);
		clueSolver.getClue().setObject(object);
		clueSolver.outputSolutionTriples();
		
		//... and all triples where The_Beatles is object...
		System.out.println();
		clueSolver.getClue().setPredicate(null);
		clueSolver.outputSolutionTriples();
		
		//... and where The_Beatles is subject (there are none):
		System.out.println();
		clueSolver.getClue().setSubject(object);
		clueSolver.getClue().setObject(null);
		clueSolver.outputSolutionTriples();
		
		//Triples where The_Beatles is artist using the alternative ClueSolver interface:
		String dbNs = "http://dbpedia.org/resource/";
		String db_owlNs = "http://dbpedia.org/ontology/";
		String clueAsString = "? " + db_owlNs + "artist " + dbNs + "The_Beatles";
		ClueSolver anotherClueSolver = new ClueSolver(clueAsString, newModel);
		anotherClueSolver.outputSolutionTriples();
	}

}