package com.gtcc.library.webserviceproxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Base64;

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
	
	public int uploadUserImage(String userName, Bitmap bitmap) throws JSONException, IOException {
		JSONArray array = new JSONArray();
		array.put(userName);
		array.put(convertBitmapToString(bitmap));
		
        JSONObject result = super.callService(WebServiceInfo.USER_SERVICE, WebServiceInfo.USER_METHOD_UPLOAD_IMAGE, array);
        if (result != null) {
            return result.getInt("_returnCode");
        }

		return WebServiceInfo.OPERATION_FAILED;
	}
	
	private String convertBitmapToString(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); 
		byte[] byte_arr = stream.toByteArray();
		return Base64.encodeToString(byte_arr, Base64.DEFAULT);
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
