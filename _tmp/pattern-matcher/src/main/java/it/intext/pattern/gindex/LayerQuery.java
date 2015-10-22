package it.intext.pattern.gindex;

import it.intext.pattern.gindex.LayerIndex.Confidence;
import it.intext.pattern.gindex.SemanticIndex.SemanticReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.queryParser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryParser.surround.query.DistanceSubQuery;
import org.apache.lucene.queryParser.surround.query.SimpleTerm.MatchingTermVisitor;
import org.apache.lucene.queryParser.surround.query.SpanNearClauseFactory;
import org.apache.lucene.queryParser.surround.query.SrndQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;

public class LayerQuery extends SrndQuery implements DistanceSubQuery{

	private Term term;
	private Confidence confidence = Confidence.UNSET;

	public LayerQuery(Term term) {
		this.term = term;
		if (term.text().contains("."))
		{
			try{
				String conf = term.text().substring(term.text().lastIndexOf(".")+1);
				confidence = Confidence.valueOf(conf.toUpperCase());
				this.term = new Term(term.field(),
						term.text().substring(0,term.text().lastIndexOf(".")));
			}catch(Exception e){
				e.printStackTrace();
			};
		}
	}

	private static final long serialVersionUID = 5465307402189460813L;	

	@Override
	public Query makeLuceneQueryFieldNoBoost(String fieldName,
			BasicQueryFactory qf) {
		return new Query(){

			private static final long serialVersionUID = 1L;

			public Query rewrite(IndexReader reader) throws IOException {
				if (!(reader instanceof SemanticReader))
					return new SpanSrndTermQuery(term.text(),false)
				.makeLuceneQueryFieldNoBoost(term.field(), new BasicQueryFactory());
				else
				{
					List<SpanNearQuery> qs = createSpanNear((SemanticReader)reader, term.field());
					SpanQuery[] clauses = new SpanQuery[qs.size()];
					return new SpanOrQuery(qs.toArray(clauses));
				}
			}

			public String toString(String field) {
				return "LayerQuery["+field+"]";
			}

		};
	}

	@Override
	public String toString() {
		return new String("Layer:["+this.term.field()+"] "+this.term.text());
	}




	public void addSpanQueries(final SpanNearClauseFactory sncf) throws IOException {
		visitMatchingTerms(sncf.getIndexReader(),
				sncf.getFieldName(),
				new MatchingTermVisitor() {
			public void visitMatchingTerm(Term term) throws IOException {
				List<SpanNearQuery> spans = createSpanNear((SemanticReader)sncf.getIndexReader(),term.field());
				SpanNearQuery[] sarr = new SpanNearQuery[spans.size()];
				sarr = spans.toArray(sarr);
				SpanOrQuery orq = new SpanOrQuery(sarr);
				SpanNearQuery sq = new SpanNearQuery(new SpanQuery[]{orq},0,true);
				sncf.addSpanNearQuery(sq);			
			}
		});
	}

	public String distanceSubQueryNotAllowed() {
		/*
		 * yes, they are...
		 */
		return null;
	}

