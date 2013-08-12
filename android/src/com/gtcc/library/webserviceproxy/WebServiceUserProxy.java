package com.gtcc.library.webserviceproxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class WebServiceUserProxy {
	
	public static boolean isLogin(JSONObject user) throws UnsupportedEncodingException{
		
		String strUser = null, strPWD = null;
		JSONObject jsonParas = new JSONObject();
		try {
			jsonParas.put(WebServiceInfo.SERVICENAME, WebServiceInfo.LOGINSERVICE);
			jsonParas.put(WebServiceInfo.METHODNAME, WebServiceInfo.LOGINMETHOD);
			strUser = user.getString("username");
			strPWD = user.getString("password");
			JSONArray array = new JSONArray();
			array.put(strUser);
			array.put(strPWD);
			jsonParas.put(WebServiceInfo.PARAMETERS, array);
			
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(URI.create(WebServiceInfo.SERVER));
		//request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		request.addHeader("Content-Type", "application/json");
		//request.addHeader(name, value)
        //HttpEntity httpEntity=new UrlEncodedFormEntity(params,"gb2312");
        StringEntity httpEntity = new StringEntity(jsonParas.toString()); 
        request.setEntity(httpEntity);
        HttpResponse httpResponse = null;
		try {
			httpResponse = httpclient.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if(statusCode==HttpStatus.SC_OK){
		     	String retSrc;
				try {
					
					retSrc = EntityUtils.toString(httpResponse.getEntity());
					JSONObject result = new JSONObject( retSrc);
					String returnCode = result.getString("_returnCode");
					if(returnCode.equals(WebServiceInfo.OPERATIONSUCCESS))
						return true;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		     	
        		
        }
        
        return false;
	}
	
	public static boolean addUser(JSONObject user) throws UnsupportedEncodingException{
		
		String strUser = null, strPWD = null, strEmail = null;
		JSONObject jsonParas = new JSONObject();
		try {
			jsonParas.put(WebServiceInfo.SERVICENAME, WebServiceInfo.USERSERVICE);
			jsonParas.put(WebServiceInfo.METHODNAME, WebServiceInfo.ADDUSERMETHOD);
			strUser = user.getString("username");
			strPWD = user.getString("password");
			strEmail = user.getString("email");
			JSONArray array = new JSONArray();
			array.put(strUser);
			array.put(strPWD);
			array.put(strEmail);
			jsonParas.put(WebServiceInfo.PARAMETERS, array);
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(URI.create(WebServiceInfo.SERVER));
		request.addHeader("Content-Type", "application/json");
        //HttpEntity httpEntity=new UrlEncodedFormEntity(params,"gb2312");
        StringEntity httpEntity = new StringEntity(jsonParas.toString()); 
        request.setEntity(httpEntity);
        HttpResponse httpResponse = null;
		try {
			httpResponse = httpclient.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if(statusCode==HttpStatus.SC_OK){
		     	String retSrc;
				try {
					
					retSrc = EntityUtils.toString(httpResponse.getEntity());
					JSONObject result = new JSONObject( retSrc);
					String returnCode = result.getString("_returnCode");
					if(returnCode.equals(WebServiceInfo.OPERATIONSUCCESS))
						return true;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			     	
        		
        }
        
        return false;
	}
	
	public static boolean removeUser(JSONObject user) throws UnsupportedEncodingException{
		
		String strUser = null;
		JSONObject jsonParas = new JSONObject();
		try {
			jsonParas.put(WebServiceInfo.SERVICENAME, WebServiceInfo.USERSERVICE);
			jsonParas.put(WebServiceInfo.METHODNAME, WebServiceInfo.REMOVEUSERMETHOD);
			strUser = user.getString("username");
			JSONArray array = new JSONArray();
			array.put(strUser);
			jsonParas.put(WebServiceInfo.PARAMETERS, array);
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(URI.create(WebServiceInfo.SERVER));
		request.addHeader("Content-Type", "application/json");
        //HttpEntity httpEntity=new UrlEncodedFormEntity(params,"gb2312");
        StringEntity httpEntity = new StringEntity(jsonParas.toString()); 
        request.setEntity(httpEntity);
        HttpResponse httpResponse = null;
		try {
			httpResponse = httpclient.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if(statusCode==HttpStatus.SC_OK){
		     	String retSrc;
				try {
					retSrc = EntityUtils.toString(httpResponse.getEntity());
					JSONObject result = new JSONObject( retSrc);
					String returnCode = result.getString("_returnCode");
					if(returnCode.equals(WebServiceInfo.OPERATIONSUCCESS))
						return true;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			     	
        		
        }
        
        return false;
	}
}
