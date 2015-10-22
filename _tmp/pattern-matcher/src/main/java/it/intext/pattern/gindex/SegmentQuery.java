package it.intext.pattern.gindex;

import it.intext.pattern.gindex.SemanticIndex.SemanticReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

public class SegmentQuery extends SrndQuery implements DistanceSubQuery{

	private static final long serialVersionUID = 4509269137434381280L;
	private String segName;
	private SrndQuery clause;
	static final Logger logger = LoggerFactory.getLogger(SegmentQuery.class);

	public SegmentQuery(SrndQuery clause, String segmentName)
	{
		this.segName = segmentName;
		this.clause = clause;
	}

	@Override
	public String toString() {
		return new String("SegmentQuery:["+segName+"]"+clause);
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
	
	private SortedSet<Integer> getPositions(SemanticReader reader)
	{
		SortedSet<Integer> positions = new TreeSet<Integer>();
		try{
			Query q = clause.makeLuceneQueryFieldNoBoost(
					SemanticIndex.TEXT_FIELD, new BasicQueryFactory()).rewrite(reader);
			if (q == null)
				throw new Exception("Error getting SegmentQuery : Clause is null ");
			if (q instanceof SpanQuery)
			{
				Spans lSpans = ((SpanQuery)q).getSpans(reader);
				SortedSet<Integer> availables = reader.getSegmentPositions(segName);
				if (lSpans != null)
				{
					while(lSpans.next())
					{
						SortedSet<Integer> tmp = new TreeSet<Integer>();
						boolean contained = true;
						for (int s = lSpans.start(); s < lSpans.end(); s++){
							contained &= availables.contains(s);
							if (contained){
								tmp.add(s);
							}else{
								break;
							}
						}
						if (contained)
							positions.addAll(tmp);
					}
				}
			}
			else{
				throw new Exception("Error getting SegmentQuery :  Clause is "+q.getClass());
			}
			
			return positions;
		}catch(Exception e){
			logger.error("Error creating SegmentQuery : "+e.getMessage());
			return positions;
		}

	}
	
	private List<SpanNearQuery> createSpanNear(SemanticReader reader){
		List<SpanNearQuery> ret = new ArrayList<SpanNearQuery>();
		List<SpanTermQuery> terms = new ArrayList<SpanTermQuery>();
		int last = -2;
		for (int position : getPositions(reader))
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
				return "SegmentQuery["+field+"]";
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
