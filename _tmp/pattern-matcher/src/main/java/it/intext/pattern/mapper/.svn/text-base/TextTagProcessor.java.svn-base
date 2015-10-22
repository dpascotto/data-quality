package it.intext.pattern.mapper;

import it.intext.pattern.abs.RuleSet;
import it.intext.pattern.dsl.DslLoader;
import it.intext.pattern.gindex.LayerQuery;
import it.intext.pattern.gindex.SemanticIndex;
import it.intext.pattern.grules.QueryMatchParser;
import it.intext.pattern.grules.QueryMatchParser.Result;
import it.intext.pattern.grules.QueryMatchParser.ResultReport;
import it.intext.pattern.inject.Injector;
import it.intext.pattern.mapper.TagMapper.Mode;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextTagProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(TextTagProcessor.class);
	
	protected RuleSet rules;
	protected List<TagMapper> mappings;
	protected List<Injector> injectors;
	protected List<MappingInfoProcessor> mappingInfoProcessors;
	
	public TextTagProcessor(String ruleUri) {
		this(DslLoader.loadRules(ruleUri));
	}
	
	public TextTagProcessor(RuleSet rules) {
		this.mappings = new ArrayList<TagMapper>();
		this.rules = rules;		
	}
	
	public synchronized void add(TagMapper t) {
		this.mappings.add(t);
	}
	
	public synchronized void add(Injector injector) {
		if (this.injectors == null) {
			this.injectors = new ArrayList<Injector>();
		}
		this.injectors.add(injector);
	}
	
	public synchronized void add(MappingInfoProcessor processor) {
		if (this.mappingInfoProcessors == null) {
			this.mappingInfoProcessors = new ArrayList<MappingInfoProcessor>();
		}
		this.mappingInfoProcessors.add(processor);
	}
	

	public String process(String text, Analyzer analyzer) {
		
		logger.debug("Process started. Text length = {}.", text.length());
		SemanticIndex index = prepareIndex(text, analyzer);
		List<MappingNode> nodes = analyze(text, index);
		// TODO: Eventually, handle overlapping matches
		String ret = mapMatches(index, nodes);
		logger.debug("Process completed.");		
		return ret;
	}
	
	protected SemanticIndex prepareIndex(String text, Analyzer analyzer) {

		logger.debug("Process started. Text length = {}.", text.length());
		SemanticIndex index = rules.createIndex();
		logger.trace("Adding text to semantic index...");
		index.addTextField(analyzer.tokenStream(SemanticIndex.TEXT_FIELD, new StringReader(text)));
		logger.debug("Text field added to index.");
		
		return index;
	} 
	public List<MappingNode> analyze(String text, Analyzer analyzer) {
		return analyze(text, analyzer, null);
	}
	
	public List<MappingNode> analyze(String text, Analyzer analyzer, List<String> layers) {
		
		logger.debug("Process started. Text length = {}.", text.length());
		SemanticIndex index = prepareIndex(text, analyzer);
		return analyze(text, index, layers);
	}
	
	protected List<MappingNode> analyze(String text, SemanticIndex index) {
		return analyze(text, index, null);
	}
	
	protected void runInjectors(String text, SemanticIndex index){
		// Run injectors
		if (this.injectors != null) {
			for(Injector injector : this.injectors) {
				logger.debug("Applying injector {}...", injector.toString());
				injector.inject(text, index);
			}
		}
	}
	
	protected List<MappingNode> analyze(String text, SemanticIndex index, List<String> layers) {

		runInjectors(text, index);
		logger.debug("Injectors added.");
		rules.execute(index);
		logger.debug("Rules executed.");
		
		logger.trace("Applying mappers to index results...");
		
		List<MappingInfo> info = new ArrayList<MappingInfo>();
		
		if(this.mappings != null) {
			for(TagMapper mapper : this.mappings) {
				List<MappingInfo> matches = getMapperMatches(mapper, index);
				if (matches != null) {
					info.addAll(matches);
				}
			}
		}
		
		logger.debug("Mappers applied.");
		
		Collections.sort(info);
		
		if (this.mappingInfoProcessors != null) {
			for(MappingInfoProcessor processor : this.mappingInfoProcessors) {
				info = processor.process(info);
			}
		}
		
		List<MappingNode> nodes = Utils.convert(info);
		
		return nodes;
	}

