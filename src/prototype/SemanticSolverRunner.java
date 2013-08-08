package prototype;

/**
 * @author Ben Griffiths
 *
 */
public class SemanticSolverRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleModelLoader modelLoader = new SimpleModelLoader("Lombok is alive");
		modelLoader.go();

	}

}
