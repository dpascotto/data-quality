package it.intext.pattern.abs;

import it.intext.pattern.gindex.IndexChangeObserver;
import it.intext.pattern.gindex.SemanticIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleSet {
	static final Logger logger = LoggerFactory.getLogger(RuleSet.class);
	private List<Stage> stages = new ArrayList<Stage>();
	private List<Replacer> replacers = new ArrayList<Replacer>();
	private Map<String,String> scopeReplacers = new HashMap<String,String>();
	
	public List<Stage> getStages() {
		return stages;
	}
	public void setStages(List<Stage> stages) {
		this.stages = stages;
	}
	public List<Replacer> getReplacers() {
		return replacers;
	}
	public void setReplacers(List<Replacer> replacers) {
		this.replacers = replacers;
	}
	
	public SemanticIndex createIndex()
	{
		return new SemanticIndex(new IndexChangeObserver() {
			
			private boolean locked = false;
			
			public void setHasChanged(boolean hasChanged) {
				for (Stage stage : stages)
				{
					stage.observer.setHasChanged(hasChanged);
				}
			}
			
			public boolean isHasChanged() {
				for (Stage stage : stages)
				{
					if (stage.observer.isHasChanged())
						return true;
				}
				return false;
			}
			
			public void indexChanged() {
				if (locked){
					logger.trace("IndexChange not registered, observer locked");
					return;
				}
				for (Stage stage : stages)
				{
					stage.observer.indexChanged();
				}
			}

			public void lockChanges() {
				this.locked = true;				
			}

			public boolean isLockChanges() {
				return this.locked;
			}

			public void unlockChanges() {
				this.locked = false;				
			}
		});
	}
	
	public void execute(SemanticIndex idx){
		
		logger.debug("Execute started.");
		
		logger.debug("Applying document level replacer(s)...");
		
		for (Replacer rep : this.replacers){
			logger.trace("Applying replacer {}",rep);
			rep.applyOnIndex(idx);
			}
		
		for (Stage stage : this.stages)
		{
			logger.debug("Executing stage {}",stage.getName());
			stage.executeStage(idx);
			logger.debug("Stage {} completed.", stage.getName());
		}
		
		logger.debug("Execute completed.");
	}
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("[RuleSet]\n");
		for (Replacer rp : replacers)
		{
			buf.append(rp.toString()+"\n");
		}
		for (Stage stage : stages)
		{
			buf.append(stage.toString()+"\n");
		}		
		return buf.toString().trim();
	}
	public Map<String, String> getScopeReplacers() {
		return scopeReplacers;
	}
	

}
