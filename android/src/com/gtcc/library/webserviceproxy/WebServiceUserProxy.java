package com.gtcc.library.webserviceproxy;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebServiceUserProxy extends WebServiceProxyBase{

	public int login(String userName, String password) throws JSONException,
			IOException {
		JSONArray array = new JSONArray();
		array.put(userName);
		array.put(password);

        JSONObject result = super.callService(WebServiceInfo.LOGIN_SERVICE, WebServiceInfo.LOGIN_METHOD_LOGIN, array);
        if (result !=null){
            return result.getInt("_returnCode");
        }

		return WebServiceInfo.OPERATION_FAILED;
	}

	public int addUser(String userName, String password, String email) throws JSONException,
			IOException {
		JSONArray array = new JSONArray();
		array.put(userName);
		array.put(password);
		array.put(email);

        JSONObject result = super.callService(WebServiceInfo.USER_SERVICE, WebServiceInfo.USER_METHOD_ADD_USER, array);
        if (result != null) {
            return result.getInt("_returnCode");
        }

		return WebServiceInfo.OPERATION_FAILED;
	}
	
	public void getAllUsers() throws Exception {
		JSONObject result = super.callService(WebServiceInfo.USER_SERVICE, WebServiceInfo.USER_METHOD_GET_ALL_USERS, null);
		if (result != null) {
			
		}
	}
	
	public void removeUser() throws Exception {
		JSONObject result = super.callService(WebServiceInfo.USER_SERVICE, WebServiceInfo.USER_METHOD_ADD_USER, null);
		if (result != null) {
			
		}
	}
	
	public void editUser() throws Exception {
		JSONObject result = super.callService(WebServiceInfo.USER_SERVICE, WebServiceInfo.USER_METHOD_EDIT_USER, null);
		if (result != null) {
			
		}
	}
	
	public void removeAllUser() throws Exception {
		JSONObject result = super.callService(WebServiceInfo.USER_SERVICE, WebServiceInfo.USER_METHOD_REMOVE_ALL_USER, null);
		if (result != null) {
			
		}
	}
	
}
