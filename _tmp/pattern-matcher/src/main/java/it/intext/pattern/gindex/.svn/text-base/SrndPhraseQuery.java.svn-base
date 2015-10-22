package it.intext.pattern.gindex;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryParser.surround.query.SrndQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

public class SrndPhraseQuery extends SrndQuery{
	
	private String query;
	private Analyzer analyzer;
	QueryParser parser = null;
	
	public SrndPhraseQuery(String query, Analyzer analyzer) {
		this.query = query;
		this.analyzer = analyzer;
		parser = new QueryParser(Version.LUCENE_29,query,
				this.analyzer);
	}

	@Override
	public Query makeLuceneQueryFieldNoBoost(String fieldName,
			BasicQueryFactory qf) {
		if (query != null){
		
		try {
			return parser.parse(query);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		}
		return null;
	}

	@Override
	public String toString() {
		return new String(this.query);
	}

	protected Analyzer getAnalyzer() {
		return analyzer;
	}

	protected void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	protected QueryParser getParser() {
		return parser;
	}

	protected void setParser(QueryParser parser) {
		this.parser = parser;
	}

	

	

}
