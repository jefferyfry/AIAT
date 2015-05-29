package com.clearwire.tools.mobile.aiat.traffic;

public interface TrafficGeneratorClientListener {
	
	public void onTrafficGeneratorError(Exception e);
	
	public void onTrafficGeneratorStart();
	
	public void onTrafficGeneratorStop();
	
	public void onTrafficGeneratorUDPUplinkThroughput(double rate);

}
