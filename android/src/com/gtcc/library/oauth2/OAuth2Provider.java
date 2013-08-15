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

	private String apiKey;
	private String secretKey;
	private String authUrl;
	private String redirectUrl;
	private String responseType = "code";
	private String grantType = "authorization_code";

	private ArrayList<RequestGrantScope> scopes = new ArrayList<RequestGrantScope>();

	/**
	 * @return the apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * @param apiKey
	 *            the apiKey to set
	 */
	public OAuth2Provider setApiKey(String apiKey) {
		this.apiKey = apiKey;
		return this;
	}

	/**
	 * @return the secretKey
	 */
	public String getSecretKey() {
		return secretKey;
	}

	/**
	 * @param secretKey
	 *            the secretKey to set
	 */
	public OAuth2Provider setSecretKey(String secretKey) {
		this.secretKey = secretKey;
		return this;
	}

	/**
	 * @return the authUrl
	 */
	public String getAuthUrl() {
		return authUrl;
	}

	/**
	 * @param authUrl
	 *            the authUrl to set
	 */
	public OAuth2Provider setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
		return this;
	}

	/**
	 * @return the redirectUrl
	 */
	public String getRedirectUrl() {
		return redirectUrl;
	}

	/**
	 * @param redirectUrl
	 *            the redirectUrl to set
	 */
	public OAuth2Provider setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
		return this;
	}

	/**
	 * @return the type
	 */
	public String getResponseType() {
		return responseType;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public OAuth2Provider setResponseType(String type) {
		this.responseType = type;
		return this;
	}

	/**
	 * @return the grantType
	 */
	public String getGrantType() {
		return grantType;
	}

	/**
	 * @param grantType
	 *            the grantType to set
	 */
	public OAuth2Provider setGrantType(String grantType) {
		this.grantType = grantType;
		return this;
	}

	public OAuth2Provider addScope(RequestGrantScope scope) {
		this.scopes.add(scope);
		return this;
	}

	public String getGetCodeRedirectUrl() {
		if (this.redirectUrl == null || TextUtils.isEmpty(this.redirectUrl)) {
			LogUtils.LOGE(TAG,
					"Redirect url cannot be null or empty, did you forget to set it?");
			return null;
		}
		StringBuilder getCodeUrl = new StringBuilder(this.authUrl);
		getCodeUrl.append("?client_id=").append(this.apiKey)
				.append("&redirect_uri=").append(this.redirectUrl)
				.append("&response_type=").append(this.responseType);
		if (!this.scopes.isEmpty()) {
			getCodeUrl.append("&scope=").append(generateScopeString());
		}
		return getCodeUrl.toString();
	}

	public String tradeAccessTokenWithCode(String code) throws OAuth2Exception {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("client_id", this.apiKey);
			params.put("client_secret", this.secretKey);
			params.put("redirect_uri", DefaultConfigs.DOUBAN_REDIRECT_URL);
			params.put("grant_type", "authorization_code");
			params.put("code", code);
			String responseStr = new HttpManager().postEncodedEntry(
					DefaultConfigs.DOUBAN_TOKEN_URL, params, false);
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