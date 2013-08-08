package experiments;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ModelStripper {

	/**
	 * This doesn't work how it is supposed to!
	 * @param args
	 */
	public static void main(String[] args) {
		
		/* log4j logging configuration */
		Logger.getRootLogger().setLevel(Level.INFO);
		PropertyConfigurator.configure("log4j.properties");
		
		Model model = ModelFactory.createDefaultModel();
		Model strippedModel = ModelFactory.createDefaultModel();
		
		String fileName = "popv3.owl";
		
		model = FileManager.get().loadModel(fileName);
		
		
		NsPrefixLoader prefixLoader = new NsPrefixLoader(model);
		prefixLoader.loadStandardPrefixes();
		
		Resource keeper = model.createResource("http://www.griffithsben.com/ontologies/pop.owl#keeper");
		
		strippedModel = model.query(
				new SimpleSelector(null, RDFS.subClassOf, keeper)); // get all statements of form x subClassOf pop:keeper
		
		ResIterator resIterator = strippedModel.listSubjects(); // get all subjects of those statements

		while(resIterator.hasNext()) {
			Resource nextKeeper = resIterator.nextResource();
			/* list all statements from the full model where this subclass of pop:keeper is the subject... */
			StmtIterator sIterator = model.listStatements(new SimpleSelector(nextKeeper, null , (RDFNode)null));
			while(sIterator.hasNext())
				strippedModel.add(sIterator.nextStatement()); // ... and add them to the stripped model
			/* Now do the same where the keeper is the object... */
			sIterator = model.listStatements(new SimpleSelector(null, null , (RDFNode)nextKeeper));
			while(sIterator.hasNext())
				strippedModel.add(sIterator.nextStatement());
			
		}
		
		// Now, write the model out to a file in RDF/XML-ABBREV format:
		String output = "data\\popStripped.owl";
		try {
			FileOutputStream outFile = new FileOutputStream(output);
			strippedModel.write(outFile, "RDF/XML-ABBREV");
			outFile.close();
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		

	}

}
