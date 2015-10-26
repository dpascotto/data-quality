package it.mapsgroup.dq.vo;

import java.io.Serializable;

public class GroupVo implements Serializable {
	
	private String code;
	
	private String descriptionEn;
	
	private String descriptionIt;

	@Override
	public String toString() {
		return "GroupVo [code=" + code + ", descriptionEn=" + descriptionEn
				+ ", descriptionIt=" + descriptionIt + "]";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
