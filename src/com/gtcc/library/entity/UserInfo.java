package com.gtcc.library.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo {

	private String uid;
	private String userName;
	private String userImageUrl;

	public UserInfo(String jsonUser) {
		JSONObject jObj;
		try {
			jObj = new JSONObject(jsonUser);

			if (jObj.has("uid")) {
				uid = jObj.getString("uid");
			}
			if (jObj.has("name")) {
				userName = jObj.getString("name");
			}
			if (jObj.has("avatar")) {
				userImageUrl = jObj.getString("avatar");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getUserId() {
		return uid;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserImageUrl() {
		return userImageUrl;
	}
}