package it.mapsgroup.dq.reader.bigexcel;

import java.util.HashMap;

import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ExcelHandler extends DefaultHandler {
	private SharedStringsTable sst;
	private String lastContents;
	private boolean nextIsString;
	private ExcelRowCol currentERC;
	private HashMap rows = new HashMap(); // Whole file arranged by rows (each row is map with the column letter as the key)
	private int lastRow = -1;
	
	public ExcelHandler(SharedStringsTable sst) {
		this.sst = sst;
	}
	
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		// c => cell
		
		if(name.equals("c")) {
			
			String cellReference = attributes.getValue("r"); // e.g. B3, AB224
			currentERC = new ExcelRowCol(cellReference); // Keeping the current row/col reference
			
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
	
	public void endElement(String uri, String localName, String name) throws SAXException {
		// Process the last contents as required.
		// Do now, as characters() may be called more than once
		if(nextIsString) {
			int idx = Integer.parseInt(lastContents);
			lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
			nextIsString = false;
		}

		// v => contents of a cell
		if(name.equals("v")) {
			//System.out.println(lastContents);
			int currentRowIndex = currentERC.row;
			
			if (currentRowIndex > lastRow) {
				lastRow = currentRowIndex;
			}
			
			HashMap currentRow = null;
			currentRow = (HashMap)rows.get(currentRowIndex);
			if (currentRow == null) {
				currentRow = new HashMap();
				rows.put(currentRowIndex, currentRow);
			}
			currentRow.put(currentERC.col, lastContents);
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		lastContents += new String(ch, start, length);
	}
	
	public int getLastRow() {
		return lastRow;
	}
	
	public HashMap getRows() {
		return rows;
	}
	
}
