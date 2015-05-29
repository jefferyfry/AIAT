package com.clearwire.tools.mobile.aiat.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import com.clearwire.tools.mobile.aiat.common.Constants;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;

public class ImageUtil {
	
	private static final String sTag = "ImageUtil";
	
	public static void shareViewAsImage(Context context,View view,String subject,String body,String filename) throws FileNotFoundException, IOException{
		File imageFile = saveViewAsImage(context, view, filename);
		
		ContentValues values = new ContentValues(7);
		   values.put(Images.Media.TITLE, filename);
		   values.put(Images.Media.DISPLAY_NAME, filename);
		   values.put(Images.Media.DATE_TAKEN, new Date().getTime());
		   values.put(Images.Media.MIME_TYPE, "image/png");
		   values.put(Images.ImageColumns.BUCKET_ID, imageFile.getPath().hashCode());
		   values.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, filename);
		   values.put("_data", imageFile.getPath());
		   
		Uri uri = context.getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);

		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); 
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		shareIntent.putExtra(Intent.EXTRA_TEXT, body);
		shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		shareIntent.setType("image/png");
		context.startActivity(shareIntent);
	}
	
	public static File saveViewAsImage(Context context,View view,String filename) throws FileNotFoundException,IOException {    	
    	Bitmap capture = Bitmap.createBitmap(view.getMeasuredWidth(),
				view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        	
		Canvas canvas = new Canvas(capture);

		Log.i(sTag, "Capturing view.");
		view.draw(canvas);
		
    		
		Log.i(sTag, "Writing image to file.");
    	File imageFile = new File(Constants.AIAT_DIRECTORY,filename);
    	FileOutputStream fout = new FileOutputStream(imageFile);
		capture.compress(CompressFormat.PNG, 100, fout);
		fout.flush();
		fout.close();
		capture.recycle();
		return imageFile;  
    }
}
