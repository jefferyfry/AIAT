package com.clearwire.tools.mobile.aiat.diagnostics;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.http.conn.util.InetAddressUtils;

import com.clearwire.tools.mobile.aiat.log.Log;
import com.clearwire.tools.mobile.aiat.traffic.LatencyTestClient;
import com.clearwire.tools.mobile.aiat.traffic.LatencyTestClientListener;
import com.clearwire.tools.mobile.aiat.traffic.TrafficGeneratorClient;
import com.clearwire.tools.mobile.aiat.traffic.TrafficGeneratorClientListener;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.TrafficStats;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;

public class DiagnosticsService extends Service implements LocationListener, LatencyTestClientListener,TrafficGeneratorClientListener {
	
	private static final String sTag = "DiagnosticsService";
	
	private static boolean sServiceRunning=false;
	private boolean mTrafficGeneratorRunning=false;
	private boolean mLatencyTestRunning=false;
	
	private TelephonyManager mTelephonyManager;
	private LocationManager mLocationManager;
	
	private LatencyTestClient mLatencyTestClient = new LatencyTestClient();
	private TrafficGeneratorClient mTrafficGeneratorClient = new TrafficGeneratorClient();
	
	private List<DiagnosticsServiceListener> mListeners = new ArrayList<DiagnosticsServiceListener>();
	
	//metrics
	private int mUid;
	private long mLastTime=-1l;
	private double mTxBytes;
	private double mRxBytes;
	private double mTcpTxBytes;
	private double mTcpRxBytes;
	private double mUdpTxBytes;
	private double mUdpRxBytes;
	
	private double mLatency;
	
	private double mLatitude;
	private double mLongitude;
	private double mAltitude;
	private double mSpeed;
	private double mBearing;
	private double mAccuracy;
	private String mIp;
	private int mCellId;
	private int mPhysicalCellId;
	private int mTrackingAreaCode;
	private int mRsrp; 
	private int mRsrq;
	private int mRssnr;
	private int mCqi;
	
	private boolean mEventUpdateRunning=false;
	private int mEventUpdateDelay=1000;
	private Handler mEventUpdateHandler = new Handler();
	
	private final IBinder mDiagnosticsServiceBinder = new DiagnosticsServiceBinder();

	@Override
	public IBinder onBind(Intent arg0) {
		return mDiagnosticsServiceBinder;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		sServiceRunning=true;
		mTelephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		mUid = getApplicationInfo().uid;
		
		//start latency test
		mLatencyTestClient.addListener(this);
		mTrafficGeneratorClient.addListener(this);
		
		mIp = getIPAddress(true);
	}
	
	public void startLatencyTest(String address){
		mLatencyTestClient.startTest(address);
	}
	
	public void stopLatencyTest(){
		mLatencyTestClient.stopTest();
	}
	
	public void startTrafficTest(String address,int type,int threads){
		mTrafficGeneratorClient.startTraffic(address, type, threads);
	}
	
	public void stopTrafficTest(){
		mTrafficGeneratorClient.stopTraffic();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		sServiceRunning=false;
		mLocationManager.removeUpdates(this);
	}
	
	public class DiagnosticsServiceBinder extends Binder {
	    public DiagnosticsService getService() {
	      return DiagnosticsService.this;
	    }
	  }

	public void addListener(DiagnosticsServiceListener listener){
		mListeners.add(listener);
		if(!mEventUpdateRunning){
			mEventUpdateRunning=true;
			mEventUpdateHandler.postDelayed(new EventUpdate(), mEventUpdateDelay);
		}
	}
	
	public void removeListener(DiagnosticsServiceListener listener){
		mListeners.remove(listener);
		if(mListeners.isEmpty())
			mEventUpdateRunning=false;
	}
	
	private void fireEvent(DiagnosticsServiceEvent event){
		for(DiagnosticsServiceListener listener:mListeners)
			listener.eventOccurred(event);
	}
	
	private void fireLatencyTestStart(){
		for(DiagnosticsServiceListener listener:mListeners)
			listener.onLatencyTestStart();
	}
	
	private void fireLatencyTestStop(){
		for(DiagnosticsServiceListener listener:mListeners)
			listener.onLatencyTestStop();
	}
	
	private void fireLatencyTestError(Exception e){
		for(DiagnosticsServiceListener listener:mListeners)
			listener.onLatencyTestError(e);
	}
	
	private void fireTrafficGeneratorStart(){
		for(DiagnosticsServiceListener listener:mListeners)
			listener.onTrafficGeneratorStart();
	}
	
	private void fireTrafficGeneratorStop(){
		for(DiagnosticsServiceListener listener:mListeners)
			listener.onTrafficGeneratorStop();
	}
	
	private void fireTrafficGeneratorError(Exception e){
		for(DiagnosticsServiceListener listener:mListeners)
			listener.onTrafficGeneratorError(e);
	}
	
