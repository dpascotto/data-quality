package it.mapsgroup.dq.job;

public interface Populator {
	int populateMaterials(String fileName, String sheetId, boolean firstRowIsHeader) throws Exception;
	int populateFlatTable(String fileName, String sheetId, boolean firstRowIsHeader) throws Exception;
}
