package it.mapsgroup.dq.job.test;

import it.mapsgroup.dq.job.PopulatorImpl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-context.xml" })

public class TestPopulator {
	@Autowired PopulatorImpl populator;
	

	private static final String DATA = "C:/javasource/my/data-quality/data-reader/src/saipem-data/DATA.xlsx";
	//private static final String DATA_SMALL = "C:/javasource/my/data-quality/data-reader/src/saipem-data/small/DATA_SMALL.xlsx";

	private static final String MAT = "C:/javasource/my/data-quality/data-reader/src/saipem-data/ZCTRL_MATERIALI_12102015_def.xlsx";
	//private static final String MAT_SMALL = "C:/javasource/my/data-quality/data-reader/src/saipem-data/small/ZCTRL_MATERIALI_12102015_def_SMALL.xlsx";

	@Test
	public void testPopulator() {
		try {
			//
			//	Product Group
			//
			if (false) {
				int productGroups = populator.populateProductGroups(DATA, "rId4", true, true);
				Assert.assertTrue("At least one product group inserted", productGroups > 0);
			}
			
			//
			//	Manufacturers
			//
			if (false) {
				int manufacturers = populator.populateManufacturers(DATA, "rId1", true, true);
				Assert.assertTrue("At least one manufacturer inserted", manufacturers > 0);
			}
			
			//
			//	Groups
			//
			if (false) {
				int groups = populator.populateGroups(DATA, "rId2", true, true);
				Assert.assertTrue("At least one group inserted", groups > 0);
			}
			
			//
			//	Subgroups
			//
			if (false) {
				int subgroups = populator.populateSubgroups(DATA, "rId3", true, true);
				Assert.assertTrue("At least one subgroup inserted", subgroups > 0);
			}
			
			//
			//	Materials (Flat table)
			//
			if (false) {
				int insertedMaterialsFlat = populator.populateFlatTable(MAT, "rId1", true, true);
				Assert.assertTrue("At least one material inserted in flat table", insertedMaterialsFlat > 0);
			}
			
			//
			//	Materials (from flat table)
			//
			if (true) {
				int insertedMaterials = populator.populateMaterialsFromFlatTable(true);
				Assert.assertTrue("At least one material inserted", insertedMaterials > 0);
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
