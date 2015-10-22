package it.intext.pattern.gindex;

public interface IndexChangeObserver {
	
	public void indexChanged();
	public boolean isHasChanged();
	public void setHasChanged(boolean hasChanged);
	public void lockChanges();
	public boolean isLockChanges();
	public void unlockChanges();

}
