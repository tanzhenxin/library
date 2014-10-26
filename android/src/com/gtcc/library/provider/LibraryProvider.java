package com.gtcc.library.provider;

import java.util.ArrayList;
import java.util.Arrays;

import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.provider.LibraryContract.SearchSuggest;
import com.gtcc.library.provider.LibraryContract.Users;
import com.gtcc.library.provider.LibraryDatabase.BooksSearchColumns;
import com.gtcc.library.provider.LibraryDatabase.Tables;
import com.gtcc.library.provider.LibraryDatabase.UserBooks;
import com.gtcc.library.util.LogUtils;
import com.gtcc.library.util.SelectionBuilder;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

public class LibraryProvider extends ContentProvider {

	private static final String TAG = LogUtils
			.makeLogTag(LibraryProvider.class);

	private LibraryDatabase mDatabaseHelper;

	private static final UriMatcher sUriMatcher = buildUriMatcher();

	public static final int USERS = 100;
	public static final int USERS_ID = 101;
	public static final int USERS_ID_BOOKS = 102;
	public static final int USERS_ID_BOOKS_ID = 103;

	public static final int BOOKS = 200;
	public static final int BOOKS_ID = 201;
	public static final int BOOKS_CATEGORY = 202;
	public static final int BOOKS_ISBN = 203;
	public static final int BOOKS_SEARCH = 204;

