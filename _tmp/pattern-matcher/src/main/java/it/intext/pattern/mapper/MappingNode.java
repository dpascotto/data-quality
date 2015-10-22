package it.intext.pattern.mapper;

import java.util.ArrayList;
import java.util.List;

public class MappingNode {

	protected MappingInfo info;
	protected List<MappingNode> children;
	 
	public MappingNode(MappingInfo info) {
		this.info = info;
	} 
	
	public void addChild(MappingNode node) {
		if (this.children == null) this.children = new ArrayList<MappingNode>();
		this.children.add(node);
	}	
	
	boolean contains(MappingNode node) {
		return this.info.contains(node.info);
	}
	
	boolean isLeaf() {
		return (this.children == null);
	}
	
	public MappingInfo getInfo() {
		return this.info;
	}

	public List<MappingNode> getChildren() {
		return children;
	}

}
