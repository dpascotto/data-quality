package it.mapsgroup.dq.vo;

import java.io.Serializable;

public class MachineVo implements Serializable {
	
	/**
	 * The 14-character code uniquely identifying a machine. PK
	 * E.g. "C1819A00Z110A0"
	 */
	private String machineCode;
	
	/**
	 * The 5-letter code referencing the manufacturer.
	 * E.g. "C0059"
	 */
	private String manufacturerCode;
	
	/**
	 * The referende to the manufacturer
	 */
	private ManufacturerVo manufacturer;
	
	/**
	 * The code of the model, e.g. "A41"
	 */
	private String model;
	
	private String groupCode;
	
	/**
	 * The reference to the group instance
	 */
	private GroupVo group;
	
	private String subgroupCode;
	
	/**
	 * The reference to the subgroup
	 */
	private SubgroupVo subgroup;

	public String getModelFromMachineCode() {
		if (machineCode == null || machineCode.length() == 0) {
			throw new RuntimeException("Cannot extract model from machineCode code since it's null or empty");
		}
		return machineCode.substring(5, 8);
	}
	
	public String getManufacturerFromMachineCode() {
		if (machineCode == null || machineCode.length() == 0) {
			throw new RuntimeException("Cannot extract manufacturer from machineCode code since it's null or empty");
		}
		return machineCode.substring(0, 5);
	}
	public String getGroupFromMachineCode() {
		if (machineCode == null || machineCode.length() == 0) {
			throw new RuntimeException("Cannot extract group from machineCode code since it's null or empty");
		}
		return machineCode.substring(8, 12);
	}
	public String getSubgroupFromMachineCode() {
		if (machineCode == null || machineCode.length() == 0) {
			throw new RuntimeException("Cannot extract subgroup from machineCode code since it's null or empty");
		}
		return machineCode.substring(12, 14);
	}
	
	public void validate() {
		String x = "";
		if (!manufacturerCode.equals(getManufacturerFromMachineCode())) {
			x += " manufacturerCode " + manufacturerCode + " does not match with the one extracted from machine code: " + getManufacturerFromMachineCode();
		}
		
		if (!model.equals(getModelFromMachineCode())) {
			x += " model " + model + " does not match with the one extracted from machine code: " + getModelFromMachineCode();
		}
		
		if (!groupCode.equals(getGroupFromMachineCode())) {
			x += " group " + groupCode + " does not match with the one extracted from machine code: " + getGroupFromMachineCode();
		}
		
		if (!subgroupCode.equals(getSubgroupFromMachineCode())) {
			x += " subgroup " + subgroupCode + " does not match with the one extracted from machine code: " + getSubgroupFromMachineCode();
		}
		
		if (x.length() > 0) {
			throw new RuntimeException("Problems handling machine code " + machineCode + ": " + x);
		}
	}
	
	public static void main(String[] args) {
		MachineVo m = new MachineVo();
		
		m.setMachineCode("C1819A00Z110A0");
		m.setManufacturerCode("C1819");
		m.setModel("A00");
		m.setGroupCode("Z110");
		m.setSubgroupCode("A0");
		
		m.validate();
	}
	


	@Override
	public String toString() {
		return "MachineVo [machineCode=" + machineCode + "]";
	}

	public String getMachineCode() {
		return machineCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	public String getManufacturerCode() {
		return manufacturerCode;
	}

	public void setManufacturerCode(String manufacturerCode) {
		this.manufacturerCode = manufacturerCode;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
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

	public ManufacturerVo getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(ManufacturerVo manufacturer) {
		this.manufacturer = manufacturer;
	}

	public GroupVo getGroup() {
		return group;
	}

	public void setGroup(GroupVo group) {
		this.group = group;
	}

	public SubgroupVo getSubgroup() {
		return subgroup;
	}

	public void setSubgroup(SubgroupVo subgroup) {
		this.subgroup = subgroup;
	}

	


	

}
