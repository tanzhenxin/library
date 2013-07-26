package com.gtcc.library.ui.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.gtcc.library.R;
import com.gtcc.library.ui.HomeActivity;

public class UserLoginActivity extends Activity {
	public static final String LOGIN_TYPE = "login_type";
	public static final int LOGIN_NORMAL = 0;
	public static final int LOGIN_DOUBAN = 1;

	private Button mSignup;
	private Button mSignin;
	private EditText mUserName;
	private EditText mUserPassword;

	private int REQUEST_DOUBAN_LOGIN = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mUserName = (EditText) findViewById(R.id.login_user);
		mUserPassword = (EditText) findViewById(R.id.login_password);

		mSignup = (Button) findViewById(R.id.login_signin);
		mSignin = (Button) findViewById(R.id.login_signin);
		mSignup.setOnClickListener(btnClickListener);
		mSignin.setOnClickListener(btnClickListener);
		
		ViewGroup mDoubanLogin = (ViewGroup) findViewById(R.id.login_douban);
		mDoubanLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserLoginActivity.this,
						UserOAuth2LoginActivity.class);
				startActivityForResult(intent, REQUEST_DOUBAN_LOGIN);
			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_DOUBAN_LOGIN) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				data.putExtra(LOGIN_TYPE, LOGIN_DOUBAN);
				setResult(RESULT_OK, data);
				finish();
				break;
			}
		}
	}

	private OnClickListener btnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mUserName.length() == 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						UserLoginActivity.this);
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
			} else if (mUserPassword.length() == 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						UserLoginActivity.this);
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
				Intent intent = new Intent();
				intent.putExtra(LOGIN_TYPE, LOGIN_NORMAL);
				intent.putExtra(HomeActivity.USER_ID, "1");
				intent.putExtra(HomeActivity.USER_NAME, mUserName.getText()
						.toString());
				intent.putExtra(HomeActivity.USER_PASSWORD, mUserPassword
						.getText().toString());
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	};
}
