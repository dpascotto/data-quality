package it.intext.pattern.gindex;

import java.io.IOException;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryParser.surround.query.DistanceSubQuery;
import org.apache.lucene.queryParser.surround.query.SpanNearClauseFactory;
import org.apache.lucene.queryParser.surround.query.SrndQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.regex.SpanRegexQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;

public class SrndRegexpQuery extends SrndQuery implements DistanceSubQuery{
	
	private Term term;

	public SrndRegexpQuery(Term term) {
		this.term = term;		
	}
	 
	@Override
	public Query makeLuceneQueryFieldNoBoost(String fieldName,
			BasicQueryFactory qf) {
		this.term = new Term(SemanticIndex.TEXT_FIELD,term.text());
		return new SpanRegexQuery(this.term);
	}

	@Override
	public String toString() {
		return new String("SrndRegexpQuery["+this.term.toString()+"]");
	}

	public void addSpanQueries(SpanNearClauseFactory sncf) throws IOException {
		
		SpanQuery clause = (SpanQuery)new SpanRegexQuery(this.term).rewrite(sncf.getIndexReader());
		if (clause != null && clause.getField() != null)
		sncf.addSpanNearQuery(new SpanNearQuery(
				new SpanQuery[]{clause},0,true));
	}

	public String distanceSubQueryNotAllowed() {
		/*
		 * yes, they are.. ;)
		 */
		return null;
	}

}
