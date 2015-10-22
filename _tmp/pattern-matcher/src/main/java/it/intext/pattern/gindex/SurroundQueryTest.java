package it.intext.pattern.gindex;



import it.intext.pattern.grules.QueryMatchParser;
import it.intext.pattern.grules.QueryMatchParser.Result;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.surround.parser.ParseException;
import org.apache.lucene.queryParser.surround.query.SrndQuery;
import org.apache.lucene.util.Version;

public class SurroundQueryTest {

	/*
	 * Now using the surround query
	 * parser, seems to be
	 * what needed, except for the
	 * lack of phrase query 
	 * support!
	 */

	public static void main(String[] args) throws ParseException, IOException
	{

		IntextSurroundQueryParser parser = new IntextSurroundQueryParser(new StandardAnalyzer(Version.LUCENE_29));
		//SrndQuery query = parser.parse("\"acuna matata\"~3");
		//SrndQuery query = parser.parse("OS#UNIX{CONCEPT#SECURITY}{CONCEPT#UNIX} w <l.k.> w &any{1-3}");
		SrndQuery query = parser.parse("w(&any{3}, free, secure, &any{7}) and open w source and OS#UNIX and (CONCEPT#* or OS#*)");
		SemanticIndex index = new SemanticIndex();
		String testText = "NetBSD is a free, secure, and highly portable UNIX-like Open Source operating system available for many platforms, from 64-bit AlphaServers and desktop systems to handheld and embedded devices. Its clean design and advanced features make it excellent in both production and research environments, and it is user-supported with complete source. Many applications are easily available through The NetBSD Packages Collection.";
		index.addTextField(new StandardAnalyzer(Version.LUCENE_29).tokenStream(SemanticIndex.TEXT_FIELD, new StringReader(testText)));
		index.addLayer("CONCEPT");
		index.addLayer("OS");
		index.getLayer("CONCEPT").addTerm("SECURITY", 2);
		index.getLayer("CONCEPT").addTerm("SECURITY", 1);
		index.getLayer("CONCEPT").addTerm("SECURITY", 4);
		
		index.getLayer("CONCEPT").addTerm("SECURITY", 5);
		index.getLayer("OS").addTerm("SECURITY", 2);
		index.getLayer("OS").addTerm("SECURITY", 5);
		index.getLayer("OS").addTerm("UNIX", 5);
		index.getLayer("OS").addTerm("UNIX", 6);

		//long qTime = System.currentTimeMillis();
		QueryMatchParser qm = new QueryMatchParser(index);
		Result result = qm.getQueryResults(query);
		System.out.println(result);
		System.out.println(qm.getTarget("OS#UNIX", result));
	}	

}
