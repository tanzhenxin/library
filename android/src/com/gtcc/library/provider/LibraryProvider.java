package com.gtcc.library.provider;

import java.util.Arrays;

import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.provider.LibraryContract.Comments;
import com.gtcc.library.provider.LibraryContract.Users;
import com.gtcc.library.provider.LibraryDatabase.Tables;
import com.gtcc.library.provider.LibraryDatabase.UserBooks;
import com.gtcc.library.util.LogUtils;
import com.gtcc.library.util.SelectionBuilder;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

public class LibraryProvider extends ContentProvider {
	
	private static final String TAG = LogUtils.makeLogTag(LibraryProvider.class);
	
	private LibraryDatabase mDatabaseHelper; 
	
	private static UriMatcher sUriMatcher = buildUriMatcher();
	
	public static final int USERS = 100;
	public static final int USERS_ID = 101;
	public static final int USERS_ID_BOOKS = 102;
	public static final int USERS_ID_BOOKS_ID = 103;
	public static final int USERS_ID_COMMENTS = 110;
	
	public static final int BOOKS = 200;
	public static final int BOOKS_ID = 201;
	public static final int BOOKS_TECHNICAL = 202;
	public static final int BOOKS_SELF = 203;
	public static final int BOOKS_ENGLISH = 204;
	public static final int BOOKS_MISC = 205;
	public static final int BOOKS_ID_COMMENTS = 210;
	
	public static final int COMMENTS = 300;
	public static final int COMMENTS_ID = 301;
	
	private static UriMatcher buildUriMatcher() {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = LibraryContract.CONTENT_AUTHORITY;
		
		sUriMatcher.addURI(authority, "users", USERS);
		sUriMatcher.addURI(authority, "users/*", USERS_ID);
		sUriMatcher.addURI(authority, "users/*/books", USERS_ID_BOOKS);
		sUriMatcher.addURI(authority, "users/*/books/*", USERS_ID_BOOKS_ID);
		sUriMatcher.addURI(authority, "users/*/comments", USERS_ID_COMMENTS);
		
		sUriMatcher.addURI(authority, "books", BOOKS);
		sUriMatcher.addURI(authority, "books/*", BOOKS_ID);
		sUriMatcher.addURI(authority, "books/technical", BOOKS_TECHNICAL);
		sUriMatcher.addURI(authority, "books/self", BOOKS_SELF);
		sUriMatcher.addURI(authority, "books/english", BOOKS_ENGLISH);
		sUriMatcher.addURI(authority, "books/misc", BOOKS_MISC);
		sUriMatcher.addURI(authority, "books/*/comments", BOOKS_ID_COMMENTS);
		
		sUriMatcher.addURI(authority, "comments", COMMENTS);
		sUriMatcher.addURI(authority, "comments/*", COMMENTS_ID);
		
		return sUriMatcher;
	}

	@Override
	public boolean onCreate() {
		mDatabaseHelper = new LibraryDatabase(getContext());
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case USERS:
			return Users.CONTENT_TYPE;
		case USERS_ID:
			return Users.CONTENT_ITEM_TYPE;
		case USERS_ID_BOOKS:
			return Books.CONTENT_TYPE;
		case USERS_ID_BOOKS_ID:
			return Books.CONTENT_ITEM_TYPE;
		case USERS_ID_COMMENTS:
			return Comments.CONTENT_TYPE;
			
		case BOOKS:
			return Books.CONTENT_TYPE;
		case BOOKS_ID:
			return Books.CONTENT_ITEM_TYPE;
		case BOOKS_TECHNICAL:
			return Books.CONTENT_TYPE;
		case BOOKS_SELF:
			return Books.CONTENT_TYPE;
		case BOOKS_ENGLISH:
			return Books.CONTENT_TYPE;
		case BOOKS_MISC:
			return Books.CONTENT_TYPE;
		case BOOKS_ID_COMMENTS:
			return Comments.CONTENT_TYPE;
			
		case COMMENTS:
			return Comments.CONTENT_TYPE;
		case COMMENTS_ID:
			return Comments.CONTENT_ITEM_TYPE;
			
        default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		LogUtils.LOGV(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) + ")");
		final SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
		
