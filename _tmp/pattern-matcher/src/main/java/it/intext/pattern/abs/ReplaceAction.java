package it.intext.pattern.abs;

import it.intext.pattern.gindex.SemanticIndex;
import it.intext.pattern.grules.QueryMatchParser;
import it.intext.pattern.grules.QueryMatchParser.Result;
import it.intext.pattern.grules.QueryMatchParser.Target;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplaceAction implements Action{

	private List<String> targets = new ArrayList<String>();
	private List<String> addElements = new ArrayList<String>();
	static final Pattern LAYERPATTERN = Pattern.compile("^([A-z0-9]+)#([^#]+?)$"); 
	static final Logger logger = LoggerFactory.getLogger(ReplaceAction.class);

	public List<String> getTargets() {
		return targets;
	}

	public TYPE getType() {
		return TYPE.REPLACE;
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
		this.targets = targets;
	}

	public boolean executeAction(Result result, QueryMatchParser qm) {
		boolean done = false;
		for(String tm : this.targets)
		{
			Target tg = qm.getTarget(tm, result);
			Matcher tMatcher = LAYERPATTERN.matcher(tm);
			if (tMatcher.matches())
			{
				String ttext = tMatcher.group(2);
				String tlayer = tMatcher.group(1);
				for (String term : addElements)
				{
					Matcher matcher = LAYERPATTERN.matcher(term);
					if (matcher.matches())
					{
						String text = matcher.group(2);
						String layer = matcher.group(1);
						SemanticIndex idx = qm.getIndex();
						idx.addLayer(layer);
						try{
							logger.debug("Replacing term {} {}.", tm, tg.getPositions());
							done |= 
								(idx.getLayer(tlayer).removeTerm(ttext, tg.getPositions())
										&&
										idx.getLayer(layer).addTerm(text, tg.getPositions()));
						}catch(Exception e)
						{
							logger.warn("Error replacing : {} - {}",tm,term);
						}

					}
					else
						logger.warn("Unable to apply action for tag : {}",term);
				}
			}
			else
				logger.warn("Unable to apply action for tag : {}",tm);
		}
		return done;

	}	

	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(getType());
		buf.append(" [");
		for (String tg : this.getTargets())
			buf.append(tg+" ");
		buf.append("]");
		return buf.toString();
	}

}
