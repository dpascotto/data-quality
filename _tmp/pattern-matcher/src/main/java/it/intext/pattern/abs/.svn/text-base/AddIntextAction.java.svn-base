package it.intext.pattern.abs;

import it.intext.pattern.gindex.SemanticIndex;
import it.intext.pattern.gindex.LayerIndex.Confidence;
import it.intext.pattern.grules.QueryMatchParser;
import it.intext.pattern.grules.QueryMatchParser.Result;
import it.intext.pattern.grules.QueryMatchParser.Target;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddIntextAction implements Action{

	private List<String> targets = new ArrayList<String>();
	private List<String> addElements = new ArrayList<String>();
	static final Pattern LAYERPATTERN = Pattern.compile("^([A-z0-9]+)#([^#]+?)$"); 
	static final Logger logger = LoggerFactory.getLogger(AddIntextAction.class);

	public List<String> getTargets() {
		return targets;
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
		this.targets = targets;
	}

	public boolean executeAction(Result result, QueryMatchParser qm) {
		boolean done = false;
		for(String tm : this.targets)
		{
			Target tg = qm.getTarget(tm, result);
			List<String> matches = new ArrayList<String>();
			int start = -1;
			int last = -1;
			for (int position : tg.getPositions())
			{
				if (start < 0)
					start = position;
				else
				{
					if (position - last > 1)
					{
						matches.add(qm.getIndex().getTextIn(start, last));
						start = position;
					}					
				}
				last = position;
			}
			if (start < last)
				matches.add(qm.getIndex().getTextIn(start, last));
			for (String query : matches){
				try{
					Result tres = qm.getQueryResults(Rule.parser.parse("\""+query+"\""));
					tg = qm.getTarget("*", tres);
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
									continue;
								}catch(Exception e){}
							}
							done |= idx.getLayer(layer).addTerm(text, tg.getPositions());
						}
						else
							logger.warn("Unable to apply action for tag : {}",term);
					}
				}
				catch(Exception e)
				{
					logger.error("Error parsing {}",query);
				}
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

}
