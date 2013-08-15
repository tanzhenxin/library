package com.gtcc.library.oauth2;

import org.apache.http.client.HttpResponseException;


public class ErrorHandler {
	  
	  public static final int HTTP_RESPONSE_ERROR_STATUS_CODE = 1015;
	  public static final int ACCESS_TOKEN_NOT_SET = 727;
	  public static final int MISSING_REQUIRED_PARAM = 728;
	  public static final int WRONG_JSON_FORMAT = 100;
	  
	  public static AuthException accessTokenNotSet () {
	    return new AuthException("This method needs access token to gain accessability", ACCESS_TOKEN_NOT_SET);
	  }
	  
	  public static AuthException missingRequiredParam () {
	    return new AuthException("This method is missing required params", MISSING_REQUIRED_PARAM);
	  }
	  
	  public static AuthException cannotGetAccessToken () {
	    return new AuthException("Cannot get access token, IO exception", ACCESS_TOKEN_NOT_SET);
	  }
	
	  public static AuthException handleHttpResponseError (HttpResponseException ex) {
	    return new AuthException("HttpResponseException : http status : " + ex.getStatusCode() + " message : " + ex.getMessage(), HTTP_RESPONSE_ERROR_STATUS_CODE);
	  }
	  
	  public static AuthException wrongJsonFormat (String rawString) {
	    return new AuthException("Illegal JSON format : " + rawString, WRONG_JSON_FORMAT);
	  }
	 
	  public static AuthException getCustomException (int code, String msg) {
		    return new AuthException(msg, code);
		  }
	}
