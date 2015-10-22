package it.intext.pattern.dsl;

import it.intext.gmatcher.DslStandaloneSetup;
import it.intext.gmatcher.dsl.Action;
import it.intext.gmatcher.dsl.ActionAdd;
import it.intext.gmatcher.dsl.ActionAddIntext;
import it.intext.gmatcher.dsl.ActionDowngrade;
import it.intext.gmatcher.dsl.ActionMarkEnd;
import it.intext.gmatcher.dsl.ActionMarkStart;
import it.intext.gmatcher.dsl.ActionRemove;
import it.intext.gmatcher.dsl.ActionReplace;
import it.intext.gmatcher.dsl.ActionUpgrade;
import it.intext.gmatcher.dsl.Constrain;
import it.intext.gmatcher.dsl.Option;
import it.intext.gmatcher.dsl.Replacer;
import it.intext.gmatcher.dsl.ReplacerInit;
import it.intext.gmatcher.dsl.Rule;
import it.intext.gmatcher.dsl.RuleSet;
import it.intext.gmatcher.dsl.ScopeReplacer;
import it.intext.gmatcher.dsl.Stage;
import it.intext.pattern.abs.AddAction;
import it.intext.pattern.abs.AddIntextAction;
import it.intext.pattern.abs.DecreaseConfidenceAction;
import it.intext.pattern.abs.IncreaseConfidenceAction;
import it.intext.pattern.abs.MarkEndAction;
import it.intext.pattern.abs.MarkStartAction;
import it.intext.pattern.abs.RemoveAction;
import it.intext.pattern.abs.ReplaceAction;
import it.intext.pattern.abs.Replacer.Scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

public class DslLoader {

	static final Logger logger = LoggerFactory.getLogger(DslLoader.class);
	//Simple dsl Ruleset loader
	static final String REPLACER_NORM = "[\\s\\t]*\\:=[\\s\\t]*(.+?)[\\s\\t]*\\.[\\s\\t]*";
	static final Pattern REPLACER_PAT = Pattern.compile(REPLACER_NORM);
	
	static final String CONSTRAIN_NORM = "[\\s\\t]*\\:[\\s\\t]*(.+?)[\\s\\t]*\\.[\\s\\t]*";
	static final Pattern CONSTRAIN_PAT = Pattern.compile(CONSTRAIN_NORM);
	
	static final String PATTERN_NORM = "[\\s\\t]*\\:[\\s\\t]*(.+?)[\\s\\t]*\\.[\\s\\t]*";
	static final Pattern PATTERN_PAT = Pattern.compile(CONSTRAIN_NORM);
	
	private static String normalizePattern(Object in){
		
		if (in instanceof String)
		{
			/*replacer ?*/
			Matcher matcher = REPLACER_PAT.matcher((String)in);
			if (matcher.matches())
			{
				return matcher.group(1);
			}
			/*constrain ?*/
			matcher = CONSTRAIN_PAT.matcher((String)in);
			if (matcher.matches())
			{
				return matcher.group(1);
			}			
		}
		if (in instanceof it.intext.gmatcher.dsl.Pattern)
		{
			String ip = ((it.intext.gmatcher.dsl.Pattern)in).getValue();
			Matcher matcher = PATTERN_PAT.matcher(ip);
			if (matcher.matches())
			{
				return matcher.group(1);
			}
		}
		if (in instanceof it.intext.gmatcher.dsl.Scope)
		{
			String ip = ((it.intext.gmatcher.dsl.Scope)in).getValue();
			Matcher matcher = PATTERN_PAT.matcher(ip);
			if (matcher.matches())
			{
				return matcher.group(1);
			}
		}
		if (in instanceof it.intext.gmatcher.dsl.Param)
		{
			String ip = ((it.intext.gmatcher.dsl.Param)in).getValue().get(0);
			return ip;
		}
		if (in instanceof ReplacerInit)
		{
			ReplacerInit ri = (ReplacerInit)in;
			return "@"+ri.getName();
		}
		if (in instanceof it.intext.gmatcher.dsl.ScopeTarget)
		{
			return "$COPE";
		}
				
		//System.out.println(in+" ["+in.getClass()+"]");
		return in.toString();
	}
	
