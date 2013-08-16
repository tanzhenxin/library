package com.gtcc.library.ui.user;

import android.os.Bundle;

import com.gtcc.library.oauth2.Constants;
import com.gtcc.library.oauth2.OAuth2Provider;
import com.gtcc.library.oauth2.RequestGrantScope;

public class DoubanLoginActivity extends AuthLoginActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		doOAuth2Login();
	}

	@Override
	public OAuth2Provider GetOAuth2Provider() {
		return new OAuth2Provider()
			.setApiKey(Constants.DOUBAN_API_KEY)
			.setSecretKey(Constants.DOUBAN_SECRET_KEY)
			.setAuthUrl(Constants.DOUBAN_AUTH_URL)
			.setTokenUrl(Constants.DOUBAN_TOKEN_URL)
			.setRedirectUrl(Constants.DOUBAN_REDIRECT_URL)
			.addScope(RequestGrantScope.BASIC_COMMON_SCOPE)
			.addScope(RequestGrantScope.BOOK_READ_SCOPE);
	}
}