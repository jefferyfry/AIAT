package com.clearwire.tools.mobile.aiat.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.clearwire.tools.mobile.aiat.R;
import com.clearwire.tools.mobile.aiat.log.Log;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class LogcatFragment extends ListFragment {
	
	private static final String sTag = "LogcatFragment";
	
	private LogcatAdapter mLogcatAdapter = new LogcatAdapter();
		
	private boolean mLogcatRunning=false;
	
	private CheckBox mLogEnable;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragView = inflater.inflate(R.layout.fragment_logcat, container, false);
		mLogEnable = (CheckBox)fragView.findViewById(R.id.logcat_enable);
		mLogEnable.setChecked(false);
		mLogEnable.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mLogcatRunning)
					stopLog();
				else
					startLog();
			}
		});
		setListAdapter(mLogcatAdapter);
		return fragView;
	}
	
	


	private void startLog(){
		if(!mLogcatRunning){
			mLogcatRunning=true;
			(new LogcatTask()).execute();
		}
	}
	
	private void stopLog(){
		mLogcatRunning=false;
	}
	
	
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		stopLog();
	}



	class LogcatTask extends AsyncTask<Void, String, Void> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Process process = Runtime.getRuntime().exec("logcat");
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(process.getInputStream()));
				String line;
				long lastTime = System.currentTimeMillis();
				while((line=bufferedReader.readLine())!=null&&mLogcatRunning){
					publishProgress(line);
					long newTime = System.currentTimeMillis();
					if((newTime-lastTime)>5000){
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							Log.e(sTag, "ERROR", e);
						}
						lastTime = newTime;
					}
				}
				bufferedReader.close();
			} catch (IOException e) {
				Log.e(sTag, "ERROR", e);
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(String... values) {
			mLogcatAdapter.setNewLine(values[0]);
			Log.toDebugFileOnly(sTag, values[0]);
		}
		
		
	}
	
	class LogcatAdapter extends BaseAdapter {
		
		private List<String> lines = new ArrayList<String>();
		
		public void setNewLine(String line){
			if(lines.size()>30)
				lines.remove(30);
			lines.add(0, line);
			
			notifyDataSetChanged();
		}
		
		public void clear(){
			lines.clear();
			notifyDataSetChanged();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return lines.size();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return lines.get(position);
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null)
				convertView = new TextView(getActivity());
			
			TextView textConvertView = (TextView)convertView;
			textConvertView.setText(lines.get(position));
			
			return convertView;
		}

		/* (non-Javadoc)
		 * @see android.widget.BaseAdapter#isEnabled(int)
		 */
		@Override
		public boolean isEnabled(int position) {
			return false;
		}
	}
}
