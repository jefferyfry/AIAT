package com.clearwire.tools.mobile.aiat.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class IndeterminateProgressDialogFragment extends DialogFragment {
	
	public static IndeterminateProgressDialogFragment newInstance(int title,String message){
		IndeterminateProgressDialogFragment dialogFrag = new IndeterminateProgressDialogFragment();
		Bundle args = new Bundle();
        args.putInt("title", title);
        args.putString("message", message);
        dialogFrag.setArguments(args);
        return dialogFrag;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int title = getArguments().getInt("title");
		String message = getArguments().getString("message");
		ProgressDialog dialog = new ProgressDialog(getActivity());
	    dialog.setTitle(title);
	    dialog.setMessage(message);
	    dialog.setIndeterminate(true);
	    dialog.setCancelable(false);
	    dialog.setProgress(1);
	    return dialog;
	}
	
	

}
