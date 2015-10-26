package it.mapsgroup.dq.reader.bigexcel.mapper;

import java.util.HashMap;

public abstract class ExcelMapper {
	public abstract Object map(HashMap row);
	
	protected String getString(HashMap row, String columnLetter) {
		return (String)row.get(columnLetter);
	}
	
	protected String getStringNull(HashMap row, String columnLetter) {
		String val = getString(row, columnLetter);
		if ("".equals(val)) {
			return null;
		} else {
			return val;
		}
	}

	protected boolean getXBoolean(HashMap row, String columnLetter) {
		String val = getString(row, columnLetter);
		return "X".equalsIgnoreCase(val);
	}
	
	protected int getInt(HashMap row, String columnLetter) {
		String val = getString(row, columnLetter);
		if (val != null) {
			return Integer.parseInt(val);
		}
		return 0;
	}


}
