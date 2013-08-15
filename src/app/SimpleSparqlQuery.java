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

/**
 * @author Ben Griffiths
 *
 */
public class SimpleSparqlQuery implements framework.Query {
	private final String ENDPOINT_URI = "http://dbpedia.org/sparql";
	private final String DBPEDIA_PREFIX_DECLARATION = "PREFIX dbpedia: <http://dbpedia.org/resource/>";
	private final String DBPEDIA_OWL_PREFIX_DECLARATION = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>";
	private final String RDFS_PREFIX_DECLARATION = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
	
	@Setter(AccessLevel.PRIVATE) ArrayList<String> candidateSolutions;
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) Query query;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) QueryExecution queryExecution;
	
	public SimpleSparqlQuery(Clue clue) {
		this.setClue(clue);
	}

	@Override
	public ArrayList<String> getCandidateSolutions() {
		ArrayList<String> candidateSolutions = new ArrayList<String>();
		String SPARQLquery = DBPEDIA_PREFIX_DECLARATION +
				" " + DBPEDIA_OWL_PREFIX_DECLARATION +
				" " + RDFS_PREFIX_DECLARATION +
				" select distinct ?label" +
					" where {dbpedia:The_Beatles dbpedia-owl:bandMember ?object." +
					"        ?object rdfs:label ?label" +
					" FILTER (lang(?label) = 'en')}";
		
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
		return candidateSolutions;
	}
}
