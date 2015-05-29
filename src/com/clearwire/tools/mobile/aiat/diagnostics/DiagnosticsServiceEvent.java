package com.clearwire.tools.mobile.aiat.diagnostics;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

public class DiagnosticsServiceEvent {
	
	private double mTxRate;
	private double mRxRate;
	private double mTcpTxRate;
	private double mTcpRxRate;
	private double mUdpTxRate;
	private double mUdpRxRate;
	private double mLatency;
	private int mNetworkType;
	private String mOperatorName;
	private String mOperatorCode;
	private int mDataState;
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
	
	
	
	public DiagnosticsServiceEvent() {
		//no arg contructor
	}
	public DiagnosticsServiceEvent(double txRate, double rxRate,
			double tcpTxRate, double tcpRxRate, double udpTxRate,
			double udpRxRate, double latency, int networkType,
			String operatorName, String operatorCode, int dataState,
			double latitude, double longitude, double altitude, double speed,
			double bearing, double accuracy, String ip, int cellId,
			int physicalCellId, int trackingAreaCode, int rsrp, int rsrq,
			int rssnr, int cqi) {
		super();
		mTxRate = txRate;
		mRxRate = rxRate;
		mTcpTxRate = tcpTxRate;
		mTcpRxRate = tcpRxRate;
		mUdpTxRate = udpTxRate;
		mUdpRxRate = udpRxRate;
		mLatency = latency;
		mNetworkType = networkType;
		mOperatorName = operatorName;
		mOperatorCode = operatorCode;
		mDataState = dataState;
		mLatitude = latitude;
		mLongitude = longitude;
		mAltitude = altitude;
		mSpeed = speed;
		mBearing = bearing;
		mAccuracy = accuracy;
		mIp = ip;
		mCellId = cellId;
		mPhysicalCellId = physicalCellId;
		mTrackingAreaCode = trackingAreaCode;
		mRsrp = rsrp;
		mRsrq = rsrq;
		mRssnr = rssnr;
		mCqi = cqi;
	}
	/**
	 * @return the mTxRate
	 */
	public double getTxRate() {
		return mTxRate;
	}
	/**
	 * @return the mRxRate
	 */
	public double getRxRate() {
		return mRxRate;
	}
	/**
	 * @return the mTcpTxRate
	 */
	public double getTcpTxRate() {
		return mTcpTxRate;
	}
	/**
	 * @return the mTcpRxRate
	 */
	public double getTcpRxRate() {
		return mTcpRxRate;
	}
	/**
	 * @return the mUdpTxRate
	 */
	public double getUdpTxRate() {
		return mUdpTxRate;
	}
	/**
	 * @return the mUdpRxRate
	 */
	public double getUdpRxRate() {
		return mUdpRxRate;
	}
	/**
	 * @return the mLatency
	 */
	public double getLatency() {
		return mLatency;
	}
	/**
	 * @return the mNetworkType
	 */
	public int getNetworkType() {
		return mNetworkType;
	}
	/**
	 * @return the mOperatorName
	 */
	public String getOperatorName() {
		return mOperatorName;
	}
	/**
	 * @return the mOperatorCode
	 */
	public String getOperatorCode() {
		return mOperatorCode;
	}
	/**
	 * @return the mDataState
	 */
	public int getDataState() {
		return mDataState;
	}
	/**
	 * @return the mLatitude
	 */
	public double getLatitude() {
		return mLatitude;
	}
	/**
	 * @return the mLongitude
	 */
	public double getLongitude() {
		return mLongitude;
	}
	/**
	 * @return the mAltitude
	 */
	public double getAltitude() {
		return mAltitude;
	}
	/**
	 * @return the mSpeed
	 */
	public double getSpeed() {
		return mSpeed;
	}
	/**
	 * @return the mBearing
	 */
	public double getBearing() {
		return mBearing;
	}
	/**
	 * @return the mAccuracy
	 */
	public double getAccuracy() {
		return mAccuracy;
	}
	/**
	 * @return the mIp
	 */
	public String getIp() {
		return mIp;
	}
	/**
	 * @return the mCellId
	 */
	public int getCellId() {
		return mCellId;
	}
	/**
	 * @return the mPhysicalCellId
	 */
	public int getPhysicalCellId() {
		return mPhysicalCellId;
	}
	/**
	 * @return the mTrackingAreaCode
	 */
	public int getTrackingAreaCode() {
		return mTrackingAreaCode;
	}
	/**
	 * @return the mRsrp
	 */
	public int getRsrp() {
		return mRsrp;
	}
	/**
	 * @return the mRsrq
	 */
	public int getRsrq() {
		return mRsrq;
	}
	/**
	 * @return the mRssnr
	 */
	public int getRssnr() {
		return mRssnr;
	}
	/**
	 * @return the mCqi
	 */
	public int getCqi() {
		return mCqi;
	}
	
