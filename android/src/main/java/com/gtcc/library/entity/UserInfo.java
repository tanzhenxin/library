package com.gtcc.library.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo implements Serializable {

	private String mUid;
	private String mUserName;
	private String mUserEmail;
	private String mUserPassword;
	private String mUserImageUrl;
	private String mAccessToken;
	
	private static UserInfo sCurrentUser;
	
	private UserInfo() {
		
	}

	public UserInfo(String jsonUser) {
		JSONObject jObj;
		try {
			jObj = new JSONObject(jsonUser);

			if (jObj.has("uid")) {
				mUid = jObj.getString("uid");
			}
			if (jObj.has("name")) {
				mUserName = jObj.getString("name");
			}
			if (jObj.has("avatar")) {
				mUserImageUrl = jObj.getString("avatar");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public UserInfo(String userId, String userName, String password) {
		mUid = userId;
		mUserName = userName;
		mUserPassword = password;
	}
	
	public UserInfo(String userId, String userName, String email, String password) {
		this(userId, userName, password);
		mUserEmail = email;
	}
	
	public String getUserId() {
		return mUid;
	}

	public String getUserName() {
		return mUserName;
	}
	
	public String getUserEmail() {
		return mUserEmail;
	}
	
	public String getUserPassword() {
		return mUserPassword;
	}

	public String getUserImageUrl() {
		return mUserImageUrl;
	}
	
	public String getAccessToken() {
		return mAccessToken;
	}
	
	public void setUserId(String userId) {
		mUid = userId;
	}
	
	public void setUserName(String userName) {
		mUserName = userName;
	}
	
	public void setUserEmail(String userEmail) {
		mUserEmail = userEmail;
	}
	
	public void setUserPassword(String userPassword) {
		mUserPassword = userPassword;
	}
	
	public void setUserImageUrl(String imageUrl) {
		mUserImageUrl = imageUrl;
	}
	
	public void setAccessToken(String token) {
		mAccessToken = token;
	}
	
	public void copy(UserInfo other) {
		if (other.mUid != null) {
			mUid = other.mUid;
		}
		if (other.mUserName != null) {
			mUserName = other.mUserName;
		}
		if (other.mUserEmail != null) {
			mUserEmail = other.mUserEmail;
		}
		if (other.mUserPassword != null) {
			mUserPassword = other.mUserPassword;
		}
		if (other.mUserImageUrl != null) {
			mUserImageUrl = other.mUserImageUrl;
		}
		if (other.mAccessToken != null) {
			mAccessToken = other.mAccessToken;
		}
	}
	
	public static UserInfo getCurrentUser() {
		if (sCurrentUser == null) {
			sCurrentUser = new UserInfo();
		}
		
		return sCurrentUser;
	}
}