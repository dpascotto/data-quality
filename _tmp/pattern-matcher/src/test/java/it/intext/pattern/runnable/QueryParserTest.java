package it.intext.pattern.runnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import it.intext.pattern.analyzer.PatternMatchAnalyzer;
import it.intext.pattern.gindex.IntextSurroundQueryParser;

public class QueryParserTest {
	
	public static void main(String[] args) throws Exception{
		IntextSurroundQueryParser parser = new IntextSurroundQueryParser(
				new PatternMatchAnalyzer()
				);
		InputStreamReader isReader = new InputStreamReader(System.in);
		BufferedReader bufReader = new BufferedReader(isReader);
		while (true) {
			System.out.print("\n\nINPUT> ");
			System.out.flush();
			String line = bufReader.readLine();
			if (line == null || line.length() < 1 
					|| line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit"))
				break;
			System.out.println(parser.parse(line));
		}
	}

}
