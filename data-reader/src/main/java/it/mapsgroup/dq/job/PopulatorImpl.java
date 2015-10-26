package it.mapsgroup.dq.job;

import it.mapsgroup.dq.bulkwriter.JdbcBulkDataReader;
import it.mapsgroup.dq.bulkwriter.JdbcBulkDataWriter;
import it.mapsgroup.dq.reader.bigexcel.BigExcelRawDataReader;
import it.mapsgroup.dq.vo.GroupVo;
import it.mapsgroup.dq.vo.ItemVo;
import it.mapsgroup.dq.vo.MachineVo;
import it.mapsgroup.dq.vo.ManufacturerVo;
import it.mapsgroup.dq.vo.ProductGroupVo;
import it.mapsgroup.dq.vo.SaipemFlatDataVo;
import it.mapsgroup.dq.vo.SubgroupVo;
import it.mapsgroup.dq.vo.UnitOfMeasureVo;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@SuppressWarnings("unchecked")
@Service(value = "populator")
public class PopulatorImpl implements Populator {
	private Logger log = Logger.getLogger(this.getClass());
	private Logger reportLogger = Logger.getLogger("report.logger");
	private Logger faultLogger = Logger.getLogger("fault.logger");
	
	@Autowired BigExcelRawDataReader excelReader;
	@Autowired JdbcBulkDataWriter jdbcBulkDataWriter;
	@Autowired JdbcBulkDataReader jdbcBulkDataReader;
	
	@Override
	public int populateManufacturers(String fileName, String sheetId, boolean firstRowIsHeader, boolean exitOnError) throws Exception {
		StopWatch sw = new StopWatch();
		sw.start();
		reportLogger.info("\r\n\r\n\r\n");
		reportLogger.info("---------------------------");
		reportLogger.info("Populate manufacturers started");
		reportLogger.info("---------------------------");
		
		//
		//	First empty tables
		//
		jdbcBulkDataWriter.truncate("MATERIAL");
		jdbcBulkDataWriter.deleteAll("MACHINE", "MANUFACTURER");
		
		//
		//	Read all records from Excel
		//
		log.info("Start reading from " + fileName);
		Collection<ManufacturerVo> mans = excelReader.readAllManufacturers(fileName, sheetId, firstRowIsHeader);
		reportLogger.info("Successfully read file " + fileName);
		
		int numTot = mans.size();
		log.info("Number of records read from " + fileName + ": " + numTot);
		reportLogger.info("About to process " + numTot + " records");
		
		//
		//	Inserting in DB
		//
		int numSuccess = 0;
		int numFailures = 0;
		for (ManufacturerVo man : mans) {
			try {
				jdbcBulkDataWriter.insertManufacturer(man);
				
				numSuccess ++;
				if (numSuccess % 5000 == 0) {
					reportLogger.info(numSuccess + " records processed (" + _perc(numTot, numSuccess) + ")");
				}
			} catch (Exception e) {
				log.error("Unable to insert item " + man.getCode(), e);
				faultLogger.error("Unable to insert flat item " + man.getCode());
				numFailures ++;
				
				if (exitOnError) {
					reportLogger.info("Execution interrupted at flat item " + man.getCode());
					break;
				}
			}
		}
		
		//
		//	Write a short report
		//
		sw.stop();
		long elapsed = sw.getTotalTimeMillis();
		reportLogger.info("");
		reportLogger.info("");
		reportLogger.info("----------------------");
		reportLogger.info("   EXECUTION REPORT   ");
		reportLogger.info("----------------------");
		reportLogger.info("tot processed records ... " + numTot);
		reportLogger.info("successfully processed .. " + numSuccess);
		reportLogger.info("failures ................ " + ((numFailures > 0) ? numFailures : "NO FAILURES"));
		reportLogger.info("elapsed time ............ " + _formatTime(elapsed));
		
		return numSuccess;
	}

