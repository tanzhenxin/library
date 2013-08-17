package com.gtcc.library.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.gtcc.library.oauth2.AuthException;
import com.gtcc.library.oauth2.AuthListener;
import com.gtcc.library.oauth2.Constants;
import com.gtcc.library.oauth2.OAuth2AccessToken;
import com.gtcc.library.oauth2.OAuth2Provider;
import com.gtcc.library.oauth2.RequestGrantScope;
import com.gtcc.library.oauth2.SsoHandler;
import com.gtcc.library.util.LogUtils;

public class AuthWeiboLoginActivity extends AuthLoginActivity {

	private static final String TAG = LogUtils
			.makeLogTag(AuthWeiboLoginActivity.class);

	private SsoHandler mSsoHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSsoHandler = new SsoHandler(AuthWeiboLoginActivity.this, mProvider);
		mSsoHandler.authorize(new AuthListener() {
			@Override
			public void onComplete(Bundle values) {
				String token = values.getString(KEY_TOKEN);
				String expires_in = values.getString(KEY_EXPIRES);
				String refreshToken = values.getString(KEY_REFRESH_TOKEN);
				String userId = values.getString(KEY_USER_ID);
				OAuth2AccessToken accessToken = new OAuth2AccessToken(token,
						expires_in, refreshToken, userId);
				returnResult(accessToken);
			}

			@Override
			public void onError(AuthException e) {
				LogUtils.LOGD(TAG,
						"SSO logon failed. Reason: " + e.getMessage());
				doOAuth2Login();
			}

			@Override
			public void onCancel() {
				LogUtils.LOGD(TAG, "SSO logon cancelled.");
				finish();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	public OAuth2Provider GetOAuth2Provider() {
		return new OAuth2Provider().setApiKey(Constants.WEIBO_API_KEY)
				.setSecretKey(Constants.WEIBO_SECRET_KEY)
				.setAuthUrl(Constants.WEIBO_AUTH_URL)
				.setTokenUrl(Constants.WEIBO_TOKEN_URL)
				.setRedirectUrl(Constants.WEIBO_REDIRECT_URL)
				.setRemoteService(Constants.WEIBO_REMOTE_SERVICE)
				.setSignature(Constants.WEIBO_SIGNATURE)
				.addScope(RequestGrantScope.ALL_SCOPE)
				.setAdditionalParameters("&display=mobile");
	}
}
