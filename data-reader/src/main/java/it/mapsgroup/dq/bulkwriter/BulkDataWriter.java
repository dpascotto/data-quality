package it.mapsgroup.dq.bulkwriter;

import it.mapsgroup.dq.vo.ItemVo;

public interface BulkDataWriter {
	void insertItem(ItemVo item) throws Exception;
	void truncate(String... tableNames) throws Exception;
}
