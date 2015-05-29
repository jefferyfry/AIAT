package com.clearwire.tools.mobile.aiat.common;

public interface TaskListener {
	
	public void started(int progressCount);
	
	public void exceptionOccurred(Exception e);
	
	public void outOfMemoryErrorOccurred();
	
	public void taskFinished();
	
	public void taskCancelled();
	
	public void incrementProgress();
}
