package com.clearwire.tools.mobile.aiat.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.clearwire.tools.mobile.aiat.R;
import com.clearwire.tools.mobile.aiat.util.FormatUtil;

class StatsAdapter extends BaseAdapter {
	
	private List<Stat> mStatsList = new ArrayList<Stat>();
	
	private LayoutInflater mInflater;

	public StatsAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	/**
	 * @param statsBundle the statsBundle to set
	 */
	public void setStatsBundle(Bundle statsBundle) {
		mStatsList.clear();
		for(String key:statsBundle.keySet()){
			Object value = statsBundle.get(key);
			if(value instanceof Double||value instanceof Float)
				mStatsList.add(new Stat(key,FormatUtil.getTwoDecimalFormat().format(value)));
			else
				mStatsList.add(new Stat(key,value.toString()));
		}
		Collections.sort(mStatsList);
		notifyDataSetChanged();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return mStatsList.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Stat getItem(int index) {
		return mStatsList.get(index);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int index) {
		return index;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = mInflater.inflate(R.layout.stats_content, null);
		
		Stat stat = mStatsList.get(index);
		TextView statsTitle = (TextView)convertView.findViewById(R.id.stats_title);
		statsTitle.setText(stat.getName());
		
		TextView statsValue = (TextView)convertView.findViewById(R.id.stats_value);
		statsValue.setText(stat.getValue());
		
		return convertView;
	}
}
