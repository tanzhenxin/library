package com.gtcc.library.webserviceproxy;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;

/**
 * Created by ShenCV on 8/14/13.
 */
public class WebServiceProxyBase {
    protected JSONObject callService(String serviceName, String methodName, JSONArray params) throws JSONException, IOException {
        JSONObject jsonParams = new JSONObject();
        jsonParams.put(WebServiceInfo.SERVICE_NAME, serviceName);
        jsonParams.put(WebServiceInfo.METHOD_NAME, methodName);
        if (params != null) 
        	jsonParams.put(WebServiceInfo.PARAMETERS, params);

        HttpPost request = new HttpPost(URI.create(WebServiceInfo.SERVER));
        request.addHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(jsonParams.toString()));
        HttpResponse httpResponse = new DefaultHttpClient().execute(request);

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            String retSrc = EntityUtils.toString(httpResponse.getEntity());
            return new JSONObject(retSrc);
        }

        return null;
    }
}
