package it.intext.pattern.gindex;

import it.intext.pattern.gindex.SemanticIndex.SemanticReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryParser.surround.query.DistanceSubQuery;
import org.apache.lucene.queryParser.surround.query.SpanNearClauseFactory;
import org.apache.lucene.queryParser.surround.query.SrndQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.Spans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScopeQuery extends SrndQuery implements DistanceSubQuery{

	/**
	 * As for now
	 * ScopeQuery Query are usable only
	 * as stand-alone query (p.e. as replacer clause)
	 * single clauses could be as complex as wanted 
	 * (but obviously not Between!)
	 */
	private static final long serialVersionUID = 4509269137434381280L;
	private SrndQuery leftClause;
	private SrndQuery rightClause;
	static final Logger logger = LoggerFactory.getLogger(ScopeQuery.class);

	public ScopeQuery(SrndQuery leftClause, SrndQuery rightClause)
	{
		this.leftClause = leftClause;
		this.rightClause = rightClause;
	}

	@Override
	public String toString() {
		return new String("ScopeQuery:[left:"+leftClause+"][right:"+rightClause+"]");
	}

	public void addSpanQueries(final SpanNearClauseFactory sncf) throws IOException {
		for (Query q : createSpanNear((SemanticReader)sncf.getIndexReader()))				
		{
			if (((SpanNearQuery)q).getClauses().length > 0)
				sncf.addSpanNearQuery(q);
		}
	}
	
	public static class ScopePair{
		private int startPosition;
		private int endPosition;
		public ScopePair(int startPosition, int endPosition) {
			super();
			this.startPosition = startPosition;
			this.endPosition = endPosition;
		}
		public int getStartPosition() {
			return startPosition;
		}
		public int getEndPosition() {
			return endPosition;
		}		
	}
	
	private SortedSet<Integer> getPositions(SemanticReader reader,List<ScopePair> distPositions)
	{
		SortedSet<Integer> positions = new TreeSet<Integer>();
		SortedSet<Integer> leftStart = new TreeSet<Integer>();
		SortedSet<Integer> rightEnd = new TreeSet<Integer>();
		try{
			Query left = leftClause.makeLuceneQueryFieldNoBoost(
					SemanticIndex.TEXT_FIELD, new BasicQueryFactory()).rewrite(reader);
			if (left == null)
				throw new Exception("Error getting SpanQuery : LeftClause is null ");
			if (left instanceof SpanQuery)
			{
				Spans lSpans = ((SpanQuery)left).getSpans(reader);
				if (lSpans != null)
				{
					while(lSpans.next())
					{
						leftStart.add(lSpans.start());
					}
				}
			}
			else{
				throw new Exception("Error getting SpanQuery :  LeftClause is "+left.getClass());
			}

			Query right = rightClause.makeLuceneQueryFieldNoBoost(
					SemanticIndex.TEXT_FIELD, new BasicQueryFactory()).rewrite(reader);
			if (right == null)
				throw new Exception("Error getting SpanQuery : RightClause is null ");
			if (right instanceof SpanQuery)
			{
				Spans rSpans = ((SpanQuery)right).getSpans(reader);
				if (rSpans != null)
				{
					while(rSpans.next())
					{
						rightEnd.add(rSpans.end());
					}
				}
			}
			else{
				throw new Exception("Error getting SpanQuery :  RightClause is "+right.getClass());
			}

			for (Integer e : leftStart)
			{
				try{
					// Scope positions must include at least 1 token + 1 for end of scope (open interval)
					int ns = rightEnd.tailSet(e+2).first();
					if (distPositions != null)
						distPositions.add(new ScopePair(e, ns));
					for (int j=e; j < ns; j++)
						positions.add(j);
				}catch(NoSuchElementException ne){
					break;
				}
			}
			return positions;
		}catch(Exception e){
			logger.error("Error creating SpanQuery : "+e.getMessage());
			return positions;
		}

	}
	
	public List<ScopePair> getScopePositions(SemanticReader reader)
	{
		List<ScopePair> poss = new ArrayList<ScopePair>();
		getPositions(reader, poss);
		return poss;
	}

	private List<SpanNearQuery> createSpanNear(SemanticReader reader){
		List<SpanNearQuery> ret = new ArrayList<SpanNearQuery>();
		List<SpanTermQuery> terms = new ArrayList<SpanTermQuery>();
		int last = -2;
		for (int position : getPositions(reader,null))
		{
			if (position - last > 1)
			{
				if (terms.size() > 0){
					SpanQuery[] clauses = new SpanQuery[terms.size()];
					clauses = terms.toArray(clauses);
					ret.add(new SpanNearQuery(clauses,0,true));
				}
				terms = new ArrayList<SpanTermQuery>();
			}
			terms.add(new SpanFilteredTermQuery(new Term(SemanticIndex.TEXT_FIELD,
					reader.getTextTermAt(position)),position));			
			last = position;
		}
		if (terms.size() > 0){
			SpanQuery[] clauses = new SpanQuery[terms.size()];
			clauses = terms.toArray(clauses);
			ret.add(new SpanNearQuery(clauses,0,true));
		}
		return ret;
	}

	@Override
	public Query makeLuceneQueryFieldNoBoost(String fieldName,
			BasicQueryFactory qf) {
		return new Query(){

			private static final long serialVersionUID = 1L;

			public Query rewrite(IndexReader reader) throws IOException {
				if (!(reader instanceof SemanticReader))
					return null;
				else
				{
					List<SpanNearQuery> qs = createSpanNear((SemanticReader)reader);
					SpanQuery[] clauses = new SpanQuery[qs.size()];
					return new SpanOrQuery(qs.toArray(clauses));
				}
			}

			public String toString(String field) {
				return "ScopeQuery["+field+"]";
			}

		};
	}

	public String distanceSubQueryNotAllowed() {
		/*
		 * as usual, they are..
		 */
		return null;
	}
	
}
