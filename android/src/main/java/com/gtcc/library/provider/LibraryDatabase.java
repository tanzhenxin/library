package com.gtcc.library.provider;

import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.provider.LibraryContract.Users;

import android.app.SearchManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class LibraryDatabase extends SQLiteOpenHelper {
	
	private final static String DATABASE_NAME = "library.db";
	
	private final static int DATABASE_VERSION = 8;

	private static final String TAG = "LibraryProvider";
	
	interface Tables {
		String USERS = "users";
		String BOOKS = "books";
		String USER_BOOKS = "user_books";
		
		String BOOKS_SEARCH = "books_search";
		
		String SEARCH_SUGGEST = "search_suggest";
		
		String USER_BOOKS_JOIN_BOOKS = "user_books "
				+ "LEFT OUTER JOIN books ON user_books.book_id=books.book_id";
		String BOOKS_SEARCH_JOIN_BOOKS = "books_search "
				+ "LEFT OUTER JOIN books ON books_search.book_id=books.book_id";
	}
	
	interface Triggers {
		String BOOKS_SEARCH_INSERT = "books_search_insert";
		String BOOKS_SEARCH_UPDATE = "books_search_update";
		String BOOKS_SEARCH_DELETE = "books_search_delete";
	}
	
	public interface UserBooks {
		String USER_ID = "user_id";
		String BOOK_ID = "book_id";
		String USE_TYPE = "use_type";
	}
	
	interface BooksSearchColumns {
		String BOOK_ID = "book_id";
		String BODY = "body";
	}
	
	interface References {
		String BOOK_ID = "REFERENCES " + Tables.BOOKS + "(" + Books.BOOK_ID + ")";
	}
	
	interface Qualified {
		String BOOKS_SEARCH = Tables.BOOKS_SEARCH + "(" + BooksSearchColumns.BOOK_ID 
				+ "," + BooksSearchColumns.BODY + ")"; 
		String BOOKS_SEARCH_BOOK_ID = Tables.BOOKS_SEARCH + "." + BooksSearchColumns.BOOK_ID;
	}
	
	interface SubQuery {
		String BOOKS_BODY = "(new." + Books.BOOK_TITLE
				+ "||'; '||new." + Books.BOOK_AUTHOR
				+ "||'; '||new." + Books.BOOK_PUBLISHER
				+ ")";
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
                + Books.BOOK_TAG + " TEXT,"
                + Books.BOOK_TITLE + " TEXT,"
                + Books.BOOK_AUTHOR + " TEXT,"
                + Books.BOOK_DESCRIPTION + " TEXT,"
                + Books.BOOK_PUBLISHER + " TEXT,"
                + Books.BOOK_PUBLISH_DATE + " TEXT,"
                + Books.BOOK_PRICE + " TEXT,"
                + Books.BOOK_ISBN + " TEXT,"
                + Books.BOOK_PRINT_LENGTH + " INTEGER,"
                + Books.BOOK_CATEGORY + " TEXT,"
                + Books.BOOK_IMAGE_URL + " TEXT,"

                + "UNIQUE (" + Books.BOOK_ID + ") ON CONFLICT REPLACE)");
        
        Log.w(TAG, "Creating UserBooks table");
        db.execSQL("CREATE TABLE " + Tables.USER_BOOKS + " ("
        		+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        		+ UserBooks.USER_ID + " TEXT NOT NULL,"
        		+ UserBooks.BOOK_ID + " TEXT NOT NULL,"
        		+ UserBooks.USE_TYPE + " TEXT NOT NULL,"
        		+ "UNIQUE (" + UserBooks.USER_ID + "," + UserBooks.BOOK_ID + ") ON CONFLICT REPLACE)");
        
        Log.w(TAG, "Creating search_suggest table");
        db.execSQL("CREATE TABLE " + Tables.SEARCH_SUGGEST + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SearchManager.SUGGEST_COLUMN_TEXT_1 + " TEXT NOT NULL,"
                + "UNIQUE (" + SearchManager.SUGGEST_COLUMN_TEXT_1 + ") ON CONFLICT REPLACE)");
        
        createBooksSearch(db);
        
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
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SEARCH_SUGGEST);
        
        db.execSQL("DROP TRIGGER IF EXISTS " + Triggers.BOOKS_SEARCH_INSERT);
        db.execSQL("DROP TRIGGER IF EXISTS " + Triggers.BOOKS_SEARCH_DELETE);
        db.execSQL("DROP TRIGGER IF EXISTS " + Triggers.BOOKS_SEARCH_UPDATE);
        
        db.execSQL("DROP TABLE IF EXISTS " + Tables.BOOKS_SEARCH);

        // Recreates the database with a new version
        onCreate(db);
	}
	
	private void createBooksSearch(SQLiteDatabase db) {
		db.execSQL("CREATE VIRTUAL TABLE " + Tables.BOOKS_SEARCH + " USING fts3("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ BooksSearchColumns.BODY + " TEXT NOT NULL,"
				+ BooksSearchColumns.BOOK_ID + " TEXT NOT NULL " + References.BOOK_ID + ","
                + "UNIQUE (" + BooksSearchColumns.BOOK_ID + ") ON CONFLICT REPLACE,"
                + "tokenize=porter)");
		
		db.execSQL("CREATE TRIGGER " + Triggers.BOOKS_SEARCH_INSERT + " AFTER INSERT ON "
				+ Tables.BOOKS + " BEGIN INSERT INTO " + Qualified.BOOKS_SEARCH + " "
				+ "VALUES (new." + Books.BOOK_ID + ", " + SubQuery.BOOKS_BODY + ");"
				+ " END;");
		db.execSQL("CREATE TRIGGER " + Triggers.BOOKS_SEARCH_DELETE + " AFTER DELETE ON " 
				+ Tables.BOOKS + " BEGIN DELETE FROM " + Tables.BOOKS_SEARCH + " "
				+ "WHERE " + Qualified.BOOKS_SEARCH_BOOK_ID + "=old." + Books.BOOK_ID + ";"
				+ " END;");
		db.execSQL("CREATE TRIGGER " + Triggers.BOOKS_SEARCH_UPDATE + " AFTER UPDATE ON "
				+ Tables.BOOKS + " BEGIN UPDATE " + Tables.BOOKS_SEARCH + " "
				+ "SET " + BooksSearchColumns.BODY + "=" + SubQuery.BOOKS_BODY + " "
				+ "WHERE " + BooksSearchColumns.BOOK_ID + "=old." + Books.BOOK_ID + ";"
				+ " END;");
	}

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
