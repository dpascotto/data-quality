package it.mapsgroup.dq.vo;

import java.io.Serializable;

public class SubgroupVo implements Serializable {
	
	private String code;
	
	private String description;

	@Override
	public String toString() {
		return "SubgroupVo [code=" + code + ", description=" + description
				+ "]";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
