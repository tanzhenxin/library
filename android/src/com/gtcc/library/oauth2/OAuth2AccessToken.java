package com.gtcc.library.oauth2;

import java.io.Serializable;

public class OAuth2AccessToken implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String KEY_TOKEN = "access_token";
	public static final String KEY_EXPIRES = "expires_in";
	public static final String KEY_REFRESHTOKEN = "refresh_token";

	private String accessToken = null;
	private Integer expiresIn = null;
	private String refreshToken = null;
	private String doubanUserId = null;
	private String doubanUserName = null;

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken
	 *            the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return the expiresIn
	 */
	public Integer getExpiresIn() {
		return expiresIn;
	}

	/**
	 * @param expiresIn
	 *            the expiresIn to set
	 */
	public void setExpiresIn(Integer expiresIn) {
		this.expiresIn = expiresIn;
	}

	/**
	 * @return the refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * @param refreshToken
	 *            the refreshToken to set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * @return the doubanUserId
	 */
	public String getDoubanUserId() {
		return doubanUserId;
	}

	/**
	 * @param doubanUserId
	 *            the doubanUserId to set
	 */
	public void setDoubanUserId(String doubanUserId) {
		this.doubanUserId = doubanUserId;
	}

	public String getDoubanUserName() {
		return doubanUserName;
	}

	public void setDoubanUserName(String doubanUserName) {
		this.doubanUserName = doubanUserName;
	}

	public OAuth2AccessToken() {

	}

	public OAuth2AccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public OAuth2AccessToken(String accessToken, int expiresIn, String refreshToken,
			String doubanUserId) {
		this.accessToken = accessToken;
		this.doubanUserId = doubanUserId;
		this.expiresIn = expiresIn;
		this.refreshToken = refreshToken;
	}

}