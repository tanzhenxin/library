package com.gtcc.library.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.gtcc.library.R;
import com.gtcc.library.ui.HomeActivity;

public class UserLoginActivity extends Activity {
	public static final String LOGIN_TYPE = "login_type";
	public static final int LOGIN_NORMAL = 0;
	public static final int LOGIN_DOUBAN = 1;
	
	private Button mDoubanLogin;
	private Button mLoginSubmit;
	private EditText mUserName;
	private EditText mUserEmail;
	
	private int REQUEST_DOUBAN_LOGIN = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		mDoubanLogin = (Button) findViewById(R.id.login_douban);
		mDoubanLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserLoginActivity.this, UserOAuth2LoginActivity.class);
				startActivityForResult(intent, REQUEST_DOUBAN_LOGIN);
			}
			
		});
		
		mUserName = (EditText) findViewById(R.id.login_name);
		mUserEmail = (EditText) findViewById(R.id.login_email);
		mUserName.addTextChangedListener(mTextWatcher);
		mUserEmail.addTextChangedListener(mTextWatcher);
		
		mLoginSubmit = (Button) findViewById(R.id.login_submit);
		mLoginSubmit.setEnabled(false);
		mLoginSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(LOGIN_TYPE, LOGIN_NORMAL);
				intent.putExtra(HomeActivity.USER_ID, mUserName.getText().toString());
				intent.putExtra(HomeActivity.USER_EMAIL, mUserEmail.getText().toString());
				setResult(RESULT_OK, intent);
				finish();
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
	
	private TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			if (mUserName.length() > 0 && mUserEmail.length() > 0) 
				mLoginSubmit.setEnabled(true);
			else
				mLoginSubmit.setEnabled(false);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	};
}
