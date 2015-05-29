package com.clearwire.tools.mobile.aiat.log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;

import com.clearwire.tools.mobile.aiat.ActivityUtil;
import com.clearwire.tools.mobile.aiat.common.Constants;

import android.content.Context;
import android.os.Build;

public final class Log extends Thread {
	
	private static final String sTag = "Log";
	
	private static SimpleDateFormat sTimeFormat = new SimpleDateFormat("yyyy.MM.dd G HH:mm:ss z",Locale.US);
	
	private static ArrayBlockingQueue<String> sQueue = new ArrayBlockingQueue<String>(100,true);
	
	private static PrintStream sDebugOut;
			
	private static Log sLog;
	
	static {
		sLog = new Log();
		i(sTag,"Queue processing started.");
		sLog.start();
	}
	
	private Log(){
		try {
			File debugFile = new File(Constants.AIAT_DIRECTORY,"debug.txt");
			if(!debugFile.exists()){
				debugFile.createNewFile();
				i(sTag,"Create new sLog file.");
			}
			else if(debugFile.length()>200000){ //200K
				debugFile.delete();
				debugFile.createNewFile();
				i(sTag,"Roll sLog file.");
			}
			else
				i(sTag,"Using existing sLog file.");
			sDebugOut = new PrintStream(new BufferedOutputStream(new FileOutputStream(debugFile,true)));
		}
		catch(Exception e){
			android.util.Log.e(sTag,"WTF! Error with initializing Log.",e);
		}
		i(sTag,"Log initialized.");
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while(true){
			try {
				String sLogStatement = sQueue.take();
				sDebugOut.println(sLogStatement);
			} catch (Exception e) {
				android.util.Log.e(sTag,"WTF! Printing from the sQueue to error file.",e);
			}
		}
	}
	
	public static void flush(){
		try {
			if(sDebugOut!=null)
				sDebugOut.flush();
		}catch (Exception e) {
			android.util.Log.e(sTag,"WTF! Unable to flush to sLog file.",e);
		}
	}

	private static String getStackTraceAsString(Throwable tr){
		StringWriter stringWr = new StringWriter();
		PrintWriter printWr = new PrintWriter(stringWr);
		tr.printStackTrace(printWr);
		return stringWr.toString();
	}
	
	public static int e(String tag,String msg){
		try {
			StringBuffer buffer = new StringBuffer(sTimeFormat.format(System.currentTimeMillis()));
			buffer.append(" ERROR ");
			buffer.append(tag);
			buffer.append(" ");
			buffer.append(msg);
			sQueue.add(buffer.toString());
		}
		catch(Exception e){
			android.util.Log.e(sTag,"WTF! Can't add to sQueue.",e);
		}
		return android.util.Log.e(tag, msg);
	}
	
	public static int e(String tag,String msg,Throwable tr){
		try {
			StringBuffer buffer = new StringBuffer(sTimeFormat.format(System.currentTimeMillis()));
			buffer.append(" ERROR ");
			buffer.append(tag);
			buffer.append(" ");
			buffer.append(msg);
			sQueue.add(buffer.toString());
			sQueue.add(getStackTraceAsString(tr));
		}
		catch(Exception e){
			android.util.Log.e(sTag,"WTF! Can't add to sQueue.",e);
		}
		return android.util.Log.e(tag, msg, tr);
	}
	
	public static void printDebugHeader(Context context){
		try {
			String appVersion = ActivityUtil.getAppVersion(context);
			int sdkVersion = Build.VERSION.SDK_INT;
			String codename = Build.VERSION.CODENAME;
			String release = Build.VERSION.RELEASE;
			String incremental = Build.VERSION.INCREMENTAL;
			String carrier = Build.BRAND;
			String mfg = Build.MANUFACTURER;
			String device = Build.DEVICE;
			String model = Build.MODEL;
			
			StringBuffer header = new StringBuffer(sTimeFormat.format(System.currentTimeMillis()));
			header.append(" appVersion=");
			header.append(appVersion);
			header.append(" sdkVersion=");
			header.append(sdkVersion);
			header.append(" codename=");
			header.append(codename);
			header.append(" release=");
			header.append(release);
			header.append(" incremental=");
			header.append(incremental);
			header.append(" carrier=");
			header.append(carrier);
			header.append(" mfg=");
			header.append(mfg);
			header.append(" device=");
			header.append(device);
			header.append(" model=");
			header.append(model);
			
			Log.i(sTag, header.toString());
		}
		catch(Exception e){
			android.util.Log.e(sTag,"Can't print debug header.",e);
		}
	}
	
	public static void toDebugFileOnly(String tag,String msg){
		try {
			StringBuffer buffer = new StringBuffer(sTimeFormat.format(System.currentTimeMillis()));
			buffer.append(" FILE ");
			buffer.append(tag);
			buffer.append(" ");
			buffer.append(msg);
			sQueue.add(buffer.toString());
		}
		catch(Exception e){
			android.util.Log.e(sTag,"WTF! Can't add to sQueue.",e);
		}
	}
	
	public static int i(String tag,String msg){
		try {
			StringBuffer buffer = new StringBuffer(sTimeFormat.format(System.currentTimeMillis()));
			buffer.append(" INFO ");
			buffer.append(tag);
			buffer.append(" ");
			buffer.append(msg);
			sQueue.add(buffer.toString());
		}
		catch(Exception e){
			android.util.Log.e(sTag,"WTF! Can't add to sQueue.",e);
		}
		return android.util.Log.i(tag, msg);
	}
	
	public static int i(String tag,String msg,Throwable tr){
		try {
			StringBuffer buffer = new StringBuffer(sTimeFormat.format(System.currentTimeMillis()));
			buffer.append(" INFO ");
			buffer.append(tag);
			buffer.append(" ");
			buffer.append(msg);
			sQueue.add(buffer.toString());
			sQueue.add(getStackTraceAsString(tr));
		}
		catch(Exception e){
			android.util.Log.e(sTag,"WTF! Can't add to sQueue.",e);
		}
		return android.util.Log.i(tag, msg, tr);
	}
	
	public static int w(String tag,String msg){
		try {
			StringBuffer buffer = new StringBuffer(sTimeFormat.format(System.currentTimeMillis()));
			buffer.append(" WARN ");
			buffer.append(tag);
			buffer.append(" ");
			buffer.append(msg);
			sQueue.add(buffer.toString());
		}
		catch(Exception e){
			android.util.Log.e(sTag,"WTF! Can't add to sQueue.",e);
		}
		return android.util.Log.w(tag, msg);
	}
	
	public static int w(String tag,String msg,Throwable tr){
		try {
			StringBuffer buffer = new StringBuffer(sTimeFormat.format(System.currentTimeMillis()));
			buffer.append(" WARN ");
			buffer.append(tag);
			buffer.append(" ");
			buffer.append(msg);
			sQueue.add(buffer.toString());
			sQueue.add(getStackTraceAsString(tr));
		}
		catch(Exception e){
			android.util.Log.e(sTag,"WTF! Can't add to sQueue.",e);
		}
		return android.util.Log.w(tag, msg, tr);
	}
	
	public static int w(String tag,Throwable tr){
		try {
			StringBuffer buffer = new StringBuffer(sTimeFormat.format(System.currentTimeMillis()));
			buffer.append(" WARN ");
			buffer.append(tag);
			sQueue.add(buffer.toString());
			sQueue.add(getStackTraceAsString(tr));
		}
		catch(Exception e){
			android.util.Log.e(sTag,"WTF! Can't add to sQueue.",e);
		}
		return android.util.Log.w(tag, tr);
	}
}
