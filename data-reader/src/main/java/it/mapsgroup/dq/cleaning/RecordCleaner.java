package it.mapsgroup.dq.cleaning;

public interface RecordCleaner<T> {
	Object clean(T record);
}
