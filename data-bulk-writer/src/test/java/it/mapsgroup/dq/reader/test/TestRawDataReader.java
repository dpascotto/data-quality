package it.mapsgroup.dq.reader.test;

import java.util.Collection;

import it.mapsgroup.dq.reader.bigexcel.BigExcelRawDataReader;
import it.mapsgroup.dq.vo.ItemVo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-context.xml" })

public class TestRawDataReader {
	@Autowired BigExcelRawDataReader excelReader;
	
	//private static final String FILE_NAME = "C:/Users/dipa/Dropbox/MAPS/projects/201510 - Saipem/estrazione dati anagrafici materiali movimentati.xlsx";
	private static final String FILE_NAME = "C:/Users/dipa/Dropbox/MAPS/projects/201510 - Saipem/saipem-clone.xlsx";
	private static final String SMALL_FILE_NAME = "C:/Users/dipa/Dropbox/MAPS/projects/201510 - Saipem/saipem-clone-small.xlsx";

	@Test
	public void testExcelReader() {
		try {
			Collection<ItemVo> items = excelReader.readAllItems(SMALL_FILE_NAME, "rId1", true);
			
			org.junit.Assert.assertNotNull("Returned items", items);
			org.junit.Assert.assertTrue("At least one item", items.size() > 0);
			
			for (ItemVo item: items) {
				System.out.println(item);
			}
		
			System.out.println("Test OK");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
