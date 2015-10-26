package it.mapsgroup.dq.reader.bigexcel.mapper;

import it.mapsgroup.dq.vo.ProductGroupVo;
import it.mapsgroup.dq.vo.SaipemFlatDataVo;

import java.util.HashMap;


public class ProductGroupMapper extends ExcelMapper {

	@Override
	public ProductGroupVo map(HashMap row) {
		ProductGroupVo pg = new ProductGroupVo();
		
		pg.setCode(getString(row, "A"));
		pg.setCodeParent(getString(row, "D"));
		pg.setHierarchicalLevel(getInt(row, "B"));
		// Description not available from Excel
			
		return pg;
	}



}
