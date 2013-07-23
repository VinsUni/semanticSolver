/**
 * 
 */
package experiments;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

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
		
		// instantiate an input stream and read the file into the model
		InputStream in;
		try {
			in = new FileInputStream(modelFileName);
			
			String lang = "RDF/XML";
			
			model.read(new InputStreamReader(in), "", lang);
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
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

	}

}
