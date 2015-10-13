package it.mapsgroup.dq.reader.test;

import java.util.Collection;

import it.mapsgroup.dq.reader.RawDataReaderExcel;
import it.mapsgroup.dq.vo.ItemVo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-context.xml" })

public class TestRawDataReader {
	@Autowired RawDataReaderExcel excelReader;
	
	private static final String FILE_NAME = "C:/Users/dipa/Dropbox/MAPS/projects/201510 - Saipem/estrazione dati anagrafici materiali movimentati.xlsx";

	@Test
	public void testExcelReader() {
		try {
			Collection<ItemVo> items = excelReader.readAllItems(FILE_NAME);
			
			org.junit.Assert.assertNotNull("Returned items", items);
			org.junit.Assert.assertTrue("At least one item", items.size() > 0);
			
			
			System.out.println("Test OK");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
