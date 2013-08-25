package experiments;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class DatasetMerger {
	
	/**
	 * From <http://jena.apache.org/tutorials/rdf_api.html#ch-Operations on Models>:
	 * Jena provides three operations for manipulating Models as a whole. These are the common set operations of 
	 * union, intersection and difference. The union of two Models is the union of the sets of statements which 
	 * represent each Model. This is one of the key operations that the design of RDF supports. It enables data from 
	 * disparate data sources to be merged.
	 * 
	 * I am going to use the union operation to merge my two (abridged) datasets, triplesWhereObjectIsAMusicalWorkExtract.xml and
	 * triplesWhereSubjectIsAMusicalWorkExtract.xml
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		/* log4j logging configuration */
		Logger.getRootLogger().setLevel(Level.INFO);
		PropertyConfigurator.configure("log4j.properties");
		
		// create two empty models into which to read my two RDF/XML files
		Model model1 = ModelFactory.createDefaultModel();
		Model model2 = ModelFactory.createDefaultModel();
		
		/*
		String model1Url = "data\\mergedTestDatasetAug13h.xml";
		String model2Url = "data\\dbPediaExtracts\\MusicalArtistNamesEN.xml";
		*/
		
		ArrayList<String> modelsToBeMerged = new ArrayList<String>();
		String modelFileName;
		//modelFileName = "data\\dbPediaExtracts\\AlbumLabelsEN.xml";
		//modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\AlbumNamesEN.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\BandLabelsEN.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\BandNamesEN.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\MusicalArtistLabelsEN.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\MusicalArtistNamesEN.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\objectIsAMusicalArtistNoAbstractsNoComments.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\objectIsAnAlbumNoAbstractsNoComments.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\objectIsARecordLabelNoAbstractsNoComments.xml";
		modelsToBeMerged.add(modelFileName);
		/*modelFileName = "data\\dbPediaExtracts\\predicateIsDBalbum.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\predicateIsDBartist.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\predicateIsDBassociatedBand.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\predicateIsDBbandMember.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\predicateIsDBgenre.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\predicateIsDBppropArtist.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\predicateIsDBproducer.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\predicateIsDBrecordLabel.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\predicateIsDBwriter.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\subjectIsAnAlbumNoAbstractsNoCommentsWithLabels.xml";
		modelsToBeMerged.add(modelFileName);
		modelFileName = "data\\dbPediaExtracts\\subjectIsARecordLabelNoAbstractsNoComments.xml";
		modelsToBeMerged.add(modelFileName); */
		
		String modelToMergeInto = "data\\newMergedTestDataset.xml";
		model1 = FileManager.get().loadModel(modelToMergeInto);
		Model mergedModel = ModelFactory.createDefaultModel();
		while(modelsToBeMerged.size() > 0) {
			String thisModel = modelsToBeMerged.get(0);
			System.out.println("Loading two files to be merged...");
			// Load the two files into the two models
			
			model2 = FileManager.get().loadModel(thisModel);
			
			
			System.out.println("Performing merge...");
			mergedModel = model1.union(model2); // create a third model which is the union of the two models
			modelsToBeMerged.remove(thisModel);
			model1 = mergedModel;
			System.out.println(thisModel + " has been merged into the dataset.");
			
		}
		/* Define a prefix for one of the many namespaces used in the merged model
		mergedModel.setNsPrefix("dbpedia-owl","http://dbpedia.org/ontology/"); */
		
		NsPrefixLoader prefixLoader = new NsPrefixLoader(mergedModel);
		prefixLoader.loadStandardPrefixes();
		// Now, write the model out to a file in RDF/XML-ABBREV format:
		String fileName = "data\\newTestDataset.xml";
		try {
			System.out.println("Writing result out to disk...");
			FileOutputStream outFile = new FileOutputStream(fileName);
			mergedModel.write(outFile, "RDF/XML-ABBREV");
			outFile.close();
			System.out.println("Operation complete");
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	
		System.out.println("All models now merged");
	}
}