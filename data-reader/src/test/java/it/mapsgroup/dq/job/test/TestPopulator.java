package it.mapsgroup.dq.job.test;

import it.mapsgroup.dq.job.PopulatorImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-context.xml" })

public class TestPopulator {
	@Autowired PopulatorImpl populator;
	
	//private static final String FILE_NAME = "C:/Users/dipa/Dropbox/MAPS/projects/201510 - Saipem/estrazione dati anagrafici materiali movimentati.xlsx";
	private static final String FILE_NAME = "C:/Users/dipa/Dropbox/MAPS/projects/201510 - Saipem/saipem-clone.xlsx";
	private static final String SMALL_FILE_NAME = "C:/Users/dipa/Dropbox/MAPS/projects/201510 - Saipem/saipem-clone-small.xlsx";

	@Test
	public void testPopulator() {
		try {
			int insertedRows = populator.populateMaterials(SMALL_FILE_NAME, "rId1", true);
					
			System.out.println("Test OK, inserted records: " + insertedRows);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