	@Override
	public int populateFlatTable(String fileName, String sheetId, boolean firstRowIsHeader, boolean exitOnError) throws Exception {
		StopWatch sw = new StopWatch();
		sw.start();
		reportLogger.info("\r\n\r\n\r\n");
		reportLogger.info("---------------------------");
		reportLogger.info("Populate flat table started");
		reportLogger.info("---------------------------");
		
		//
		//	First empty tables
		//
		jdbcBulkDataWriter.truncate("SAIPEM_FLAT_DATA");
		
		//
		//	Read all records from Excel
		//
		log.info("Start reading from " + fileName);
		Collection<SaipemFlatDataVo> items = excelReader.readAllItemsFlat(fileName, sheetId, firstRowIsHeader);
		reportLogger.info("Successfully read file " + fileName);
		
		int numTot = items.size();
		log.info("Number of records read from " + fileName + ": " + numTot);
		reportLogger.info("About to process " + numTot + " records");
		
		//
		//	Inserting in DB
		//
		int numSuccess = 0;
		int numFailures = 0;
		for (SaipemFlatDataVo item : items) {
			try {
				jdbcBulkDataWriter.insertFlatItem(item);
				
				numSuccess ++;
				if (numSuccess % 5000 == 0) {
					reportLogger.info(numSuccess + " records processed (" + _perc(numTot, numSuccess) + ")");
				}
			} catch (Exception e) {
				log.error("Unable to insert item " + item.getItemCode(), e);
				faultLogger.error("Unable to insert flat item " + item.getItemCode());
				numFailures ++;
				
				if (exitOnError) {
					reportLogger.info("Execution interrupted at flat item " + item.getItemCode());
					break;
				}
			}
		}
		
		//
		//	Write a short report
		//
		sw.stop();
		long elapsed = sw.getTotalTimeMillis();
		reportLogger.info("");
		reportLogger.info("");
		reportLogger.info("----------------------");
		reportLogger.info("   EXECUTION REPORT   ");
		reportLogger.info("----------------------");
		reportLogger.info("tot processed records ... " + numTot);
		reportLogger.info("successfully processed .. " + numSuccess);
		reportLogger.info("failures ................ " + ((numFailures > 0) ? numFailures : "NO FAILURES"));
		reportLogger.info("elapsed time ............ " + _formatTime(elapsed));
		
		return numSuccess;
	}

	private String _perc(int numTot, int numSuccess) {
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMinimumFractionDigits(0);
		long p = (long)(numSuccess * 100) / numTot;
		String perc = numberFormat.format(p) + " %";
		return perc;
	}

	private String _formatTime(long elapsed) {
		Date date = new Date(elapsed);
		DateFormat formatter = new SimpleDateFormat("mm:ss");
		String dateFormatted = formatter.format(date);
		return dateFormatted;
	}
	
