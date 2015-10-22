package it.intext.pattern.abs;

import it.intext.pattern.abs.Replacer.Scope;
import it.intext.pattern.gindex.IndexChangeObserver;
import it.intext.pattern.gindex.LayerIndex;
import it.intext.pattern.gindex.SemanticIndex;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stage {

	static final Logger logger = LoggerFactory.getLogger(Stage.class);
	private String name;
	private List<Rule> rules = new ArrayList<Rule>();
	static final int MAX_LOOP = 100;
	private int maxLoop = MAX_LOOP;
	private boolean isLoop = true;
	private List<Constrain> constrains = new ArrayList<Constrain>();
	private List<Replacer> replacers = new ArrayList<Replacer>();
	public IndexChangeObserver observer = new IndexChangeObserver(){
		boolean hasChanged = false;
		boolean locked = false;

		public boolean isHasChanged() {
			return hasChanged;
		}

		public void setHasChanged(boolean hasChanged) {
			this.hasChanged = hasChanged;
		}

		public void indexChanged() {
			if (locked){
				logger.trace("IndexChange not registered, observer locked");
				return;
			}
			hasChanged = true;
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

	};
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Rule> getRules() {
		return rules;
	}
	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}
	public int getMaxLoop() {
		return maxLoop;
	}
	public void setMaxLoop(int maxLoop) {
		this.maxLoop = maxLoop;
	}
	public boolean isLoop() {
		return isLoop;
	}
	public void setLoop(boolean isLoop) {
		this.isLoop = isLoop;
	}

	public void executeStage(SemanticIndex idx)
	{

		if (logger.isDebugEnabled()) {
			if ((this.constrains != null) && (this.constrains.size() > 0))
				logger.debug("Applying constraints...");
			else 
				logger.trace("No constraints applicable to this stage.");
		}

		for (Constrain cons : this.constrains) {
			if (!cons.isSatisfied(idx))
			{
				logger.debug("{} not satisfied.",cons);
				return;
			}
		}

		logger.debug("Applying replacers...");		
		for (Replacer rep : this.replacers){
			logger.trace("Applying replacer {}",rep);
			rep.applyOnIndex(idx);
		}

		boolean looped = true;
		this.observer.setHasChanged(false);
		int loopCount = 0;
		while(looped)
		{
			loopCount++;
			looped = false;
			logger.debug("Going through stage {}. Loop #{}.", this.name, loopCount);
			for (Rule rule : rules)
			{
				if (!(rule.isOneShot() && loopCount > 1)){
					if (logger.isTraceEnabled()) {
						logger.trace("Applying rule {}.", rule);
					}
					else logger.debug("Applying rule {}.", rule.getName());

					rule.applyRule(idx);
					looped |= this.observer.isHasChanged() && isLoop && loopCount <= MAX_LOOP;
				}
				this.observer.setHasChanged(false);				
			}

		}
		logger.trace("Cleaning stage replacer..");
		LayerIndex lidx = idx.getLayer(Replacer.getLayer(Scope.STAGE));
		if (lidx != null)
			lidx.clear();
	}
	public List<Constrain> getConstrains() {
		return constrains;
	}
	public void setConstrains(List<Constrain> constrains) {
		this.constrains = constrains;
	}
	public List<Replacer> getReplacers() {
		return replacers;
	}
	public void setReplacers(List<Replacer> replacers) {
		this.replacers = replacers;
	}

	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(name+" (loop:"+isLoop+", maxloop:"+maxLoop+")\n");
		for (Replacer rp : replacers)
		{
			buf.append("\t"+rp.toString()+"\n");
		}
		for (Constrain c : this.constrains)
		{
			buf.append("\t"+c.toString()+"\n");
		}
		for (Rule rule : rules)
		{
			buf.append("\t"+rule.toString()+"\n");
		}
		return buf.toString().trim();
	}
}
