package it.mapsgroup.dq.job;

import java.util.Collection;

import it.mapsgroup.dq.bulkwriter.JdbcBulkDataWriter;
import it.mapsgroup.dq.reader.bigexcel.BigExcelRawDataReader;
import it.mapsgroup.dq.vo.ItemVo;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "populator")
public class PopulatorImpl implements Populator {
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired BigExcelRawDataReader excelReader;
	@Autowired JdbcBulkDataWriter jdbcBulkDataWriter;

	@Override
	public int populateMaterials(String fileName, String sheetId, boolean firstRowIsHeader) throws Exception {
		//
		//	First empty all tables (in the correct order)
		//
		//jdbcBulkDataWriter.truncate("PRODUCT_GROUP", "MATERIAL");
		jdbcBulkDataWriter.truncate("MATERIAL");
		
		//
		//	Read all records from Excel
		//
		log.info("Start reading from " + fileName);
		Collection<ItemVo> items = excelReader.readAllItems(fileName, sheetId, firstRowIsHeader);
		
		int numTot = items.size();
		log.info("Number of records read from " + fileName + ": " + numTot);
		
		//
		//	Inserting in DB
		//
		int numSuccess = 0;
		int numFailures = 0;
		for (ItemVo item : items) {
			try {
				jdbcBulkDataWriter.insertItem(item);
				numSuccess ++;
			} catch (Exception e) {
				log.error("Unable to insert item " + item.getItemCode(), e);
				numFailures ++;
			}
		}
		
		//
		//	Write a short report
		//
		log.info("Execution complete:\r\n" +
				"\ttot processed records ... " + numTot + "\r\n" +
				"\tsuccessfully processed .. " + numSuccess + "\r\n" +
				"\tfailures ................ " + numFailures + "\r\n" +
				"");
		
		return numSuccess;
	}

}
