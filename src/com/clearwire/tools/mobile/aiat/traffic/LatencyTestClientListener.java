package com.clearwire.tools.mobile.aiat.traffic;

public interface LatencyTestClientListener {
	
	public void onLatencyTestValue(double latency);
	
	public void onLatencyTestStart();
	
	public void onLatencyTestStop();
	
	public void onLatencyTestError(Exception e);

}
