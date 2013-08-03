/**
 * 
 */
package extraction;

/**
 * @author Ben
 * Extracts a subset of data from DBpedia. The data extracted is a set of triples in which the supplied resource is either the subject,
 * the predicate, or the object
 */
public class ExtractionRunner {

	/**
	 * @param args - accepts a single String argument, which is the URL of a resource
	 */
	public static void main(String[] args) {
		if(args.length != 1)
			throw new IllegalArgumentException("Invalid argument to main method");
		String url = args[0];
		
		System.err.println("Resource: " + url );

	}

}
