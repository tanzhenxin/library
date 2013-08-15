package com.gtcc.library.webserviceproxy;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gtcc.library.entity.Book;

public class WebServiceBookProxy extends WebServiceProxyBase {
	
	/**
	 * Get all books from GTCCLibrary server
	 * 
	 * @param offset start point of a query
	 * @param count	how many records to return
	 * @return
	 * @throws Exception
	 */
	public List<Book> getAllBooks(int offset, int count) throws Exception {
		List<Book> books = new ArrayList<Book>();
		JSONArray array = new JSONArray();
		array.put(String.valueOf(offset));
		array.put(String.valueOf(count));
		JSONObject result = super.callService(WebServiceInfo.BOOK_SERVICE, WebServiceInfo.BOOK_METHOD_GET_ALL_BOOKS, array);
		if (result != null) {
			JSONObject jsonBooks = result.getJSONObject("Books");
			int length = jsonBooks.length();
			for (int i = 0; i < length; i++) {
				JSONObject jsonBook = jsonBooks.getJSONObject(Integer.toString(i));
				Book book = new Book();
				book.setAuthor(jsonBook.getString("author"));
				book.setDescription(jsonBook.getString("bookDescription"));
				book.setTitle(jsonBook.getString("title"));
				book.setPrice(jsonBook.getString("price"));
				book.setISBN(jsonBook.getString("ISBN"));
				book.setLanguage(jsonBook.getString("language"));
				book.setPublishDate(jsonBook.getString("publishedDate"));
				book.setBianhao(jsonBook.getString("bianhao"));
				book.setId(jsonBook.getString("bianhao"));
				books.add(book);
			}
		}
		
		return books;
	}

	public void addBook() throws Exception {
		JSONObject result = super.callService(WebServiceInfo.BOOK_SERVICE, WebServiceInfo.BOOK_METHOD_ADD_BOOKS, null);
		if (result != null) {
			
		}
	}
	
	public void removeBook() throws Exception {
		JSONObject result = super.callService(WebServiceInfo.BOOK_SERVICE, WebServiceInfo.USER_METHOD_REMOVE_USER, null);
		if (result != null) {
			
		}
	}
	
	public void editBook() throws Exception {
		JSONObject result = super.callService(WebServiceInfo.BOOK_SERVICE, WebServiceInfo.BOOK_METHOD_EDIT_BOOKS, null);
		if (result != null) {
			
		}
	}
	
	public void removeAll() throws Exception {
		JSONObject result = super.callService(WebServiceInfo.BOOK_SERVICE, WebServiceInfo.BOOK_METHOD_REMOVE_ALL, null);
		if (result != null) {
			
		}
	}
}
