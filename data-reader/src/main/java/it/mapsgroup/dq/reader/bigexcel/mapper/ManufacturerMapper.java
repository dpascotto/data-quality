package it.mapsgroup.dq.reader.bigexcel.mapper;

import it.mapsgroup.dq.vo.ManufacturerVo;

import java.util.HashMap;

public class ManufacturerMapper extends ExcelMapper {

	@Override
	public ManufacturerVo map(HashMap row) {
		ManufacturerVo man = new ManufacturerVo();
		
		man.setCode(getString(row, "A"));
		man.setName(getString(row, "B"));

		return man;
	}

}
