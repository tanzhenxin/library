package com.gtcc.library.oauth2;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.gtcc.library.util.LogUtils;

import android.text.TextUtils;

public class OAuth2AccessToken implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final String TAG = LogUtils.makeLogTag(OAuth2AccessToken.class);
	
	public static final String KEY_TOKEN = "access_token";
	public static final String KEY_EXPIRES = "expires_in";
	public static final String KEY_REFRESH_TOKEN = "refresh_token";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_USER_NAME = "user_name";

	private String mAccessToken;
	private String mExpiresIn;
	private String mRefreshToken;
	private String mUserId;
	private String mUserName;
	
	public OAuth2AccessToken() {

	}

	public OAuth2AccessToken(String accessToken) {
		this.mAccessToken = accessToken;
	}
	
	public OAuth2AccessToken(String accessToken, String expiresIn) {
		this(accessToken);
		this.mExpiresIn = expiresIn;
	}

	public OAuth2AccessToken(String accessToken, String expiresIn, String refreshToken,
			String userId) {
		this(accessToken, expiresIn);
		this.mRefreshToken = refreshToken;
		this.mUserId = userId;
	}

	public String getAccessToken() {
		return mAccessToken;
	}

	public void setAccessToken(String accessToken) {
		this.mAccessToken = accessToken;
	}

	public String getExpiresIn() {
		return mExpiresIn;
	}

	public void setExpiresIn(String expiresIn) {
		this.mExpiresIn = expiresIn;
	}

	public String getRefreshToken() {
		return mRefreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.mRefreshToken = refreshToken;
	}

	public String getUserId() {
		return mUserId;
	}

	public void setUserId(String doubanUserId) {
		this.mUserId = doubanUserId;
	}

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String doubanUserName) {
		this.mUserName = doubanUserName;
	}
	
	public boolean isSessionValid() {
		long expiresIn = System.currentTimeMillis() + Long.parseLong(mExpiresIn)*1000;
		return (!TextUtils.isEmpty(mAccessToken) && (expiresIn == 0 || (System
				.currentTimeMillis() < expiresIn)));
	}

	public static OAuth2AccessToken stringToAccessToken(String responseStr)
			throws AuthException {
		LogUtils.LOGD(TAG, "Begin to get access token. Trade code is: " + responseStr);
		
		if (responseStr == null) {
			throw ErrorHandler.cannotGetAccessToken();
		}
		
		OAuth2AccessToken token = new OAuth2AccessToken();
		try {
			JSONObject jObj = new JSONObject(responseStr);
			
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
			if (jObj.has("douban_user_id")) {
				String doubanUserId = jObj.getString("douban_user_id");
				token.setUserId(doubanUserId);
			}
			if (jObj.has("douban_user_name")) {
				String doubanUserName = jObj.getString("douban_user_name");
				token.setUserName(doubanUserName);
			}
		} catch (JSONException e) {
			LogUtils.LOGE(TAG, "Failed to get access token");
		}
		
		LogUtils.LOGD(TAG, "Get access token succeed.");
		return token;
	}
}