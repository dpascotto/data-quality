package it.intext.pattern.runnable;

import it.intext.pattern.analyzer.PatternMatchAnalyzer;
import it.intext.pattern.dsl.DslLoader;
import it.intext.pattern.gindex.SemanticIndex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;

public class TestPatterMatcher {

	static final String ruleFile = "test/grammar.gprset";
	
	public static void main(String[] args) throws IOException {
		
		it.intext.pattern.abs.RuleSet set = DslLoader.loadRules(ruleFile);
		Analyzer analyzer = new PatternMatchAnalyzer();
		//Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_29);
		InputStreamReader isReader = new InputStreamReader(System.in);
		BufferedReader bufReader = new BufferedReader(isReader);
		while (true) {
			System.out.print("\n\nINPUT> ");
			System.out.flush();
			String line = bufReader.readLine();
			if (line == null || line.length() < 1 
					|| line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit"))
				break;
			SemanticIndex idx = set.createIndex();
			idx.addTextField(analyzer.tokenStream(SemanticIndex.TEXT_FIELD, 
					new StringReader(line)));
			set.execute(idx);
			if (idx.getLayer("HL") != null){
			System.out.println(idx.getLayer("HL").toString(idx));
			}else{
				System.out.println("HL is null!");
			}
		}
	}

}
