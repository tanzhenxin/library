package com.gtcc.library.ui.user;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.entity.UserInfo;
import com.gtcc.library.oauth2.Constants;
import com.gtcc.library.oauth2.AuthException;
import com.gtcc.library.oauth2.OAuth2Provider;
import com.gtcc.library.oauth2.RequestGrantScope;
import com.gtcc.library.ui.HomeActivity;
import com.gtcc.library.util.HttpManager;
import com.gtcc.library.util.LogUtils;

public class DoubanLoginActivity extends SherlockActivity {
	private static final String TAG = LogUtils
			.makeLogTag(DoubanLoginActivity.class);

	private WebView webview;

	private static final String AUTHORIZATION_CODE = "code=";
	private OAuth2Provider mProvider;

	private UserInfo userInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oauth2_login);

		mProvider = new OAuth2Provider()
				.setApiKey(Constants.DOUBAN_API_KEY)
				.setSecretKey(Constants.DOUBAN_SECRET_KEY)
				.setAuthUrl(Constants.DOUBAN_AUTH_URL)
				.setRedirectUrl(Constants.DOUBAN_REDIRECT_URL)
				.addScope(RequestGrantScope.BASIC_COMMON_SCOPE)
				.addScope(RequestGrantScope.BOOK_READ_SCOPE);

		webview = (WebView) findViewById(R.id.login_webview);
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith(Constants.DOUBAN_REDIRECT_URL)) {
					if (url.indexOf(AUTHORIZATION_CODE) != -1) {
						String accessCode = extractCode(url);

						new TradeAccessTokenAsyncTask().execute(accessCode);
					}

					return true;
				}

				return super.shouldOverrideUrlLoading(view, url);
			}
		});

		// do OAuth2 login
		String authorizationUri = mProvider.getGetCodeRedirectUrl();
		webview.loadUrl(authorizationUri);

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
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private String extractCode(String url) {
		String[] sArray = url.split(AUTHORIZATION_CODE);
		return sArray[1];
	}

	private class TradeAccessTokenAsyncTask extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String accessToken = null;
			try {
				accessToken = mProvider.tradeAccessTokenWithCode(params[0]);

				HttpManager httpManager = new HttpManager(accessToken);
				userInfo = httpManager.getUserInfo();

			} catch (AuthException e1) {
				LogUtils.LOGE(TAG, "OAuth2 login failed.");
			} catch (IOException e2) {
				LogUtils.LOGE(TAG, "OAuth2 login failed.");
			}

			return accessToken;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			Intent intent = new Intent();
			intent.putExtra(HomeActivity.ACCESS_TOKEN, result);
			intent.putExtra(HomeActivity.USER_ID, userInfo);
			setResult(Activity.RESULT_OK, intent);
			finish();
		}

	}
}