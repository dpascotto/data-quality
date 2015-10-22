package it.intext.pattern.gindex;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;

public class SpanSrndTermQuery extends SpanSimpleTerm {

	public SpanSrndTermQuery(String termText, boolean quoted) {
		super(quoted);
		this.termText = termText;
	}

	private final String termText;
	public String getTermText() {return termText;}

	public Term getLuceneTerm(String fieldName) {
		return new Term(fieldName, getTermText());
	}

	public String toStringUnquoted() {return getTermText();}

	public void visitMatchingTerms(
			IndexReader reader,
			String fieldName,
			MatchingTermVisitor mtv) throws IOException
			{
		/* check term presence in index here for symmetry with other SimpleTerm's */
		TermEnum enumerator = reader.terms(getLuceneTerm(fieldName));
		if (enumerator.docFreq() <= 0)
			return;
		try {
			Term it= enumerator.term(); /* same or following index term */
			if ((it != null)
					&& it.text().equals(getTermText())
					&& it.field().equals(fieldName)) {
				mtv.visitMatchingTerm(it);
			} else {
				//System.out.println("No term in " + fieldName + " field for: " + toString());
			}
		} finally {
			enumerator.close();
		}
			}
}

