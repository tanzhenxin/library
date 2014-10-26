package com.gtcc.library.provider;

import java.util.List;

import android.net.Uri;
import android.provider.BaseColumns;


public class LibraryContract {
	public final static String CONTENT_AUTHORITY = "com.gtcc.library";
	public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
	
	public interface UserColumns {
		String USER_ID = "user_id";
		String USER_NAME = "user_name";
		String USER_IMAGE_URL = "user_image_url";
	}
	
	public interface BookColumns {
		String BOOK_ID = "book_id";
		String BOOK_TITLE = "book_title";
		String BOOK_AUTHOR = "book_author";
		String BOOK_AUTHRO_INTRO = "author_intro";
		String BOOK_DESCRIPTION = "book_description";
		String BOOK_LANGUAGE = "book_language";
		String BOOK_PUBLISHER = "book_publisher";
		String BOOK_PUBLISH_DATE = "book_publish_date";
		String BOOK_PRICE = "book_price";
		String BOOK_ISBN = "book_isbn";
		String BOOK_IMAGE_URL = "book_image_url";
		String BOOK_CATEGORY = "book_category";
	}
	
	private final static String PATH_USERS = "users";
	private final static String PATH_BOOKS = "books";
	private final static String PATH_CATEGORY = "category";
	private final static String PATH_ISBN = "isbn";
	private final static String PATH_SEARCH = "search";
	private final static String PATH_SEARCH_SUGGEST = "search_suggest_query";
	
	public static final class Users implements BaseColumns, UserColumns {
		public static final Uri CONTENT_URI = 
				BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();
		
		public static final String CONTENT_TYPE =
				"vnd.android.cursor.dir/vnd.library.user";
		public static final String CONTENT_ITEM_TYPE = 
				"vnd.android.cursor.item/vnd.library.user";
		public static final String DEFAULT_SORT_ORDER = 
				"users.user_id asc";
		
		public static final Uri buildUserUri(String userId) {
			return CONTENT_URI.buildUpon().appendPath(userId).build();
		}
		
		public static final Uri buildUserBooksUri(String userId) {
			return CONTENT_URI.buildUpon().appendPath(userId).
					appendPath(PATH_BOOKS).build();
		}
		
		public static final Uri buildUserBooksUri(String userId, String bookId) {
			return CONTENT_URI.buildUpon().appendPath(userId).
					appendPath(PATH_BOOKS).appendPath(bookId).build();
		}
		
		public static final Uri buildUserBooksUri(String userId, String bookId, String relation) {
			return CONTENT_URI.buildUpon().appendPath(userId).
					appendPath(PATH_BOOKS).appendPath(bookId).appendPath(relation).build();
		}
		
		public static final String getUserId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
		
		public static final String getBookId(Uri uri) {
			return uri.getPathSegments().get(3);
		}
	}
	
	public static final class Books implements BaseColumns, BookColumns {
		public static final Uri CONTENT_URI = 
				BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOKS).build();
		
		public static final String CONTENT_TYPE =
				"vnd.android.cursor.dir/vnd.library.book";
		public static final String CONTENT_ITEM_TYPE =
				"vnd.android.cursor.item/vnd.library.book";
		public static final String DEFAULT_SORT_ORDER = 
				" books.book_id asc";
		
		public static final Uri buildBookUri(String bookId) {
			return CONTENT_URI.buildUpon().appendPath(bookId).build();
		}
		
		public static final Uri buildCategoryUri(String category) {
			return CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).appendPath(category).build();
		}
		
		public static final Uri buildIsbnUri(String isbn) {
			return CONTENT_URI.buildUpon().appendPath(PATH_ISBN).appendPath(isbn).build();
		}
		
		public static final Uri buildSearchUri(String query) {
			return CONTENT_URI.buildUpon().appendPath(PATH_SEARCH).appendPath(query).build();
		}
		
		public static final String getBookId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
		
		public static final String getBookCategory(Uri uri) {
			return uri.getPathSegments().get(2);
		}
		
		public static final String getBookISBN(Uri uri) {
			return uri.getPathSegments().get(2);
		}
		
		public static final String getSearchQuery(Uri uri) {
			return uri.getPathSegments().get(2);
		}
		
		public static final boolean isSearchUri(Uri uri) {
			List<String> pathSegments = uri.getPathSegments();
			return pathSegments.size() > 2 && PATH_SEARCH.equals(pathSegments.get(1));
		}
	}
	
    public static class SearchSuggest {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCH_SUGGEST).build();

        public static final String DEFAULT_SORT = BaseColumns._ID
                + " DESC";
    }
}
