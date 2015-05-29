package com.clearwire.tools.mobile.aiat.fragments;

import com.clearwire.tools.mobile.aiat.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class StatsFragment extends Fragment {
	
	private static final String sTag = "StatsFragment";
	
	private StatsAdapter mStatsAdapter;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mStatsAdapter = new StatsAdapter(getActivity());
	}



	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		GridView fragmentView = (GridView)inflater.inflate(R.layout.fragment_stats, container, false);
		fragmentView.setAdapter(mStatsAdapter);
		return fragmentView;
	}
	
	public void resetValues(){
		setStatsBundle(new Bundle());
	}
	
	public void setStatsBundle(Bundle statsBundle){
		mStatsAdapter.setStatsBundle(statsBundle);
	}
}
