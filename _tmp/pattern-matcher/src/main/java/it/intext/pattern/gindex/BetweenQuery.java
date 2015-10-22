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

public class BetweenQuery extends SrndQuery implements DistanceSubQuery{

	/**
	 * As for now
	 * Between Query are usable only
	 * as stand-alone query (p.e. as replacer clause)
	 * single clauses could be as complex as wanted 
	 * (but obviously not Between!)
	 */
	private static final long serialVersionUID = 4509269137434381280L;
	private SrndQuery leftClause;
	private SrndQuery rightClause;
	static final Logger logger = LoggerFactory.getLogger(BetweenQuery.class);

	public BetweenQuery(SrndQuery leftClause, SrndQuery rightClause)
	{
		this.leftClause = leftClause;
		this.rightClause = rightClause;
	}

	@Override
	public String toString() {
		return new String("BetweenQuery:[left:"+leftClause+"][right:"+rightClause+"]");
	}

	public void addSpanQueries(final SpanNearClauseFactory sncf) throws IOException {
		for (Query q : createSpanNear((SemanticReader)sncf.getIndexReader()))				
		{
			if (((SpanNearQuery)q).getClauses().length > 0)
				sncf.addSpanNearQuery(q);
		}
	}

	private SortedSet<Integer> getPositions(SemanticReader reader)
	{
		SortedSet<Integer> positions = new TreeSet<Integer>();
		SortedSet<Integer> leftEnd = new TreeSet<Integer>();
		SortedSet<Integer> rightStart = new TreeSet<Integer>();
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
						leftEnd.add(lSpans.end());
					}
				}
			}
			else{
				// throw new Exception("Error getting SpanQuery :  LeftClause is "+left.getClass());
				logger.debug("No matches for between LeftClause {}.", leftClause.toString());
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
						rightStart.add(rSpans.start());
					}
				}
			}
			else{
				// throw new Exception("Error getting SpanQuery :  RightClause is "+right.getClass());
				logger.debug("No matches for between RightClause {}.", rightClause.toString());
			}

			for (Integer e : leftEnd)
			{
				try{
					int ns = rightStart.tailSet(e+1).first();
					try{
					int cleft = leftEnd.tailSet(e+1).first();
					if (cleft < ns)
						continue;
					}catch(NoSuchElementException ne){}
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
				return "BetweenQuery["+field+"]";
			}

		};
	}

	public String distanceSubQueryNotAllowed() {
		/*
		 * as usual, they are..
		 */
		return null;
	}

	//	public class BetweenSpanQuery extends SpanNearQuery{
	//		
	//		private static final long serialVersionUID = -2087109059778136476L;
	//				
	//		@Override
	//		public String toString() {
	//			return new String("BetweenSpanQuery:[left:"+leftClause+"][right:"+rightClause+"]");
	//		}
	//
	//		@Override
	//		public String getField() {
	//			return SemanticIndex.TEXT_FIELD;
	//		}
	//
	//		@Override
	//		public Collection getTerms() {
	//			return null;
	//		}
	//
	//		@Override
	//		public String toString(String field) {
	//			return toString();
	//		}
	//		
	//		@Override
	//		public Spans getSpans(IndexReader reader) throws IOException {
	//			final SemanticReader sreader = (SemanticReader)reader; 
	//			if (reader instanceof SemanticReader){
	//				
	//				return new Spans(){
	//
	//					private SortedSet<Integer> acceptable = getPositions(sreader);
	//					
	//					private int currStart = -1;
	//					private int currEnd = -1;
	//					
	//					@Override
	//					public int doc() {
	//						return 0;
	//					}
	//
	//					@Override
	//					public int end() {
	//						return currEnd;
	//					}
	//
	//					@Override
	//					public Collection<Object> getPayload() throws IOException {
	//						return null;
	//					}
	//
	//					@Override
	//					public boolean isPayloadAvailable() {
	//						return false;
	//					}
	//
	//					@Override
	//					public boolean next() throws IOException {
	//						try{
	//						acceptable = acceptable.tailSet(currEnd+1);
	//						currStart = acceptable.first();
	//						int n = currStart;
	//						Iterator<Integer> it = acceptable.iterator();
	//						while(it.hasNext())
	//						{
	//							int in = it.next();
	//							if (in-n > 1)
	//								break;
	//							n = in;
	//						}
	//						currEnd = n;
	//						return true;
	//						}catch(NoSuchElementException e){
	//							return false;
	//						}
	//						
	//					}
	//
	//					@Override
	//					public boolean skipTo(int target) throws IOException {
	//						logger.error("Error : BetweenQuery-skipTo");
	//						throw new UnsupportedOperationException();
	//					}
	//
	//					@Override
	//					public int start() {
	//						return currStart;
	//					}			
	//
	//				};
	//
	//				
	//			}
	//			return null;
	//		}
	//		
	//		private SortedSet<Integer> getPositions(SemanticReader reader)
	//		{
	//			SortedSet<Integer> positions = new TreeSet<Integer>();
	//			SortedSet<Integer> leftEnd = new TreeSet<Integer>();
	//			SortedSet<Integer> rightStart = new TreeSet<Integer>();
	//			try{
	//			Query left = leftClause.makeLuceneQueryFieldNoBoost(
	//					SemanticIndex.TEXT_FIELD, new BasicQueryFactory()).rewrite(reader);
	//			if (left == null)
	//				throw new Exception("Error getting SpanQuery : LeftClause is null ");
	//			if (left instanceof SpanQuery)
	//			{
	//				Spans lSpans = ((SpanQuery)left).getSpans(reader);
	//				if (lSpans != null)
	//				{
	//					while(lSpans.next())
	//					{
	//						leftEnd.add(lSpans.end());
	//					}
	//				}
	//			}
	//			else{
	//				throw new Exception("Error getting SpanQuery :  LeftClause is "+left.getClass());
	//			}
	//			
	//			Query right = rightClause.makeLuceneQueryFieldNoBoost(
	//					SemanticIndex.TEXT_FIELD, new BasicQueryFactory()).rewrite(reader);
	//			if (right == null)
	//				throw new Exception("Error getting SpanQuery : RightClause is null ");
	//			if (right instanceof SpanQuery)
	//			{
	//				Spans rSpans = ((SpanQuery)right).getSpans(reader);
	//				if (rSpans != null)
	//				{
	//					while(rSpans.next())
	//					{
	//						rightStart.add(rSpans.start()-1);
	//					}
	//				}
	//			}
	//			else{
	//				throw new Exception("Error getting SpanQuery :  RightClause is "+right.getClass());
	//			}
	//			
	//			for (Integer e : leftEnd)
	//			{
	//				try{
	//					int ns = rightStart.tailSet(e+1).first();
	//					for (int j=e; j <= ns; j++)
	//						positions.add(j);
	//				}catch(NoSuchElementException ne){
	//					break;
	//				}
	//			}
	//			return positions;
	//			}catch(Exception e){
	//				logger.error("Error creating SpanQuery : "+e.getMessage());
	//				return positions;
	//			}
	//			
	//		}
	//		
	//	}

}
