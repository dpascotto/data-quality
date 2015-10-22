package it.mapsgroup.dq.vo;

import java.io.Serializable;

public class UnitOfMeasureVo implements Serializable {
	
	/**
	 * The code (unique identifier) of the unit
	 */
	private String code;
	
	/**
	 * An alternate code
	 */
	private String techCode;
	
	/**
	 * The short description of the unit
	 */
	private String description;

	
	
	/*
	 * TO STRING
	 */
	
	@Override
	public String toString() {
		return "UnitOfMeasureVo [code=" + code + ", techCode=" + techCode
				+ ", description=" + description + "]";
	}
	
	/*
	 * GETTERS AND SETTERS
	 */
	
	public String getCode() {
		return code;
	}

	

	public void setCode(String code) {
		this.code = code;
	}

	public String getTechCode() {
		return techCode;
	}

	public void setTechCode(String techCode) {
		this.techCode = techCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	

	

}
