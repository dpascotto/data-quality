/**
 * 
 */
package it.intext.pattern.mapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vieri
 *
 */
public class Utils {
	
	public static List<MappingInfo> filter(List<MappingInfo> infoList, String layer) {
		List<MappingInfo> ret = new ArrayList<MappingInfo>();
		
		for(MappingInfo info : infoList) {
			if (info.getlayer().equalsIgnoreCase(layer)) {
				ret.add(info);
			}
		}
		
		return ret;
	}
	
	public static List<MappingNode> convert(List<MappingInfo> infoList) {
		
		List<MappingNode> ret = new ArrayList<MappingNode>();
		Iterator<MappingInfo> index = infoList.iterator();
		
		MappingNode node = null;
		while ((node = getNext(node, index)) != null)	{
			ret.add(node);
		}
		return ret;
	}
	
	private static MappingNode getNext(MappingNode node, Iterator<MappingInfo>index) {
		
		MappingNode next = getNext(index);
		if (next == null) return null;
		if (node == null) return next;
		
		while ((next != null) && (node.contains(next)))
		{
			node.addChild(next);
			next = getNext(next, index);
		} 
		
		return next;
	} 

	private static MappingNode getNext(Iterator<MappingInfo> index) {		
		return (index.hasNext()) ? new MappingNode(index.next()) : null;
	}
	
	public static List<MappingInfo> convertToInfo(List<MappingNode> nodes) {
		List<MappingInfo> ret = new ArrayList<MappingInfo>();
		
		if ((nodes != null) && (nodes.size() > 0)) {
			for(MappingNode node : nodes) {
				addInfo(ret, node);
			}
		}

		return ret;
	}

	private static void addInfo(List<MappingInfo> ret, MappingNode node) {
		
		ret.add(node.getInfo());
		
		if (!node.isLeaf()) {
			for(MappingNode child : node.children) {
				addInfo(ret, child);
			}
		}
		
	}
}
