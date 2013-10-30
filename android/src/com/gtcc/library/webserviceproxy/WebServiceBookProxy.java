package com.gtcc.library.webserviceproxy;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

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
				book.setPublisher(jsonBook.getString("publisher"));
				book.setPublishDate(jsonBook.getString("publishedDate"));
				book.setTag(jsonBook.getString("bianhao"));
				book.setId(jsonBook.getString("bianhao"));
				book.setImgUrl(WebServiceInfo.SERVER_IMG + book.getISBN() + ".jpg");
				books.add(book);
			}
		}
		
		return books;
	}

	public List<Book> getAllBooksInList(int offset, int count) throws Exception {
		List<Book> books = new ArrayList<Book>();
		JSONArray array = new JSONArray();
		array.put(String.valueOf(offset));
		array.put(String.valueOf(count));
		JSONObject result = super.callService(WebServiceInfo.BOOK_SERVICE, WebServiceInfo.BOOK_METHOD_GET_ALL_BOOKS_IN_LIST, array);
		if (result != null) {
			JSONObject jsonBooks = result.getJSONObject("Books");
			int length = jsonBooks.length();
			for (int i = 0; i < length; i++) {
				JSONObject jsonBook = jsonBooks.getJSONObject(Integer.toString(i));
				Book book = new Book();
				
				book.setTitle(jsonBook.getString("title"));
				book.setAuthor(jsonBook.getString("author"));
				book.setDescription(jsonBook.getString("bookDescription"));
				book.setPrice(jsonBook.getString("price"));
				book.setISBN(jsonBook.getString("ISBN"));
				book.setLanguage(jsonBook.getString("language"));
				book.setPublisher(jsonBook.getString("publisher"));
				book.setPublishDate(jsonBook.getString("publishedDate"));
				book.setTag(jsonBook.getString("bianhao"));
				book.setId(jsonBook.getString("bianhao"));
				book.setImgUrl(WebServiceInfo.SERVER_IMG + book.getISBN() + ".jpg");
				
				books.add(book);
			}
		}
		
		return books;
	}
	
	public Book getBookByBianHao(String bianHao) throws Exception {
		JSONArray array = new JSONArray();
		array.put(String.valueOf(bianHao));
		JSONObject result = super.callService(WebServiceInfo.BOOK_SERVICE, WebServiceInfo.BOOK_METHOD_GET_BOOK_BY_BIANHAO, array);
		if (result != null) {
			int ret = result.getInt("_returnCode");
			if (ret == WebServiceInfo.OPERATION_SUCCEED){
				JSONObject jsonBook = result.getJSONObject("book");
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
				
				return book;
			}
		}
		return null;
	}
	
	public Book getBookByISBN(String ISBN) throws Exception {
		JSONArray array = new JSONArray();
		array.put(String.valueOf(ISBN));
		JSONObject result = super.callService(WebServiceInfo.BOOK_SERVICE, WebServiceInfo.BOOK_METHOD_GET_BOOK_BY_ISBN, array);
		if (result != null) {
			int ret = result.getInt("_returnCode");
			if (ret == WebServiceInfo.OPERATION_SUCCEED){
				JSONObject jsonBook = result.getJSONObject("book");
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
				
				return book;
			}
		}
		return null;
	}
}
