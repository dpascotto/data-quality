package it.mapsgroup.dq.vo;

import java.io.Serializable;

public class SubgroupVo implements Serializable {
	
	private String groupCode;
	
	private String subgroupCode;
	
	private String descriptionEn;
	
	private String descriptionIt;

	@Override
	public String toString() {
		return "SubgroupVo [groupCode=" + groupCode + ", subgroupCode="
				+ subgroupCode + ", descriptionEn=" + descriptionEn
				+ ", descriptionIt=" + descriptionIt + "]";
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getSubgroupCode() {
		return subgroupCode;
	}

	public void setSubgroupCode(String subgroupCode) {
		this.subgroupCode = subgroupCode;
	}

	public String getDescriptionEn() {
		return descriptionEn;
	}

	public void setDescriptionEn(String descriptionEn) {
		this.descriptionEn = descriptionEn;
	}

	public String getDescriptionIt() {
		return descriptionIt;
	}

	public void setDescriptionIt(String descriptionIt) {
		this.descriptionIt = descriptionIt;
	}

	
	

}
