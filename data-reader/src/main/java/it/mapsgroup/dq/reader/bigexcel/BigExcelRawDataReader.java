package it.mapsgroup.dq.reader.bigexcel;

import it.mapsgroup.dq.reader.RawDataReader;
import it.mapsgroup.dq.reader.bigexcel.mapper.ExcelMapper;
import it.mapsgroup.dq.reader.bigexcel.mapper.ItemMapper;
import it.mapsgroup.dq.vo.ItemVo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.springframework.stereotype.Service;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

@Service(value = "raw-data-reader-excel")
public class BigExcelRawDataReader<T> implements RawDataReader {
	
	Logger log = Logger.getLogger(this.getClass());

	public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
		XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
		ContentHandler handler = new ExcelHandler(sst);
		parser.setContentHandler(handler);
		return parser;
	}

	@Override
	public Collection<ItemVo> readAllItems(String file, String sheetId, boolean firstRowIsHeader) throws Exception {
		return readAll(file, sheetId, firstRowIsHeader, new ItemMapper());
	}
	
	private Collection readAll(String file, String sheetId, boolean firstRowIsHeader, ExcelMapper mapper) throws Exception {
		
		Collection allRecords = new ArrayList();
		
 		OPCPackage pkg = OPCPackage.open(file);
		XSSFReader reader = new XSSFReader( pkg );
		SharedStringsTable sst = reader.getSharedStringsTable();

		XMLReader parser = fetchSheetParser(sst);

		InputStream sheet = reader.getSheet(sheetId);
		InputSource sheetSource = new InputSource(sheet);
	
		parser.parse(sheetSource);
		
		ExcelHandler xh = (ExcelHandler)parser.getContentHandler();
		for (int r = 1; r <= xh.getLastRow(); r++) {
			if (r == 1 && firstRowIsHeader) {
				continue; // Skip first row if it's the table header 
			}
			HashMap row = (HashMap)xh.getRows().get(r);
			
			Object record = mapper.map(row);
			
			allRecords.add(record);
		}
		
		sheet.close();
        
		log.info("All records read: " + allRecords.size());
		return allRecords;
	}





}
