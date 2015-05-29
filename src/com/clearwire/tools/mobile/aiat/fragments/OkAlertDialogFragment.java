package com.clearwire.tools.mobile.aiat.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class OkAlertDialogFragment extends DialogFragment {
		
	 public static OkAlertDialogFragment newInstance(int title,String message) {
		 OkAlertDialogFragment frag = new OkAlertDialogFragment();
         Bundle args = new Bundle();
         args.putInt("title", title);
         args.putString("message", message);
         frag.setArguments(args);
         return frag;
     }

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int title = getArguments().getInt("title");
		String message = getArguments().getString("message");
		return new AlertDialog.Builder(getActivity())
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
			}
		}).create();
	}
	
	

}
