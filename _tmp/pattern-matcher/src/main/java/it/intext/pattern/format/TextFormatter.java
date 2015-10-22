package it.intext.pattern.format;

import it.intext.pattern.abs.RuleSet;
import it.intext.pattern.analyzer.PatternMatchAnalyzer;
import it.intext.pattern.dsl.DslLoader;
import it.intext.pattern.inject.Injector;
import it.intext.pattern.mapper.FixedTextMapper;
import it.intext.pattern.mapper.TagMapper;
import it.intext.pattern.mapper.TextMapper;
import it.intext.pattern.mapper.TextTagProcessor;

import org.apache.lucene.analysis.Analyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextFormatter {

	private static final Logger logger = LoggerFactory.getLogger(TextFormatter.class);
	
	public static final String DEFAULT_RULEFILE = "rules/format.gprset";
	public static String DEFAULT_PATTERN = "(?i)[\\w\\dàáâãäåæçèéêëìíîïðñòóôõöùúûüýÿÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖÙÚÛÜÝ@&ß\\$_#]+(['‘'’])?|[\"\\[\\]\\(\\)\\.,;\\:'‘'’„“”«»<>/\n\r\t\\-]";
	// static String DEFAULT_PATTERN = "(?i)[\\w\\dàáâãäåæçèéêëìíîïðñòóôõöùúûüýÿÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖÙÚÛÜÝ@&ß\\$_#]+|[\"\\[\\]\\(\\)\\.,;\\:'‘'’„“”«»<>/\n\r\t\\-]";

	protected Analyzer analyzer;
	protected RuleSet ruleSet;
	protected TextTagProcessor tagProcessor;
	
	public TextFormatter() {
		this(TextFormatter.DEFAULT_RULEFILE, DEFAULT_PATTERN);
	}

	public TextFormatter(String ruleFileUri) {
		this(ruleFileUri, new PatternMatchAnalyzer(DEFAULT_PATTERN));
	}
	
	public TextFormatter(String ruleFileUri, String pattern) {
		this(ruleFileUri, new PatternMatchAnalyzer(pattern));
	}

	public TextFormatter(Analyzer analyzer) {
		this(TextFormatter.DEFAULT_RULEFILE, analyzer);
	}
	
	public TextFormatter(String ruleFileUri, Analyzer analyzer) {
		logger.trace("Loading rule file from uri: {} ...", ruleFileUri);
		this.ruleSet = DslLoader.loadRules(ruleFileUri);
		this.analyzer = analyzer;
		logger.trace("Running setup method...");
		setup();
		logger.debug("TextFormatter({}) created.", ruleFileUri);
	}

	protected void setup() {
		this.tagProcessor = createTextTagProcessor();		
	}
	
	protected TextTagProcessor createTextTagProcessor() {

		logger.trace("Creating TextTagProcessor...");
		TextTagProcessor processor = new TextTagProcessor(this.ruleSet);

		logger.trace("Creating TextMappers...");
		TextMapper spacer = new FixedTextMapper(" ");
		TextMapper nuller = new FixedTextMapper("");
		TextMapper joiner = new FixedTextMapper("||JOIN||");
		TextMapper eop = new FixedTextMapper(" ||EOP||\n||BOP|| ");

		logger.trace("Adding TextMappers to TextTagProcessor...");
		processor.add(new TagMapper("F", "NEWLINE", spacer));
		processor.add(new TagMapper("F", "LINEBREAK", joiner));
		processor.add(new TagMapper("F", "SKIP", nuller));
		processor.add(new TagMapper("F", "PARA", eop));
		
		return processor;
	}
	
	public synchronized void addMapper(TagMapper mapper) {
		this.tagProcessor.add(mapper);
	}
	
	public synchronized void addInjector(Injector injector) {
		this.tagProcessor.add(injector);
	}
	
	
	public String process(String text) {
		if ((text == null) || (text.length() == 0)) {
			logger.info("Cannot process a null or empty text.");
			return null;
		}
		
		logger.info("Processing text ({} chars)... ", text.length());
		StringBuilder ret = new StringBuilder("||BOP|| ");
		String proc = tagProcessor.process(text, this.analyzer);
		if (proc != null) { 
			proc = proc.replace(" ||JOIN|| ", "");
			if (proc.endsWith("||BOP|| ")) proc = proc.substring(0, proc.length() - "||BOP|| ".length());
			ret.append(proc);
			if (!proc.trim().endsWith("||EOP||")) ret.append(" ||EOP||"); 
		}
		
		logger.info("Process completed.");
		return ret.toString();
	}
}