//	private String mapMatches(String text, List<MappingInfo> info) {
//		StringBuilder sb = new StringBuilder(text.length());
//		int start = 0;
//		for(MappingInfo m : info) {
//			String before = text.substring(start, m.getStart());
//			sb.append(before);
//			sb.append(m.getValue());
//			start = m.getEnd();
//		}
//		
//		if (start < (text.length() - 1)) {
//			sb.append(text.substring(start));
//		}
//		
//		return sb.toString();
//	}

	// Tokens mode

	protected String mapMatches(SemanticIndex index, List<MappingNode> info) {
		StringBuilder sb = new StringBuilder();
		
		int start = 0;

		for(MappingNode node : info) {
			sb.append(emit(node, start, index));
			start = node.getInfo().getEnd() + 1;
		}
		
		if (start < index.getTextLength()) {
			sb.append(getText(start, index.getTextLength(), index));
		}
		
		return sb.toString();
	}	
	
	private String emit(MappingNode node, int start, SemanticIndex index) {
		StringBuilder sb = new StringBuilder();
		MappingInfo info = node.getInfo();
		
		TagMapper.Mode mode = info.getMapper().getMode(); 
		if (mode == Mode.Replace) {
			// In Replace mode outer node takes all
			String before = getText(start, info.getStart(), index); 
			sb.append(before);
			sb.append(" ");
			sb.append(info.getValue());
		}
		else if (mode == Mode.Inject) {
			// In Inject mode no inner nodes are allowed
			String before = getText(start, info.getStart(), index); 
			sb.append(before);
			sb.append(" ");
			sb.append(info.getValue());
		}
		else if (mode == Mode.Annotate) {
			// let's play hierarchy
			sb.append(annotate(node, start, index));
		}
		return sb.toString();
	}
	
	private String annotate(MappingNode node, int start, SemanticIndex index) {
		StringBuilder sb = new StringBuilder();
		MappingInfo info = node.getInfo();

		sb.append(getText(start, info.getStart(), index));
		
		if (node.isLeaf()) {
			sb.append(" ");
			sb.append(info.getValue());
			return sb.toString();
		}
		else {
			int localStart = info.getStart();
			StringBuilder lsb = new StringBuilder();
			for(MappingNode child : node.children) {
				lsb.append(annotate(child, localStart, index));
				localStart = child.getInfo().getEnd() + 1;
			}

			if (localStart <= info.getEnd()) {
				lsb.append(getText(localStart, info.getEnd() + 1, index));
			}
			
			sb.append(info.getMapper().map(lsb.toString()));
		}
		
		return sb.toString();
	}
	
	private String getText(int start, int to, SemanticIndex index) {
		int end = to - 1;
		if (end < start) return " ";
		return " " + index.getTextIn(start, end);
	}
	
	
	
	// Text mode

	@SuppressWarnings("unused")
	private String mapMatches(String text, List<MappingNode> info) {

		StringBuilder sb = new StringBuilder(text.length());
		int start = 0;
		for(MappingNode node : info) {
			sb.append(emit(node, start, text));
			start = node.getInfo().getEnd();
		}
		
		if (start < (text.length() - 1)) {
			sb.append(text.substring(start));
		}
		
		return sb.toString();
		
	}
		
	private String emit(MappingNode node, int start, String text) {
		StringBuilder sb = new StringBuilder();
		MappingInfo info = node.getInfo();
		
		// TODO: Check it out - VE:2010-08-18 Temporary patch for match after text length
		int endBefore = Math.min(text.length(), info.getStart());
		
		TagMapper.Mode mode = info.getMapper().getMode(); 
		if (mode == Mode.Replace) {
			// In Replace mode outer node takes all
			String before = text.substring(start, endBefore);		
			sb.append(before);
			sb.append(info.getValue());
		}
		else if (mode == Mode.Inject) {
			// In Inject mode no inner nodes are allowed
			String before = text.substring(start, endBefore);		
			sb.append(before);
			sb.append(info.getValue());
		}
		else if (mode == Mode.Annotate) {
			// let's play hierarchy
			sb.append(annotate(node, start, text));
		}
		return sb.toString();
	}
	
	private String annotate(MappingNode node, int start, String text) {
		StringBuilder sb = new StringBuilder();
		MappingInfo info = node.getInfo();

		// TODO: Check it out - VE:2010-08-18 Temporary patch for match after text length
		int endBefore = Math.min(text.length(), info.getStart());
		
		String before = text.substring(start, endBefore);		
		sb.append(before);
		
		if (node.isLeaf()) {
			sb.append(info.getValue());
			return sb.toString();
		}
		else {
			int localStart = info.getStart();
			StringBuilder lsb = new StringBuilder();
			for(MappingNode child : node.children) {
				lsb.append(annotate(child, localStart, text));
				localStart = child.getInfo().getEnd();
			}
			// TODO: Check it out - VE:2010-08-18 Temporary patch for match after text length
			int endAfter = Math.min(text.length(), info.getEnd());
			
			if (localStart < endAfter) {
				lsb.append(text.substring(localStart, endAfter));
			}
			
			sb.append(info.getMapper().map(lsb.toString()));
		}
		
		return sb.toString();
	}

	private List<MappingInfo> getMapperMatches(TagMapper mapper, SemanticIndex index) {
		Term term = new Term(mapper.getLayer(), mapper.getTag());
		LayerQuery query = new LayerQuery(term);
		QueryMatchParser parser = new QueryMatchParser(index);
		Result results = parser.getQueryResults(query);
		return resultToMappingInfo(mapper, results);
	}

	private List<MappingInfo> resultToMappingInfo(TagMapper mapper, Result results) {
		
		if ((results == null) || (results.getReports().size() == 0)) {
			return null;
		}

		List<MappingInfo> ret = new ArrayList<MappingInfo>();
		
		for(ResultReport report : results.getReports()) {
			String match = report.getMatchingText();
			// SWITCH TO THIS IF YOU WANT TO WORK WITH OFFSET
//			int start = report.getMatchOffset();
//			int end = (mapper.getMode().equals(TagMapper.Mode.Inject))
//						? report.getMatchOffset()
//						: report.getMatchOffset() + report.getMatchLength();
			int start = report.getMatchStartPosition();
			int end = (mapper.getMode().equals(TagMapper.Mode.Inject))
						? start
						: report.getMatchEndPosition();
			MappingInfo info = new MappingInfo(start, end, match, mapper);
			ret.add(info);
		}
		
		return (ret.size() == 0) ? null : ret;
	}	
}
