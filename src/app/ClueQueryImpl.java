/**
 * 
 */
package app;

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

/**
 * @author Ben Griffiths
 *
 */
public class ClueQueryImpl implements ClueQuery {
	private final String ENDPOINT_URI = "http://dbpedia.org/sparql";
	private final String DBPEDIA_PREFIX_DECLARATION = "PREFIX dbpedia: <http://dbpedia.org/resource/>";
	private final String DBPEDIA_OWL_PREFIX_DECLARATION = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>";
	private final String RDFS_PREFIX_DECLARATION = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
	
	@Setter(AccessLevel.PRIVATE) ArrayList<String> candidateSolutions;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) EntityRecogniser entityRecogniser;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) Query query;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) QueryExecution queryExecution;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PUBLIC) String whereClause;
	
	public ClueQueryImpl(Clue clue, EntityRecogniser entityRecogniser) {
		this.setClue(clue);
		this.setEntityRecogniser(entityRecogniser);
	}

	@Override
	public ArrayList<String> getCandidateSolutions() {
		ArrayList<String> candidateSolutions = new ArrayList<String>();
		
		for(String resourceUri : this.getEntityRecogniser().getRecognisedResourceURIs()) {
			for(String propertyUri : this.getEntityRecogniser().getRecognisedPropertyURIs()) {
				String SPARQLquery = DBPEDIA_PREFIX_DECLARATION +
						" " + DBPEDIA_OWL_PREFIX_DECLARATION +
						" " + RDFS_PREFIX_DECLARATION +
						" select distinct ?label" +
							" where {?subject <" + propertyUri + "> <" + resourceUri +">." +
							"        ?subject rdfs:label ?label.}";
				
				Query query = QueryFactory.create(SPARQLquery);
				QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ENDPOINT_URI, query);
				ResultSet resultSet = queryExecution.execSelect();
				
				while(resultSet.hasNext()) {
					QuerySolution querySolution = resultSet.nextSolution();
					String label = querySolution.getLiteral("?label").toString();
					if(!candidateSolutions.contains(label))
						candidateSolutions.add(label);
				}
				queryExecution.close();
			}
		}
		
		return candidateSolutions;
	}
}
