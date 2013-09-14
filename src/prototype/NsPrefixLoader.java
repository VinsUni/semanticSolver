package prototype;

import java.io.InputStream;
import java.util.Scanner;

import com.hp.hpl.jena.rdf.model.Model;

public class NsPrefixLoader {
	public Model model;
	private final String STANDARD_PREFIXES = "standardNsPrefixes.txt";
	
	
	public NsPrefixLoader(Model model) {
		this.setModel(model);
	}
	
	private Model getModel() {
		return this.model;
	}
	
	private void setModel(Model model) {
		this.model = model;	
	}

	/**
	 * Load the file containing standard namespace prefixes from http://dbpedia.org/sparql?nsdecl and apply each
	 * prefix to the model member
	 */
	public void loadStandardPrefixes() {
        ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(STANDARD_PREFIXES);
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
