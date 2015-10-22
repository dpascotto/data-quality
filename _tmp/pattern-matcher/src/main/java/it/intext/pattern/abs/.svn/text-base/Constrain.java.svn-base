package it.intext.pattern.abs;

import java.util.HashMap;

import it.intext.pattern.abs.Replacer.Scope;
import it.intext.pattern.gindex.SemanticIndex;
import it.intext.pattern.grules.QueryMatchParser;

import org.apache.lucene.queryParser.surround.query.SrndQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constrain {

	private String query;
	private SrndQuery parsedQuery;
	static final Logger logger = LoggerFactory.getLogger(Constrain.class);

	public Constrain(String query)
	{
		this.query = query;
		parseQs(null);
	}
	
	public Constrain(String query, HashMap<Scope,HashMap<String,String>> repMap)
	{
		this.query = query;
		parseQs(repMap);
	}
	
	private void parseQs(HashMap<Scope,HashMap<String,String>> repMap) {		
		try{
			if (repMap == null)
				parsedQuery = Rule.parser.parse(query);
			else
				parsedQuery = Rule.parser.parse(query,repMap);
		}catch(Exception e)
		{
			logger.error("Error parsing query : {}",query);
		}
	}
	
	public boolean isSatisfied(SemanticIndex idx)
	{
		if (parsedQuery == null)
			parseQs(null);
		QueryMatchParser qm = new QueryMatchParser(idx);
		return qm.getQueryResults(this.parsedQuery) != null;
	}
	
	public String toString()
	{
		return new String("Constrain[ "+this.query+" ]");
	}

}
