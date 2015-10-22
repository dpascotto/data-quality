package it.intext.pattern.abs;

import it.intext.pattern.grules.QueryMatchParser;
import it.intext.pattern.grules.QueryMatchParser.Result;

import java.util.List;

public interface Action {
	
	public enum TYPE{		
		ADD,
		REMOVE,
		SET_CONFIDENCE,
		INCREASE_CONFIDENCE,
		DECREASE_CONFIDENCE,
		REPLACE,
		MARK
	}
	
	public List<String> getTargets();
	public void setTargets(List<String> targets);
	public TYPE getType();
	public boolean executeAction(Result result, QueryMatchParser qm);
	public void setParameters(List<String> params);
	public String toString();

}
