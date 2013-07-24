package experiments;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class ClueSolver {
	private final String UNKNOWN = "?";
	private Clue clue;
	private Model model;
	
	public ClueSolver() {
		this.setClue(null);
		this.setModel(null);
	}
	
	public ClueSolver(Clue clue, Model model) {
		this.setClue(clue);
		this.setModel(model);
	}
	
	public ClueSolver(String clue, Model model) {
		this.setModel(model);
		this.setClue(new Clue(null, null, null));
		this.setClueStatement(clue);
	}

	public void outputSolutionTriples() {
		System.out.println("Candidate triples:");
		StmtIterator iterator = this.getModel().listStatements(
								new SimpleSelector(this.getClue().getSubject(), this.getClue().getPredicate(), 
										(RDFNode) this.getClue().getObject()));
		if(!iterator.hasNext())
			System.out.println("No triples in the model match this clue");
		while(iterator.hasNext()) {
			 Statement statement = iterator.nextStatement(); // get the next statement
			 Resource subject = statement.getSubject(); // get the subject of the statement
			 Property predicate = statement.getPredicate(); // get the predicate
			 RDFNode object = statement.getObject(); // get the object
			 /*Since the object of a statement can be either a resource or a literal, the getObject() method returns 
			  * an object typed as RDFNode, which is a common superclass of both Resource and Literal.
			  */
			 System.out.print(subject.toString());
			 System.out.print(" " + predicate.toString() + " ");
			 if(object instanceof Resource)
				 System.out.print(object.toString());
			 else System.out.print(" \"" + object.toString() + "\""); // the object is a literal, so surround it with quotes
			 
			 System.out.println(" .");
		}
	}

	/**
	 * @return the clue
	 */
	public Clue getClue() {
		return clue;
	}

	/**
	 * @param clue the clue to set
	 */
	public void setClue(Clue clue) {
		this.clue = clue;
	}
	/**
	 * Set the clue by parsing a String argument
	 * @param clue - a String form of a clue, in the form "subject predicate object"
	 * @throws IllegalArgumentException if the String passed in does not consist of three words and two spaces
	 * An unknown is represented by the word "?", which will be transformed into a null argument to the SimpleSelector
	 */
	public void setClueStatement(String clue) throws IllegalArgumentException {
		String[] statementFragments = clue.split(" ");
		if(statementFragments.length != 3)
			throw new IllegalArgumentException(clue);
		for(int i = 0; i < statementFragments.length; i++) {
			if(statementFragments[i].equals(UNKNOWN))
					statementFragments[i] = null;
		}
		Resource subject = (
				statementFragments[0] == null ? 
						null : 
						this.getModel().createResource(statementFragments[0]));
		this.getClue().setSubject(subject);
		Property predicate = (
				statementFragments[1] == null ? 
						null : 
						this.getModel().createProperty(statementFragments[1]));
		this.getClue().setPredicate(predicate);
		
		Resource object = (
				statementFragments[2] == null ? 
						null : 
						this.getModel().createResource(statementFragments[2]));
		this.getClue().setObject(object);
	}

	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}
}
