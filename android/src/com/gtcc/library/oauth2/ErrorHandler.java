package com.gtcc.library.oauth2;

import org.apache.http.client.HttpResponseException;


public class ErrorHandler {
	  
	  public static final int HTTP_RESPONSE_ERROR_STATUS_CODE = 1015;
	  public static final int ACCESS_TOKEN_NOT_SET = 727;
	  public static final int MISSING_REQUIRED_PARAM = 728;
	  public static final int WRONG_JSON_FORMAT = 100;
	  
	  public static OAuth2Exception accessTokenNotSet () {
	    return new OAuth2Exception("This method needs access token to gain accessability", ACCESS_TOKEN_NOT_SET);
	  }
	  
	  public static OAuth2Exception missingRequiredParam () {
	    return new OAuth2Exception("This method is missing required params", MISSING_REQUIRED_PARAM);
	  }
	  
	  public static OAuth2Exception cannotGetAccessToken () {
	    return new OAuth2Exception("Cannot get access token, IO exception", ACCESS_TOKEN_NOT_SET);
	  }
	
	  public static OAuth2Exception handleHttpResponseError (HttpResponseException ex) {
	    return new OAuth2Exception("HttpResponseException : http status : " + ex.getStatusCode() + " message : " + ex.getMessage(), HTTP_RESPONSE_ERROR_STATUS_CODE);
	  }
	  
	  public static OAuth2Exception wrongJsonFormat (String rawString) {
	    return new OAuth2Exception("Illegal JSON format : " + rawString, WRONG_JSON_FORMAT);
	  }
	 
	  public static OAuth2Exception getCustomException (int code, String msg) {
		    return new OAuth2Exception(msg, code);
		  }
	}
