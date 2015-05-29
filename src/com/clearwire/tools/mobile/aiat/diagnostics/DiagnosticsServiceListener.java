package com.clearwire.tools.mobile.aiat.diagnostics;

public interface DiagnosticsServiceListener {
	
	public void eventOccurred(DiagnosticsServiceEvent event);
	
	public void onLatencyTestStart();
	
	public void onLatencyTestStop();
	
	public void onTrafficGeneratorStart();
	
	public void onTrafficGeneratorStop();
	
	public void onLatencyTestError(Exception e);
	
	public void onTrafficGeneratorError(Exception e);

}
