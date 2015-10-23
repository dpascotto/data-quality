package it.mapsgroup.dq.vo;

import java.io.Serializable;

public class ManufacturerVo implements Serializable {
	
	private String code;
	
	private String name;
	

	@Override
	public String toString() {
		return "ManufacturerVo [code=" + code + ", name=" + name + "]";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
