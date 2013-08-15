package com.gtcc.library.webserviceproxy;

import org.json.JSONObject;

public class WebServiceBorrowProxy extends WebServiceProxyBase {
	
	public void getAllHistory() throws Exception {
		JSONObject result = super.callService(WebServiceInfo.BORROW_SERVICE, WebServiceInfo.BORROW_METHOD_GET_ALL_HISTORY, null);
		if (result != null) {
			
		}
	}

	public void borrow() throws Exception {
		JSONObject result = super.callService(WebServiceInfo.BORROW_SERVICE, WebServiceInfo.BORROW_METHOD_BORROW, null);
		if (result != null) {
			
		}
	}
	
	public void returnBook() throws Exception {
		JSONObject result = super.callService(WebServiceInfo.BORROW_SERVICE, WebServiceInfo.BORROW_METHOD_RETURN_BOOK, null);
		if (result != null) {
			
		}
	}
	
	public void getBorrowInfo() throws Exception {
		JSONObject result = super.callService(WebServiceInfo.BORROW_SERVICE, WebServiceInfo.BORROW_METHOD_GET_BORROW_INFO, null);
		if (result != null) {
			
		}
	}
	
	public void checkWhetherBookInBorrow() throws Exception {
		JSONObject result = super.callService(WebServiceInfo.BORROW_SERVICE, WebServiceInfo.BORROW_METHOD_CHECK_WHETHER_BOOK_IN_BORROW, null);
		if (result != null) {
			
		}
	}
	
	public void removeAll() throws Exception {
		JSONObject result = super.callService(WebServiceInfo.BORROW_SERVICE, WebServiceInfo.BORROW_METHOD_REMOVE_ALL, null);
		if (result != null) {
			
		}
	}
}
