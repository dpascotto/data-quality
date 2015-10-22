package it.mapsgroup.dq.vo;

import java.io.Serializable;

public class ItemVo implements Serializable {
	
	/**
	 * The code (unique identifier) in the source archive
	 */
	private String itemCode;
	
	/**
	 * The short description in English
	 */
	private String itemDescription;
	
	/*
	 * A qualificator telling if the material is a 
	 * ZRIC (spare parts)
	 * ZCON (consumable)
	 * ZFUE (fuel)
	 */
	private String type;
	
	/**
	 * The reference to the UM instance
	 */
	private UnitOfMeasureVo unitOfMeasure;
	
	/**
	 * The reference to the product group instance
	 */
	private ProductGroupVo productGroup;
	
	public String getUnitOfMeasureCode() {
		return getUnitOfMeasure().getCode();
	}
	
	public String getProductGroupCode() {
		return getProductGroup().getCode();
	}
	
	/*
	 * TO STRING
	 */
	@Override
	public String toString() {
		return "ItemVo [itemCode=" + itemCode + ", itemDescription="
				+ itemDescription + "]";
	}
	
	
	/*
	 * GETTERS AND SETTERS
	 */


	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}


	public UnitOfMeasureVo getUnitOfMeasure() {
		return unitOfMeasure;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public void setUnitOfMeasure(UnitOfMeasureVo unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public ProductGroupVo getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(ProductGroupVo productGroup) {
		this.productGroup = productGroup;
	}
	
	

}
