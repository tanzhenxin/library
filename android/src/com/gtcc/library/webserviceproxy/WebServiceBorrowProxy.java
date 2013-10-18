package com.gtcc.library.webserviceproxy;

import com.gtcc.library.entity.Book;
import com.gtcc.library.entity.Borrow;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WebServiceBorrowProxy extends WebServiceProxyBase {
	
	public void getAllHistory() throws Exception {
		JSONObject result = super.callService(WebServiceInfo.BORROW_SERVICE, WebServiceInfo.BORROW_METHOD_GET_ALL_HISTORY, null);
		if (result != null) {
			
		}
	}

	public int borrow(String userName, String bookBianhao) throws Exception {
		JSONArray array = new JSONArray();
		array.put(String.valueOf(userName));
		array.put(String.valueOf(bookBianhao));
		JSONObject result = super.callService(WebServiceInfo.BORROW_SERVICE, WebServiceInfo.BORROW_METHOD_BORROW, array);
		if (result != null) {
			return result.getInt("_returnCode");
		}
		
		return WebServiceInfo.OPERATION_FAILED;
	}
	
	public int returnBook(String userName, String bookBianhao) throws Exception {
		JSONArray array = new JSONArray();
		array.put(String.valueOf(userName));
		array.put(String.valueOf(bookBianhao));
		JSONObject result = super.callService(WebServiceInfo.BORROW_SERVICE, WebServiceInfo.BORROW_METHOD_RETURN_BOOK, array);
		if (result != null) {
			return result.getInt("_returnCode");
		}
		
		return WebServiceInfo.OPERATION_FAILED;
	}
	
	public List<Borrow> getBorrowInfo(String userName) throws Exception {
        List<Borrow> books = new ArrayList<Borrow>();
        JSONArray array = new JSONArray();
        array.put(userName);
        JSONObject result = super.callService(WebServiceInfo.BORROW_SERVICE, WebServiceInfo.BORROW_METHOD_GET_BORROW_INFO, array);
        if (result != null) {
            JSONObject jsonBooks = result.getJSONObject("borrowInfo");
            int length = jsonBooks.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonBook = jsonBooks.getJSONObject(Integer.toString(i));
                Borrow book = new Borrow();
                book.setUserName(jsonBook.getString("username"));
                book.setBookName(jsonBook.getString("bookName"));
                book.setBookBianhao(jsonBook.getString("bookBianhao"));
                book.setBorrowDate(jsonBook.getString("borrowDate"));
                book.setISBN(jsonBook.getString("ISBN"));
                book.setPlanReturnDate(jsonBook.getString("planReturnDate"));
                book.setRealReturnDate(jsonBook.getString("realReturnDate"));
                book.setImgUrl(WebServiceInfo.SERVER_IMG + book.getISBN() + ".jpg");
                books.add(book);
            }
        }

        return books;
	}
	
	public String checkWhetherBookInBorrow(String bookBianhao) throws Exception {
        JSONArray array = new JSONArray();
        array.put(String.valueOf(bookBianhao));
		JSONObject result = super.callService(WebServiceInfo.BORROW_SERVICE, WebServiceInfo.BORROW_METHOD_CHECK_WHETHER_BOOK_IN_BORROW, array);
		if (result != null) {
            int ret = result.getInt("_returnCode");
            if (ret == WebServiceInfo.OPERATION_SUCCEED){
            	JSONObject jsonBorrowHistory = result.getJSONObject("borrowHistory");
            	return jsonBorrowHistory.getString("username");
            }
		}
        return null;
	}
	
	public void removeAll() throws Exception {
		JSONObject result = super.callService(WebServiceInfo.BORROW_SERVICE, WebServiceInfo.BORROW_METHOD_REMOVE_ALL, null);
		if (result != null) {
			
		}
	}
}
