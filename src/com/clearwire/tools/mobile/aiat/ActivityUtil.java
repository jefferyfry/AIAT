package com.clearwire.tools.mobile.aiat;

import android.content.Context;

public final class ActivityUtil {
	
	public static String getAppVersion(Context context){
    	StringBuffer bannerBottomString = new StringBuffer();
        try {
	        bannerBottomString.append("V");
	        bannerBottomString.append(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
        }
        catch(Exception e){}
        
        return bannerBottomString.toString();
    }

}
