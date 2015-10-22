package it.mapsgroup.dq.reader.bigexcel.mapper;

import it.mapsgroup.dq.vo.ItemVo;
import it.mapsgroup.dq.vo.ProductGroupVo;
import it.mapsgroup.dq.vo.UnitOfMeasureVo;

import java.util.HashMap;

public class ItemMapper extends ExcelMapper {

	@Override
	public ItemVo map(HashMap row) {
		//	Populate an ItemVo
		ItemVo item = new ItemVo();
		item.setItemCode(getString(row, "A"));
		item.setItemDescription(getString(row, "B"));
		item.setType(getString(row, "E"));
		
		//	UM
		UnitOfMeasureVo um = new UnitOfMeasureVo();
		um.setCode(getString(row, "F"));
		um.setTechCode(getString(row, "G"));
		um.setDescription(getString(row, "H"));
		item.setUnitOfMeasure(um);

		//	Product Group
		ProductGroupVo pg = new ProductGroupVo();
		pg.setCode(getString(row, "I"));
		pg.setDescription(getString(row, "M"));
		item.setProductGroup(pg);

		return item;
	}

}
