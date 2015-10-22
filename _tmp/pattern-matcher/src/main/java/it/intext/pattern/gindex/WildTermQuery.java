package it.intext.pattern.gindex;

import it.intext.pattern.gindex.SemanticIndex.SemanticReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryParser.surround.query.DistanceSubQuery;
import org.apache.lucene.queryParser.surround.query.SpanNearClauseFactory;
import org.apache.lucene.queryParser.surround.query.SrndQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.regex.SpanRegexQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;


public class WildTermQuery extends SrndQuery implements DistanceSubQuery{

	private int min = 1;
	private int max;
	/*
	 * WARNING : first dummy implementation, only max count
	 */
	public WildTermQuery(int min, int max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public Query makeLuceneQueryFieldNoBoost(String fieldName,
			BasicQueryFactory qf) {
		throw new UnsupportedOperationException("WildTermQuery could not be used as stand-alone query!");
	}

	@Override
	public String toString() {
		return new String("WildTermQuery["+this.min+","+this.max+"]");
	}

	public void addSpanQueries(SpanNearClauseFactory sncf) throws IOException {
		sncf.addSpanNearQuery(multiQuery((SemanticReader)sncf.getIndexReader()));
	}

	public String distanceSubQueryNotAllowed() {
		/*
		 * yes, they are.. ;)
		 */
		return null;
	}

	private SpanQuery multiQuery(SemanticReader reader)
	{
		List<SpanQuery> clauses = new ArrayList<SpanQuery>();
		for (int j=0; j <= max; j++)
			try {
				clauses.add((SpanQuery)new SpanRegexQuery(new Term(SemanticIndex.TEXT_FIELD,".*"))
				.rewrite(reader));
			} catch (IOException e) {
				e.printStackTrace();
			}
		SpanQuery[] clas = new SpanQuery[clauses.size()];
		clas = clauses.toArray(clas);
		return new SpanNearQuery(clas,0,true);
	}
	
	

}



