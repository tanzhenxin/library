package com.gtcc.library.provider;

import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.provider.LibraryContract.Users;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class LibraryDatabase extends SQLiteOpenHelper {
	
	private final static String DATABASE_NAME = "library.db";
	
	private final static int DATABASE_VERSION = 4;

	private static final String TAG = "LibraryProvider";
	
	interface Tables {
		String USERS = "users";
		String BOOKS = "books";
		String USER_BOOKS = "user_books";
		
		String USER_BOOKS_JOIN_BOOKS = "user_books "
				+ "LEFT OUTER JOIN books ON user_books.book_id=books.book_id";
	}
	
	public interface UserBooks {
		String USER_ID = "user_id";
		String BOOK_ID = "book_id";
		String USE_TYPE = "use_type";
		
		String TYPE_READING = "reading";
		String TYPE_READ = "read";
		String TYPE_WISH = "wish";
		String TYPE_DONATE = "donate";
	}

	public LibraryDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.w(TAG, "Creating Users table");
        db.execSQL("CREATE TABLE " + Tables.USERS + " ("
                + Users._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Users.USER_ID + " TEXT,"
                + Users.USER_NAME + " TEXT,"
                + Users.USER_IMAGE_URL + " TEXT,"
                + "UNIQUE (" + Users.USER_ID + ") ON CONFLICT REPLACE)");
        
        Log.w(TAG, "Creating Books table");
        db.execSQL("CREATE TABLE " + Tables.BOOKS + " ("
                + Books._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Books.BOOK_ID + " TEXT NOT NULL,"
                + Books.BOOK_TITLE + " TEXT NOT NULL,"
                + Books.BOOK_AUTHOR + " TEXT NOT NULL,"
                + Books.BOOK_SUMMARY + " TEXT NOT NULL,"
                + Books.BOOK_CATEGORY + " TEXT,"
                + Books.BOOK_IMAGE_URL + " TEXT,"
                + Books.BOOK_OWNER + " TEXT,"
                + Books.BOOK_USER + " TEXT,"
                + Books.DUE_DATE + " INTEGER,"
                + "UNIQUE (" + Books.BOOK_ID + ") ON CONFLICT REPLACE)");
        
        Log.w(TAG, "Creating UserBooks table");
        db.execSQL("CREATE TABLE " + Tables.USER_BOOKS + " ("
        		+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        		+ UserBooks.USER_ID + " TEXT NOT NULL,"
        		+ UserBooks.BOOK_ID + " TEXT NOT NULL,"
        		+ UserBooks.USE_TYPE + " TEXT NOT NULL,"
        		+ "UNIQUE (" + UserBooks.USER_ID + "," + UserBooks.BOOK_ID + ") ON CONFLICT REPLACE)");
        Log.w(TAG, "Finish creating tables");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Logs that the database is being upgraded
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        // Kills the table and existing data
        db.execSQL("DROP TABLE IF EXISTS " + Tables.USERS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.USER_BOOKS);

        // Recreates the database with a new version
        onCreate(db);
	}

}
