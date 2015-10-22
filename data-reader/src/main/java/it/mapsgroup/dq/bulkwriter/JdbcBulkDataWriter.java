package it.mapsgroup.dq.bulkwriter;

import it.mapsgroup.dq.vo.ItemVo;
import it.mapsgroup.dq.vo.ProductGroupVo;
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
	public void insertItem(ItemVo item) throws Exception {
		lazyInsertUnitOfMeasure(item.getUnitOfMeasure());
		lazyInsertProductGroup(item.getProductGroup());
		
		int inserted = jdbcTemplate.update("INSERT INTO MATERIAL (CODE, SHORT_DESCRIPTION_EN, TYPE) VALUES (?, ?, ?)",
				item.getItemCode(),
				item.getItemDescription(),
				item.getType()
				);
		
		log.debug(inserted == 1 ? "OK" : "KO");
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
