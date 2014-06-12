package com.gtcc.library.ui.user;

import java.io.IOException;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.entity.UserInfo;
import com.gtcc.library.oauth2.OAuth2AccessToken;
import com.gtcc.library.ui.HomeActivity;
import com.gtcc.library.util.LogUtils;
import com.gtcc.library.util.Utils;
import com.gtcc.library.webserviceproxy.WebServiceInfo;
import com.gtcc.library.webserviceproxy.WebServiceUserProxy;

public class UserLoginActivity extends SherlockActivity {
	private static final String TAG = LogUtils
			.makeLogTag(UserLoginActivity.class);

	public static final String LOGIN_TYPE = "login_type";
	public static final int LOGIN_NORMAL = 0;
	public static final int LOGIN_AUTH_DOUBAN = 1;
	public static final int LOGIN_AUTH_WEIBO = 2;
	
	public static final String LOGIN_USER = "login_user";

	private EditText mUserName;
	private EditText mUserPassword;
	
	private ProgressDialog mSpinner;
	private AsyncLoginTask mAsyncLoginTask;

	private static final int REQUEST_REGISTER = 1;
	private static final int REQUEST_DOUBAN_LOGIN = 2;
	private static final int REQUEST_WEIBO_LOGIN = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_signin);

		mUserName = (EditText) findViewById(R.id.login_user);
		mUserPassword = (EditText) findViewById(R.id.login_password);
		mUserPassword.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				attemptLogin();
				return true;
			}
		});

		final Button mSignin = (Button) findViewById(R.id.login_signin);
		mSignin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				attemptLogin();
			}
		});

		ViewGroup mDoubanLogin = (ViewGroup) findViewById(R.id.login_douban);
		mDoubanLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Utils.isNetworkConnected(UserLoginActivity.this)) {
					Intent intent = new Intent(UserLoginActivity.this,
							AuthDoubanLoginActivity.class);
					startActivityForResult(intent, REQUEST_DOUBAN_LOGIN);
				}
			}

		});

		ViewGroup mSinaLogin = (ViewGroup) findViewById(R.id.login_sina);
		mSinaLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (Utils.isNetworkConnected(UserLoginActivity.this)) {
					Intent intent = new Intent(UserLoginActivity.this,
							AuthWeiboLoginActivity.class);
					startActivityForResult(intent, REQUEST_WEIBO_LOGIN);
				}
			}
		});
		
		mSpinner = new ProgressDialog(this);
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage(getString(R.string.login_in_progress));
		mSpinner.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				mSpinner.dismiss();
				if (mAsyncLoginTask != null && mAsyncLoginTask.getStatus() != Status.FINISHED) {
					mAsyncLoginTask.cancel(true);
				}
				return true;
			}

		});

		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		View view = View.inflate(this, R.layout.login_register_btn, null);
//		final Button register = (Button) view
//				.findViewById(android.R.id.button1);
//		register.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(UserLoginActivity.this,
//						UserRegisterActivity.class);
//				startActivityForResult(intent, REQUEST_REGISTER);
//			}
//		});
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

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_DOUBAN_LOGIN: {
				data.putExtra(LOGIN_TYPE, LOGIN_AUTH_DOUBAN);
				setResult(RESULT_OK, data);
				finish();
				break;
			}
			case REQUEST_WEIBO_LOGIN: {
				data.putExtra(LOGIN_TYPE, LOGIN_AUTH_WEIBO);
				setResult(RESULT_OK, data);
				finish();
				break;
			}
			case REQUEST_REGISTER:
				data.putExtra(LOGIN_TYPE, LOGIN_NORMAL);
				setResult(RESULT_OK, data);
				finish();
				break;
			}
		} else if (resultCode != RESULT_CANCELED) {
			Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void attemptLogin() {
		if (Utils.isNetworkConnected(this)) {
			final String userName = mUserName.getText().toString();
			final String password = mUserPassword.getText().toString();

			if (TextUtils.isEmpty(userName)) {
				mUserName.setError(getString(R.string.user_name_not_empty));
				mUserName.requestFocus();
			} else if (TextUtils.isEmpty(password)) {
				mUserPassword
						.setError(getString(R.string.user_password_not_empty));
				mUserPassword.requestFocus();
			} else {
				mSpinner.show();
				
				mAsyncLoginTask = new AsyncLoginTask();
				mAsyncLoginTask.execute(userName, password);
			}
		}
	}

	class AsyncLoginTask extends AsyncTask<String, Void, Integer> {

		String userName;
		String password;

		@Override
		protected Integer doInBackground(String... params) {
			int ret = WebServiceInfo.OPERATION_FAILED;

			userName = params[0];
			password = params[1];

			try {
				ret = new WebServiceUserProxy().login(userName, password);
			} catch (JSONException e) {
				LogUtils.LOGE(TAG, e.toString());
			} catch (IOException e) {
				LogUtils.LOGE(TAG, e.toString());
			}

			return ret;
		}

		@Override
		protected void onPostExecute(Integer result) {
			mSpinner.dismiss();
			
			if (isCancelled())
				return;
			
			switch (result) {
			case WebServiceInfo.OPERATION_SUCCEED:
				Intent intent = new Intent();
				intent.putExtra(LOGIN_TYPE, LOGIN_NORMAL);
				intent.putExtra(LOGIN_USER, new UserInfo(userName, userName, password));
				setResult(RESULT_OK, intent);
				Toast.makeText(UserLoginActivity.this, R.string.login_succeed,
						Toast.LENGTH_SHORT).show();
				finish();
				break;
			case WebServiceInfo.USER_NOT_EXISTS:
				Toast.makeText(UserLoginActivity.this, R.string.login_failed_user_invalid,
						Toast.LENGTH_SHORT).show();
				break;
			case WebServiceInfo.USER_PASSWORD_WRONG:
				Toast.makeText(UserLoginActivity.this, R.string.login_failed_password_invalid,
						Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(UserLoginActivity.this, R.string.login_failed,
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
}
