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
	
	/**
	 * The reference to the machine
	 */
	private MachineVo machine;
	
	/**
	 * The part number flagged as 'main' among those associated to this material
	 */
	private String mainPartNumber;
	
	public String getUnitOfMeasureCode() {
		return getUnitOfMeasure().getCode();
	}
	
	public String getProductGroupCode() {
		return getProductGroup().getCode();
	}
	
	public String getMachineCode() {
		return getMachine().getMachineCode();
	}
	
	public String getManufacturerCode() {
		return getMachine().getManufacturerCode();
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

	public MachineVo getMachine() {
		return machine;
	}

	public void setMachine(MachineVo machine) {
		this.machine = machine;
	}

	public String getMainPartNumber() {
		return mainPartNumber;
	}

	public void setMainPartNumber(String mainPartNumber) {
		this.mainPartNumber = mainPartNumber;
	}
	
	

}
