package com.gtcc.library.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.gtcc.library.R;
import com.gtcc.library.oauth2.DefaultConfigs;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;

public class WeiboOAuth2LoginActivity extends SherlockFragmentActivity {

	private static final String ACCESS_CODE = "code";

	private SsoHandler mSsoHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empty_pane);

		Weibo weibo = Weibo.getInstance(DefaultConfigs.WEIBO_API_KEY,
				DefaultConfigs.WEIBO_REDIRECT_URL);
		mSsoHandler = new SsoHandler(WeiboOAuth2LoginActivity.this, weibo);
		mSsoHandler.authorize(new AuthDialogListener());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			String code = values.getString(ACCESS_CODE);
			if (code != null) {
				Toast.makeText(WeiboOAuth2LoginActivity.this, "code: " + code,
						Toast.LENGTH_SHORT).show();
				return;
			}
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			Oauth2AccessToken accessToken = new Oauth2AccessToken(token,
					expires_in);
			if (accessToken.isSessionValid()) {
				Toast.makeText(WeiboOAuth2LoginActivity.this,
						"token: " + accessToken, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onError(WeiboDialogError e) {
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}

}