	public void visitMatchingTerms(
			IndexReader reader,
			String fieldName,
			MatchingTermVisitor mtv) throws IOException
			{
		/* check term presence in index here for symmetry with other SimpleTerm's */
		if (term.text().equals("*"))
		{
			for (String term : ((SemanticReader)reader).layers.get(this.term.field()).getAllTerms())
			{
				Term tt = new Term(this.term.field(),term);
				TermEnum enumerator = ((SemanticReader)reader).layers.get(this.term.field()).getReader().terms(tt);
				if (enumerator != null){
					try {
						Term it = enumerator.term();						
						mtv.visitMatchingTerm(it);						
					} finally {
						enumerator.close();
					}
				}

			}
		}
		LayerIndex lidx = ((SemanticReader)reader).layers.get(this.term.field());
		if (lidx != null){
			TermEnum enumerator = lidx.getReader().terms(term);
			if (enumerator != null){
				try {
					Term it = enumerator.term();
					if ((it != null)
							&& it.text().equals(term.text())
							&& it.field().equals(term.field())) {
						mtv.visitMatchingTerm(it);
					} else {
						//System.out.println("No term in " + fieldName + " field for: " + toString());
					}
				} finally {
					enumerator.close();
				}
			}
		}
			}

//	private SpanNearQuery createSpanNear(SemanticReader reader, List<SpanQuery> clauses){
//		
//		if (clauses.size() == 1){
//			SpanQuery sq = clauses.get(0);
//			if (sq instanceof SpanFilteredTermQuery){
//				clauses.clear();
//				SpanFilteredTermQuery spt = (SpanFilteredTermQuery)sq;
//				List<Pair<String,String>> tmpl = new ArrayList<Pair<String,String>>();
//				tmpl.add(new Pair<String,String>(term.field(),term.text()));
//				String tr = null;
//				for (int p : spt.getPositions()){
//					tmpl.add(new Pair<String,String>(SemanticIndex.TEXT_FIELD,reader.getTextTermAt(p)));
//					if (tr == null)
//						tr = reader.getTextTermAt(p);
//				}
//				clauses.add(new SpanFilteredTermQuery(
//						new Term(SemanticIndex.TEXT_FIELD,tr),reader.getCommonIndexPositions(tmpl)));
//			}
//		}
//		
//		SpanQuery[] rr = new SpanQuery[clauses.size()];
//		rr = clauses.toArray(rr);
//		//System.err.println(this.term+" : "+new SpanNearQuery(rr,0,true));
//		return new SpanNearQuery(rr,0,true);
//	}

	private List<SpanNearQuery> createSpanNear(SemanticReader reader,String layer)
	{
		List<SpanNearQuery> ret = new ArrayList<SpanNearQuery>();
		if (term.text().equals("*"))
		{
			for (String term : reader.layers.get(layer).getAllTerms())
			{
				ret.addAll(new LayerQuery(new Term(layer,term)).createSpanNear(reader, layer));
			}
			return ret;
		}
		
		try{
			LayerIndex lidx = reader.layers.get(layer);
			if (lidx == null)
				return ret;

			int[] pos = lidx.getPositions(term.text());
			
			/*
			 * segmenting positions
			 */
			
			int lp = -1;
			SortedSet<Integer> currSet = new TreeSet<Integer>();
			for (int p : pos){
				if (((LayerIndex.LayerIndexReader)reader.layers.get(layer).getReader())
						.matchConfidence(term.text(), p, confidence)){
					if (lp >= 0 && p-lp > 1){
						if (!currSet.isEmpty()){
							ret.add(createSpanNear(reader,currSet));
							currSet.clear();
						}
					}
					lp = p;
					currSet.add(p);
				}
			}	
			if (!currSet.isEmpty()){
				ret.add(createSpanNear(reader,currSet));				
			}
		}catch(Exception e){e.printStackTrace();}
		//System.out.println(term+" : "+ret);
		return ret;
	}
	
	private int[] toIntArray(final Collection<Integer> ct){
		int[] ret = new int[ct.size()];
		Iterator<Integer> it = ct.iterator();
		for (int j=0; j < ct.size(); j++)
			ret[j] = it.next();
		return ret;
	}

	private SpanNearQuery createSpanNear(SemanticReader reader,
			SortedSet<Integer> currSet) {
		SpanQuery[] clauses = new SpanQuery[currSet.size()];
		int[] poss = toIntArray(currSet);
		for (int j = 0; j < currSet.size(); j++){
			int position = poss[j];
			clauses[j] = new SpanFilteredTermQuery(
					new Term(SemanticIndex.TEXT_FIELD,reader.getTextTermAt(position)),position);
		}
		return new SpanNearQuery(clauses,0,true);
	}

}
