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

public class IncreaseConfidenceAction implements Action{

	private List<String> targets = new ArrayList<String>();
	static final Pattern LAYERPATTERN = Pattern.compile("^([A-z0-9]+)#([^#]+?)$"); 
	static final Logger logger = LoggerFactory.getLogger(IncreaseConfidenceAction.class);

	public List<String> getTargets() {
		return targets;
	}

	public TYPE getType() {
		return TYPE.INCREASE_CONFIDENCE;
	}

	public void setParameters(List<String> params) {
		/*
		 * Nothing here, no parameters are expected
		 */
	}

	public void setTargets(List<String> targets) {
		this.targets = targets;
	}

	public boolean executeAction(Result result, QueryMatchParser qm) {
		boolean done = false;
		for(String tm : this.targets)
		{
			Target tg = qm.getTarget(tm, result);
			Matcher matcher = LAYERPATTERN.matcher(tm);
			if (matcher.matches())
			{
				String text = matcher.group(2);
				String layer = matcher.group(1);
				SemanticIndex idx = qm.getIndex();
				idx.addLayer(layer);
				try{
				done |= idx.getLayer(layer).increaseConfidence(text, tg.getPositions());
				}catch(Exception e)
				{
					logger.warn("Error increasing confidence for : {}",tm);
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
