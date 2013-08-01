package com.gtcc.library.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;

class AlertDialogPreference extends DialogPreference {
	
	public DialogInterface.OnClickListener onClickListener;

	public AlertDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AlertDialogPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setOnClickListener(DialogInterface.OnClickListener listener) {
		onClickListener = listener;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick();
		if (onClickListener != null) 
			onClickListener.onClick(dialog, which);
	}
}