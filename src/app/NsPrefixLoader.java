package app;

import java.io.InputStream;
import java.util.Scanner;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Ben Griffiths
 * NsPrefixLoader
 * Utility class used to load a set of standard namespace prefixes, as used on DBpedia, into an instance of com.hp.hpl.jena.rdf.model.Model
 */
public class NsPrefixLoader {
	private final String STANDARD_PREFIXES = "standardNsPrefixes.txt";
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Model model;
	
	/**
	 * Constructor - instantiate a new NsPrefixLoader with the given model
	 * @param model - an instance of com.hp.hpl.jena.rdf.model.Model into which prefixes are to be loaded
	 */
	public NsPrefixLoader(Model model) {
		this.setModel(model);
	}

	/**
	 * loadStandardPrefixes - loads the file containing standard namespace prefixes from http://dbpedia.org/sparql?nsdecl and applies each
	 * prefix to the model member
	 */
	public void loadStandardPrefixes() {
        ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(this.STANDARD_PREFIXES);
		String prefix, ns;

		Scanner scanner = new Scanner(inputStream);
		while (scanner.hasNext()) {
			prefix = scanner.nextLine();
			ns = scanner.nextLine();
			this.getModel().setNsPrefix(prefix, ns);
		}
		scanner.close();
	}
}
