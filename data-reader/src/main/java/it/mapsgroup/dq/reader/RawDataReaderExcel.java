package it.mapsgroup.dq.reader;

import it.mapsgroup.dq.vo.ItemVo;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service(value = "raw-data-reader-excel")
public class RawDataReaderExcel implements RawDataReader {
	
	Logger log = Logger.getLogger(this.getClass());

	@Override
	public Collection<ItemVo> readAllItems(String file) {
		
		log.info("All items read");
		return null;
	}

}
