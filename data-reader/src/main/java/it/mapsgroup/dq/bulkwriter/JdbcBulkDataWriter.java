package it.mapsgroup.dq.bulkwriter;

import it.mapsgroup.dq.vo.GroupVo;
import it.mapsgroup.dq.vo.ItemVo;
import it.mapsgroup.dq.vo.MachineVo;
import it.mapsgroup.dq.vo.ManufacturerVo;
import it.mapsgroup.dq.vo.ProductGroupVo;
import it.mapsgroup.dq.vo.SaipemFlatDataVo;
import it.mapsgroup.dq.vo.SubgroupVo;
import it.mapsgroup.dq.vo.UnitOfMeasureVo;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@SuppressWarnings("unchecked")
@Service(value = "jdbc-bulk-data-writer")
public class JdbcBulkDataWriter implements BulkDataWriter {
	
	private Logger log = Logger.getLogger(this.getClass());
	//	TODO generare file specifico per i fault
	private Logger faults = Logger.getLogger(this.getClass());
	
	//@Autowired DataSource dataSource;
	@Autowired JdbcTemplate jdbcTemplate;

	@Override
	public void insertFlatItem(SaipemFlatDataVo item) throws Exception {
		
		int inserted = jdbcTemplate.update("INSERT INTO SAIPEM_FLAT_DATA " +
				" (CODE, SHORT_DESCRIPTION_IT, SHORT_DESCRIPTION_EN, SHORT_DESCRIPTION_ES, TYPE, UM_CODE, UM_TECH_CODE, UM_DESCRIPTION, PG_CODE, PG_SHORT_DES_IT, PG_LONG_DES_IT, PG_SHORT_DES_EN, PG_LONG_DES_EN, PG_SHORT_DES_ES, PG_LONG_DES_ES, LAB, MACHINE_CODE, MAIN_PART_NUMBER, GROUP_CODE, SUBGROUP_CODE, MANUFACTURER_CODE) " +
				" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				_awfulPatch(item.getItemCode()),
				_awfulPatch(item.getItemDescriptionIt()),
				_awfulPatch(item.getItemDescriptionEn()),
				_awfulPatch(item.getItemDescriptionEs()),
				_awfulPatch(item.getType()),
				_awfulPatch(item.getUmCode()),
				_awfulPatch(item.getUmTechCode()),
				_awfulPatch(item.getUmDescription()),
				_awfulPatch(item.getProductGroupCode()),
				_awfulPatch(item.getProductGroupShortDescriptionIt()),
				_awfulPatch(item.getProductGroupLongDescriptionIt()),
				_awfulPatch(item.getProductGroupShortDescriptionEn()),
				_awfulPatch(item.getProductGroupLongDescriptionEn()),
				_awfulPatch(item.getProductGroupShortDescriptionEs()),
				_awfulPatch(item.getProductGroupLongDescriptionEs()),
				_awfulPatch(item.getLab()),
				_awfulPatch(item.getMachineCode()),
				_awfulPatch(item.getMainPartNumber()),
				_awfulPatch(item.getGroupCode()),
				_awfulPatch(item.getSubgroupCode()),
				_awfulPatch(item.getManufacturerCode())
				);
		
		log.debug(inserted == 1 ? "OK" : "KO");
	}
	
	private String _awfulPatch(String val) {
		String nonNull = (val != null ? val : "");
		
		return nonNull;
	}
	
	@Override
	public void insertItem(ItemVo item) throws Exception {
		lazyInsertUnitOfMeasure(item.getUnitOfMeasure());
		lazyInsertProductGroup(item.getProductGroup());
		lazyInsertMachine(item.getMachine());
		
		int inserted = jdbcTemplate.update("INSERT INTO MATERIAL " +
				" (CODE, SHORT_DESCRIPTION_EN, TYPE, UM_CODE, PRODUCT_GROUP_CODE, MACHINE_CODE, MAIN_PART_NUMBER) " +
				" VALUES (?, ?, ?, ?, ? , ?, ?)",
				item.getItemCode(),
				item.getItemDescription(),
				item.getType(),
				item.getUnitOfMeasureCode(),
				item.getProductGroupCode(),
				item.getMachineCode(),
				item.getMainPartNumber() != null ? item.getMainPartNumber() : ""
				);
		
		log.debug(inserted == 1 ? "OK" : "KO");
	}
	
	private void lazyInsertMachine(MachineVo machine) {
		int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM MACHINE WHERE MACHINE_CODE = ?", 
				new Object[] { machine.getMachineCode() },
				Integer.class);
		
