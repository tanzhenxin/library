package com.gtcc.library.ui.user;

import java.io.IOException;

import com.gtcc.library.provider.LibraryContract;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
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
import com.gtcc.library.ui.HomeActivity;
import com.gtcc.library.util.LogUtils;
import com.gtcc.library.webserviceproxy.WebServiceInfo;
import com.gtcc.library.webserviceproxy.WebServiceUserProxy;

public class UserLoginActivity extends SherlockActivity {
	private static final String TAG = LogUtils.makeLogTag(UserLoginActivity.class);
	
	public static final String LOGIN_TYPE = "login_type";
	public static final int LOGIN_NORMAL = 0;
	public static final int LOGIN_DOUBAN = 1;

	private EditText mUserName;
	private EditText mUserPassword;

	private int REQUEST_DOUBAN_LOGIN = 1;
	private int REQUEST_REGISTER = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_signin);

		mUserName = (EditText) findViewById(R.id.login_user);
		mUserPassword = (EditText) findViewById(R.id.login_password);
		mUserPassword.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
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
				Intent intent = new Intent(UserLoginActivity.this,
						UserOAuth2LoginActivity.class);
				startActivityForResult(intent, REQUEST_DOUBAN_LOGIN);
			}

		});

        ViewGroup mSinaLogin = (ViewGroup) findViewById(R.id.login_sina);
        mSinaLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Weibo weibo = Weibo.getInstance(LibraryContract.weiboAppKey, LibraryContract.weiboRedirectURL, LibraryContract.weiboScope);
                weibo.anthorize(UserLoginActivity.this, new WeiboAuthListener() {
                    @Override
                    public void onComplete(Bundle bundle) {

                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(WeiboDialogError weiboDialogError) {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
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
				startActivityForResult(intent, REQUEST_REGISTER);
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
		} else if (requestCode == REQUEST_REGISTER) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				data.putExtra(LOGIN_TYPE, LOGIN_NORMAL);
				setResult(RESULT_OK, data);
				finish();
				break;
			}
		}
	}
	
	private void attemptLogin() {
		final String userName = mUserName.getText().toString();
		final String password = mUserPassword.getText().toString();
		
		if (TextUtils.isEmpty(userName)) {
			mUserName.setError(getString(R.string.user_name_not_empty));
			mUserName.requestFocus();
		} else if (TextUtils.isEmpty(password)) {
			mUserPassword.setError(getString(R.string.user_password_not_empty));
			mUserPassword.requestFocus();
		} else {
			new AsyncLoginTask().execute(userName, password);
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
				ret = WebServiceUserProxy.login(userName, password);
			} catch (JSONException e) {
				LogUtils.LOGE(TAG, e.toString());
			} catch (IOException e) {
				LogUtils.LOGE(TAG, e.toString());
			}
			
			return ret;
		}

		@Override
		protected void onPostExecute(Integer result) {
			switch (result) {
			case WebServiceInfo.OPERATION_SUCCEED:
				Intent intent = new Intent();
				intent.putExtra(LOGIN_TYPE, LOGIN_NORMAL);
				intent.putExtra(HomeActivity.USER_ID, userName);
				intent.putExtra(HomeActivity.USER_NAME, userName);
				intent.putExtra(HomeActivity.USER_PASSWORD, userName);
				setResult(RESULT_OK, intent);
				Toast.makeText(UserLoginActivity.this, R.string.login_succeed, Toast.LENGTH_SHORT).show();
				finish();
				break;
			default:
				Toast.makeText(UserLoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
	}
}
