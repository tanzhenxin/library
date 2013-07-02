package com.gtcc.library.util;

import org.json.JSONException;
import org.json.JSONObject;

import com.gtcc.library.oauth2.AccessToken;
import com.gtcc.library.oauth2.DoubanException;

public class Converters {

	  public static AccessToken stringToAccessToken(String responseStr) throws DoubanException {
	    if (responseStr == null) {
	      throw ErrorHandler.cannotGetAccessToken();
	    }
	    System.out.println("got result !");
	    System.out.println(responseStr);
	    JSONObject jObj = Converters.toJsonObj(responseStr);
	    AccessToken token = new AccessToken();
	    try {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return token;
	  }

//	  public static Date convertStringToDateTimeInRFC3339(String dateStr) {
//	    DateTime dt = DateTime.parseRfc3339(dateStr);
//	    return new Date(dt.getValue());
//	  }
//
//	  public static String convertDateToStringInRFC3339(Date date) {
//	    DateTime dt = new DateTime(date.getTime(), 480);
//	    String wholeFormat = dt.toString();
//	    //Do a little hack here for converting the date into the proper string
//	    String result = wholeFormat.substring(0, wholeFormat.indexOf(".")) + wholeFormat.substring(wholeFormat.indexOf(".") + 4);
//	    return result;
//	  }

	  public static JSONObject toJsonObj(String jsonStr) throws DoubanException {
	    try {
	      JSONObject result = new JSONObject(jsonStr);
	      return result;
	    } catch (JSONException ex) {
	      throw ErrorHandler.wrongJsonFormat(jsonStr);
	    }
	  }

//	  public static <T> String parseDoubanObjToJSONStr(T obj) throws IOException {
//	    JsonHttpContent content = new JsonHttpContent(new JacksonFactory(), obj);
//	    ByteArrayOutputStream os = new ByteArrayOutputStream();
//	    content.writeTo(os);
//	    String result = new String(os.toByteArray());
//	    return result;
//	  }
	  
//	  public static <T> String parseDoubanObjToXMLStr(T obj) throws IOException {
//	    XmlHttpContent content = new XmlHttpContent(DefaultConfigs.DOUBAN_XML_NAMESPACE, "entry", obj);
//	    ByteArrayOutputStream os = new ByteArrayOutputStream();
//	    content.writeTo(os);
//	    String result = new String(os.toByteArray());
//	    return result;
//	  }

//	  public static void main(String[] args) {
//	    System.out.println(convertStringToDateTimeInRFC3339("2006-03-29T10:36:19+08:00"));
//	    System.out.println(convertDateToStringInRFC3339(new Date()));
//	  }
	}