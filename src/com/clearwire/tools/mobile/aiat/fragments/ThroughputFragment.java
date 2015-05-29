package com.clearwire.tools.mobile.aiat.fragments;

import com.clearwire.tools.mobile.aiat.R;
import com.clearwire.tools.mobile.aiat.fragments.views.GaugeView;
import com.clearwire.tools.mobile.aiat.util.AnimationLooper;
import com.clearwire.tools.mobile.aiat.util.FormatUtil;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
//import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ThroughputFragment extends Fragment {
	
	private GaugeView mGaugeView;
	
	private ProgressBar mProgressBar;
	
	private LinearLayout mResultsBox;
	
	private LinearLayout mMaxDownBox;
	private TextView mMaxDownValue;
	
	private LinearLayout mAvgLatencyBox;
	private TextView mAvgLatencyValue;
	
	private LinearLayout mMaxUpBox;
	private TextView mMaxUpValue;
	
	//private ImageView mJohnSaw;
	
	public ThroughputFragment() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_throughput, container, false);
		//mJohnSaw = (ImageView)fragmentView.findViewById(R.id.throughput_john_saw);
		mProgressBar = (ProgressBar)fragmentView.findViewById(R.id.throughput_progress);
		
		mGaugeView = (GaugeView)fragmentView.findViewById(R.id.throughput_gauge);
		mGaugeView.setTargetValue(0);
		mResultsBox = (LinearLayout)fragmentView.findViewById(R.id.throughput_results_box);
		
		mMaxDownBox = (LinearLayout)fragmentView.findViewById(R.id.throughput_max_down_box);
		mMaxDownValue = (TextView)mResultsBox.findViewById(R.id.throughput_max_down_value);
		
		mAvgLatencyBox = (LinearLayout)fragmentView.findViewById(R.id.throughput_avg_latency_box);
		mAvgLatencyValue = (TextView)mResultsBox.findViewById(R.id.throughput_avg_latency_value);
		
		mMaxUpBox = (LinearLayout)fragmentView.findViewById(R.id.throughput_max_up_box);
		mMaxUpValue = (TextView)mResultsBox.findViewById(R.id.throughput_max_up_value);
		return fragmentView;
	}
	
	public void showProgressBar(boolean show){
		if(show)
			mProgressBar.setVisibility(View.VISIBLE);
		else
			mProgressBar.setVisibility(View.GONE);
	}
	
	public void setProgressBarTotal(int total){
		mProgressBar.setMax(total);
	}
	
	public void setProgressBarValue(int value){
		mProgressBar.setProgress(value);
	}
	
	public void incrementProgressBar(){
		mProgressBar.incrementProgressBy(1);
	}
	
	public void resetValues(){
		setGaugeValue(0);
		setMaxDownValue(0);
		setAvgLatencyValue(0);
		setMaxUpValue(0);
	}
	
	public void setGaugeValue(float value){
		mGaugeView.setTargetValue(value);
	}
	
	public void setMaxDownValue(float value){
		mMaxDownValue.setText(FormatUtil.getTwoDecimalFormat().format(value));
	}
	
	public void setAvgLatencyValue(float value){
		mAvgLatencyValue.setText(FormatUtil.getIntegerFormat().format(value));
	}
	
	public void setMaxUpValue(float value){
		mMaxUpValue.setText(FormatUtil.getTwoDecimalFormat().format(value));
	}
	
	public void showResultsBox(boolean show){
		if(show){
			AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
	        anim.setDuration(500);
	        mResultsBox.startAnimation(anim);
	        mResultsBox.setVisibility(View.VISIBLE);
		}
		else {
			AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
	        anim.setDuration(500);
	        mResultsBox.startAnimation(anim);
	        mResultsBox.setVisibility(View.GONE);
		}
	}
	
	public void showMaxDownValue(boolean show){
		if(show){
			AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
	        anim.setDuration(500);
	        mMaxDownBox.startAnimation(anim);
	        mMaxDownBox.setVisibility(View.VISIBLE);
		}
		else {
			AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
	        anim.setDuration(500);
	        mMaxDownBox.startAnimation(anim);
	        mMaxDownBox.setVisibility(View.GONE);
		}
	}
	
	public void showMaxUpValue(boolean show){
		if(show){
			AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
	        anim.setDuration(500);
	        mMaxUpBox.startAnimation(anim);
	        mMaxUpBox.setVisibility(View.VISIBLE);
		}
		else {
			AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
	        anim.setDuration(500);
	        mMaxUpBox.startAnimation(anim);
	        mMaxUpBox.setVisibility(View.GONE);
		}
	}
	
	public void showAvgLatencyValue(boolean show){
		if(show){
			AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
	        anim.setDuration(500);
	        mAvgLatencyBox.startAnimation(anim);
	        mAvgLatencyBox.setVisibility(View.VISIBLE);
		}
		else {
			AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
	        anim.setDuration(500);
	        mAvgLatencyBox.startAnimation(anim);
	        mAvgLatencyBox.setVisibility(View.GONE);
		}
	}
	
	public void blinkMaxDownBox(Context context,boolean animate){
		if(animate)
			AnimationLooper.start(mMaxDownBox, R.animator.blink_text_view);
		else
			AnimationLooper.stop(mMaxDownBox);
	}
	
	public void blinkMaxUpBox(Context context,boolean animate){
		if(animate)
			AnimationLooper.start(mMaxUpBox, R.animator.blink_text_view);
		else
			AnimationLooper.stop(mMaxUpBox);
	}
	
	public void blinkAvgLatencyBox(Context context,boolean animate){
		if(animate)
			AnimationLooper.start(mAvgLatencyBox, R.animator.blink_text_view);
		else
			AnimationLooper.stop(mAvgLatencyBox);
	}
	
//	public void showJohnSaw(Context context,boolean show){
//		if(show){
//			mJohnSaw.setVisibility(View.VISIBLE);
//			AnimationLooper.start(mJohnSaw, R.animator.rotate_text_view);
//		}
//		else {
//			AnimationLooper.stop(mJohnSaw);
//			mJohnSaw.setVisibility(View.GONE);
//		}
//	}
}
