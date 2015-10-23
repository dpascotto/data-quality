package it.mapsgroup.dq.vo;

import java.io.Serializable;

public class SaipemFlatDataVo implements Serializable {
	/**
	 * The code (unique identifier) in the source archive
	 */
	private String itemCode;
	
	/**
	 * The short description in Italian (...)
	 */
	private String itemDescriptionIt;
	/**
	 * The short description in English
	 */
	private String itemDescriptionEn;
	/**
	 * The short description in Spanish (...)
	 */
	private String itemDescriptionEs;
	
	/*
	 * A qualificator telling if the material is a 
	 * ZRIC (spare parts)
	 * ZCON (consumable)
	 * ZFUE (fuel)
	 */
	private String type;
	
	/**
	 * The code of the unit of measure
	 */
	private String umCode;
	
	/**
	 * An alternate code ot the UoM
	 */
	private String umTechCode;
	
	/**
	 * The short description of the UoM (in italian...)
	 */
	private String umDescription;
	
	/*
	 * Codes and multilingual short/long descriptions of product groups
	 */
	
	private String productGroupCode;
	
	private String productGroupShortDescriptionIt;
	private String productGroupLongDescriptionIt;

	private String productGroupShortDescriptionEn;
	private String productGroupLongDescriptionEn;

	private String productGroupShortDescriptionEs;
	private String productGroupLongDescriptionEs;
	
	/**
	 * No idea...
	 */
	private String lab;
	
	private String machineCode;
	
	private String mainPartNumber;
	
	private String groupCode;
	
	private String subgroupCode;
	
	private String manufacturerCode;
	
	/*
	 * GETTERS AND SETTERS
	 */

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemDescriptionIt() {
		return itemDescriptionIt;
	}

	public void setItemDescriptionIt(String itemDescriptionIt) {
		this.itemDescriptionIt = itemDescriptionIt;
	}

	public String getItemDescriptionEn() {
		return itemDescriptionEn;
	}

	public void setItemDescriptionEn(String itemDescriptionEn) {
		this.itemDescriptionEn = itemDescriptionEn;
	}

	public String getItemDescriptionEs() {
		return itemDescriptionEs;
	}

	public void setItemDescriptionEs(String itemDescriptionEs) {
		this.itemDescriptionEs = itemDescriptionEs;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUmCode() {
		return umCode;
	}

	public void setUmCode(String umCode) {
		this.umCode = umCode;
	}

	public String getUmTechCode() {
		return umTechCode;
	}

	public void setUmTechCode(String umTechCode) {
		this.umTechCode = umTechCode;
	}

	public String getUmDescription() {
		return umDescription;
	}

	public void setUmDescription(String umDescription) {
		this.umDescription = umDescription;
	}

	public String getProductGroupCode() {
		return productGroupCode;
	}

	public void setProductGroupCode(String productGroupCode) {
		this.productGroupCode = productGroupCode;
	}

	public String getProductGroupShortDescriptionIt() {
		return productGroupShortDescriptionIt;
	}

	public void setProductGroupShortDescriptionIt(
			String productGroupShortDescriptionIt) {
		this.productGroupShortDescriptionIt = productGroupShortDescriptionIt;
	}

	public String getProductGroupLongDescriptionIt() {
		return productGroupLongDescriptionIt;
	}

	public void setProductGroupLongDescriptionIt(
			String productGroupLongDescriptionIt) {
		this.productGroupLongDescriptionIt = productGroupLongDescriptionIt;
	}

	public String getProductGroupShortDescriptionEn() {
		return productGroupShortDescriptionEn;
	}

	public void setProductGroupShortDescriptionEn(
			String productGroupShortDescriptionEn) {
		this.productGroupShortDescriptionEn = productGroupShortDescriptionEn;
	}

	public String getProductGroupLongDescriptionEn() {
		return productGroupLongDescriptionEn;
	}

	public void setProductGroupLongDescriptionEn(
			String productGroupLongDescriptionEn) {
		this.productGroupLongDescriptionEn = productGroupLongDescriptionEn;
	}

	public String getProductGroupShortDescriptionEs() {
		return productGroupShortDescriptionEs;
	}

	public void setProductGroupShortDescriptionEs(
			String productGroupShortDescriptionEs) {
		this.productGroupShortDescriptionEs = productGroupShortDescriptionEs;
	}

	public String getProductGroupLongDescriptionEs() {
		return productGroupLongDescriptionEs;
	}

	public void setProductGroupLongDescriptionEs(
			String productGroupLongDescriptionEs) {
		this.productGroupLongDescriptionEs = productGroupLongDescriptionEs;
	}

	public String getLab() {
		return lab;
	}

	public void setLab(String lab) {
		this.lab = lab;
	}

	public String getMachineCode() {
		return machineCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	public String getMainPartNumber() {
		return mainPartNumber;
	}

	public void setMainPartNumber(String mainPartNumber) {
		this.mainPartNumber = mainPartNumber;
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

	public String getManufacturerCode() {
		return manufacturerCode;
	}

	public void setManufacturerCode(String manufacturerCode) {
		this.manufacturerCode = manufacturerCode;
	}

	/*
	 * GETTERS AND SETTERS
	 */
	
	

}
