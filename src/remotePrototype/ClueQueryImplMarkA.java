/**
 * 
 */
package remotePrototype;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import framework.Clue;
import framework.ClueQuery;
import framework.EntityRecogniser;
import framework.Solution;

/**
 * @author Ben Griffiths
 *
 */
public class ClueQueryImplMarkA implements ClueQuery {
	private final String ENDPOINT_URI = "http://dbpedia.org/sparql";
	private final String DBPEDIA_PREFIX_DECLARATION = "PREFIX dbpedia: <http://dbpedia.org/resource/>";
	private final String DBPEDIA_OWL_PREFIX_DECLARATION = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>";
	private final String RDFS_PREFIX_DECLARATION = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
	
	@Setter(AccessLevel.PRIVATE) private ArrayList<String> candidateSolutions;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniser entityRecogniser;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Query query;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private QueryExecution queryExecution;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PUBLIC) private String whereClause;
	
	public ClueQueryImplMarkA(Clue clue, EntityRecogniser entityRecogniser) {
		this.setClue(clue);
		this.setEntityRecogniser(entityRecogniser);
		this.setCandidateSolutions(new ArrayList<String>());
	}
	
	/**
	 * @return ArrayList<String>
	 */
	@Override
	public ArrayList<String> getCandidateSolutions() {
		ArrayList<String> recognisedResourceURIs = this.getEntityRecogniser().getRecognisedResourceURIs();
		ArrayList<String> recognisedPropertyURIs = this.getEntityRecogniser().getRecognisedPropertyURIs();
		for(String resourceUri : recognisedResourceURIs) {
			for(String propertyUri : recognisedPropertyURIs) {
				// Look for subjects where <?subject propertyUri ResourceUri>
				String sparqlQuery = DBPEDIA_PREFIX_DECLARATION +
						" " + DBPEDIA_OWL_PREFIX_DECLARATION +
						" " + RDFS_PREFIX_DECLARATION +
						" select distinct ?label" +
							" where {?subject <" + propertyUri + "> <" + resourceUri +">." +
							"        ?subject rdfs:label ?label.}";
				this.executeSparqlQuery(sparqlQuery);
				
				// Look for objects where <?resourceUri propertyUri object>
				sparqlQuery = RDFS_PREFIX_DECLARATION +
							" select distinct ?label" +
							" where { <" + resourceUri + "> <" + propertyUri + "> ?object." +
							"        ?object rdfs:label ?label.}";
				this.executeSparqlQuery(sparqlQuery);
			}
		}
		return candidateSolutions;
	}
	
	/**
	 * executeSparqlQuery
	 * @param sparqlQuery
	 */
	private void executeSparqlQuery(String sparqlQuery) {
		Query query = QueryFactory.create(sparqlQuery);
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ENDPOINT_URI, query);
		ResultSet resultSet = queryExecution.execSelect();
		
		while(resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.nextSolution();
			String label = querySolution.getLiteral("?label").toString();
			
			if(label != null && !(this.candidateSolutions.contains(label)))
				this.candidateSolutions.add(label);
		}
		queryExecution.close();
	}

	@Override
	public ArrayList<Solution> getSolutions() {
		// TODO Auto-generated method stub
		return null;
	}
}
