package com.gtcc.library.webserviceproxy;

import com.gtcc.library.entity.Book;
import com.gtcc.library.entity.Borrow;
import org.json.JSONArray;
import org.json.JSONException;
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
                JSONObject jsonBorrow = jsonBooks.getJSONObject(Integer.toString(i));
                books.add(parseJSONObject(jsonBorrow));
            }
        }

        return books;
	}
	
	public List<Borrow> getBorrowedInfo(String username) throws Exception {
        List<Borrow> books = new ArrayList<Borrow>();
        JSONArray array = new JSONArray();
        array.put(username);
        JSONObject result = super.callService(WebServiceInfo.BORROW_SERVICE, WebServiceInfo.BORROW_METHOD_GET_BORROWED_INFO, array);
        if (result != null) {
            JSONObject jsonBooks = result.getJSONObject("borrowInfo");
            int length = jsonBooks.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonBorrow = jsonBooks.getJSONObject(Integer.toString(i));
                Borrow borrow = parseJSONObject(jsonBorrow);
                if (!contains(books, borrow)) {
                	books.add(borrow);
                }
            }
        }

        return books;
	}
	
	public Borrow checkWhetherBookInBorrow(String bookBianhao) throws Exception {
        JSONArray array = new JSONArray();
        array.put(String.valueOf(bookBianhao));
		JSONObject result = super.callService(WebServiceInfo.BORROW_SERVICE, WebServiceInfo.BORROW_METHOD_CHECK_WHETHER_BOOK_IN_BORROW, array);
		if (result != null) {
            int ret = result.getInt("_returnCode");
            if (ret == WebServiceInfo.OPERATION_SUCCEED){
            	JSONObject jsonBorrowHistory = result.getJSONObject("borrowHistory");
            	return parseJSONObject(jsonBorrowHistory);
            }
		}
        return null;
	}
	
	private Boolean contains(List<Borrow> borrows, Borrow borrow) {
		for (Borrow b : borrows) {
			if (b.getBook().getTag().equals(borrow.getBook().getTag())) {
				return true;
			}
		}
		return false;
	}
	
	private Borrow parseJSONObject(JSONObject jsonBorrow) throws JSONException {
        
        Borrow borrow = new Borrow();
        borrow.setUserName(jsonBorrow.getString("username"));
        borrow.setBorrowDate(jsonBorrow.getString("borrowDate"));
        borrow.setPlanReturnDate(jsonBorrow.getString("planReturnDate"));
        borrow.setRealReturnDate(jsonBorrow.getString("realReturnDate"));
        
        JSONObject jsonBook = jsonBorrow.getJSONObject("book");
		Book book = new Book();
		book.setAuthor(jsonBook.getString("author"));
		book.setDescription(jsonBook.getString("bookDescription"));
		book.setTitle(jsonBook.getString("title"));
		book.setPrice(jsonBook.getString("price"));
		book.setISBN(jsonBook.getString("ISBN"));
		book.setLanguage(jsonBook.getString("language"));
		book.setPublisher(jsonBook.getString("publisher"));
		book.setPublishDate(jsonBook.getString("publishedDate"));
		book.setTag(jsonBook.getString("bianhao"));
		book.setId(jsonBook.getString("bianhao"));
		book.setImgUrl(WebServiceInfo.SERVER_IMG + book.getISBN() + ".jpg");
		
		borrow.setBook(book);
		return borrow;
	}
}
