package com.clearwire.tools.mobile.aiat.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
 
public class AnimationLooper{
	
	public static void start(final View v, final int animationResId){
		v.clearAnimation();
		Animation a = AnimationUtils.loadAnimation(v.getContext(), animationResId);
		a.setAnimationListener(new AnimationListener() {
			@Override public void onAnimationStart(Animation animation) {}
			@Override public void onAnimationRepeat(Animation animation) {}
			@Override public void onAnimationEnd(Animation animation) {
				animation.reset();
				animation.startNow();
			}
		});
		v.startAnimation(a);
	}
 
	public static void stop(final View v){
		v.clearAnimation();
	}
 
}