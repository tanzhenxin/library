package com.gtcc.library.ui.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;

public class UserRegisterActivity extends SherlockActivity {
	
	private TextView mUserName;
	private TextView mEmail;
	private TextView mPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_signup);
		
		mUserName = (TextView) findViewById(R.id.register_user);
		mEmail = (TextView) findViewById(R.id.register_email);
		mPassword = (TextView) findViewById(R.id.register_password);
		
		final Button registerBtn = (Button) findViewById(R.id.login_signup);
		registerBtn.setOnClickListener(onRegisterBtnClicked);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private OnClickListener onRegisterBtnClicked = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mUserName.length() == 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						UserRegisterActivity.this);
				builder.setMessage(R.string.user_name_not_empty)
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}

								});
				builder.create().show();
			} else if (mEmail.length() == 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						UserRegisterActivity.this);
				builder.setMessage(R.string.user_email_not_empty)
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}

								});
				builder.create().show();
			} else if (mPassword.length() == 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						UserRegisterActivity.this);
				builder.setMessage(R.string.user_password_not_empty)
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}

								});
				builder.create().show();
			} else {

			}
		}
	};
}
