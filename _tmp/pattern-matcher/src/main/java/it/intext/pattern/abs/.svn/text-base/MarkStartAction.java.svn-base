package it.intext.pattern.abs;

import it.intext.pattern.gindex.SemanticIndex;
import it.intext.pattern.gindex.LayerIndex.Confidence;
import it.intext.pattern.grules.QueryMatchParser;
import it.intext.pattern.grules.QueryMatchParser.Result;
import it.intext.pattern.grules.QueryMatchParser.ResultReport;
import it.intext.pattern.grules.QueryMatchParser.Target;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkStartAction implements Action{

	private List<String> addElements = new ArrayList<String>();
	static final Pattern LAYERPATTERN = Pattern.compile("^([A-z0-9]+)#([^#]+?)$"); 
	static final Logger logger = LoggerFactory.getLogger(MarkStartAction.class);

	public List<String> getTargets() {
		return null;
	}

	public TYPE getType() {
		return TYPE.ADD;
	}

	public void setParameters(List<String> params) {
		this.addElements = params;		
	}

	public List<String> getAddElements() {
		return addElements;
	}

	public void setAddElements(List<String> addElements) {
		this.addElements = addElements;
	}

	public void setTargets(List<String> targets) {
		throw new RuntimeException("setTarget not supported for Mark Action");
	}

	public boolean executeAction(Result result, QueryMatchParser qm) {
		boolean done = false;
		for(ResultReport repo : result.getReports())
		{
			Target tg = qm.new Target();
			tg.setPositions(new int[]{repo.getMatchStartPosition()});
			for (String term : addElements)
			{
				Matcher matcher = LAYERPATTERN.matcher(term);
				if (matcher.matches())
				{
					String text = matcher.group(2);
					String layer = matcher.group(1);
					SemanticIndex idx = qm.getIndex();
					idx.addLayer(layer);
					if (text.contains("."))
					{
						try{
							String sText = text.substring(0,text.lastIndexOf("."));
							String conf = text.substring(text.lastIndexOf(".")+1);
							done |= idx.getLayer(layer).addTerm(sText, tg.getPositions(),
									Confidence.valueOf(conf.toUpperCase()));
							logger.debug("Start marked: {} {}.", term, tg.getPositions());
							continue;
						}catch(Exception e){}
					}
					done |= idx.getLayer(layer).addTerm(text, tg.getPositions());
				}
				else
					logger.warn("Unable to apply action for tag : {}",term);
			}
		}
		return done;

	}	

	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(getType());
		buf.append(" [");
		buf.append(this.getAddElements());
		buf.append("]");
		return buf.toString();
	}

}
