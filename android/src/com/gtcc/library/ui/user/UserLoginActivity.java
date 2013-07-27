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

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.ui.HomeActivity;

public class UserLoginActivity extends SherlockActivity {
	public static final String LOGIN_TYPE = "login_type";
	public static final int LOGIN_NORMAL = 0;
	public static final int LOGIN_DOUBAN = 1;

	private EditText mUserName;
	private EditText mUserPassword;

	private int REQUEST_DOUBAN_LOGIN = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_signin);

		mUserName = (EditText) findViewById(R.id.login_user);
		mUserPassword = (EditText) findViewById(R.id.login_password);

		final Button mSignin = (Button) findViewById(R.id.login_signin);
		mSignin.setOnClickListener(onLoginClicked);

		ViewGroup mDoubanLogin = (ViewGroup) findViewById(R.id.login_douban);
		mDoubanLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserLoginActivity.this,
						UserOAuth2LoginActivity.class);
				startActivityForResult(intent, REQUEST_DOUBAN_LOGIN);
			}

		});

		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		View view = View.inflate(this, R.layout.login_register_btn, null);
		final Button register = (Button) view.findViewById(android.R.id.button1);
		register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserLoginActivity.this, UserRegisterActivity.class);
				startActivity(intent);
			}
		});
		getSupportActionBar().setCustomView(view);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
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

	private OnClickListener onLoginClicked = new OnClickListener() {

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
