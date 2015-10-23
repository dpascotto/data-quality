package it.mapsgroup.dq.reader.bigexcel.mapper;

import it.mapsgroup.dq.vo.SaipemFlatDataVo;

import java.util.HashMap;


public class FlatTableMapper extends ExcelMapper {

	@Override
	public SaipemFlatDataVo map(HashMap row) {
		SaipemFlatDataVo sfd = new SaipemFlatDataVo();
		
		sfd.setItemCode(getString(row, "A"));
		sfd.setItemDescriptionIt(getString(row, "B"));
		sfd.setItemDescriptionEn(getString(row, "C"));
		sfd.setItemDescriptionEs(getString(row, "D"));
		sfd.setType(getString(row, "E"));
		sfd.setUmCode(getString(row, "F"));
		sfd.setUmTechCode(getString(row, "G"));
		sfd.setUmDescription(getString(row, "H"));
		sfd.setProductGroupCode(getString(row, "I"));
		sfd.setProductGroupShortDescriptionIt(getString(row, "J"));
		sfd.setProductGroupLongDescriptionIt(getString(row, "K"));
		sfd.setProductGroupShortDescriptionEn(getString(row, "L"));
		sfd.setProductGroupLongDescriptionEn(getString(row, "M"));
		sfd.setProductGroupShortDescriptionEs(getString(row, "N"));
		sfd.setProductGroupLongDescriptionEs(getString(row, "O"));
		sfd.setLab(getString(row, "P"));
		sfd.setMachineCode(getString(row, "Q"));
		sfd.setMainPartNumber(getString(row, "R"));
		sfd.setGroupCode(getString(row, "S"));
		sfd.setSubgroupCode(getString(row, "T"));
		sfd.setManufacturerCode(getString(row, "U"));
			
		return sfd;
	}

}
