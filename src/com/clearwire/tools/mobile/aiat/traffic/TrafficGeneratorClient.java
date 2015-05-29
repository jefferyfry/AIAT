package com.clearwire.tools.mobile.aiat.traffic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.clearwire.tools.mobile.aiat.log.Log;

public class TrafficGeneratorClient {
	
	private static final String sTag = "TrafficGeneratorClient";
	
	private int BUFFER_SIZE = 16 * 1024;
	
	public final static int TRAFFIC_TYPE_TCP_DOWN=0;
	public final static int TRAFFIC_TYPE_TCP_UP=1;
	public final static int TRAFFIC_TYPE_UDP_DOWN=2;
	public final static int TRAFFIC_TYPE_UDP_UP=3;
	
	private boolean mTrafficRunning=false;
	
	private List<TrafficGeneratorClientListener> listeners = new ArrayList<TrafficGeneratorClientListener>();
		
	public void startTraffic(String address,int type, int threads){
		mTrafficRunning=true;
		switch(type){
			case TRAFFIC_TYPE_TCP_DOWN:
				for(int i=0;i<threads;i++)
					(new TCPDownlinkTrafficThread(address)).start();
				break;
			case TRAFFIC_TYPE_TCP_UP:
				for(int i=0;i<threads;i++)
					(new TCPUplinkTrafficThread(address)).start();
				break;
			case TRAFFIC_TYPE_UDP_DOWN:
				for(int i=0;i<threads;i++)
					(new UDPDownlinkTrafficThread(address)).start();
				break;
			case TRAFFIC_TYPE_UDP_UP:
				(new UDPUplinkTrafficThread(address)).start();
				(new UDPUplinkReportThread(address)).start();
				break;
			default:
				mTrafficRunning=false;
				Log.e(sTag, "Huh?");
		}
		
	}
	
	public void addListener(TrafficGeneratorClientListener listener){
		listeners.add(listener);
	}
	
	public void removeListener(TrafficGeneratorClientListener listener){
		listeners.remove(listener);
	}
	
	private void fireUDPUplinkThroughput(double rate){
		for(TrafficGeneratorClientListener listener:listeners)
			listener.onTrafficGeneratorUDPUplinkThroughput(rate);
	}
	
	private void fireError(Exception e){
		for(TrafficGeneratorClientListener listener:listeners)
			listener.onTrafficGeneratorError(e);
	}
	
	private void fireStart(){
		for(TrafficGeneratorClientListener listener:listeners)
			listener.onTrafficGeneratorStart();
	}
	
	private void fireStop(){
		for(TrafficGeneratorClientListener listener:listeners)
			listener.onTrafficGeneratorStop();
	}
	
	public void stopTraffic(){
		mTrafficRunning=false;
	}
	
	class TCPDownlinkTrafficThread extends Thread {
		
		private String address;
		
		private byte[] data = new byte[BUFFER_SIZE];
		
		public TCPDownlinkTrafficThread(String address){
			this.address=address;
		}
		
		public void run(){
			try {
				Socket trafficSocket = new Socket(InetAddress.getByName(address),6789);
				InputStream in = trafficSocket.getInputStream();
				fireStart();
				while(mTrafficRunning){
					in.read(data);
				}
			} catch (UnknownHostException e) {
				fireError(e);
				Log.e(sTag,"ERROR", e);
			} catch (IOException e) {
				fireError(e);
				Log.e(sTag,"ERROR", e);
			}
			finally {
				mTrafficRunning=false;
				fireStop();
			}
		}
	}
	
	class TCPUplinkTrafficThread extends Thread {
		
		private String address;
		
		private byte[] data = new byte[BUFFER_SIZE];
		
		public TCPUplinkTrafficThread(String address){
			this.address=address;
		}
		
		public void run(){
			try {
				Socket trafficSocket = new Socket(InetAddress.getByName(address),6790);
				OutputStream out = trafficSocket.getOutputStream();
				fireStart();
				while(mTrafficRunning){
					out.write(data);
				}
			} catch (UnknownHostException e) {
				fireError(e);
				Log.e(sTag,"ERROR", e);
			} catch (IOException e) {
				fireError(e);
				Log.e(sTag,"ERROR", e);
			}
			finally {
				mTrafficRunning=false;
				fireStop();
			}
		}
	}
	
	class UDPDownlinkTrafficThread extends Thread {
		
		private String address;
				
		public UDPDownlinkTrafficThread(String address){
			this.address=address;
		}
		
		public void run(){
			try {
				Socket trafficSocket = new Socket(InetAddress.getByName(address),6792);
				InputStream in = trafficSocket.getInputStream();
				fireStart();
				while(mTrafficRunning){
					in.read();
				}
			} catch (UnknownHostException e) {
				fireError(e);
				Log.e(sTag,"ERROR", e);
			} catch (IOException e) {
				fireError(e);
				Log.e(sTag,"ERROR", e);
			}
			finally {
				mTrafficRunning=false;
				fireStop();
			}
		}
	}
	
	class UDPUplinkReportThread extends Thread {
		
		private String address;
		private byte[] doubleData = new byte[8];
		
		public UDPUplinkReportThread(String address) {
			super();
			this.address = address;
		}
		
		public void run(){
			try {
				Socket trafficSocket = new Socket(InetAddress.getByName(address),6790);
				InputStream in = trafficSocket.getInputStream();
				fireStart();
				while(mTrafficRunning){
					in.read(doubleData);
					double rate = Double.longBitsToDouble(byteArrayToLong(doubleData));
					fireUDPUplinkThroughput(rate);
				}
			} catch (UnknownHostException e) {
				fireError(e);
				Log.e(sTag,"ERROR", e);
			} catch (IOException e) {
				fireError(e);
				Log.e(sTag,"ERROR", e);
			}
			finally {
				mTrafficRunning=false;
				fireStop();
			}
			
		}
	}
	
	class UDPUplinkTrafficThread extends Thread {
		
		private String address;
		
		private byte[] data = new byte[BUFFER_SIZE];
		
		public UDPUplinkTrafficThread(String address){
			this.address=address;
		}
		
		public void run(){
			try {
				DatagramPacket packet = new DatagramPacket(data, BUFFER_SIZE,InetAddress.getByName(address),6795);
				DatagramSocket socket = new DatagramSocket();
				fireStart();
				while(mTrafficRunning)
					socket.send(packet);
				socket.close();
			} catch (UnknownHostException e) {
				fireError(e);
				Log.e(sTag,"ERROR", e);
			} catch (IOException e) {
				fireError(e);
				Log.e(sTag,"ERROR", e);
			}
			finally {
				mTrafficRunning=false;
				fireStop();
			}
		}
	}
	
	public static long byteArrayToLong(byte[] b) {
		long n = (long) (b[0] & 0xff) << 56 | (long) (b[1] & 0xff) << 48
				| (long) (b[2] & 0xff) << 40 | (long) (b[3] & 0xff) << 32
				| (long) (b[4] & 0xff) << 24 | (long) (b[5] & 0xff) << 16
				| (long) (b[6] & 0xff) << 8 | (long) (b[7] & 0xff);
		return n;
	}

}
