package com.clearwire.tools.mobile.aiat.traffic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.clearwire.tools.mobile.aiat.log.Log;

public class LatencyTestClient {
	
	private static final String sTag = "LatencyTestClient";
	
	private boolean mTestRunning=false;
	
	private String address;
	
	private List<LatencyTestClientListener> listeners = new ArrayList<LatencyTestClientListener>();
	
	public void startTest(String address){
		this.address=address;
		if(!mTestRunning){
			mTestRunning=true;
			(new LatencyTestThread()).start();
		}
	}
	
	public void stopTest(){
		mTestRunning=false;
	}
	
	private void fireError(Exception e){
		for(LatencyTestClientListener listener:listeners)
			listener.onLatencyTestError(e);
	}
	
	private void fireStart(){
		for(LatencyTestClientListener listener:listeners)
			listener.onLatencyTestStart();
	}
	
	private void fireStop(){
		for(LatencyTestClientListener listener:listeners)
			listener.onLatencyTestStop();
	}
	
	private void fireLatency(double latency){
		for(LatencyTestClientListener listener:listeners)
			listener.onLatencyTestValue(latency);
	}
	
	public void addListener(LatencyTestClientListener listener){
		listeners.add(listener);
	}
	
	public void removeListener(LatencyTestClientListener listener){
		listeners.remove(listener);
	}
	
	class LatencyTestThread extends Thread {
		
		public void run(){
			try {
				fireStart();
				InetAddress testServer = InetAddress.getByName(address);
				while(mTestRunning){
					Thread.sleep(1000);
					long startTime = System.currentTimeMillis();
					testServer.isReachable(3000);
					long endTime = System.currentTimeMillis();
					fireLatency((double)(endTime-startTime));
				}
			}
			catch(IOException e){
				fireError(e);
				Log.e(sTag,"ERROR", e);
			} catch (InterruptedException e) {
				fireError(e);
				Log.e(sTag,"ERROR", e);
			}
			finally {
				mTestRunning=false;
				fireStop();
			}
		}
		
	}

}
