package com.gtcc.library.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.gtcc.library.R;
import com.gtcc.library.oauth2.AuthException;
import com.gtcc.library.oauth2.Constants;
import com.gtcc.library.oauth2.AuthListener;
import com.gtcc.library.oauth2.OAuth2AccessToken;
import com.gtcc.library.oauth2.OAuth2Provider;
import com.gtcc.library.oauth2.RequestGrantScope;
import com.gtcc.library.oauth2.SsoHandler;

public class WeiboLoginActivity extends SherlockActivity {

	private static final String ACCESS_CODE = "code";

	private OAuth2Provider mProvider;
	private SsoHandler mSsoHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empty_pane);

		mProvider = new OAuth2Provider().setApiKey(Constants.WEIBO_API_KEY)
				.setSecretKey(Constants.WEIBO_SECRET_KEY)
				.setAuthUrl(Constants.WEIBO_AUTH_URL)
				.setRedirectUrl(Constants.WEIBO_REDIRECT_URL)
				.setRemoteService(Constants.WEIBO_REMOTE_SERVICE)
				.setSignature(Constants.WEIBO_SIGNATURE)
				.addScope(RequestGrantScope.ALL_SCOPE);

		mSsoHandler = new SsoHandler(WeiboLoginActivity.this, mProvider);
		mSsoHandler.authorize(new AuthDialogListener());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	class AuthDialogListener implements AuthListener {

		@Override
		public void onComplete(Bundle values) {
			String code = values.getString(ACCESS_CODE);
			if (code != null) {
				Toast.makeText(WeiboLoginActivity.this, "code: " + code,
						Toast.LENGTH_SHORT).show();
				return;
			}
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			OAuth2AccessToken accessToken = new OAuth2AccessToken(token,
					expires_in);
			if (accessToken.isSessionValid()) {
				Toast.makeText(WeiboLoginActivity.this,
						"token: " + accessToken, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onError(AuthException e) {
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}
	}
}
