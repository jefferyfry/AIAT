package com.clearwire.tools.mobile.aiat;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import com.clearwire.tools.mobile.aiat.diagnostics.DiagnosticsService;
import com.clearwire.tools.mobile.aiat.diagnostics.DiagnosticsServiceEvent;
import com.clearwire.tools.mobile.aiat.diagnostics.DiagnosticsServiceListener;
import com.clearwire.tools.mobile.aiat.fragments.LogcatFragment;
import com.clearwire.tools.mobile.aiat.fragments.OkAlertDialogFragment;
import com.clearwire.tools.mobile.aiat.fragments.PreferenceListFragment.OnPreferenceAttachedListener;
import com.clearwire.tools.mobile.aiat.fragments.IndeterminateProgressDialogFragment;
import com.clearwire.tools.mobile.aiat.fragments.SettingsFragment;
import com.clearwire.tools.mobile.aiat.fragments.StatsFragment;
import com.clearwire.tools.mobile.aiat.fragments.ThroughputFragment;
import com.clearwire.tools.mobile.aiat.image.ImageUtil;
import com.clearwire.tools.mobile.aiat.log.Log;
import com.clearwire.tools.mobile.aiat.traffic.TrafficGeneratorClient;
import com.clearwire.tools.mobile.aiat.util.FormatUtil;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener,ServiceConnection,DiagnosticsServiceListener,OnPreferenceAttachedListener {
	
	private static final String sTag = "MainActivity";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;
	
	private Button mStartButton;
	private Button mStopButton;
	private Button mSaveButton;
	private Button mSnapButton;
		
	private ThroughputFragment mThroughputFragment = new ThroughputFragment();
	
	private StatsFragment mStatsFragment = new StatsFragment();
	
	private LogcatFragment mLogcatFragment = new LogcatFragment();
	
	private SettingsFragment mSettingsFragment = new SettingsFragment();
	
	private DiagnosticsService mDiagnosticsService;
	
	private int mTrafficType = 4;
		
	//metrics
	private int mSampleCount;
	private int mTotalLatency;
	
	private double mMaxTxRate;
	private double mTotalTxRate;
	
	private double mMaxRxRate;
	private double mTotalRxRate;
	
	private double mMaxTcpTxRate;
	private double mTotalTcpTxRate;
	
	private double mMaxTcpRxRate;
	private double mTotalTcpRxRate;
	
	private double mMaxUdpTxRate;
	private double mTotalUdpTxRate;
	
	private double mMaxUdpRxRate;
	private double mTotalUdpRxRate;
	
	private Handler mTcpDownUpTestHandler = new Handler();
	private boolean mTcpDownUpTestIsDown=true;
	
	private Context mContext;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext=this;
		createAIATDirectory(true);
		setContentView(R.layout.activity_main);
		
		mStartButton = (Button)findViewById(R.id.menu_start);
		mStartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startTest();
			}
		});
		
		mStopButton = (Button)findViewById(R.id.menu_stop);
		mStopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopTest();
			}
		});
		
		mSaveButton = (Button)findViewById(R.id.menu_save);
		mSaveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveTest();
			}
		});
		
		mSnapButton = (Button)findViewById(R.id.menu_snap);
		mSnapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				snapScreen();
			}
		});
		//mSnapButton.setEnabled(false);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		if(!DiagnosticsService.isServiceRunning()){
			Log.i(sTag, "Starting Logger Service...");
			Intent startIntent = new Intent(this, DiagnosticsService.class);
			startService(startIntent);
		}
		bindService(new Intent(this, 
				DiagnosticsService.class), this, Context.BIND_AUTO_CREATE);
	}



	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		try {
			Log.i(sTag, "Unbinding from Diagnostics Service...");
			boolean isTestRunning = mDiagnosticsService.isLatencyTestRunning()||mDiagnosticsService.isTrafficGeneratorRunning();
			unbindService(this);
			
			if(!isTestRunning){
				Log.i(sTag, "Stopping Diagnostics Service...");
				mDiagnosticsService.removeListener(this);
				stopService(new Intent(this,DiagnosticsService.class));
			}
		}
		catch(Throwable t){
			t.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder)
	 */
	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		Log.i(sTag, "Binding to Logger Service...");
		mDiagnosticsService =((DiagnosticsService.DiagnosticsServiceBinder)service).getService();
		mDiagnosticsService.addListener(this);
		
		//TODO do some initialization if needed
	}



	/* (non-Javadoc)
	 * @see android.content.ServiceConnection#onServiceDisconnected(android.content.ComponentName)
	 */
	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		mDiagnosticsService = null;
	}
	
	/* (non-Javadoc)
	 * @see com.clearwire.tools.mobile.aiat.fragments.PreferenceListFragment.OnPreferenceAttachedListener#onPreferenceAttached(android.preference.PreferenceScreen, int)
	 */
	@Override
	public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see com.clearwire.tools.mobile.aiat.diagnostics.DiagnosticsServiceListener#eventOccurred(com.clearwire.tools.mobile.aiat.diagnostics.DiagnosticsServiceEvent)
	 */
	@Override
	public void eventOccurred(DiagnosticsServiceEvent event) {
		Bundle bundle = event.getBundle();
		mSampleCount++;
		mTotalLatency+=event.getLatency();
		double avgLatency = mTotalLatency/mSampleCount;
		
		double txRate = event.getTxRate();
		mMaxTxRate = (txRate > mMaxTxRate) ? txRate : mMaxTxRate;
		mTotalTxRate+=txRate;
		double avgTxRate = mTotalTxRate/mSampleCount;
		bundle.putDouble("AVG TX Rate", avgTxRate);
		
		double rxRate = event.getRxRate();
		mMaxRxRate = (rxRate > mMaxRxRate) ? rxRate : mMaxRxRate;
		mTotalRxRate+=rxRate;
		double avgRxRate=mTotalRxRate/mSampleCount;
		bundle.putDouble("AVG RX Rate", avgRxRate);
		
		double tcpTxRate = event.getTcpTxRate();
		mMaxTcpTxRate = (tcpTxRate > mMaxTcpTxRate) ? tcpTxRate : mMaxTcpTxRate;
		mTotalTcpTxRate+=mTotalTcpTxRate;
		double avgTcpTxRate=mTotalTcpTxRate/mSampleCount;
		bundle.putDouble("AVG TCP TX Rate", avgTcpTxRate);
		
		double tcpRxRate = event.getTcpRxRate();
		mMaxTcpRxRate = (tcpRxRate > mMaxTcpRxRate) ? tcpRxRate : mMaxTcpRxRate;
		mTotalTcpRxRate+=tcpRxRate;
		double avgTcpRxRate=mTotalTcpRxRate/mSampleCount;
		bundle.putDouble("AVG TCP RX Rate", avgTcpRxRate);
		
		double udpTxRate = event.getUdpTxRate();
		mMaxUdpTxRate = (udpTxRate > mMaxUdpTxRate) ? udpTxRate : mMaxUdpTxRate;
		mTotalUdpTxRate+=udpTxRate;
		double avgUdpTxRate=mTotalUdpTxRate/mSampleCount;
		bundle.putDouble("AVG UDP TX Rate", avgUdpTxRate);
		
		double udpRxRate = event.getUdpRxRate();
		mMaxUdpRxRate = (udpRxRate > mMaxUdpRxRate) ? udpRxRate : mMaxUdpRxRate;
		mTotalUdpRxRate+=udpRxRate;
		double avgUdpRxRate=mTotalUdpRxRate/mSampleCount;
		bundle.putDouble("AVG UDP RX Rate", avgUdpRxRate);
		
		//update throughput tab
		if(mDiagnosticsService.isLatencyTestRunning())
			mThroughputFragment.setAvgLatencyValue((float)avgLatency);
		
		if(mDiagnosticsService.isTrafficGeneratorRunning()){
			mThroughputFragment.setMaxDownValue((float)mMaxRxRate);
			mThroughputFragment.setMaxUpValue((float)mMaxTxRate);
			switch(mTrafficType){
				case TrafficGeneratorClient.TRAFFIC_TYPE_TCP_DOWN:
					mThroughputFragment.setGaugeValue((float)tcpRxRate);
					break;
				case TrafficGeneratorClient.TRAFFIC_TYPE_TCP_UP:
					mThroughputFragment.setGaugeValue((float)tcpTxRate);
					break;
				case TrafficGeneratorClient.TRAFFIC_TYPE_UDP_DOWN:
					mThroughputFragment.setGaugeValue((float)udpRxRate);
					break;
				case TrafficGeneratorClient.TRAFFIC_TYPE_UDP_UP:
					mThroughputFragment.setGaugeValue((float)udpTxRate);
					break;
				default: //tcp down up
					if(mTcpDownUpTestIsDown) {
						mThroughputFragment.setGaugeValue((float)mMaxTcpRxRate);
//						if(mMaxTcpRxRate>60)
//							mThroughputFragment.showJohnSaw(mContext, true);
					}
					else
						mThroughputFragment.setGaugeValue((float)mMaxTcpTxRate);
			}
		}
		
		//update stats fragment
		mStatsFragment.setStatsBundle(bundle);
	}



	/* (non-Javadoc)
	 * @see com.clearwire.tools.mobile.aiat.diagnostics.DiagnosticsServiceListener#onLatencyTestStart()
	 */
	@Override
	public void onLatencyTestStart() {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see com.clearwire.tools.mobile.aiat.diagnostics.DiagnosticsServiceListener#onLatencyTestStop()
	 */
	@Override
	public void onLatencyTestStop() {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see com.clearwire.tools.mobile.aiat.diagnostics.DiagnosticsServiceListener#onTrafficGeneratorStart()
	 */
	@Override
	public void onTrafficGeneratorStart() {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see com.clearwire.tools.mobile.aiat.diagnostics.DiagnosticsServiceListener#onTrafficGeneratorStop()
	 */
	@Override
	public void onTrafficGeneratorStop() {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see com.clearwire.tools.mobile.aiat.diagnostics.DiagnosticsServiceListener#onLatencyTestError(java.lang.Exception)
	 */
	@Override
	public void onLatencyTestError(Exception e) {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see com.clearwire.tools.mobile.aiat.diagnostics.DiagnosticsServiceListener#onTrafficGeneratorError(java.lang.Exception)
	 */
	@Override
	public void onTrafficGeneratorError(Exception e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_main_menu, menu);
	    return true;
	}
	
	



	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String appVersion = mContext.getResources().getString(R.string.app_name)+" "+ActivityUtil.getAppVersion(mContext)+"\nJeff Fry\njeff.fry@clearwire.com";
		OkAlertDialogFragment dialogFrag = OkAlertDialogFragment.newInstance(R.string.dialog_about_title, appVersion);
		dialogFrag.show(getSupportFragmentManager(), "appVersionDialog");
		
		return true;
	}



	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
//		if(tab.getPosition()!=0)
//			mSnapButton.setEnabled(false);
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	private void startTest(){
		resetCounters();
		mThroughputFragment.showProgressBar(false);
		mThroughputFragment.resetValues();
		//mThroughputFragment.showJohnSaw(mContext, false);
		mStatsFragment.resetValues();
		boolean enableTraffic = getSharedPreferences("settings", 0).getBoolean("settings_enable_traffic", true);
		String address = getSharedPreferences("settings", 0).getString("settings_traffic_server", "speedy.clearwire-lab.net");
		mStartButton.setEnabled(false);
		mStopButton.setEnabled(true);
		//mSnapButton.setEnabled(false);
		mDiagnosticsService.startLatencyTest(address);
		if(enableTraffic){
			mTrafficType = Integer.parseInt(getSharedPreferences("settings", 0).getString("settings_traffic_type", "4"));
			int threads = Integer.parseInt(getSharedPreferences("settings", 0).getString("settings_num_traffic_threads", "3"));
			
			switch(mTrafficType){
				case TrafficGeneratorClient.TRAFFIC_TYPE_TCP_DOWN:
					mThroughputFragment.showResultsBox(true);
					mThroughputFragment.showMaxDownValue(true);
					mThroughputFragment.showAvgLatencyValue(true);
					mThroughputFragment.showMaxUpValue(false);
					break;
				case TrafficGeneratorClient.TRAFFIC_TYPE_TCP_UP:
					mThroughputFragment.showResultsBox(true);
					mThroughputFragment.showMaxDownValue(false);
					mThroughputFragment.showAvgLatencyValue(true);
					mThroughputFragment.showMaxUpValue(true);
					break;
				case TrafficGeneratorClient.TRAFFIC_TYPE_UDP_DOWN:
					mThroughputFragment.showResultsBox(true);
					mThroughputFragment.showMaxDownValue(true);
					mThroughputFragment.showAvgLatencyValue(true);
					mThroughputFragment.showMaxUpValue(false);
					break;
				case TrafficGeneratorClient.TRAFFIC_TYPE_UDP_UP:
					mThroughputFragment.showResultsBox(true);
					mThroughputFragment.showMaxDownValue(false);
					mThroughputFragment.showAvgLatencyValue(true);
					mThroughputFragment.showMaxUpValue(true);
					break;
				default:  //alternate TCP
					mThroughputFragment.showResultsBox(true);
					mThroughputFragment.showMaxDownValue(true);
					mThroughputFragment.showAvgLatencyValue(true);
					mThroughputFragment.showMaxUpValue(true);
			}
				
			if(mTrafficType==4){ //tcp down up test
				final String addressFinal = address;
				final int threadsFinal = threads;
				mThroughputFragment.setProgressBarTotal(28);
				mThroughputFragment.setProgressBarValue(1);
				mThroughputFragment.showProgressBar(true);
				mThroughputFragment.blinkAvgLatencyBox(this, true);
				mTcpDownUpTestHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						mDiagnosticsService.stopLatencyTest();
						mThroughputFragment.blinkAvgLatencyBox(mContext, false);
					}
				}, 3000);
				mTcpDownUpTestHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						mTcpDownUpTestIsDown=true;
						mDiagnosticsService.startTrafficTest(addressFinal, TrafficGeneratorClient.TRAFFIC_TYPE_TCP_DOWN, threadsFinal);
						mThroughputFragment.blinkMaxDownBox(mContext, true);
					}
				}, 5000);
				mTcpDownUpTestHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						mDiagnosticsService.stopTrafficTest();
						mThroughputFragment.setGaugeValue(0);
						mThroughputFragment.blinkMaxDownBox(mContext, false);
					}
				}, 15000);
				mTcpDownUpTestHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						mTcpDownUpTestIsDown=false;
						mDiagnosticsService.startTrafficTest(addressFinal, TrafficGeneratorClient.TRAFFIC_TYPE_TCP_UP, threadsFinal);
						mThroughputFragment.blinkMaxUpBox(mContext, true);
					}
				}, 18000);
				mTcpDownUpTestHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						mTcpDownUpTestHandler.removeCallbacksAndMessages(null);
						mDiagnosticsService.stopTrafficTest();
						mThroughputFragment.setGaugeValue(0);
						mStartButton.setEnabled(true);
						mStopButton.setEnabled(false);
						//mSnapButton.setEnabled(true);
						mThroughputFragment.blinkMaxUpBox(mContext, false);
						mThroughputFragment.setProgressBarValue(28);
					}
				}, 28000);
				mTcpDownUpTestHandler.postDelayed(new ProgressBarUpdater(), 1100);
			}
			else
				mDiagnosticsService.startTrafficTest(address, mTrafficType, threads);
		}
		else { //latency test only
			mThroughputFragment.showResultsBox(true);
			mThroughputFragment.showMaxDownValue(false);
			mThroughputFragment.showAvgLatencyValue(true);
			mThroughputFragment.showMaxUpValue(false);
		}
	}
	
	private void stopTest(){
		mTcpDownUpTestHandler.removeCallbacksAndMessages(null);
		mDiagnosticsService.stopTrafficTest();
		mDiagnosticsService.stopLatencyTest();
		mStartButton.setEnabled(true);
		mStopButton.setEnabled(false);
		//mSnapButton.setEnabled(true);
		mThroughputFragment.showResultsBox(true);
		mThroughputFragment.blinkMaxDownBox(mContext, false);
		mThroughputFragment.blinkMaxUpBox(mContext, false);
		mThroughputFragment.blinkAvgLatencyBox(mContext, false);
	}
	
	private void saveTest(){
		
	}
	
	private void snapScreen(){
		int tab = mViewPager.getCurrentItem();
		switch(tab){
			case 0:
				(new SnapImageTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mThroughputFragment.getView());
				break;
			case 1:
				(new SnapImageTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mStatsFragment.getView());
				break;
			case 2:
				(new SnapImageTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mLogcatFragment.getView());
				break;
			case 3:
				(new SnapImageTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mSettingsFragment.getView());
				break;
			default:
		}
		
	}
	
	private void resetCounters(){
		mSampleCount=0;
		mTotalLatency=0;
		mMaxTxRate=0;
		mTotalTxRate=0;
		mMaxRxRate=0;
		mTotalRxRate=0;
		mMaxTcpTxRate=0;
		mTotalTcpTxRate=0;
		mMaxTcpRxRate=0;
		mTotalTcpRxRate=0;
		mMaxUdpTxRate=0;
		mTotalUdpTxRate=0;
		mMaxUdpRxRate=0;
		mTotalUdpRxRate=0;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch(position){
				case 0:
					return mThroughputFragment;
				case 1:
					return mStatsFragment;
				case 2:
					return mLogcatFragment;
				default:
					return mSettingsFragment;
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.throughput_tab_title).toUpperCase(Locale.US);
			case 1:
				return getString(R.string.stats_tab_title).toUpperCase(Locale.US);
			case 2:
				return getString(R.string.logcat_tab_title).toUpperCase(Locale.US);
			case 3:
				return getString(R.string.settings_tab_title).toUpperCase(Locale.US);
			}
			return null;
		}
	}
	
	public static boolean createAIATDirectory(boolean makeDir){
		File aiatDirectory = new File(com.clearwire.tools.mobile.aiat.common.Constants.AIAT_DIRECTORY);
		if(makeDir&&!aiatDirectory.exists()){
			aiatDirectory.mkdir();
			return true;
		}
		else if (aiatDirectory.exists())
			return true;
		else
			return false;
			
	}
	
	class SnapImageTask extends AsyncTask<View, Void, Void> {
		
		private IndeterminateProgressDialogFragment progressDialog;
				
		
		public SnapImageTask() {
			progressDialog = IndeterminateProgressDialogFragment.newInstance(R.string.dialog_snap_progress_title, getResources().getString(R.string.dialog_snap_progress_message));
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			progressDialog.show(getSupportFragmentManager(), "snapProgressDialog");
		}

		@Override
		protected Void doInBackground(View... view) {
			try {
				ImageUtil.shareViewAsImage(mContext, view[0], getResources().getString(R.string.image_snap_msg_subject), getResources().getString(R.string.image_snap_msg_body), "aiat_snap"+FormatUtil.getSimpleShortDateFormat().format(new Date())+".png");
			} catch (Exception e){
				Log.e(sTag, "ERROR", e);
			}
			
			return null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
		}
	}
	
	class ProgressBarUpdater implements Runnable {
		
		public void run(){
			mThroughputFragment.incrementProgressBar();
			mTcpDownUpTestHandler.postDelayed(new ProgressBarUpdater(), 1100);
		}
	}

}
