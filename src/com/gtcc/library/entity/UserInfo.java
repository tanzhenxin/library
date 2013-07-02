package com.gtcc.library.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo {
	
	private String uid;
	
	public UserInfo(String jsonUser) {
		JSONObject jObj;
		try {
			jObj = new JSONObject(jsonUser);

		    if (jObj.has("uid")) {
		      uid = jObj.getString("uid");
		    }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String GetUserId() {
		return uid;
	}
}