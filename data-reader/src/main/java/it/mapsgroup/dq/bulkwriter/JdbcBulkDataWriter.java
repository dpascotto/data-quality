package it.mapsgroup.dq.bulkwriter;

import it.mapsgroup.dq.vo.ItemVo;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service(value = "jdbc-bulk-data-writer")
public class JdbcBulkDataWriter implements BulkDataWriter {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired DataSource dataSource;
	@Autowired JdbcTemplate jdbcTemplate;

	@Override
	public void insertItem(ItemVo item) throws Exception {
		//Object[] values = new Object[](2);
		
		int inserted = jdbcTemplate.update("INSERT INTO MATERIAL (CODE, SHORT_DESCRIPTION_EN) VALUES (?, ?)",
				item.getItemCode(),
				item.getItemDescription()
				);
		
		log.debug(inserted == 1 ? "OK" : "KO");

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