		final int match = sUriMatcher.match(uri);
		return buildSelection(uri, match).where(selection, selectionArgs).query(database, projection, sortOrder);
	}
	
	private SelectionBuilder buildSelection(Uri uri, int match) {
		final SelectionBuilder queryBuilder = new SelectionBuilder();
		switch (match) {
		case USERS: {
			return queryBuilder.table(Tables.USERS);
		}
		case USERS_ID: {
			final String userId = Users.getUserId(uri);
			return queryBuilder.table(Tables.USERS).where(Users.USER_ID + "=?", userId);
		}
		case USERS_ID_BOOKS: {
			final String userId = Users.getUserId(uri);
			return queryBuilder
					.table(Tables.USER_BOOKS_JOIN_BOOKS)
					.mapToTable(BaseColumns._ID, Tables.USER_BOOKS)
					.mapToTable(Books.BOOK_ID, Tables.USER_BOOKS)
					.where(UserBooks.USER_ID + "=?", userId);
		}
		case USERS_ID_BOOKS_ID: {
			final String userId = Users.getUserId(uri);
			final String bookId = Users.getBookId(uri);
			return queryBuilder
					.table(Tables.USER_BOOKS)
					.where(UserBooks.USER_ID + "=?", userId)
					.where(UserBooks.BOOK_ID + "=?", bookId);
		}
			
		case BOOKS: {
			return queryBuilder.table(Tables.BOOKS);
		}
		case BOOKS_ID: {
			final String bookId = Books.getBookId(uri);
			return queryBuilder.table(Tables.BOOKS).where(Books.BOOK_ID + "=?", bookId);
		}
		case BOOKS_TECHNICAL: {
			return queryBuilder
					.table(Tables.BOOKS)
					.where(Books.BOOK_CATEGORY + "=?", Books.CATEGORY_TECHNICAL);
		}
		case BOOKS_SELF: {
			return queryBuilder
					.table(Tables.BOOKS)
					.where(Books.BOOK_CATEGORY + "=?", Books.CATEGORY_SELF);
		}
		case BOOKS_ENGLISH: {
			return queryBuilder
					.table(Tables.BOOKS)
					.where(Books.BOOK_CATEGORY + "=?", Books.CATEGORY_ENGLISH);
		}
		case BOOKS_MISC: {
			return queryBuilder
					.table(Tables.BOOKS)
					.where(Books.BOOK_CATEGORY + "=?", Books.CATEGORY_MISC);
		}
		case BOOKS_ID_COMMENTS: {
			final String bookId = Books.getBookId(uri);
			return queryBuilder
					.table(Tables.COMMENTS_JOIN_USERS)
					.mapToTable(Comments._ID, Tables.COMMENTS)
					.mapToTable(Comments.USER_ID, Tables.COMMENTS)
					.where(Comments.BOOK_ID + "=?", bookId);
		}
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
        LogUtils.LOGV(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Boolean syncToNetwork = true;
        switch (match) {
        case USERS:
        	db.insertOrThrow(Tables.USERS, null, values);
        	getContext().getContentResolver().notifyChange(uri, null, syncToNetwork);
        	return Users.buildUserUri(values.getAsString(Users.USER_ID));
        case BOOKS:
        	db.insertOrThrow(Tables.BOOKS, null, values);
        	getContext().getContentResolver().notifyChange(uri, null, syncToNetwork);
        	return Books.buildBookUri(values.getAsString(Books.BOOK_ID));
        case COMMENTS:
        	long commentId = db.insertOrThrow(Tables.COMMENTS, null, values);
        	getContext().getContentResolver().notifyChange(uri, null, syncToNetwork);
        	return Books.buildCommentUri(values.getAsString(Comments.BOOK_ID), Long.toString(commentId));
        	
        case USERS_ID_BOOKS_ID:
        	db.insertOrThrow(Tables.USER_BOOKS, null, values);
        	getContext().getContentResolver().notifyChange(uri, null, syncToNetwork);
        	return Users.buildUserBooksUri(values.getAsString(Users.USER_ID), 
        			values.getAsString(Books.BOOK_ID), values.getAsString(UserBooks.USE_TYPE));    
        default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}
}
