package com.gtcc.library.oauth2;

public class AuthException extends Throwable {
	
	private static final long serialVersionUID = 1L;

	private int mErrorCode;
	private String mFailingUrl;

	public int getErrorCode() {
		return mErrorCode;
	}
	
	public String getFailingUrl() {
        return mFailingUrl;
    }
	
	public AuthException(String msg, int code) {
		super(msg);
		this.mErrorCode = code;
	}

	public AuthException(String message, int errorCode, String failingUrl) {
		this(message, errorCode);
		
	}
}