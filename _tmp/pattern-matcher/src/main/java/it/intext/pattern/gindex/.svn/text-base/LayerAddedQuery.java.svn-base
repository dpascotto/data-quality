package it.intext.pattern.gindex;

import it.intext.pattern.gindex.SemanticIndex.SemanticReader;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryParser.surround.query.DistanceSubQuery;
import org.apache.lucene.queryParser.surround.query.SpanNearClauseFactory;
import org.apache.lucene.queryParser.surround.query.SrndQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.Spans;


public class LayerAddedQuery extends SrndQuery
implements DistanceSubQuery{

	private SrndQuery parent;
	private SrndQuery[] children;

	public LayerAddedQuery(SrndQuery parent,SrndQuery[] children)
	{
		this.parent = parent;
		this.children = children;
	}

	@Override
	public Query makeLuceneQueryFieldNoBoost(final String fieldName,
			final BasicQueryFactory qf) {
		return new SpanNearQuery(new SpanQuery[]{new CoincidenceTermQuery(parent, children)},
				0,true);
	}

	@Override
	public String toString() {
		return new String("LayerAddedQuery:{"+this.parent+"}("+Arrays.toString(children)+")");
	}


	public void addSpanQueries(final SpanNearClauseFactory sncf) throws IOException {
		SpanNearQuery q = (SpanNearQuery)makeLuceneQueryFieldNoBoost(sncf.getFieldName(), sncf.getBasicQueryFactory());
		if (q.getClauses().length > 0)
			sncf.addSpanNearQuery(q);

	}

	public String distanceSubQueryNotAllowed() {
		/*
		 * yes, they are...
		 */
		return null;
	}
	
	protected class CoincidenceTermQuery extends SpanQuery{

		private static final long serialVersionUID = 737172205012869215L;
		
		private SrndQuery parent;
		private SrndQuery[] children;
		
		public CoincidenceTermQuery(SrndQuery parent, SrndQuery[] children)
		{
			this.parent = parent;
			this.children = children;
		}

		@Override
		public String getField() {
			return SemanticIndex.TEXT_FIELD;
		}

		@Override
		public Spans getSpans(IndexReader reader) throws IOException {
			Query qs = parent.makeLuceneQueryFieldNoBoost(SemanticIndex.TEXT_FIELD
					, new BasicQueryFactory()).rewrite(reader);
			if (!(qs instanceof SpanQuery))
				return new Spans(){

					@Override
					public int doc() {
						return -1;
					}

					@Override
					public int end() {
						return -1;
					}

					@Override
					public Collection<Object> getPayload() throws IOException {
						return null;
					}

					@Override
					public boolean isPayloadAvailable() {
						return false;
					}

					@Override
					public boolean next() throws IOException {
						return false;
					}

					@Override
					public boolean skipTo(int target) throws IOException {
						return false;
					}

					@Override
					public int start() {
						return -1;
					}
				
			};
			SpanQuery q = (SpanQuery)qs;
			final Spans pSpans = q.getSpans(reader);
			final SemanticReader sreader = (SemanticReader)reader;

			return new Spans(){

				private Set<Integer> acceptable;

				@Override
				public int doc() {
					return pSpans.doc();
				}

				@Override
				public int end() {
					return pSpans.end();
				}

				@Override
				public Collection<Object> getPayload() throws IOException {
					return null;
				}

				@Override
				public boolean isPayloadAvailable() {
					return false;
				}

				@Override
				public boolean next() throws IOException {
					boolean ret = false;
					do{
						ret = pSpans.next();
						if (ret){
							ret &= isAcceptable();							
						}
						else
							return false;						
					}while(!ret);
					return ret;
				}

				@Override
				public boolean skipTo(int target) throws IOException {
					if (!pSpans.skipTo(target))
						return false;
					else return isAcceptable();
				}

				@Override
				public int start() {
					return pSpans.start();
				}

				protected Set<Integer> acceptablePositions()
				{
					HashMap<Integer,Integer> posMap = new HashMap<Integer,Integer>();

					for (int j = 0; j < children.length; j++)
					{
						Spans span;
						try {
							span = ((SpanQuery)children[j].makeLuceneQueryFieldNoBoost(SemanticIndex.TEXT_FIELD
									, new BasicQueryFactory()).rewrite(sreader)).getSpans(sreader);

							while(span.next()){
								for (int pos = span.start(); pos <= span.end(); pos++)
								{
									if (!posMap.containsKey(pos))
									{
										if (j == 0)
											posMap.put(pos, 1);
									}else
									{
										int count = posMap.get(pos);
										if (count < j)
											posMap.remove(pos);
										else
											posMap.put(pos, count+1);
									}						
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					int[] psSet = toIntArray(posMap.keySet());
					for (int pos : psSet){
						if (posMap.get(pos) < children.length)
							posMap.remove(pos);
					}
					return posMap.keySet();
				}

				protected boolean isAcceptable()
				{
					if (acceptable == null)
						acceptable = acceptablePositions();
					for (int j = pSpans.start(); j <= pSpans.end(); j++)
					{
						if (!acceptable.contains(j))
							return false;
					}
					return true;
				}

			};
		}
		
		private int[] toIntArray(final Collection<Integer> ct){
			int[] ret = new int[ct.size()];
			Iterator<Integer> it = ct.iterator();
			for (int j=0; j < ct.size(); j++)
				ret[j] = it.next();
			return ret;
		}
		
		@Override
		public Collection<Object> getTerms() {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void extractTerms(Set terms) {
		}

		@Override
		public String toString(String field) {
			StringBuffer buf = new StringBuffer();
			buf.append(parent.toString()+" ");
			for (SrndQuery c : children)
				buf.append(c.toString()+" ");
			return buf.toString().trim();
		}
		
	}	

}
