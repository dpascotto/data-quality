package it.mapsgroup.dq.bulkwriter;

import it.mapsgroup.dq.vo.ItemVo;
import it.mapsgroup.dq.vo.SaipemFlatDataVo;
import it.mapsgroup.dq.vo.UnitOfMeasureVo;

public interface BulkDataWriter {
	void insertItem(ItemVo item) throws Exception;
	void truncate(String... tableNames) throws Exception;
	void lazyInsertUnitOfMeasure(UnitOfMeasureVo unitOfMeasure) throws Exception;
	void insertFlatItem(SaipemFlatDataVo item) throws Exception;
	void deleteAll(String[] tableNames) throws Exception;
}