	public Bundle getBundle(){
		Bundle bundle = new Bundle();
		bundle.putDouble("Accuracy", mAccuracy);
		bundle.putDouble("Altitude", mAltitude);
		bundle.putDouble("Bearing",mBearing);
		bundle.putDouble("Latitude",mLatitude);
		bundle.putDouble("Longitude",mLongitude);
		bundle.putDouble("Speed",mSpeed);
		switch(mDataState){
			case 0:
				bundle.putString("Data", "Disconnected");
				break;
			case 1:
				bundle.putString("Data", "Connecting");
				break;
			case 2:
				bundle.putString("Data", "Disconnecting");
				break;
			case 3:
				bundle.putString("Data", "Suspended");
				break;
			default:
				bundle.putString("Data", "Unknown");
		}
		bundle.putString("IP", mIp);
		
		switch(mNetworkType){
			case 0:
				bundle.putString("Network", "Unknown");
				break;
			case 1:
				bundle.putString("Network", "GPRS");
				break;
			case 2:
				bundle.putString("Network", "EDGE");
				break;
			case 3:
				bundle.putString("Network", "UMTS");
				break;
			case 4:
				bundle.putString("Network", "CDMA");
				break;
			case 5:
				bundle.putString("Network", "EVDO 0");
				break;
			case 6:
				bundle.putString("Network", "EVDO A");
				break;
			case 7:
				bundle.putString("Network", "1xRTT");
				break;
			case 8:
				bundle.putString("Network", "HSDPA");
				break;
			case 9:
				bundle.putString("Network", "HSUPA");
				break;
			case 10:
				bundle.putString("Network", "HSPA");
				break;
			case 11:
				bundle.putString("Network", "IDEN");
				break;
			case 12:
				bundle.putString("Network", "EVDO B");
				break;
			case 13:
				bundle.putString("Network", "LTE");
				break;
			case 14:
				bundle.putString("Network", "EHRPD");
				break;
			case 15:
				bundle.putString("Network", "HSPAP");
				break;
			default:
				bundle.putString("Network", "Unknown");
		}
		
		bundle.putString("Op Code", mOperatorCode);
		bundle.putString("Op Name", mOperatorName);
		bundle.putInt("TAC", mTrackingAreaCode);
		bundle.putInt("PCI", mPhysicalCellId);
		bundle.putInt("Cell ID",mCellId);
		bundle.putInt("CQI", mCqi);
		bundle.putInt("RSRP", mRsrp);
		bundle.putInt("RSRQ", mRsrq);
		bundle.putInt("RSSNR", mRssnr);
		bundle.putDouble("RX Rate", mRxRate);
		bundle.putDouble("TX Rate", mTxRate);
		bundle.putDouble("TCP RX Rate", mTcpRxRate);
		bundle.putDouble("TCP TX Rate", mTcpTxRate);
		bundle.putDouble("UDP RX Rate", mUdpRxRate);
		bundle.putDouble("UDP TX Rate", mUdpTxRate);
		bundle.putDouble("Latency",mLatency);
		return bundle;
	}
}
