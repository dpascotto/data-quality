package it.intext.pattern.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Attribute;

public class PatternMatchAnalyzer extends Analyzer{

	protected String PATTERN;
	private static String DEFAULT_PATTERN = "[\\w\\dàèéìòù@&ß\\$_\\-#]+(['‘'’])?|[\"\\[\\]\\(\\)\\.,;\\:‘'’„“”«»<>/]";

	public PatternMatchAnalyzer(String pattern){
		this.PATTERN = pattern;
	}

	public PatternMatchAnalyzer(){
		this(DEFAULT_PATTERN);
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		try{
			BufferedReader breader = new BufferedReader(reader);
			StringBuffer buff = new StringBuffer();
			String line;
			while((line = breader.readLine()) != null)
				buff.append(line+"\n");
			Matcher matcher = Pattern.compile(this.PATTERN).matcher(buff.toString());
			return new MatchTokenStream(matcher,buff.toString());
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	protected class MatchTokenStream extends TokenStream{

		private Matcher matcher;
		private String text;

		protected MatchTokenStream(Matcher matcher, String text){
			this.matcher = matcher;
			this.text = text;
		}

		@Override
		public boolean incrementToken() throws IOException {
			return matcher.find();
		}

		@Override
		public void reset() throws IOException {
			matcher.reset();
		}

		@Override
		public Token next() throws IOException {
			Token nTok = new Token();
			return next(nTok);
		}

		@Override
		public Token next(Token reusableToken) throws IOException {
			if (this.incrementToken())
			{
				reusableToken.setStartOffset(matcher.start());
				reusableToken.setEndOffset(matcher.end());
				reusableToken.setPositionIncrement(1);
				String token = text.substring(reusableToken.startOffset(),reusableToken.endOffset());
				//System.out.println("Tokenizer : "+token);
				reusableToken.setTermBuffer(token);
				reusableToken.setTermLength(matcher.end()-matcher.start());				
				return reusableToken;
			}
			else
				return null;			
		}


		@Override
		public Attribute getAttribute(@SuppressWarnings("unchecked")Class attClass) {
			if (attClass.equals(OffsetAttribute.class)){
				Token tok = new Token();
				tok.setStartOffset(matcher.start());
				tok.setEndOffset(matcher.end());
				tok.setPositionIncrement(1);
				String tokenText = text.substring(tok.startOffset(),tok.endOffset());
				tok.setTermBuffer(tokenText);
				tok.setTermLength(matcher.end()-matcher.start());

				final Token token = tok;

				return new OffsetAttribute(){

					public int endOffset() {
						return token.endOffset();
					}

					public void setOffset(int startOffset, int endOffset) {
//						throw new UnimplementedException("");						
					}

					public int startOffset() {
						return token.startOffset();
					}
					
				};
			}
			if (attClass.equals(TermAttribute.class))
			{
				Token tok = new Token();
				tok.setStartOffset(matcher.start());
				tok.setEndOffset(matcher.end());
				tok.setPositionIncrement(1);
				String tokenText = text.substring(tok.startOffset(),tok.endOffset());
				tok.setTermBuffer(tokenText);
				tok.setTermLength(matcher.end()-matcher.start());

				final Token token = tok;

				return new TermAttribute(){

					public char[] resizeTermBuffer(int newSize) {
						throw new UnsupportedOperationException();			
					}

					public void setTermBuffer(String buffer) {
						throw new UnsupportedOperationException();						
					}

					public void setTermBuffer(char[] buffer, int offset,
							int length) {
						throw new UnsupportedOperationException();			

					}

					public void setTermBuffer(String buffer, int offset,
							int length) {
						throw new UnsupportedOperationException();			
					}

					public void setTermLength(int length) {
						throw new UnsupportedOperationException();			
					}

					public String term() {
						return token.term();
					}

					public char[] termBuffer() {
						return token.termBuffer();
					}

					public int termLength() {
						return token.termLength();
					}

				};								
			}
			return super.getAttribute(attClass);
		}
	}

}
