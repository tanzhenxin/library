package com.gtcc.library.oauth2;

public class OAuth2Exception extends Throwable {
	
	private static final long serialVersionUID = 1L;

	private int mErrorCode;
	private String mFailingUrl;

	public int getErrorCode() {
		return mErrorCode;
	}
	
	public String getFailingUrl() {
        return mFailingUrl;
    }
	
	public OAuth2Exception(String msg, int code) {
		super(msg);
		this.mErrorCode = code;
	}

	public OAuth2Exception(String message, int errorCode, String failingUrl) {
		this(message, errorCode);
		
	}
}