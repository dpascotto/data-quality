package it.mapsgroup.dq.reader.bigexcel;

public class ExcelRowCol {
	
	public int row;
	public String col;
	
	public ExcelRowCol(String rc) { //	Expecting something simple like A3 or something more complicated like AZ129888
		if (rc == null || rc.length() == 0) {
			throw new RuntimeException("Empty string, nothing to parse");
		}
		
		// Trying to get the first number
		int x = -1;
		for (int i = 0; i < rc.length(); i++) {
			char c = rc.charAt(i);
			if (Character.isDigit(c)) {
				x = i;
				break;
			}
		}
		
		if (x < 0) {
			throw new RuntimeException("Invalid cell reference: " + rc);
		}
		
		this.col = rc.substring(0, x);
		this.row = Integer.parseInt(rc.substring(x));
	}
	
	public String getCellPK() { // A3 --> A.3, AZ123 --> AZ.123
		return col + "." + row;
	}
	
	public void assertEquals(int r, String c) throws RuntimeException {
		if (r != row) {
			throw new RuntimeException("Input row (" + r + ") does not match with row (" + row + ")");
		}
		if (!c.equalsIgnoreCase(col)) {
			throw new RuntimeException("Input column (" + c + ") does not match with column (" + col + ")");
		}
 	}
	
	public static void main (String[] args) {
		ExcelRowCol erc = null;

		erc = new ExcelRowCol("A3");
		erc.assertEquals(3, "A");
		
		erc = new ExcelRowCol("AB55");
		erc.assertEquals(55, "AB");
		
		erc = new ExcelRowCol("AB1048576");
		erc.assertEquals(1048576, "AB");
		
		System.out.println("Test completed");
	}

}
