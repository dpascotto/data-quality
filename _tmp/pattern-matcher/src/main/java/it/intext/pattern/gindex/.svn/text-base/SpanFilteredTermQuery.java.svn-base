package it.intext.pattern.gindex;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.Spans;

public class SpanFilteredTermQuery extends SpanTermQuery{
	
	private Set<Integer> positions = new TreeSet<Integer>();
	
	public SpanFilteredTermQuery(Term term, Set<Integer> positions) {
		super(term);
		this.positions = positions;
	}
	
	public SpanFilteredTermQuery(Term term, int position) {
		super(term);
		this.positions.add(position);
	}
	
	private static final long serialVersionUID = 4643546087771970023L;
	
	@Override
	public Spans getSpans(IndexReader reader) throws IOException {
		final Spans sp =super.getSpans(reader);
		return new Spans(){

			@Override
			public int doc() {
				return sp.doc();
			}

			@Override
			public int end() {
				return sp.end();
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
				while(true)
				{
					boolean n = sp.next();
					if (!n)
						return n;
					if (positions.contains(sp.start()))
						return true;						
				}
			}

			@Override
			public boolean skipTo(int target) throws IOException {
				return false;
			}

			@Override
			public int start() {
				return sp.start();
			}
			
		};
	}

	protected Set<Integer> getPositions() {
		return positions;
	}

	protected void setPositions(Set<Integer> positions) {
		this.positions = positions;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(positions);
		return sb.toString().trim();
	}
}
