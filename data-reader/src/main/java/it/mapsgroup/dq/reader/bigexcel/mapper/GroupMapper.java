package it.mapsgroup.dq.reader.bigexcel.mapper;

import it.mapsgroup.dq.vo.GroupVo;

import java.util.HashMap;


public class GroupMapper extends ExcelMapper {

	@Override
	public GroupVo map(HashMap row) {
		GroupVo g = new GroupVo();
		
		g.setCode(getString(row, "A"));
		
		String lang = getString(row, "B");
		if ("EN".equalsIgnoreCase(lang)) {
			g.setDescriptionEn(getString(row, "C"));
		} else {
			g.setDescriptionIt(getString(row, "C"));
		}

		return g;
	}



}
