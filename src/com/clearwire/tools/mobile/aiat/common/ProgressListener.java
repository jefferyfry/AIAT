package com.clearwire.tools.mobile.aiat.common;

public interface ProgressListener {
	
	public void incrementProgress();
	
	public void setProgressTotal(int value);
	
	public boolean isCancelled();
}
