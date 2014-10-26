package com.gtcc.library.oauth2;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.gtcc.library.util.LogUtils;

import android.text.TextUtils;

public class OAuth2AccessToken implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final String TAG = LogUtils.makeLogTag(OAuth2AccessToken.class);

	private String mAccessToken;
	private String mExpiresIn;
	private String mRefreshToken;
	private String mUserId;

	public OAuth2AccessToken() {

	}

	public OAuth2AccessToken(String accessToken, String expiresIn, String refreshToken,
			String userId) {
		this.mAccessToken = accessToken;
		this.mExpiresIn = expiresIn;
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

	public boolean isSessionValid() {
		long expiresIn = System.currentTimeMillis() + Long.parseLong(mExpiresIn)*1000;
		return (!TextUtils.isEmpty(mAccessToken) && (expiresIn == 0 || (System
				.currentTimeMillis() < expiresIn)));
	}
}