package com.gtcc.library.oauth2;

import org.json.JSONException;
import org.json.JSONObject;

import com.gtcc.library.util.LogUtils;

public class Converters {
	
	private static final String TAG = LogUtils.makeLogTag(Converters.class);

	public static AccessToken stringToAccessToken(String responseStr)
			throws DoubanException {
		LogUtils.LOGD(TAG, "Begin to get access token. Trade code is: " + responseStr);
		
		if (responseStr == null) {
			throw ErrorHandler.cannotGetAccessToken();
		}
		
		AccessToken token = new AccessToken();
		try {
			JSONObject jObj = new JSONObject(responseStr);
			
			if (jObj.has("access_token")) {
				String accessToken;
				accessToken = jObj.getString("access_token");
				token.setAccessToken(accessToken);
			} else {
				throw ErrorHandler.cannotGetAccessToken();
			}
			if (jObj.has("expires_in")) {
				int expiresIn = jObj.getInt("expires_in");
				token.setExpiresIn(expiresIn);
			} else {
				throw ErrorHandler.cannotGetAccessToken();
			}
			if (jObj.has("refresh_token")) {
				String refreshToken = jObj.getString("refresh_token");
				token.setRefreshToken(refreshToken);
			}
			if (jObj.has("douban_user_id")) {
				String doubanUserId = jObj.getString("douban_user_id");
				token.setDoubanUserId(doubanUserId);
			}
			if (jObj.has("douban_user_name")) {
				String doubanUserName = jObj.getString("douban_user_name");
				token.setDoubanUserName(doubanUserName);
			}
		} catch (JSONException e) {
			LogUtils.LOGE(TAG, "Failed to get access token");
		}
		
		LogUtils.LOGD(TAG, "Get access token succeed.");
		return token;
	}
}