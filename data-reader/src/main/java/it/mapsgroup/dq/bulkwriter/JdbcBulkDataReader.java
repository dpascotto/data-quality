package it.mapsgroup.dq.bulkwriter;

import it.mapsgroup.dq.bulkwriter.mapper.SaipemFlatDataRowMapper;
import it.mapsgroup.dq.vo.SaipemFlatDataVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@SuppressWarnings("unchecked")
@Service(value = "jdbc-bulk-data-reader")
public class JdbcBulkDataReader {
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired JdbcTemplate jdbcTemplate;
	
	public Collection<SaipemFlatDataVo> readAllItemsFromFlatTable() {
		
		List<SaipemFlatDataVo> items = jdbcTemplate.query("SELECT * FROM SAIPEM_FLAT_DATA", new SaipemFlatDataRowMapper());
		
		return items;
		
	}

}
