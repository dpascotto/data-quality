package it.intext.pattern.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;

import it.intext.pattern.dsl.DslLoader;

public class RulesCompiler {
	
	public static it.intext.pattern.abs.RuleSet loadRules(String filename) throws FileNotFoundException {
		XStream xstream = new XStream();
		return (it.intext.pattern.abs.RuleSet) xstream.fromXML(new FileInputStream(filename));
	}
	
	public static void main(String[] args) throws IOException{
		if (args.length < 2){
			printUsage();
			return;
		}
		
		String inputFile = args[0];
		String outputFile = args[1];
		
		it.intext.pattern.abs.RuleSet set = DslLoader.loadRules(inputFile);
		if (set == null){
			System.err.println("Unable to load specified ruleSet : "+inputFile);
			return;
		}
		
		XStream xstream = new XStream();
		File out = new File(outputFile);
		if (out.isDirectory())
		{
			System.err.println(out.getAbsolutePath()+" is a directory, could not continue.");
			return;
		}
		if (out.exists())
			System.err.println("Warning : overwriting "+out.getAbsolutePath());
		FileWriter writer = new FileWriter(outputFile,false);
		xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
		xstream.toXML(set, writer);	
		writer.close();
	}

	private static void printUsage() {
		System.err.println("Usage : RulesCompiler <input_rules> <output_rules>");		
	}

}
