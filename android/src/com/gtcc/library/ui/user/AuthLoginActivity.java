package com.gtcc.library.ui.user;

import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.oauth2.AuthException;
import com.gtcc.library.oauth2.OAuth2Provider;
import com.gtcc.library.ui.HomeActivity;
import com.gtcc.library.util.LogUtils;
import com.gtcc.library.util.Utils;

public abstract class AuthLoginActivity extends SherlockActivity {
	
	private static final String TAG = LogUtils
			.makeLogTag(AuthLoginActivity.class);
	
	private WebView mWebview;
	private ViewGroup mProgressBar;

	protected OAuth2Provider mProvider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oauth2_login);

		mProvider = GetOAuth2Provider();
		
		mProgressBar = (ViewGroup) findViewById(R.id.loading_progress);
		mProgressBar.setVisibility(View.VISIBLE);

		mWebview = (WebView) findViewById(R.id.login_webview);
		mWebview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith(mProvider.getRedirectUrl())) {
					handleRedirectUrl(url);
					return true;
				}

				return super.shouldOverrideUrlLoading(view, url);
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				
				mProgressBar.setVisibility(View.GONE);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		});
		mWebview.getSettings().setJavaScriptEnabled(true);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			this.overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void doOAuth2Login() {
		String authorizationUri = mProvider.getGetCodeRedirectUrl();
		mWebview.loadUrl(authorizationUri);
	}
	
	private void handleRedirectUrl(String url) {
		Bundle values = Utils.parseUrl(url);
		
		String code = values.getString("code");
		if (code != null) {
			new TradeAccessTokenAsyncTask().execute(code);
		}

//		String error = values.getString("error");
//		String error_code = values.getString("error_code");
//
//		if (error == null && error_code == null) {
//			Intent intent = new Intent();
//			intent.putExtras(values);
//			setResult(RESULT_OK, intent);
//			finish();
		else {
			Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show();
			finish();
		}
	}
	
	public abstract OAuth2Provider GetOAuth2Provider();
	
	private class TradeAccessTokenAsyncTask extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String accessToken = null;
			try {
				accessToken = mProvider.tradeAccessTokenWithCode(params[0]);

				//HttpManager httpManager = new HttpManager(accessToken);
				//mUserInfo = httpManager.getUserInfo();

			} catch (AuthException e1) {
				LogUtils.LOGE(TAG, "OAuth2 login failed.");
			}

			return accessToken;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			Intent intent = new Intent();
			intent.putExtra(HomeActivity.ACCESS_TOKEN, result);
			//intent.putExtra(HomeActivity.USER_ID, mUserInfo);
			setResult(RESULT_OK, intent);
			finish();
		}
	}
}
