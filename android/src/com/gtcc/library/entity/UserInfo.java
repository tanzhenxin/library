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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public UserInfo(String userName, String email, String password) {
		mUserName = userName;
		mUserEmail = email;
		mUserPassword = password;
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
}