	private void fireDiagnosticsServiceEvent(){
		long newTime = System.currentTimeMillis();
		if(!mListeners.isEmpty()&&mLastTime>0){
			double period = newTime - mLastTime; 
			
			double txBytes = TrafficStats.getUidTxBytes(mUid);
			double txRate = (txBytes-mTxBytes)*8d*(period/1000d)/1000000d;
			mTxBytes = txBytes;
			
			double rxBytes = TrafficStats.getUidRxBytes(mUid);
			double rxRate = (rxBytes-mRxBytes)*8d*(period/1000d)/1000000d;
			mRxBytes = rxBytes;
			
			double tcpTxBytes = TrafficStats.getUidTcpTxBytes(mUid);
			double tcpTxRate = (tcpTxBytes-mTcpTxBytes)*8d*(period/1000d)/1000000d;
			mTcpTxBytes=tcpTxBytes;
			
			double tcpRxBytes = TrafficStats.getUidTcpRxBytes(mUid);
			double tcpRxRate = (tcpRxBytes-mTcpRxBytes)*8d*(period/1000d)/1000000d;
			mTcpRxBytes=tcpRxBytes;
			
			double udpTxBytes = TrafficStats.getUidUdpTxBytes(mUid);
			double udpTxRate = (udpTxBytes-mUdpTxBytes)*8d*(period/1000d)/1000000d;
			mUdpTxBytes=udpTxBytes;
			
			double udpRxBytes = TrafficStats.getUidUdpRxBytes(mUid);
			double udpRxRate = (udpRxBytes-mUdpRxBytes)*8d*(period/1000d)/1000000d;
			
			//garbage check
			if((txRate>100)||(rxRate>100)||(tcpTxRate>100)||(tcpRxRate>100)||(udpTxRate>100)||(udpRxRate>100))
				return;
			
			
			int networkType = mTelephonyManager.getNetworkType();
			String operatorName = mTelephonyManager.getNetworkOperatorName();
			String operatorCode = mTelephonyManager.getNetworkOperator();
			int dataState = mTelephonyManager.getDataState();
			
			DiagnosticsServiceEvent event = new DiagnosticsServiceEvent(txRate, rxRate, tcpTxRate, tcpRxRate, udpTxRate, udpRxRate, mLatency, networkType, operatorName, operatorCode, dataState, mLatitude, mLongitude, mAltitude, mSpeed, mBearing, mAccuracy, mIp, mCellId, mPhysicalCellId, mTrackingAreaCode, mRsrp, mRsrq, mRssnr, mCqi);
			fireEvent(event);
		}
		mLastTime = newTime;
	}
	
	/* (non-Javadoc)
	 * @see com.clearwire.tools.mobile.aiat.traffic.TrafficGeneratorClientListener#onTrafficGeneratorError(java.lang.Exception)
	 */
	@Override
	public void onTrafficGeneratorError(Exception e) {
		fireTrafficGeneratorError(e);
	}

	/* (non-Javadoc)
	 * @see com.clearwire.tools.mobile.aiat.traffic.TrafficGeneratorClientListener#onTrafficGeneratorStart()
	 */
	@Override
	public void onTrafficGeneratorStart() {
		fireTrafficGeneratorStart();
		mTrafficGeneratorRunning=true;
	}

	/* (non-Javadoc)
	 * @see com.clearwire.tools.mobile.aiat.traffic.TrafficGeneratorClientListener#onTrafficGeneratorStop()
	 */
	@Override
	public void onTrafficGeneratorStop() {
		fireTrafficGeneratorStop();
		mTrafficGeneratorRunning=false;
	}

	/* (non-Javadoc)
	 * @see com.clearwire.tools.mobile.aiat.traffic.TrafficGeneratorClientListener#onTrafficGeneratorUDPUplinkThroughput(double)
	 */
	@Override
	public void onTrafficGeneratorUDPUplinkThroughput(double rate) {
		//don't do anything with this yet
	}

	/* (non-Javadoc)
	 * @see com.clearwire.tools.mobile.aiat.traffic.LatencyTestClientListener#onLatencyTestValue(double)
	 */
	@Override
	public void onLatencyTestValue(double latency) {
		this.mLatency = latency;
	}

	/* (non-Javadoc)
	 * @see com.clearwire.tools.mobile.aiat.traffic.LatencyTestClientListener#onLatencyTestStart()
	 */
	@Override
	public void onLatencyTestStart() {
		fireLatencyTestStart();
		mLatencyTestRunning=true;
	}

	/* (non-Javadoc)
	 * @see com.clearwire.tools.mobile.aiat.traffic.LatencyTestClientListener#onLatencyTestStop()
	 */
	@Override
	public void onLatencyTestStop() {
		fireLatencyTestStop();
		mLatencyTestRunning=false;
	}

	/* (non-Javadoc)
	 * @see com.clearwire.tools.mobile.aiat.traffic.LatencyTestClientListener#onLatencyTestError(java.lang.Exception)
	 */
	@Override
	public void onLatencyTestError(Exception e) {
		fireLatencyTestError(e);
	}
	
	

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location location) {
		mLatitude = location.getLatitude();
		mLongitude = location.getLongitude();
		mAltitude = location.getAltitude();
		mSpeed = location.getSpeed();
		mBearing = location.getBearing();
		mAccuracy = location.getAccuracy();
	}
	
//	private CellInfoLte getCellInfoLte(){
//		List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();
//		for(CellInfo cellInfo:cellInfos){
//			if(cellInfo instanceof CellInfoLte)
//				return (CellInfoLte)cellInfo;
//		}
//		return null;
//	}
	
	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String provider) {
		//not needed
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String provider) {
		//not needed
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		//not needed
	}

	/**
	 * @return the serviceRunning
	 */
	public static boolean isServiceRunning() {
		return sServiceRunning;
	}

	/**
	 * @return the trafficGeneratorRunning
	 */
	public boolean isTrafficGeneratorRunning() {
		return mTrafficGeneratorRunning;
	}

	/**
	 * @return the latencyTestRunning
	 */
	public boolean isLatencyTestRunning() {
		return mLatencyTestRunning;
	}
	
	class EventUpdate implements Runnable {
		
		public void run(){
			if(mEventUpdateRunning){
				try {
					fireDiagnosticsServiceEvent();
				}
				catch(Exception e){
					Log.e(sTag, "ERROR", e);
				}
				mEventUpdateHandler.postDelayed(new EventUpdate(), mEventUpdateDelay);
			}
		}
	}
	
	public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase(Locale.US);
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                        if (useIPv4) {
                            if (isIPv4) 
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
}
