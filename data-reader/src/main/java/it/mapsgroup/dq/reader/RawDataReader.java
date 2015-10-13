package it.mapsgroup.dq.reader;

import it.mapsgroup.dq.vo.ItemVo;

import java.util.Collection;

public interface RawDataReader {
	Collection<ItemVo> readAllItems(String file) throws Exception;
}
