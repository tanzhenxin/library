package com.gtcc.library.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import com.gtcc.library.entity.Book;
import com.gtcc.library.entity.UserInfo;
import com.gtcc.library.oauth2.OAuth2AccessToken;
import com.gtcc.library.oauth2.Constants;
import com.gtcc.library.oauth2.AuthException;
import com.gtcc.library.webserviceproxy.WebServiceBookProxy;
import com.gtcc.library.webserviceproxy.WebServiceBorrowProxy;
import com.gtcc.library.webserviceproxy.WebServiceInfo;
import com.gtcc.library.webserviceproxy.WebServiceUserProxy;

public class HttpManager {

	private String mAccessToken;
	private static final String TAG = LogUtils.makeLogTag(HttpManager.class);
	public static WebServiceBookProxy webServiceBookProxy = new WebServiceBookProxy();
	public static WebServiceBorrowProxy webServiceBorrowProxy = new WebServiceBorrowProxy();
	public static WebServiceUserProxy webServiceUserProxy = new WebServiceUserProxy();

	public HttpManager() {

	}

	public HttpManager(String token) {
		mAccessToken = token;
	}

	public String postEncodedEntry(String url, Map<String, String> params,
			boolean needAccessToken) throws AuthException, IOException {
		String resultData = "";

		HttpURLConnection urlConn = (HttpURLConnection) new URL(url)
				.openConnection();
		urlConn.setDoOutput(true);
		urlConn.setDoInput(true);
		urlConn.setRequestMethod("POST");
		urlConn.setUseCaches(false);
		urlConn.setInstanceFollowRedirects(true);
		urlConn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		urlConn.setRequestProperty("Charset", "UTF-8");

		DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
		out.writeBytes(constructParams(params));
		out.close();

		int responseCode = urlConn.getResponseCode();
		if (responseCode != 200) {
			InputStream errorstream = urlConn.getErrorStream();
			BufferedReader errorReader = new BufferedReader(
					new InputStreamReader(errorstream));
			String errorLine = "";

			while (((errorLine = errorReader.readLine()) != null)) {
				resultData += errorLine + "\n";
			}
			errorReader.close();

			LogUtils.LOGE(TAG, "postEncodedEntry failed. Reason: " + resultData);
		} else {
			InputStream inputStream = urlConn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			String inputLine = null;

			while (((inputLine = reader.readLine()) != null)) {
				resultData += inputLine + "\n";
			}
			reader.close();

			LogUtils.LOGV(TAG, "postEncodedEntry succeed.");
		}
		urlConn.disconnect();

		return resultData;
	}

	private String constructParams(Map<String, String> params)
			throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(entry.getKey()).append("=")
					.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			sb.append("&");
		}
		return sb.deleteCharAt(sb.length() - 1).toString();
	}

	private String appendApiKey(String url) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		if (url.contains("?"))
			sb.append("&");
		else
			sb.append("?");

		sb.append("apikey").append("=")
			.append(URLEncoder.encode(Constants.DOUBAN_API_KEY, "UTF-8"));
		return url + sb.toString();
	}

    public static int GetServerVerCode() {
    	int newVerCode = -1;
    	
        try {  
            String verjson = GetServerFile(WebServiceInfo.SERVER_ROOT  
                    + WebServiceInfo.CHECK_VERSION);  
            JSONArray array = new JSONArray(verjson);  
            if (array.length() > 0) {  
                JSONObject obj = array.getJSONObject(0);  
                newVerCode = Integer.parseInt(obj.getString("verCode"));  
            }  
        } catch (Exception e) {  
        	e.printStackTrace();
        }  
        
        return newVerCode;
    } 
    
    private static String GetServerFile(String urlString) {
    	String result = "";
    	try {
    	    URL url = new URL(urlString);

    	    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
    	    String str;
    	    while ((str = in.readLine()) != null) {
    	    	result += str;
    	    }
    	    in.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return result;
    }
}