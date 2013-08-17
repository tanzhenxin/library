package com.gtcc.library.ui.user;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
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
import com.gtcc.library.oauth2.ErrorHandler;
import com.gtcc.library.oauth2.OAuth2AccessToken;
import com.gtcc.library.oauth2.OAuth2Provider;
import com.gtcc.library.ui.HomeActivity;
import com.gtcc.library.util.LogUtils;
import com.gtcc.library.util.Utils;

public abstract class AuthLoginActivity extends SherlockActivity {

	private static final String TAG = LogUtils
			.makeLogTag(AuthLoginActivity.class);

	protected static final String KEY_TOKEN = "access_token";
	protected static final String KEY_EXPIRES = "expires_in";
	protected static final String KEY_REFRESH_TOKEN = "refresh_token";
	protected static String KEY_USER_ID = "user_id";

	private WebView mWebView;
	private ViewGroup mProgressBar;

	protected OAuth2Provider mProvider;
	private TradeAccessTokenAsyncTask mAsyncTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oauth2_login);

		mProvider = GetOAuth2Provider();

		mProgressBar = (ViewGroup) findViewById(R.id.loading_progress);
		mProgressBar.setVisibility(View.VISIBLE);

		mWebView = (WebView) findViewById(R.id.login_webview);
		mWebView.setWebViewClient(new WebViewClient() {
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
		mWebView.getSettings().setJavaScriptEnabled(true);

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

	@Override
	protected void onPause() {
		super.onPause();

		if (mAsyncTask != null && mAsyncTask.getStatus() != Status.FINISHED) {
			mAsyncTask.cancel(false);
		}
	}

	public void doOAuth2Login() {
		String authorizationUri = mProvider.getGetCodeRedirectUrl();
		mWebView.loadUrl(authorizationUri);
	}
	
	public abstract OAuth2Provider GetOAuth2Provider();

	private void handleRedirectUrl(String url) {
		Bundle values = Utils.parseUrl(url);

		String code = values.getString("code");
		if (code != null) {
			mAsyncTask = new TradeAccessTokenAsyncTask();
			mAsyncTask.execute(code);
		} else {
			finish();
		}
	}

	protected void returnResult(OAuth2AccessToken accessToken) {
		if (accessToken != null && accessToken.isSessionValid()) {
			Intent intent = new Intent();
			intent.putExtra(HomeActivity.ACCESS_TOKEN, accessToken);
			setResult(RESULT_OK, intent);
		} 

		finish();
	}

	private OAuth2AccessToken stringToAccessToken(String response) throws AuthException {
		if (response == null) {
			throw ErrorHandler.cannotGetAccessToken();
		}
		
		OAuth2AccessToken token = new OAuth2AccessToken();
		try {
			JSONObject jObj = new JSONObject(response);
			
			if (jObj.has(KEY_TOKEN)) {
				token.setAccessToken(jObj.getString(KEY_TOKEN));
			} else {
				throw ErrorHandler.cannotGetAccessToken();
			}
			if (jObj.has(KEY_EXPIRES)) {
				token.setExpiresIn(jObj.getString(KEY_EXPIRES));
			} else {
				throw ErrorHandler.cannotGetAccessToken();
			}
			if (jObj.has(KEY_REFRESH_TOKEN)) {
				token.setRefreshToken(jObj.getString(KEY_REFRESH_TOKEN));
			}
			if (jObj.has(KEY_USER_ID)) {
				String doubanUserId = jObj.getString(KEY_USER_ID);
				token.setUserId(doubanUserId);
			}
		} catch (JSONException e) {
			LogUtils.LOGE(TAG, "Failed to get access token");
		}
		
		LogUtils.LOGD(TAG, "Get access token succeed.");
		return token;
	}

	private class TradeAccessTokenAsyncTask extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String accessToken = null;
			try {
				accessToken = mProvider.tradeAccessTokenWithCode(params[0]);
			} catch (AuthException e1) {
				LogUtils.LOGE(TAG, "OAuth2 login failed.");
			}

			return accessToken;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (isCancelled()) {
				return;
			}

			OAuth2AccessToken accessToken = null;
			try {
				accessToken = stringToAccessToken(result);
			} catch (AuthException e) {
				LogUtils.LOGE(TAG, "OAuth2 login failed.");
			}
			returnResult(accessToken);
		}
	}
}
