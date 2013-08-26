/**
 * 
 */
package app;

import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import framework.Clue;
import framework.Solution;
import framework.SolutionScorer;

/**
 * @author Ben Griffiths
 *
 */
public class SolutionScorerImpl implements SolutionScorer {
	
	@Override
	public double score(Solution solution) {
		
		/* Start by getting the values of RDF:type for the solutionResource
		 * Then check my local ontology for class types with labels that match fragments of the solution text
		 * 
		 * Then...
		 * 
		 */
		
		/* Find values of RDF:type for the solutionResource and add these resources to a list */
		ArrayList<Resource> solutionResourceTypes = new ArrayList<Resource>();
		StmtIterator statementIterator = solution.getSolutionResource().listProperties(RDF.type);
		while(statementIterator.hasNext()) {
			Resource r = (Resource) statementIterator.nextStatement().getObject();
			solutionResourceTypes.add(r);
		}
		
		/* Find types within my ontology that match fragments of the clue text.
		 * I will need to add these to a list of objects that allow me to store together the particular fragment of the
		 * text and the type from my ontology that it has matched. For now, I will lazily keep these in two parallel lists,
		 * but I REALLY MUST REFACTOR THIS
		 */
		ArrayList<String> recognisedClueFragments = new ArrayList<String>(); // TWO PARALLEL LISTS
		ArrayList<Resource> recognisedClueResourceTypes = new ArrayList<Resource>();
		
		/* Next, I need to get hold of the complete list of clue fragments... */
		
		return distance(solution.getSolutionResource(), solution.getClueResource());
	}
	
	private double distance(Resource firstResource, Resource secondResource) {
		
		double numberOfLinks = this.countLinks(firstResource, secondResource);
		
		double distance = (1.0 / (1.0 + numberOfLinks));
		
		return distance;
	}

	private double countLinks(Resource firstResource, Resource secondResource) {
		// TODO Auto-generated method stub
		return 3.0;
	}
	

}
