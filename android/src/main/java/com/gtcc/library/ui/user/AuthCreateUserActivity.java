package com.gtcc.library.ui.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

import com.gtcc.library.R;

public class AuthCreateUserActivity extends Activity {
	
	private ProgressDialog mSpinner;
	private CreateUserAsyncTask mCreateUserTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oauth2_new_user);
		
		mSpinner = new ProgressDialog(this);
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage(getString(R.string.login_in_progress));
		mSpinner.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				mSpinner.dismiss();
				if (mCreateUserTask != null
						&& mCreateUserTask.getStatus() != Status.FINISHED) {
					mCreateUserTask.cancel(true);
				}
				return true;
			}
		});
	}

	class CreateUserAsyncTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
	}
}