	@Override
	@Deprecated
	public int populateMaterials(String fileName, String sheetId, boolean firstRowIsHeader, boolean exitOnError) throws Exception {
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
					reportLogger.info(numSuccess + " records processed (" + _perc(numTot, numSuccess) + ")");
				}
			} catch (Exception e) {
				log.error("Unable to insert item " + item.getItemCode(), e);
				numFailures ++;
				
				if (exitOnError)
					throw e;
			}
		}
		
		//
		//	Write a short report
		//
		log.info("Execution complete:\r\n" +
				"\ttot records ............. " + numTot + "\r\n" +
				"\tsuccessfully processed .. " + numSuccess + "\r\n" +
				"\tfailures ................ " + numFailures + "\r\n" +
				"");
		
		return numSuccess;
	}

	public int populateMaterialsFromFlatTable(boolean exitOnError) throws Exception {
		StopWatch sw = new StopWatch();
		sw.start();
		reportLogger.info("\r\n\r\n\r\n");
		reportLogger.info("------------------------------");
		reportLogger.info("Populate structured DB started");
		reportLogger.info("------------------------------");
		
		jdbcBulkDataWriter.truncate("MATERIAL");

		Collection<SaipemFlatDataVo> flatItems = jdbcBulkDataReader.readAllItemsFromFlatTable();
		
		//
		//	Converting in a relational model
		//
		Collection<ItemVo> items = convertFromFlatItems(flatItems);
		
		int numTot = items.size();
		log.info("Number of records read from flat table: " + numTot);
		reportLogger.info("Number of records read from flat table: " + numTot);
		
		//
		//	Inserting in DB
		//
		int numSuccess = 0;
		int numFailures = 0;
		for (ItemVo item : items) {
			try {
				jdbcBulkDataWriter.insertItem(item);
				numSuccess ++;
				if (numSuccess % 5000 == 0) {
					reportLogger.info(numSuccess + " records processed (" + _perc(numTot, numSuccess) + ")");
				}
			} catch (Exception e) {
				log.error("Unable to insert item " + item.getItemCode(), e);
				faultLogger.error("Unable to insert item " + item.getItemCode());
				numFailures ++;
				
				if (exitOnError) {
					reportLogger.info("Execution interrupted at item " + item.getItemCode() + ": " + e.getMessage());
					break;
				}
			}
		}
	
		//
		//	Write a short report
		//
		sw.stop();
		long elapsed = sw.getTotalTimeMillis();
		reportLogger.info("");
		reportLogger.info("");
		reportLogger.info("----------------------");
		reportLogger.info("   EXECUTION REPORT   ");
		reportLogger.info("----------------------");
		reportLogger.info("tot processed records ... " + numTot);
		reportLogger.info("successfully processed .. " + numSuccess);
		reportLogger.info("failures ................ " + ((numFailures > 0) ? numFailures : "NO FAILURES"));
		reportLogger.info("elapsed time ............ " + _formatTime(elapsed));

		return numSuccess;
	}

	private Collection<ItemVo> convertFromFlatItems(Collection<SaipemFlatDataVo> flatItems) {
		Collection<ItemVo> items = new ArrayList<ItemVo>();
		
		for (SaipemFlatDataVo flatItem : flatItems) {
			ItemVo item = new ItemVo();
			
			item.setItemCode(flatItem.getItemCode());
			item.setItemDescription(flatItem.getItemDescriptionEn());
			item.setType(flatItem.getType());
			
			//	UM
			UnitOfMeasureVo um = new UnitOfMeasureVo();
			um.setCode(flatItem.getUmCode());
			um.setTechCode(flatItem.getUmTechCode());
			um.setDescription(flatItem.getUmDescription());
			item.setUnitOfMeasure(um);

			//	Product Group
			ProductGroupVo pg = new ProductGroupVo();
			pg.setCode(flatItem.getProductGroupCode());
			pg.setDescription(flatItem.getProductGroupLongDescriptionEn());
			item.setProductGroup(pg);
			
			item.setMainPartNumber(flatItem.getMainPartNumber());
			
			if ((flatItem.getMachineCode()) != null) {
				//	Machine
				MachineVo m = new MachineVo();
				m.setMachineCode(flatItem.getMachineCode());
				
				//	Machine --> Manufacturer
				m.setManufacturerCode(flatItem.getManufacturerCode());
				m.setModel(m.getModelFromMachineCode()); // Extracting according to rule
				ManufacturerVo man = new ManufacturerVo();
				man.setCode(m.getManufacturerCode());
				m.setManufacturer(man);
				
				//	Machine --> Group
				m.setGroupCode(flatItem.getGroupCode());
				GroupVo g = new GroupVo();
				g.setCode(m.getGroupCode());
				m.setGroup(g);
				
				//	Machine --> Subgroup
				m.setSubgroupCode(flatItem.getSubgroupCode());
				SubgroupVo s = new SubgroupVo();
				s.setGroupCode(m.getGroupCode());
				s.setSubgroupCode(m.getSubgroupCode());
				m.setSubgroup(s);
				
				m.validate();
				item.setMachine(m);
			} else {
				faultLogger.warn("Item " + item.getItemCode() + " has no machine code associated");
			}
			
			
			items.add(item);
		}
		
		return items;
	}

	public int populateProductGroups(String fileName, String sheetId, boolean firstRowIsHeader, boolean exitOnError) throws Exception {
		StopWatch sw = new StopWatch();
		sw.start();
		reportLogger.info("\r\n\r\n\r\n");
		reportLogger.info("-------------------------------");
		reportLogger.info("Populate product groups started");
		reportLogger.info("-------------------------------");
		
		//
		//	First empty tables
		//
		jdbcBulkDataWriter.truncate("MATERIAL");
		jdbcBulkDataWriter.deleteAll("PRODUCT_GROUP");
		
		//
		//	Read all records from Excel
		//
		log.info("Start reading from " + fileName);
		Collection<ProductGroupVo> pgs = excelReader.readAllProductGroups(fileName, sheetId, firstRowIsHeader);
		reportLogger.info("Successfully read file " + fileName);
		
		int numTot = pgs.size();
		log.info("Number of records read from " + fileName + ": " + numTot);
		reportLogger.info("About to process " + numTot + " records");
		
		//
		//	Inserting in DB
		//
		int numSuccess = 0;
		int numFailures = 0;
		for (ProductGroupVo pg : pgs) {
			try {
				jdbcBulkDataWriter.insertProductGroup(pg);
				
				numSuccess ++;
				if (numSuccess % 100 == 0) {
					reportLogger.info(numSuccess + " records processed (" + _perc(numTot, numSuccess) + ")");
				}
			} catch (Exception e) {
				log.error("Unable to insert product group " + pg.getCode(), e);
				faultLogger.error("Unable to insert product group " + pg.getCode());
				numFailures ++;
				
				if (exitOnError) {
					reportLogger.info("Execution interrupted at product group " + pg.getCode());
					break;
				}
			}
		}
		
		//
		//	Write a short report
		//
		sw.stop();
		long elapsed = sw.getTotalTimeMillis();
		reportLogger.info("");
		reportLogger.info("");
		reportLogger.info("----------------------");
		reportLogger.info("   EXECUTION REPORT   ");
		reportLogger.info("----------------------");
		reportLogger.info("tot processed records ... " + numTot);
		reportLogger.info("successfully processed .. " + numSuccess);
		reportLogger.info("failures ................ " + ((numFailures > 0) ? numFailures : "NO FAILURES"));
		reportLogger.info("elapsed time ............ " + _formatTime(elapsed));
		
		return numSuccess;
	}

	public int populateGroups(String fileName, String sheetId, boolean firstRowIsHeader, boolean exitOnError) throws Exception {
		StopWatch sw = new StopWatch();
		sw.start();
		reportLogger.info("\r\n\r\n\r\n");
		reportLogger.info("-------------------------------");
		reportLogger.info("Populate groups started");
		reportLogger.info("-------------------------------");
		
		//
		//	First empty tables
		//
		//jdbcBulkDataWriter.deleteAll("SUBGROUP", "G_ROUP");
		
		//
		//	Read all records from Excel
		//
		log.info("Start reading from " + fileName);
		Collection<GroupVo> gs = excelReader.readAllGroups(fileName, sheetId, firstRowIsHeader);
		reportLogger.info("Successfully read file " + fileName);
		
		int numTot = gs.size();
		log.info("Number of records read from " + fileName + ": " + numTot);
		reportLogger.info("About to process " + numTot + " records");
		
		//
		//	Inserting in DB
		//
		int numSuccess = 0;
		int numFailures = 0;
		for (GroupVo g : gs) {
			try {
				jdbcBulkDataWriter.insertGroup(g);
				
				numSuccess ++;
				if (numSuccess % 100 == 0) {
					reportLogger.info(numSuccess + " records processed (" + _perc(numTot, numSuccess) + ")");
				}
			} catch (Exception e) {
				log.error("Unable to insert group " + g.getCode(), e);
				faultLogger.error("Unable to insert group " + g.getCode());
				numFailures ++;
				
				if (exitOnError) {
					reportLogger.info("Execution interrupted at group " + g.getCode());
					break;
				}
			}
		}
		
		//
		//	Write a short report
		//
		sw.stop();
		long elapsed = sw.getTotalTimeMillis();
		reportLogger.info("");
		reportLogger.info("");
		reportLogger.info("----------------------");
		reportLogger.info("   EXECUTION REPORT   ");
		reportLogger.info("----------------------");
		reportLogger.info("tot processed records ... " + numTot);
		reportLogger.info("successfully processed .. " + numSuccess);
		reportLogger.info("failures ................ " + ((numFailures > 0) ? numFailures : "NO FAILURES"));
		reportLogger.info("elapsed time ............ " + _formatTime(elapsed));
		
		return numSuccess;
	}
	
	public int populateSubgroups(String fileName, String sheetId, boolean firstRowIsHeader, boolean exitOnError) throws Exception {
		StopWatch sw = new StopWatch();
		sw.start();
		reportLogger.info("\r\n\r\n\r\n");
		reportLogger.info("-------------------------------");
		reportLogger.info("Populate sub groups started");
		reportLogger.info("-------------------------------");
		
		//
		//	First empty tables
		//
		//jdbcBulkDataWriter.deleteAll("SUBGROUP");
		
		//
		//	Read all records from Excel
		//
		log.info("Start reading from " + fileName);
		Collection<SubgroupVo> sgs = excelReader.readAllSubgroups(fileName, sheetId, firstRowIsHeader);
		reportLogger.info("Successfully read file " + fileName);
		
		int numTot = sgs.size();
		log.info("Number of records read from " + fileName + ": " + numTot);
		reportLogger.info("About to process " + numTot + " records");
		
		//
		//	Inserting in DB
		//
		int numSuccess = 0;
		int numFailures = 0;
		for (SubgroupVo sg : sgs) {
			try {
				jdbcBulkDataWriter.insertSubgroup(sg);
				
				numSuccess ++;
				if (numSuccess % 100 == 0) {
					reportLogger.info(numSuccess + " records processed (" + _perc(numTot, numSuccess) + ")");
				}
			} catch (Exception e) {
				log.error("Unable to insert subgroup " + sg, e);
				faultLogger.error("Unable to insert subgroup " + sg);
				numFailures ++;
				
				if (exitOnError) {
					reportLogger.info("Execution interrupted at subgroup " + sg);
					break;
				}
			}
		}
		
		//
		//	Write a short report
		//
		sw.stop();
		long elapsed = sw.getTotalTimeMillis();
		reportLogger.info("");
		reportLogger.info("");
		reportLogger.info("----------------------");
		reportLogger.info("   EXECUTION REPORT   ");
		reportLogger.info("----------------------");
		reportLogger.info("tot processed records ... " + numTot);
		reportLogger.info("successfully processed .. " + numSuccess);
		reportLogger.info("failures ................ " + ((numFailures > 0) ? numFailures : "NO FAILURES"));
		reportLogger.info("elapsed time ............ " + _formatTime(elapsed));
		
		return numSuccess;
	}


}
