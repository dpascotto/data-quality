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

public class RemoveAction implements Action{

	private List<String> targets = new ArrayList<String>();
	private String object = null;
	static final Pattern LAYERPATTERN = Pattern.compile("^([A-z0-9]+)#([^#]+?)$"); 
	static final Logger logger = LoggerFactory.getLogger(RemoveAction.class);

	public List<String> getTargets() {
		return targets;
	}

	public TYPE getType() {
		return TYPE.REMOVE;
	}

	public void setParameters(List<String> params) {
		/*
		 * No params here
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
			if (object != null && !object.equals("*")){
				Matcher matcher = LAYERPATTERN.matcher(object);
				if (matcher.matches())
				{
					String text = matcher.group(2);
					String layer = matcher.group(1);
					SemanticIndex idx = qm.getIndex();
					idx.addLayer(layer);
					try{
						logger.debug("Removing term {} {}.", tm, tg.getPositions());
						done |= idx.getLayer(layer).removeTerm(text, tg.getPositions());
					}catch(Exception e)
					{
						logger.warn("Error removing : {}",tm);
					}
				}
			}
			else
			{
				if (object == null || object.equals("*"))
				{
					Matcher matcher = LAYERPATTERN.matcher(tm);
					if (matcher.matches())
					{
						String text = matcher.group(2);
						String layer = matcher.group(1);
						SemanticIndex idx = qm.getIndex();
						idx.addLayer(layer);
						try{
							logger.debug("Removing term {} {}.", tm, tg.getPositions());
							done |= idx.getLayer(layer).removeTerm(text, tg.getPositions());
						}catch(Exception e)
						{
							logger.warn("Error removing : {}",tm);
						}
					}
				}	
				else
					logger.warn("Unable to apply action for tag : {}",tm);
			}

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

	public void setObject(String object) {
		this.object = object;
	}

	public String getObject() {
		return object;
	}
}
