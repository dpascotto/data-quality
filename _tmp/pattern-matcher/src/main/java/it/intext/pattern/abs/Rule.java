package it.intext.pattern.abs;

import it.intext.pattern.abs.Replacer.Scope;
import it.intext.pattern.gindex.IntextSurroundQueryParser;
import it.intext.pattern.gindex.LayerIndex;
import it.intext.pattern.gindex.ScopeQuery;
import it.intext.pattern.gindex.SemanticIndex;
import it.intext.pattern.grules.QueryMatchParser;
import it.intext.pattern.grules.QueryMatchParser.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryParser.surround.query.SrndQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rule {

	private String name;
	private List<String> queries = new ArrayList<String>();
	private List<SrndQuery> parsedQuery = new ArrayList<SrndQuery>();
	private List<Action> actions = new ArrayList<Action>();
	private List<Constrain> constrains = new ArrayList<Constrain>();
	static final Logger logger = LoggerFactory.getLogger(Rule.class);
	private List<Replacer> replacers = new ArrayList<Replacer>();
	private String sscope = null;
	private ScopeQuery scope = null;
	private boolean oneShot = false;

	public Rule(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getQueries() {
		return queries;
	}
	public void setQueries(List<String> queries, HashMap<Scope,HashMap<String,String>> repMap) {
		this.queries = queries;
		parseQs(repMap);
	}
	public void setQueries(List<String> queries) {
		this.queries = queries;
		parseQs(null);
	}
	public List<SrndQuery> getParsedQuery() {
		return parsedQuery;
	}
	public void setParsedQuery(List<SrndQuery> parsedQuery) {
		this.parsedQuery = parsedQuery;
	}
	public List<Action> getActions() {
		return actions;
	}
	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	static final IntextSurroundQueryParser parser = 
		new IntextSurroundQueryParser(SemanticIndex.DEFAULT_ANALYZER);


	public void applyRule(SemanticIndex idx)
	{
		for (Constrain cons : this.constrains)
			if (!cons.isSatisfied(idx)){
				logger.trace("Constraint {} not satisfied.", cons);
				return;
			}

		for (Replacer rep : this.replacers) {
			logger.trace("Applying replacer {}.",rep);
			rep.applyOnIndex(idx);
		}

		if (parsedQuery == null)
			parseQs(null);

		QueryMatchParser qm = new QueryMatchParser(idx);
		for (SrndQuery query : this.parsedQuery)
		{
			logger.trace("Rule {} - Applying query : {}.", name, query);
			Result res = (scope == null) 
			? qm.getQueryResults(query)
					: qm.getQueryResults(query, scope);

			if (res != null)
			{
				logger.trace("{}",res);
				for (Action a : this.actions)
				{
					logger.trace("Executing Action : {}",a);
					a.executeAction(res, qm);
				}
			}
			else {
				logger.trace("No matches found.");
			}
		}

		logger.trace("Cleaning rule replacer..");
		LayerIndex lidx = idx.getLayer(Replacer.getLayer(Scope.RULE));
		if (lidx != null)
			lidx.clear();
	}

	private void parseQs(HashMap<Scope,HashMap<String,String>> repMap) {
		for (String query : this.queries)
			try{
				if (repMap == null)
					this.parsedQuery.add(parser.parse(query));
				else
					this.parsedQuery.add(parser.parse(query,repMap));
			}catch(Exception e)
			{
				logger.error("Error parsing query : {}",query);
				logger.error("{}",e.getMessage());
			}
	}

	public List<Constrain> getConstrains() {
		return constrains;
	}

	public void setConstrains(List<Constrain> constrains) {
		this.constrains = constrains;
	}

	public boolean isOneShot() {
		return oneShot;
	}

	public void setOneShot(boolean oneShot) {
		this.oneShot = oneShot;
	}

	public List<Replacer> getReplacers() {
		return replacers;
	}

	public void setReplacers(List<Replacer> replacers) {
		this.replacers = replacers;
	}

	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(name+" (oneShot:"+isOneShot()+")\n");
		for (Replacer rp : replacers)
		{
			buf.append("\t"+rp.toString()+"\n");
		}
		for (Constrain c : this.constrains)
		{
			buf.append("\t"+c.toString()+"\n");
		}
		if (scope != null)
			buf.append("\t scope : "+scope.toString()+"\n");
		for (String q : queries)
		{
			buf.append("\t"+q+"\n");
		}
		for (Action a : actions)
		{
			buf.append("\t"+a+"\n");
		}
		return buf.toString().trim();
	}

	public void setScope(String scope) {
		sscope = scope;
		try{
			SrndQuery q = parser.parseScope(scope);
			if (q instanceof ScopeQuery)
			{
				this.scope = (ScopeQuery)q;
			}
			else
				logger.error("Invalid scope query : {}",scope);
		}catch(Exception e)
		{
			logger.error("Error parsing scope query : {}",scope);
		}
	}

	public void setScope(String scope, Map<String,String> scopeReplacersMap, HashMap<Scope,HashMap<String,String>> repMap) {
		sscope = scope;
		try{
			SrndQuery q = parser.parseScope(scope,scopeReplacersMap,repMap);
			if (q instanceof ScopeQuery)
			{
				this.scope = (ScopeQuery)q;
			}
			else
				logger.error("Invalid scope query : {}",scope);
		}catch(Exception e)
		{
			logger.error("Error parsing scope query : {}",scope);
		}
	}

	public ScopeQuery getScope() {
		return scope;
	}

	public String getScopeString() {
		return sscope;
	}

}
