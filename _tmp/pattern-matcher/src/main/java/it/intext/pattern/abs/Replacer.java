package it.intext.pattern.abs;

import java.util.HashMap;

import it.intext.pattern.gindex.LayerIndex;
import it.intext.pattern.gindex.SemanticIndex;
import it.intext.pattern.grules.QueryMatchParser;
import it.intext.pattern.grules.QueryMatchParser.Result;

import org.apache.lucene.queryParser.surround.query.SrndQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Replacer {
	
	public static enum Scope{
		UNDEFINED,
		DOCUMENT,
		STAGE,
		RULE
	}
	
	private String name;
	private String query;
	private SrndQuery parsedQuery;
	private Scope scope = Scope.UNDEFINED;
	static final Logger logger = LoggerFactory.getLogger(Replacer.class);
	
	public Replacer(String name, String query, Scope scope) throws Exception
	{
		if (!name.matches("^\\@[A-z0-9_]+"))
		{
			logger.error("Invalid replacer : {}",name);
			throw new Exception("Invalid replacer : "+name);
		}
		this.name = name;
		this.query = query;
		this.scope = scope;
		parseQs();		
	}
	
	public Replacer(String name, String query, Scope scope, HashMap<Scope,HashMap<String,String>> repMap) throws Exception
	{
		this(name,query,scope);
		parseQs(repMap);		
	}
	
	public String substitute(String query)
	{
		return query.replaceAll(name+"[$\\s\\t,\\)]", this.query);
	}
	
	public void applyOnIndex(SemanticIndex idx)
	{
		if (this.getLayer() == null)
			return;
		if (parsedQuery == null)
			parseQs();
		QueryMatchParser qm = new QueryMatchParser(idx);
		Result result = qm.getQueryResults(this.parsedQuery);
		if (result == null) return;
		
		String layerName = this.getLayer();
		LayerIndex lidx = idx.getLayer(layerName);
		if (lidx == null) {
			idx.addLayer(layerName);
			lidx = idx.getLayer(layerName);
		}
		
		lidx.lockChanges();
		AddAction a = new AddAction();
		a.getTargets().add("*");
		a.getAddElements().add(this.getLayer()+"#"+this.name);
		a.executeAction(result, qm);
		lidx.unlockChanges();
	}
	
	private void parseQs() {		
		try{
			parsedQuery = Rule.parser.parse(query);
		}catch(Exception e)
		{
			logger.error("Error parsing query : {} - {}",query,e.getMessage());			
		}
	}
	
	private void parseQs(HashMap<Scope,HashMap<String,String>> repMap) {		
		try{
			parsedQuery = Rule.parser.parse(query,repMap);
		}catch(Exception e)
		{
			logger.error("Error parsing query : {} - {}",query,e.getMessage());			
		}
	}
	
	public String getLayer()
	{
		return getLayer(scope);
	}
	
	public static String getLayer(Scope scope)
	{
		switch(scope) {
		case UNDEFINED : return null;
		case DOCUMENT : return "DOCUMENT_REPLACER";
		case STAGE : return "STAGE_REPLACER";
		case RULE : return "RULE_REPLACER";
		default : return null;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SrndQuery getParsedQuery() {
		return parsedQuery;
	}

	public void setParsedQuery(SrndQuery parsedQuery) {
		this.parsedQuery = parsedQuery;
	}
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(name+"("+scope.name()+")"+" : "+query);
		return buf.toString().trim();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Replacer other = (Replacer) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		return true;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	

}
