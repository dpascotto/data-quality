package it.mapsgroup.dq.reader.bigexcel.mapper;

import it.mapsgroup.dq.vo.GroupVo;
import it.mapsgroup.dq.vo.ItemVo;
import it.mapsgroup.dq.vo.MachineVo;
import it.mapsgroup.dq.vo.ManufacturerVo;
import it.mapsgroup.dq.vo.ProductGroupVo;
import it.mapsgroup.dq.vo.SubgroupVo;
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
		
		item.setMainPartNumber(getString(row, "R"));
		
		//	Machine
		MachineVo m = new MachineVo();
		m.setMachineCode(getString(row, "Q"));
		
		//	Machine --> Manufacturer
		m.setManufacturerCode(getString(row, "U"));
		m.setModel(m.getModelFromMachineCode()); // Extracting according to rule
		ManufacturerVo man = new ManufacturerVo();
		man.setCode(m.getManufacturerCode());
		m.setManufacturer(man);
		
		//	Machine --> Group
		m.setGroupCode(getString(row, "S"));
		GroupVo g = new GroupVo();
		g.setCode(m.getGroupCode());
		m.setGroup(g);
		
		//	Machine --> Subgroup
		m.setSubgroupCode(getString(row, "T"));
		SubgroupVo s = new SubgroupVo();
		s.setCode(m.getSubgroupCode());
		m.setSubgroup(s);
		
		m.validate();
		item.setMachine(m);

		return item;
	}

}
