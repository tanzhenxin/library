package com.gtcc.library.oauth2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.gtcc.library.util.HttpManager;
import com.gtcc.library.util.LogUtils;

public class OAuth2Provider {

	private static final String TAG = LogUtils.makeLogTag(OAuth2Provider.class);

	private String mApiKey;
	private String mSecretKey;
	private String mAuthUrl;
	private String mRedirectUrl;
	private String mResponseType = "code";
	private String mGrantType = "authorization_code";
	
	// SSO logon
	private String mRemoteService;
	private String mSignature;

	private ArrayList<RequestGrantScope> scopes = new ArrayList<RequestGrantScope>();

	public String getApiKey() {
		return mApiKey;
	}

	public OAuth2Provider setApiKey(String apiKey) {
		this.mApiKey = apiKey;
		return this;
	}

	public String getSecretKey() {
		return mSecretKey;
	}

	public OAuth2Provider setSecretKey(String secretKey) {
		this.mSecretKey = secretKey;
		return this;
	}

	public String getAuthUrl() {
		return mAuthUrl;
	}

	public OAuth2Provider setAuthUrl(String authUrl) {
		this.mAuthUrl = authUrl;
		return this;
	}

	public String getRedirectUrl() {
		return mRedirectUrl;
	}

	public OAuth2Provider setRedirectUrl(String redirectUrl) {
		this.mRedirectUrl = redirectUrl;
		return this;
	}

	public String getResponseType() {
		return mResponseType;
	}

	public OAuth2Provider setResponseType(String type) {
		this.mResponseType = type;
		return this;
	}

	public String getGrantType() {
		return mGrantType;
	}

	public OAuth2Provider setGrantType(String grantType) {
		this.mGrantType = grantType;
		return this;
	}
	
	public String getRemoteSerive() {
		return mRemoteService;
	}

	public OAuth2Provider setRemoteService(String remoteService) {
		this.mRemoteService = remoteService;
		return this;
	}
	
	public String getSignature() {
		return mSignature;
	}

	public OAuth2Provider setSignature(String signature) {
		this.mSignature = signature;
		return this;
	}

	public OAuth2Provider addScope(RequestGrantScope scope) {
		this.scopes.add(scope);
		return this;
	}

	public String getGetCodeRedirectUrl() {
		if (this.mRedirectUrl == null || TextUtils.isEmpty(this.mRedirectUrl)) {
			LogUtils.LOGE(TAG,
					"Redirect url cannot be null or empty, did you forget to set it?");
			return null;
		}
		StringBuilder getCodeUrl = new StringBuilder(this.mAuthUrl);
		getCodeUrl.append("?client_id=").append(this.mApiKey)
				.append("&redirect_uri=").append(this.mRedirectUrl)
				.append("&response_type=").append(this.mResponseType);
		if (!this.scopes.isEmpty()) {
			getCodeUrl.append("&scope=").append(generateScopeString());
		}
		return getCodeUrl.toString();
	}

	public String tradeAccessTokenWithCode(String code) throws AuthException {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("client_id", this.mApiKey);
			params.put("client_secret", this.mSecretKey);
			params.put("redirect_uri", Constants.DOUBAN_REDIRECT_URL);
			params.put("grant_type", "authorization_code");
			params.put("code", code);
			String responseStr = new HttpManager().postEncodedEntry(
					Constants.DOUBAN_TOKEN_URL, params, false);
			return responseStr;
		} catch (UnsupportedEncodingException ex) {
			throw ErrorHandler.getCustomException(100,
					"Exception in trading access token : " + ex.toString());
		} catch (IOException ex) {
			throw ErrorHandler.getCustomException(100,
					"Exception in trading access token : " + ex.toString());
		}
	}

	private String generateScopeString() {
		if (this.scopes == null || this.scopes.isEmpty()) {
			return "";
		} else {
			StringBuilder scopeStr = new StringBuilder();
			for (RequestGrantScope sco : this.scopes) {
				scopeStr.append(sco.getValue()).append(",");
			}
			scopeStr.deleteCharAt(scopeStr.length() - 1); // Get rid of the last
															// comma
			return scopeStr.toString();
		}
	}
}