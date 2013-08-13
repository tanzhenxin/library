package com.gtcc.library.webserviceproxy;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebServiceUserProxy {

	public static int login(String userName, String password) throws JSONException,
			IOException {
		JSONObject jsonParas = new JSONObject();
		jsonParas.put(WebServiceInfo.SERVICE_NAME, WebServiceInfo.LOGIN_SERVICE);
		jsonParas.put(WebServiceInfo.METHOD_NAME, WebServiceInfo.LOGIN_METHOD);
		JSONArray array = new JSONArray();
		array.put(userName);
		array.put(password);
		jsonParas.put(WebServiceInfo.PARAMETERS, array);

		HttpPost request = new HttpPost(URI.create(WebServiceInfo.SERVER));
		request.addHeader("Content-Type", "application/json");
		request.setEntity(new StringEntity(jsonParas.toString()));
		HttpResponse httpResponse = new DefaultHttpClient().execute(request);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			String retSrc = EntityUtils.toString(httpResponse.getEntity());
			JSONObject result = new JSONObject(retSrc);
			return result.getInt("_returnCode");
		}

		return WebServiceInfo.OPERATION_FAILED;
	}

	public static int addUser(String userName, String password, String email) throws JSONException,
			IOException {
		JSONObject jsonParas = new JSONObject();
		jsonParas.put(WebServiceInfo.SERVICE_NAME, WebServiceInfo.USER_SERVICE);
		jsonParas.put(WebServiceInfo.METHOD_NAME, WebServiceInfo.ADD_USER_METHOD);
		JSONArray array = new JSONArray();
		array.put(userName);
		array.put(password);
		array.put(email);
		jsonParas.put(WebServiceInfo.PARAMETERS, array);

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(URI.create(WebServiceInfo.SERVER));
		request.addHeader("Content-Type", "application/json");
		request.setEntity(new StringEntity(jsonParas.toString()));

		HttpResponse httpResponse = httpclient.execute(request);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			String retSrc = EntityUtils.toString(httpResponse.getEntity());
			JSONObject result = new JSONObject(retSrc);
			return result.getInt("_returnCode");
		}
		
		return WebServiceInfo.OPERATION_FAILED;
	}
}
