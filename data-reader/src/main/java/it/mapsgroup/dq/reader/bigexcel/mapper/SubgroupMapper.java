package it.mapsgroup.dq.reader.bigexcel.mapper;

import it.mapsgroup.dq.vo.SubgroupVo;

import java.util.HashMap;


public class SubgroupMapper extends ExcelMapper {

	@Override
	public SubgroupVo map(HashMap row) {
		SubgroupVo sg = new SubgroupVo();
		
		sg.setGroupCode(getString(row, "A"));
		sg.setSubgroupCode(getString(row, "B"));
		
		String lang = getString(row, "C");
		if ("EN".equalsIgnoreCase(lang)) {
			sg.setDescriptionEn(getString(row, "D"));
		} else {
			sg.setDescriptionIt(getString(row, "D"));
		}

		return sg;
	}


}
