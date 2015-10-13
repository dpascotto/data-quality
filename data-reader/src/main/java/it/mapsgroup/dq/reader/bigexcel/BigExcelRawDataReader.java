package it.mapsgroup.dq.reader.bigexcel;

import it.mapsgroup.dq.reader.RawDataReader;
import it.mapsgroup.dq.vo.ItemVo;

import java.io.File;
import java.io.InputStream;
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
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

@Service(value = "raw-data-reader-excel")
public class BigExcelRawDataReader implements RawDataReader {
	
	Logger log = Logger.getLogger(this.getClass());

	public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
		XMLReader parser =
			XMLReaderFactory.createXMLReader(
					"org.apache.xerces.parsers.SAXParser"
			);
		//ContentHandler handler = new SheetHandler(sst);
		ContentHandler handler = new ExcelHandler(sst);
		parser.setContentHandler(handler);
		return parser;
	}
	
	/** 
	 * See org.xml.sax.helpers.DefaultHandler javadocs 
	 */
	private static class SheetHandler extends DefaultHandler {
		private SharedStringsTable sst;
		private String lastContents;
		private boolean nextIsString;
		
		private SheetHandler(SharedStringsTable sst) {
			this.sst = sst;
		}
		
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			// c => cell
			if(name.equals("c")) {
				// Print the cell reference
				System.out.print(attributes.getValue("r") + " - ");
				// Figure out if the value is an index in the SST
				String cellType = attributes.getValue("t");
				if(cellType != null && cellType.equals("s")) {
					nextIsString = true;
				} else {
					nextIsString = false;
				}
			}
			// Clear contents cache
			lastContents = "";
		}
		
		public void endElement(String uri, String localName, String name)
				throws SAXException {
			// Process the last contents as required.
			// Do now, as characters() may be called more than once
			if(nextIsString) {
				int idx = Integer.parseInt(lastContents);
				lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
				nextIsString = false;
			}

			// v => contents of a cell
			// Output after we've seen the string contents
			if(name.equals("v")) {
				System.out.println(lastContents);
			}
		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {
			lastContents += new String(ch, start, length);
		}
	}
	

	@Override
	public Collection<ItemVo> readAllItems(String file) throws Exception {
		
        //FileInputStream fis = new FileInputStream(new File(file));
		
		OPCPackage pkg = OPCPackage.open(file);
		XSSFReader reader = new XSSFReader( pkg );
		SharedStringsTable sst = reader.getSharedStringsTable();

		XMLReader parser = fetchSheetParser(sst);
		//parser.setContentHandler(new SheetHandler());

		// To look up the Sheet Name / Sheet Order / rID,
		//  you need to process the core Workbook stream.
		// Normally it's of the form rId# or rSheet#
		InputStream sheet2 = reader.getSheet("rId1");
		InputSource sheetSource = new InputSource(sheet2);
		
	
		parser.parse(sheetSource);
		
		ExcelHandler xh = (ExcelHandler)parser.getContentHandler();
		for (int r = 1; r <= xh.getLastRow(); r++) {
			HashMap row = (HashMap)xh.getRows().get(r);
			String a = (String)row.get("A");
			String b = (String)row.get("B");
			
			System.out.println("Row " + r + ": " + a + ", " + b + "...");
		}
		
		sheet2.close();
		
		
		
		if (true) return null;
        
        Workbook workbook = WorkbookFactory.create(new File(file));
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
        
        
        Iterator<Row> rowIterator = sheet.iterator();
        int i = 0;
        while (rowIterator.hasNext())
        {
        	i++;
        	if (i>10) break;
        	
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
             
            while (cellIterator.hasNext())
            {
                Cell cell = cellIterator.next();
                System.out.println(cell.getStringCellValue());
                
            }
        }
        
        
		log.info("All items read");
		return null;
	}

}
