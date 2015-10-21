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
	
	

}
