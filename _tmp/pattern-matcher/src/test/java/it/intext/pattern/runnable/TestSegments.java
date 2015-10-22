package it.intext.pattern.runnable;

import it.intext.pattern.analyzer.PatternMatchAnalyzer;
import it.intext.pattern.gindex.IntextSurroundQueryParser;
import it.intext.pattern.gindex.SemanticIndex;
import it.intext.pattern.grules.QueryMatchParser;
import it.intext.pattern.grules.QueryMatchParser.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.surround.parser.ParseException;
import org.apache.lucene.queryParser.surround.query.SrndQuery;

public class TestSegments {

	public static void main(String[] args) throws IOException, ParseException {
		
		Analyzer analyzer = new PatternMatchAnalyzer();
		final SemanticIndex idx = new SemanticIndex();
		final String subjectField = "Subject test field, useful for testing segment queries";
		final String objectField = "think this is the object, could say hello world or even foo foo";
		final String extendedField = "to be honest, the only thing I need to extend here, is rest time...";
		
		idx.addSegmentToText(analyzer.tokenStream("subject", new StringReader(subjectField)), "subject");
		idx.addSegmentToText(analyzer.tokenStream("object", new StringReader(objectField)), "object");
		idx.addSegmentToText(analyzer.tokenStream("extended", new StringReader(extendedField)), "extended");
		
		idx.addLayer("TEST");
		idx.getLayer("TEST").addTerm("T1", 2);
		idx.addLayer("TEST2");
		idx.getLayer("TEST2").addTerm("T2", 2);
		
		IntextSurroundQueryParser parser = new IntextSurroundQueryParser(analyzer);
		QueryMatchParser qm = new QueryMatchParser(idx);
		
		InputStreamReader isReader = new InputStreamReader(System.in);
		BufferedReader bufReader = new BufferedReader(isReader);
		while (true) {
			System.out.print("\n\nINPUT> ");
			System.out.flush();
			String line = bufReader.readLine();
			if (line == null || line.length() < 1 
					|| line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit"))
				break;
			/*
			 * sample query : 
			 * (extended:be w honest) and (subject:field)
			 */
			SrndQuery query = parser.parse(line);			
			Result result = qm.getQueryResults(query);
			System.out.println(result);
			System.out.println(qm.getTarget("SEGMENT#subject", result));
		}
	}

}