	public static it.intext.pattern.abs.RuleSet mergeRules(it.intext.pattern.abs.RuleSet prev, String rulesURI)
	{
		it.intext.pattern.abs.RuleSet nn = loadRules(rulesURI);
		if (nn != null){
			prev.getReplacers().addAll(nn.getReplacers());
			prev.getStages().addAll(nn.getStages());
		}
		return prev;
	}

	public static it.intext.pattern.abs.RuleSet loadRules(String rulesURI)
	{
		DslStandaloneSetup.doSetup();
		Injector injector = new DslStandaloneSetup().createInjector();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		HashMap<Scope,HashMap<String,String>> repMap = new HashMap<Scope,HashMap<String,String>>();
		repMap.put(Scope.DOCUMENT, new HashMap<String,String>());
		Resource resource = resourceSet.getResource(
				URI.createURI(rulesURI), true);
		RuleSet rset = (RuleSet) resource.getContents().get(0);
		it.intext.pattern.abs.RuleSet ret = new it.intext.pattern.abs.RuleSet();
		for (ScopeReplacer srp : rset.getScopereplacer()){
			if (srp.getName().size() > 0)
				ret.getScopeReplacers().put("@@"+srp.getName().get(0).getName(), normalizePattern(srp.getValue()));			
		}
		for (Replacer rep : rset.getReplacer())
		{
			try {
				it.intext.pattern.abs.Replacer rp = new it.intext.pattern.abs.Replacer(
						"@"+rep.getName().get(0).getName(),normalizePattern(rep.getValue()),
						Scope.DOCUMENT, repMap
				);
				ret.getReplacers().add(rp);
				repMap.get(Scope.DOCUMENT).put(rp.getName(), rp.getQuery());
			} catch (Exception e) {
				logger.error("{}",e.getMessage());
			}
		}
		for (Stage stg : rset.getStages())
		{
			it.intext.pattern.abs.Stage stage = new it.intext.pattern.abs.Stage();
			stage.setName(stg.getName());
			for (Option o : stg.getOptions())
			{
				String name = o.getName();
				String value = o.getValue().get(0);
				if (name.equalsIgnoreCase("loop"))
				{
					try{
						stage.setLoop(Boolean.parseBoolean(value.trim()));
					}catch(Exception e)
					{
						logger.error("Error parsing loop value : {}"+value);
					}
				}
				else{
					if (name.equalsIgnoreCase("maxloop"))
					{
						try{
							stage.setMaxLoop(Integer.parseInt(value.trim()));
						}catch(Exception e)
						{
							logger.error("Error parsing maxloop value : {}"+value);
						}
					}
					else
						logger.warn("Unknown option : {}",name);
				}
				for (Constrain ct : stg.getConstrains())
				{
					it.intext.pattern.abs.Constrain cont = 
						new it.intext.pattern.abs.Constrain(normalizePattern(ct.getValue()),repMap);
					stage.getConstrains().add(cont);
				}
				repMap.put(Scope.STAGE, new HashMap<String,String>());
				for (Replacer srep : stg.getReplacers())
				{
					it.intext.pattern.abs.Replacer rp;
					try {
						rp = new it.intext.pattern.abs.Replacer(
								"@"+srep.getName().get(0).getName(),normalizePattern(srep.getValue()),
								Scope.STAGE, repMap
						);
						stage.getReplacers().add(rp);							
						repMap.get(Scope.STAGE).put(rp.getName(), rp.getQuery());							
					} catch (Exception e) {
						logger.error("{}",e.getMessage());
					}						
				}
				for (Rule rl : stg.getRules())
				{
					it.intext.pattern.abs.Rule rule = new it.intext.pattern.abs.Rule(rl.getName());
					for (Option ro : rl.getOptions())
					{
						if (ro.getName().equalsIgnoreCase("oneshot")
								|| ro.getName().equalsIgnoreCase("one-shot"))
							try{
								rule.setOneShot(Boolean.parseBoolean(ro.getValue().get(0)));
							}catch(Exception e)
							{
								logger.error("Error parsing one-shot parameter {}",ro.getValue().get(0));
								continue;
							}
					}
					for (Constrain ct : rl.getConstrains())
					{
						it.intext.pattern.abs.Constrain cont = 
							new it.intext.pattern.abs.Constrain(normalizePattern(ct.getValue()),repMap);
						rule.getConstrains().add(cont);
					}
					repMap.put(Scope.RULE, new HashMap<String,String>());
					for (Replacer srep : rl.getReplacers())
					{
						it.intext.pattern.abs.Replacer rp;
						try {
							rp = new it.intext.pattern.abs.Replacer(
									"@"+srep.getName().get(0).getName(),normalizePattern(srep.getValue()),
									Scope.RULE, repMap
							);
							rule.getReplacers().add(rp);
							repMap.get(Scope.RULE).put(rp.getName(), rp.getQuery());		
						} catch (Exception e) {
							logger.error("{}",e.getMessage());
						}						
					}
					for (it.intext.gmatcher.dsl.Scope scope : rl.getScope())
					{
						rule.setScope(normalizePattern(scope),ret.getScopeReplacers(),repMap);
					}
					List<String> queries = new ArrayList<String>();					
					for (it.intext.gmatcher.dsl.Pattern pt : rl.getQueries())
					{
						queries.add(normalizePattern(pt));
					}
					rule.setQueries(queries, repMap);
					for (Action a : rl.getActions())
					{
						if (a instanceof ActionAdd)
						{
							ActionAdd ad = (ActionAdd)a;
							AddAction add = new AddAction();
							if (ad.getTarget().size() > 0)
								add.getTargets().add(normalizePattern(ad.getTarget().get(0)));
							else
								add.getTargets().add("*");
							add.getAddElements().add(ad.getParams().get(0).getValue().get(0));
							rule.getActions().add(add);
						}
						if (a instanceof ActionAddIntext)
						{
							ActionAddIntext ad = (ActionAddIntext)a; 
							AddIntextAction add = new AddIntextAction();
							if (ad.getTarget().size() > 0)
								add.getTargets().add(normalizePattern(ad.getTarget().get(0)));
							else
								add.getTargets().add("*");
							add.getAddElements().add(ad.getParams().get(0).getValue().get(0));
							rule.getActions().add(add);
						}
						if (a instanceof ActionRemove)
						{
							ActionRemove ad = (ActionRemove)a;
							RemoveAction add = new RemoveAction();
							if (ad.getTarget().size() > 0)
								add.getTargets().add(normalizePattern(ad.getTarget().get(0)));
							else
								add.getTargets().add("*");
							if (ad.getParams() != null){
							for (it.intext.gmatcher.dsl.Param param : ad.getParams())
								add.setObject(param.getValue().get(0));
							}
							rule.getActions().add(add);
						}
						if (a instanceof ActionReplace)
						{
							ActionReplace ad = (ActionReplace)a;
							ReplaceAction add = new ReplaceAction();
							if (ad.getTarget().size() > 0)
								add.getTargets().add(normalizePattern(ad.getTarget().get(0)));
							else
								add.getTargets().add("*");
							add.getAddElements().add(ad.getParams().get(0).getValue().get(0));
							rule.getActions().add(add);
						}
						if (a instanceof ActionUpgrade)
						{
							ActionUpgrade ad = (ActionUpgrade)a;
							IncreaseConfidenceAction add = new IncreaseConfidenceAction();
							if (ad.getTarget().size() > 0)
								add.getTargets().add(normalizePattern(normalizePattern(ad.getTarget().get(0))));							
							else
								add.getTargets().add("*");	
							rule.getActions().add(add);
						}
						if (a instanceof ActionDowngrade)
						{
							ActionDowngrade ad = (ActionDowngrade)a;
							DecreaseConfidenceAction add = new DecreaseConfidenceAction();
							if (ad.getTarget().size() > 0)
							{
								add.getTargets().add(normalizePattern(ad.getTarget().get(0).toString()));
							}
							else
								add.getTargets().add("*");	
							rule.getActions().add(add);
						}
						if (a instanceof ActionMarkStart)
						{
							ActionMarkStart am = (ActionMarkStart)a;
							MarkStartAction ma = new MarkStartAction();
							ma.getAddElements().add(am.getParams().get(0).getValue().get(0));
							rule.getActions().add(ma);
						}
						if (a instanceof ActionMarkEnd)
						{
							ActionMarkEnd am = (ActionMarkEnd)a;
							MarkEndAction ma = new MarkEndAction();
							ma.getAddElements().add(am.getParams().get(0).getValue().get(0));
							rule.getActions().add(ma);
						}
						
					}
					stage.getRules().add(rule);
				}
				ret.getStages().add(stage);
			}
		}
		return ret;
	}

	public static void main(String[] args)
	{
		System.out.println(loadRules("file://home/lorenzo/pmengine.gprset"));
	}

}
