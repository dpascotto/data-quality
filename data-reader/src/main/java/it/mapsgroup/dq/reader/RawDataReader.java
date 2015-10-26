package it.mapsgroup.dq.reader;

import it.mapsgroup.dq.vo.ItemVo;
import it.mapsgroup.dq.vo.ManufacturerVo;
import it.mapsgroup.dq.vo.ProductGroupVo;
import it.mapsgroup.dq.vo.SaipemFlatDataVo;

import java.util.Collection;

public interface RawDataReader {
	Collection<ItemVo> readAllItems(String file, String sheetId, boolean firstRowIsHeader) throws Exception;

	Collection<SaipemFlatDataVo> readAllItemsFlat(String file, String sheetId, boolean firstRowIsHeader) throws Exception;

	Collection<ManufacturerVo> readAllManufacturers(String file, String sheetId, boolean firstRowIsHeader) throws Exception;

	Collection<ProductGroupVo> readAllProductGroups(String file, String sheetId, boolean firstRowIsHeader) throws Exception;

	Collection<ProductGroupVo> readAllGroups(String file, String sheetId, boolean firstRowIsHeader) throws Exception;

	Collection<ProductGroupVo> readAllSubgroups(String file, String sheetId, boolean firstRowIsHeader) throws Exception;
}