		if (count == 0) {
			
			lazyInsertManufacturer(machine.getManufacturer());
			lazyInsertGroup(machine.getGroup());
			lazyInsertSubgroup(machine.getSubgroup());
			//
			//	If not existing, insert
			//
			try {
				int inserted = jdbcTemplate.update("INSERT INTO MACHINE (MACHINE_CODE, MANUFACTURER_CODE, MODEL, GROUP_CODE, SUBGROUP_CODE) VALUES (?, ?, ?, ?, ?)",
						machine.getMachineCode(),
						machine.getManufacturerCode(),
						machine.getModel(),
						machine.getGroupCode(),
						machine.getSubgroupCode()
						);
			} catch (Exception e) {
				log.error("Unable to insert machine " + machine.getMachineCode(), e);
				faults.info("Unable to insert machine " + machine.getMachineCode());
				
				throw e;
			}
		} else {
			// For the moment, do nothing. Next: check consistency
			//	TODO estrarre la UoM e verificare che tech code e descrizione siano consistenti (case insensitive)
		}
	}

	private void lazyInsertManufacturer(ManufacturerVo manufacturer) {
		int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM MANUFACTURER WHERE CODE = ?", 
				new Object[] { manufacturer.getCode() },
				Integer.class);
		
		if (count == 0) {
			//
			//	If not existing, insert
			//
			try {
				int inserted = jdbcTemplate.update("INSERT INTO MANUFACTURER (CODE) VALUES (?)",
						manufacturer.getCode()
						);
			} catch (Exception e) {
				log.error("Unable to insert manufacturer " + manufacturer.getCode(), e);
				faults.info("Unable to insert manufacturer " + manufacturer.getCode());
				
				throw e;
			}
		}
	}

	private void lazyInsertGroup(GroupVo group) {
		int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM G_ROUP WHERE CODE = ?", 
				new Object[] { group.getCode() },
				Integer.class);
		
		if (count == 0) {
			//
			//	If not existing, insert
			//
			try {
				int inserted = jdbcTemplate.update("INSERT INTO G_ROUP (CODE) VALUES (?)",
						group.getCode()
						);
			} catch (Exception e) {
				log.error("Unable to insert group " + group.getCode(), e);
				faults.info("Unable to insert group " + group.getCode());
				
				throw e;
			}
		}
	}

	private void lazyInsertSubgroup(SubgroupVo subgroup) {
		int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM SUBGROUP WHERE CODE = ?", 
				new Object[] { subgroup.getCode() },
				Integer.class);
		
		if (count == 0) {
			//
			//	If not existing, insert
			//
			try {
				int inserted = jdbcTemplate.update("INSERT INTO SUBGROUP (CODE) VALUES (?)",
						subgroup.getCode()
						);
			} catch (Exception e) {
				log.error("Unable to insert subgroup " + subgroup.getCode(), e);
				faults.info("Unable to insert subgroup " + subgroup.getCode());
				
				throw e;
			}
		}
	}

	public void lazyInsertProductGroup(ProductGroupVo productGroup) throws Exception {
		int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM PRODUCT_GROUP WHERE CODE = ?", 
				new Object[] { productGroup.getCode() },
				Integer.class);
		
		if (count == 0) {
			//
			//	If not existing, insert
			//
			try {
				int inserted = jdbcTemplate.update("INSERT INTO PRODUCT_GROUP (CODE, DESCRIPTION) VALUES (?, ?)",
						productGroup.getCode(),
						productGroup.getDescription()
						);
			} catch (Exception e) {
				log.error("Unable to insert unit of measure " + productGroup.getCode(), e);
				faults.info("Unable to insert unit of measure " + productGroup.getCode());
				
				throw e;
			}
		} else {
			// For the moment, do nothing. Next: check consistency
			//	TODO estrarre la UoM e verificare che tech code e descrizione siano consistenti (case insensitive)
		}
	}
	
	@Override
	public void lazyInsertUnitOfMeasure(UnitOfMeasureVo unitOfMeasure) throws Exception {
		int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM UNIT_OF_MEASURE WHERE CODE = ?", 
				new Object[] { unitOfMeasure.getCode() },
				Integer.class);
		
		if (count == 0) {
			//
			//	If not existing, insert
			//
			try {
				int inserted = jdbcTemplate.update("INSERT INTO UNIT_OF_MEASURE (CODE, TECH_CODE, DESCRIPTION) VALUES (?, ?, ?)",
						unitOfMeasure.getCode(),
						unitOfMeasure.getTechCode(),
						unitOfMeasure.getDescription()
						);
			} catch (Exception e) {
				log.error("Unable to insert unit of measure " + unitOfMeasure.getCode(), e);
				faults.info("Unable to insert unit of measure " + unitOfMeasure.getCode());
				
				throw e;
			}
		} else {
			// For the moment, do nothing. Next: check consistency
			//	TODO estrarre la UoM e verificare che tech code e descrizione siano consistenti (case insensitive)
		}
	}

	@Override
	public void truncate(String... tableNames) throws Exception {
		
		for (String tableName : tableNames) {
			try {
				jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
				log.info("Table " + tableName + " successfully truncated");
			} catch (Exception e) {
				log.error("Unable to truncate table " + tableName, e);
				throw e;
			}
		}
		
	}


}
