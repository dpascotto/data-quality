package it.intext.pattern.grules;

import it.intext.pattern.abs.Replacer;
import it.intext.pattern.abs.Replacer.Scope;
import it.intext.pattern.gindex.ScopeQuery;
import it.intext.pattern.gindex.SemanticIndex;
import it.intext.pattern.gindex.ScopeQuery.ScopePair;
import it.intext.pattern.gindex.SemanticIndex.SemanticReader;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cxf.common.util.SortedArraySet;
import org.apache.lucene.queryParser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryParser.surround.query.SrndQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.Spans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryMatchParser {

	private SemanticIndex index;
	static final Logger logger = LoggerFactory.getLogger(QueryMatchParser.class);
	public static final String SEGMENT_LAYER = "SEGMENT";

	@SuppressWarnings("unused")
	static private final QMPConfigurator configurator = new QMPConfigurator(); 

	private static class QMPConfigurator {

		QMPConfigurator() {
			org.apache.lucene.search.BooleanQuery.setMaxClauseCount(100*1024);
		}
	}

	public QueryMatchParser(SemanticIndex index)
	{
		this.index = index;
	}

	public Result getQueryResults(SrndQuery query)
	{
		try{
			Query q = query.makeLuceneQueryFieldNoBoost(SemanticIndex.TEXT_FIELD, new BasicQueryFactory(Integer.MAX_VALUE)).rewrite(index.getReader());
			logger.trace("Checking match for {}",q);
			if (index.getSearcher().explain(q, 0).isMatch())
			{
				logger.trace("{} matched!",q);
				return parseQuery(q,null);
			}
			logger.trace("No match for {}",q);
			return null;


		}catch(Exception e)
		{
			logger.error("Error parsing match for {}",query);
			logger.error("{}",e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public Result getQueryResults(SrndQuery query, ScopeQuery scope) {
		try{
			Query q = query.makeLuceneQueryFieldNoBoost(SemanticIndex.TEXT_FIELD, new BasicQueryFactory(Integer.MAX_VALUE)).rewrite(index.getReader());
			logger.trace("Checking match for {}",q);
			if (index.getSearcher().explain(q, 0).isMatch())
			{
				logger.trace("{} matched!",q);
				return parseQuery(q,scope);
			}
			logger.trace("No match for {}",q);
			return null;


		}catch(Exception e)
		{
			logger.error("Error parsing match for {}",query);
			logger.error("{}",e.getMessage());
			e.printStackTrace();
			return null;
		}
	}	

	private Result parseQuery(Query query,ScopeQuery scope) {

		logger.trace("Parsing query[{}] {}",query.getClass(),query);
		Result result = new Result();
		if (query instanceof SpanQuery)
		{
			SpanQuery sp = (SpanQuery)query;
			try {
				SemanticIndex.SemanticReader reader = (SemanticIndex.SemanticReader)index.getReader();
				Spans spans = sp.getSpans(reader);
				ScopePair matchScopePair = null;
				while(spans.next())
				{
					if (scope != null)
					{
						int sstart = spans.start();
						int ssend = spans.end();
						boolean inScope = false;
						for (ScopePair sc : scope.getScopePositions(reader))
						{
							if (sc.getStartPosition() <= sstart && sc.getEndPosition() >= ssend)
							{
								inScope = true;
								matchScopePair = sc;
								break;
							}
						}
						if (!inScope)
						{
							logger.debug("Out Scope reject : {}",reader.getTextIn(spans.start(), spans.end()));
							continue;
						}
					}
					ResultReport r = new ResultReport();
					r.setMatchStartPosition(spans.start());
					r.setMatchEndPosition(spans.end()-1);
					r.setMatchOffset(reader.getOffset(spans.start()).getOffset());
					r.setMatchLength(reader.getOffset(spans.end()-1).getEnd()-r.matchOffset);
					r.setMatchingText(reader.getTextIn(spans.start(), spans.end()));
					r.setMatchScopePair(matchScopePair);
					result.addReport(r);
				}
				return result;
			} catch (IOException e) {
				logger.warn("Error parsing spans on {}",query);
				return null;
			}

		}
		else
			if (query instanceof BooleanQuery)
			{
				BooleanQuery bq = (BooleanQuery)query;
				for (Object o : bq.clauses())
				{
					BooleanClause clause = (BooleanClause)o;
					Result cr = parseQuery(clause.getQuery(),scope);
					if (cr != null)
						result.reports.addAll(cr.getReports());
				}
				return result;
			}
			else
				logger.warn("Unhandled query type : {}",query.getClass());
		return null;

	}

	public class Result{

		private SortedArraySet<ResultReport> reports = new SortedArraySet<ResultReport>();		


		public void addReport(ResultReport rep)
		{
			this.reports.add(rep);
		}



		public SortedSet<ResultReport> getReports() {
			return reports;
		}

		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			for (ResultReport repo : this.reports)
			{
				buf.append(repo.toString()+"\n");
			}
			return buf.toString().trim();
		}
	}

	public class ResultReport implements Comparable<ResultReport>{

		private String matchingText;
		private int matchStartPosition;
		private int matchEndPosition;
		private int matchOffset;
		private int matchLength;
		private ScopePair matchScopePair;

		public ResultReport(){}

		public int compareTo(ResultReport o) {
			return Double.compare(this.matchStartPosition, o.matchStartPosition);
		}

		public String getMatchingText() {
			return matchingText;
		}

		protected void setMatchingText(String matchingText) {
			this.matchingText = matchingText;
		}

		public int getMatchStartPosition() {
			return matchStartPosition;
		}

		protected void setMatchStartPosition(int matchStartPosition) {
			this.matchStartPosition = matchStartPosition;
		}

		public int getMatchEndPosition() {
			return matchEndPosition;
		}

		protected void setMatchEndPosition(int matchEndPosition) {
			this.matchEndPosition = matchEndPosition;
		}

		public int getMatchOffset() {
			return matchOffset;
		}

		protected void setMatchOffset(int matchOffset) {
			this.matchOffset = matchOffset;
		}

		public int getMatchLength() {
			return matchLength;
		}

		protected void setMatchLength(int matchLength) {
			this.matchLength = matchLength;
		}

		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			buf.append(this.matchingText);
			buf.append(" ["+this.matchStartPosition+","+this.matchEndPosition+"]");
			buf.append("("+this.matchOffset+","+this.matchLength+")");
			return buf.toString().trim();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + matchEndPosition;
			result = prime * result + matchLength;
			result = prime * result + matchOffset;
			result = prime * result + matchStartPosition;
			result = prime * result
			+ ((matchingText == null) ? 0 : matchingText.hashCode());
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
			ResultReport other = (ResultReport) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (matchEndPosition != other.matchEndPosition)
				return false;
			if (matchLength != other.matchLength)
				return false;
			if (matchOffset != other.matchOffset)
				return false;
			if (matchStartPosition != other.matchStartPosition)
				return false;
			if (matchingText == null) {
				if (other.matchingText != null)
					return false;
			} else if (!matchingText.equals(other.matchingText))
				return false;
			return true;
		}

		private QueryMatchParser getOuterType() {
			return QueryMatchParser.this;
		}

		protected ScopePair getMatchScopePair() {
			return matchScopePair;
		}

		protected void setMatchScopePair(ScopePair matchScopePair) {
			this.matchScopePair = matchScopePair;
		}		
	}

	public class Target{
		String target;
		int[] positions;

		public String getTarget() {
			return target;
		}
		public void setTarget(String target) {
			this.target = target;
		}
		public int[] getPositions() {
			return positions;
		}
		public void setPositions(int[] positions) {
			this.positions = positions;
		}
		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			buf.append(this.target);
			buf.append(" ");
			buf.append(Arrays.toString(positions));
			return buf.toString();
		}
	}


	static final Pattern LAYERPATTERN = Pattern.compile("^([A-z0-9]+)#([^#]+?)$");
	static final Pattern REPLACERPATTERN = Pattern.compile("^\\@[A-z0-9_]+$"); 
	public Target getTarget(String target, Result result)
	{
		/*
		 * All results matches on * target
		 */
		SortedSet<Integer> pos = new TreeSet<Integer>();
		if (target.equals("*")){			
			for (ResultReport repo : result.getReports()){
				for (int j = repo.matchStartPosition; j <= repo.matchEndPosition; j++)
					pos.add(j);
			}			
		}else if (target.equals("$COPE"))
		{
			/*
			 * match the entire scope...
			 */
			for (ResultReport repo : result.getReports()){
				if (repo.getMatchScopePair() != null){
				for (int j = repo.getMatchScopePair().getStartPosition(); 
						j < repo.getMatchScopePair().getEndPosition(); j++)
					pos.add(j);
				}
			}	
		}
		else{
			Matcher matcher = LAYERPATTERN.matcher(target);
			if (matcher.matches())
			{
				String text = matcher.group(2);
				String layer = matcher.group(1);
				if (layer.equalsIgnoreCase(SEGMENT_LAYER)){
					/*
					 * execute on the entire segment
					 */
					pos.addAll(((SemanticReader)index.getReader()).getSegmentPositions(text));
				}else{
					for (ResultReport repo : result.getReports()){
						int[] poss = this.index.getLayerTermInnerPositions(text,
								layer, repo.matchStartPosition,
								repo.matchEndPosition);
						if (poss != null)
							for (int ps : poss)
								pos.add(ps);
					}
				}
			}
			else
			{
				Matcher rmatcher = REPLACERPATTERN.matcher(target);
				if (rmatcher.matches())
				{
					/*
					 * Retrieving replacer positions
					 * scanning layers from rule to document
					 */
					String layer = Replacer.getLayer(Scope.RULE);
					for (ResultReport repo : result.getReports()){
						int[] poss = this.index.getLayerTermInnerPositions(target,
								layer, repo.matchStartPosition,
								repo.matchEndPosition);
						if (poss != null && poss.length > 0)
							for (int ps : poss)
								pos.add(ps);
						else{
							layer = Replacer.getLayer(Scope.STAGE);
							int[] poss2 = this.index.getLayerTermInnerPositions(target,
									layer, repo.matchStartPosition,
									repo.matchEndPosition);
							if (poss2 != null && poss2.length > 0)
								for (int ps : poss2)
									pos.add(ps);
							else
							{
								layer = Replacer.getLayer(Scope.DOCUMENT);
								int[] poss3 = this.index.getLayerTermInnerPositions(target,
										layer, repo.matchStartPosition,
										repo.matchEndPosition);
								if (poss3 != null && poss3.length > 0)
									for (int ps : poss3)
										pos.add(ps);
							}							
						}
					}					
				}
				else{
					logger.error("Error getting target : {}",target);
					return null;
				}
			}
		}
		Target tg = new Target();
		int[] tps = new int[pos.size()];
		Iterator<Integer> it = pos.iterator();
		int idx = 0;
		while(it.hasNext())
		{
			tps[idx] = it.next();
			idx++;
		}
		tg.positions = tps;
		tg.target = target;
		return tg;
	}

	public SemanticIndex getIndex() {
		return index;
	}

	public void setIndex(SemanticIndex index) {
		this.index = index;
	}

}
