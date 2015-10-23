package it.mapsgroup.dq.job;

import it.mapsgroup.dq.bulkwriter.JdbcBulkDataWriter;
import it.mapsgroup.dq.reader.bigexcel.BigExcelRawDataReader;
import it.mapsgroup.dq.vo.ItemVo;
import it.mapsgroup.dq.vo.SaipemFlatDataVo;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("unchecked")
@Service(value = "populator")
public class PopulatorImpl implements Populator {
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired BigExcelRawDataReader excelReader;
	@Autowired JdbcBulkDataWriter jdbcBulkDataWriter;

	@Override
	public int populateFlatTable(String fileName, String sheetId, boolean firstRowIsHeader) throws Exception {
		//
		//	First empty flat data table
		//
		jdbcBulkDataWriter.truncate("SAIPEM_FLAT_DATA");
		
		//
		//	Read all records from Excel
		//
		log.info("Start reading from " + fileName);
		Collection<SaipemFlatDataVo> items = excelReader.readAllItemsFlat(fileName, sheetId, firstRowIsHeader);
		
		int numTot = items.size();
		log.info("Number of records read from " + fileName + ": " + numTot);
		
		//
		//	Inserting in DB
		//
		int numSuccess = 0;
		int numFailures = 0;
		for (SaipemFlatDataVo item : items) {
			try {
				jdbcBulkDataWriter.insertFlatItem(item);
				numSuccess ++;
				if (numSuccess % 1000 == 0) {
					System.out.println(numSuccess + " records processed");
				}
			} catch (Exception e) {
				log.error("Unable to insert item " + item.getItemCode(), e);
				numFailures ++;
				
				throw e;
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
	
	@Override
	public int populateMaterials(String fileName, String sheetId, boolean firstRowIsHeader) throws Exception {
		//
		//	First empty all tables (in the correct order)
		//
		//jdbcBulkDataWriter.truncate("PRODUCT_GROUP", "MATERIAL");
		//jdbcBulkDataWriter.truncate("MATERIAL", "UNIT_OF_MEASURE", "PRODUCT_GROUP");
		jdbcBulkDataWriter.truncate("SAIPEM_FLAT_DATA");
		//jdbcBulkDataWriter.truncate("MACHINE", "MATERIAL");
		
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
				if (numSuccess % 1000 == 0) {
					System.out.println(numSuccess + " records processed");
				}
			} catch (Exception e) {
				log.error("Unable to insert item " + item.getItemCode(), e);
				numFailures ++;
				
				throw e;
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
