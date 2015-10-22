package it.mapsgroup.dq.vo;

public class ProductGroupVo {
	/**
	 * The code (unique identifier) of the product group
	 */
	private String code;
	

	/**
	 * The short description of the unit
	 */
	private String description;


	@Override
	public String toString() {
		return "ProductGroupVo [code=" + code + ", description=" + description
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
