package com.gtcc.library.webserviceproxy;

import com.gtcc.library.entity.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WebServiceBookProxy extends WebServiceProxyBase {

	/**
	 * Get all books from GTCCLibrary server
	 * 
	 * @param offset
	 *            start point of a query
	 * @param count
	 *            how many records to return
	 * @return
	 * @throws Exception
	 */
	public List<Book> getAllBooks(int offset, int count) throws Exception {
		return getBooks(WebServiceInfo.BOOK_METHOD_GET_ALL_BOOKS, offset, count);
	}

	public List<Book> getAllBooksInList(int offset, int count) throws Exception {
		return getBooks(WebServiceInfo.BOOK_METHOD_GET_ALL_BOOKS_IN_LIST,
				offset, count);
	}

	public List<Book> getAllBooksByCategory(String category, int offset,
			int count) throws Exception {
		return getBooks(WebServiceInfo.BOOK_METHOD_GET_ALL_BOOKS_BY_CATEGORY,
				offset, count, category);
	}

	public List<Book> searchBooks(String keyword, int offset, int count)
			throws Exception {
		return getBooks(WebServiceInfo.BOOK_METHOD_SEARCH_BOOKS, offset, count,
				keyword);
	}

	public Book getBookByBianHao(String bianHao) throws Exception {
		JSONArray array = new JSONArray();
		array.put(String.valueOf(bianHao));
		JSONObject result = super.callService(WebServiceInfo.BOOK_SERVICE,
				WebServiceInfo.BOOK_METHOD_GET_BOOK_BY_BIANHAO, array);
		if (result != null) {
			int ret = result.getInt("_returnCode");
			if (ret == WebServiceInfo.OPERATION_SUCCEED) {
				JSONObject jsonBook = result.getJSONObject("book");
				return parseJSONBook(jsonBook);
			}
		}
		return null;
	}

	public Book getBookByISBN(String ISBN) throws Exception {
		JSONArray array = new JSONArray();
		array.put(String.valueOf(ISBN));
		JSONObject result = super.callService(WebServiceInfo.BOOK_SERVICE,
				WebServiceInfo.BOOK_METHOD_GET_BOOK_BY_ISBN, array);
		if (result != null) {
			int ret = result.getInt("_returnCode");
			if (ret == WebServiceInfo.OPERATION_SUCCEED) {
				JSONObject jsonBook = result.getJSONObject("book");
				return parseJSONBook(jsonBook);
			}
		}
		return null;
	}

	public List<Book> getBookListByISBN(String ISBN) throws Exception {
		List<Book> books = new ArrayList<Book>();

		JSONArray array = new JSONArray();
		array.put(String.valueOf(ISBN));
		JSONObject result = super.callService(WebServiceInfo.BOOK_SERVICE,
				WebServiceInfo.BOOK_METHOD_GET_BOOK_LIST_BY_ISBN, array);
		if (result != null) {
			int ret = result.getInt("_returnCode");
			if (ret == WebServiceInfo.OPERATION_SUCCEED) {
				JSONObject jsonBooks = result.getJSONObject("BookList");
				int length = jsonBooks.length();
				for (int i = 0; i < length; i++) {
					JSONObject jsonBook = jsonBooks.getJSONObject(Integer
							.toString(i));
					books.add(parseJSONBook(jsonBook));
				}
			}
		}

		return books;
	}

	private List<Book> getBooks(String methodUri, int offset, int count)
			throws Exception {
		return getBooks(methodUri, offset, count, null);
	}

	private List<Book> getBooks(String methodUri, int offset, int count,
			String category) throws Exception {
		List<Book> books = new ArrayList<Book>();

		// set parameters.
		JSONArray array = new JSONArray();
		if (category != null) {
			array.put(category);
		}
		if (count != 0) {
			array.put(String.valueOf(offset));
			array.put(String.valueOf(count));
		} else {
			array.put("");
			array.put("");
		}

		JSONObject result = super.callService(WebServiceInfo.BOOK_SERVICE,
				methodUri, array);
		if (result != null) {
			int returnCode = result.getInt("_returnCode");
			if (returnCode == WebServiceInfo.OPERATION_SUCCEED) {
				JSONObject jsonBooks = result.getJSONObject("Books");
				int length = jsonBooks.length();
				for (int i = 0; i < length; i++) {
					JSONObject jsonBook = jsonBooks.getJSONObject(Integer
							.toString(i));
					books.add(parseJSONBook(jsonBook));
				}
			}
		}

		return books;
	}

	private Book parseJSONBook(JSONObject jsonBook) throws JSONException {
		Book book = new Book();

		book.setObjectId(jsonBook.getString("bianhao"));
		book.setTitle(jsonBook.getString("title"));
		book.setAuthor(jsonBook.getString("author"));
		book.setDescription(jsonBook.getString("bookDescription"));
		book.setPrice(jsonBook.getString("price"));
		book.setIsbn(jsonBook.getString("ISBN"));
		book.setPublisher(jsonBook.getString("publisher"));
		book.setPublishedDate(jsonBook.getString("publishedDate"));
		book.setImageUrl(WebServiceInfo.SERVER_IMG + book.getIsbn() + ".jpg");
		book.setCategory(book.getObjectId().substring(0, 1));

		return book;
	}
}
