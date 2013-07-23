package experiments;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class ClueSolver {
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

	public void outputSolutionTriples() {
		System.out.println("Candidate triples:");
		StmtIterator iterator = this.getModel().listStatements(
								new SimpleSelector(this.getClue().getSubject(), this.getClue().getPredicate(), 
										(RDFNode) this.getClue().getObject()));
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
