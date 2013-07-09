package com.gtcc.library.provider;

import android.net.Uri;
import android.provider.BaseColumns;


public final class LibraryContract {
	public final static String CONTENT_AUTHORITY = "com.gtcc.library";
	private final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
	
	private LibraryContract() {
		
	}
	
	interface UserColumns {
		String USER_ID = "user_id";
		String USER_NAME = "user_name";
		String USER_IMAGE_URL = "user_image_url";
	}
	
	interface BookColumns {
		String BOOK_ID = "book_id";
		String BOOK_TITLE = "book_title";
		String BOOK_AUTHOR = "book_author";
		String BOOK_AUTHRO_INTRO = "author_intro";
		String BOOK_SUMMARY = "book_summary";
		String BOOK_IMAGE_URL = "book_image_url";
		String BOOK_CATEGORY = "book_category";
		String BOOK_OWNER = "book_owner";
		String BOOK_USER = "book_user";
		String DUE_DATE = "due_date";
	}
	
	private final static String PATH_USERS = "users";
	private final static String PATH_BOOKS = "books";
	
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
		
		public static final String CATEGORY_TECHNICAL = "technical";
		public static final String CATEGORY_SELF = "self";
		public static final String CATEGORY_ENGLISH = "english";
		public static final String CATEGORY_MISC = "misc";
		
		public static final Uri buildBookUri(String bookId) {
			return CONTENT_URI.buildUpon().appendPath(bookId).build();
		}
		
		public static final String getBookId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
	}
}
