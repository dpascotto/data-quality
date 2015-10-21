package it.mapsgroup.dq.reader.bigexcel;

import it.mapsgroup.dq.reader.RawDataReader;
import it.mapsgroup.dq.vo.ItemVo;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.springframework.stereotype.Service;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

@Service(value = "raw-data-reader-excel")
public class BigExcelRawDataReader implements RawDataReader {
	
	Logger log = Logger.getLogger(this.getClass());

	public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
		XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
		ContentHandler handler = new ExcelHandler(sst);
		parser.setContentHandler(handler);
		return parser;
	}

	@Override
	public Collection<ItemVo> readAllItems(String file, String sheetId, boolean firstRowIsHeader) throws Exception {
		
		Collection<ItemVo> allItems = new ArrayList<ItemVo>();
		
 		OPCPackage pkg = OPCPackage.open(file);
		XSSFReader reader = new XSSFReader( pkg );
		SharedStringsTable sst = reader.getSharedStringsTable();

		XMLReader parser = fetchSheetParser(sst);
		//parser.setContentHandler(new SheetHandler());

		// To look up the Sheet Name / Sheet Order / rID,
		//  you need to process the core Workbook stream.
		// Normally it's of the form rId# or rSheet#
		InputStream sheet = reader.getSheet(sheetId);
		InputSource sheetSource = new InputSource(sheet);
	
		parser.parse(sheetSource);
		
		ExcelHandler xh = (ExcelHandler)parser.getContentHandler();
		for (int r = 1; r <= xh.getLastRow(); r++) {
			if (r == 1 && firstRowIsHeader) {
				continue; // Skip first row if it's the table header 
			}
			HashMap row = (HashMap)xh.getRows().get(r);
			
			//	Populate an ItemVo
			ItemVo item = new ItemVo();
			item.setItemCode(getString(row, "A"));
			item.setItemDescription(getString(row, "B"));
			
			allItems.add(item);
			
		}
		
		sheet.close();
        
		log.info("All items read: " + allItems.size());
		return allItems;
	}

	private String getString(HashMap row, String columnLetter) {
		return (String)row.get(columnLetter);
	}

}