	public static final int SEARCH_SUGGEST = 400;

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = LibraryContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, "users", USERS);
		matcher.addURI(authority, "users/*", USERS_ID);
		matcher.addURI(authority, "users/*/books", USERS_ID_BOOKS);
		matcher.addURI(authority, "users/*/books/*", USERS_ID_BOOKS_ID);

		matcher.addURI(authority, "books", BOOKS);
		matcher.addURI(authority, "books/category/*", BOOKS_CATEGORY);
		matcher.addURI(authority, "books/isbn/*", BOOKS_ISBN);
		matcher.addURI(authority, "books/search/*", BOOKS_SEARCH);
		matcher.addURI(authority, "books/*", BOOKS_ID);

		matcher.addURI(authority, "search_suggest_query", SEARCH_SUGGEST);

		return matcher;
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

		case BOOKS:
		case BOOKS_CATEGORY:
		case BOOKS_ISBN:
		case BOOKS_SEARCH:
			return Books.CONTENT_TYPE;
		case BOOKS_ID:
			return Books.CONTENT_ITEM_TYPE;

		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		LogUtils.LOGV(TAG,
				"query(uri=" + uri + ", proj=" + Arrays.toString(projection)
						+ ")");
		final SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();

		final int match = sUriMatcher.match(uri);
		switch (match) {
		default: {
			final SelectionBuilder builder = buildSelection(uri);
			return builder.where(selection, selectionArgs).query(database,
					projection, sortOrder);
		}
		case SEARCH_SUGGEST:
			final SelectionBuilder builder = new SelectionBuilder();

			selectionArgs[0] = selectionArgs[0] + "%";
			builder.table(Tables.SEARCH_SUGGEST);
			builder.where(selection, selectionArgs);
			builder.map(SearchManager.SUGGEST_COLUMN_QUERY,
					SearchManager.SUGGEST_COLUMN_TEXT_1);

			projection = new String[] { BaseColumns._ID,
					SearchManager.SUGGEST_COLUMN_QUERY,
					SearchManager.SUGGEST_COLUMN_TEXT_1 };

			final String limit = uri
					.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT);
			return builder.query(database, projection, null, null,
					SearchSuggest.DEFAULT_SORT, limit);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		LogUtils.LOGV(TAG,
				"insert(uri=" + uri + ", values=" + values.toString() + ")");
		final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case USERS:
			db.insertOrThrow(Tables.USERS, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Users.buildUserUri(values.getAsString(Users.USER_ID));
		case BOOKS:
			db.insertOrThrow(Tables.BOOKS, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Books.buildBookUri(values.getAsString(Books.BOOK_ID));

		case USERS_ID_BOOKS_ID:
			db.insertOrThrow(Tables.USER_BOOKS, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Users.buildUserBooksUri(values.getAsString(Users.USER_ID),
					values.getAsString(Books.BOOK_ID),
					values.getAsString(UserBooks.USE_TYPE));
		case SEARCH_SUGGEST: {
			db.insertOrThrow(Tables.SEARCH_SUGGEST, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return SearchSuggest.CONTENT_URI;
		}
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		LogUtils.LOGV(TAG, "update(uri=" + uri + ", values=" + values + ")");
		final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		final SelectionBuilder builder = buildSelection(uri);
		int retVal = builder.where(selection, selectionArgs).update(db, values);
		getContext().getContentResolver().notifyChange(uri, null);
		return retVal;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		LogUtils.LOGV(TAG, "delete(uri=" + uri + ")");
		if (uri == LibraryContract.BASE_CONTENT_URI) {
			// Handle whole database deletes (e.g. when signing out)
			deleteDatabase();
			getContext().getContentResolver().notifyChange(uri, null);
			return 1;
		}

		final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		final SelectionBuilder builder = buildSelection(uri);
		int retVal = builder.where(selection, selectionArgs).delete(db);
		getContext().getContentResolver().notifyChange(uri, null);
		return retVal;
	}

	@Override
	public ContentProviderResult[] applyBatch(
			ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			final int numOperations = operations.size();
			final ContentProviderResult[] results = new ContentProviderResult[numOperations];
			for (int i = 0; i < numOperations; ++i) {
				results[i] = operations.get(i).apply(this, results, numOperations);
			}
			db.setTransactionSuccessful();
			return results;
		} finally {
			db.endTransaction();
		}
	}

	private SelectionBuilder buildSelection(Uri uri) {
		final SelectionBuilder queryBuilder = new SelectionBuilder();
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case USERS: {
			return queryBuilder.table(Tables.USERS);
		}
		case USERS_ID: {
			final String userId = Users.getUserId(uri);
			return queryBuilder.table(Tables.USERS).where(Users.USER_ID + "=?",
					userId);
		}
		case USERS_ID_BOOKS: {
			final String userId = Users.getUserId(uri);
			return queryBuilder.table(Tables.USER_BOOKS_JOIN_BOOKS)
					.mapToTable(BaseColumns._ID, Tables.USER_BOOKS)
					.mapToTable(Books.BOOK_ID, Tables.USER_BOOKS)
					.where(UserBooks.USER_ID + "=?", userId);
		}
		case USERS_ID_BOOKS_ID: {
			final String userId = Users.getUserId(uri);
			final String bookId = Users.getBookId(uri);
			return queryBuilder.table(Tables.USER_BOOKS)
					.where(UserBooks.USER_ID + "=?", userId)
					.where(UserBooks.BOOK_ID + "=?", bookId);
		}

		case BOOKS: {
			return queryBuilder.table(Tables.BOOKS);
		}
		case BOOKS_ID: {
			final String bookId = Books.getBookId(uri);
			return queryBuilder.table(Tables.BOOKS).where(Books.BOOK_ID + "=?",
					bookId);
		}
		case BOOKS_CATEGORY: {
			final String bookCategory = Books.getBookCategory(uri);
			return queryBuilder.table(Tables.BOOKS).where(
					Books.BOOK_CATEGORY + "=?", bookCategory);
		}
		case BOOKS_ISBN: {
			final String isbn = Books.getBookISBN(uri);
			return queryBuilder.table(Tables.BOOKS).where(
					Books.BOOK_ISBN + "=?", isbn);
		}
		case BOOKS_SEARCH: {
			final String searchQuery = Books.getSearchQuery(uri);
			return queryBuilder.table(Tables.BOOKS_SEARCH_JOIN_BOOKS)
					.mapToTable(Books._ID, Tables.BOOKS)
					.mapToTable(Books.BOOK_ID, Tables.BOOKS)
					.where(
						BooksSearchColumns.BODY + " LIKE ?", 
						searchQuery + "%");
//					.where(BooksSearchColumns.BODY + " MATCH ?", searchQuery);
		}
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	private void deleteDatabase() {
		mDatabaseHelper.close();
		Context context = getContext();
		LibraryDatabase.deleteDatabase(context);
		mDatabaseHelper = new LibraryDatabase(getContext());
	}
}
