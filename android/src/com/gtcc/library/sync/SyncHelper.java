package com.gtcc.library.sync;

import java.io.IOException;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.gtcc.library.entity.Book;
import com.gtcc.library.entity.BookCollection;
import com.gtcc.library.entity.Borrow;
import com.gtcc.library.entity.UserInfo;
import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.provider.LibraryContract.Users;
import com.gtcc.library.provider.LibraryDatabase.UserBooks;
import com.gtcc.library.util.CommonAsyncTask;
import com.gtcc.library.util.HttpManager;

public class SyncHelper {
	
	private Context mContext;
	
	public SyncHelper(Context context) {
		mContext = context;
	}
	
	public void loadBooks(UserInfo user) {
		new AsyncTask<String, Void, Void>() {

			@Override
			protected Void doInBackground(String... params) {
				try {
					List<Borrow> borrows = HttpManager.webServiceBorrowProxy.getBorrowInfo(params[0]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
		}.execute(user.getUserId());
	}
	
	private void storeBooks(String uid, List<Book> books) {
		for (Book book : books) {
			ContentValues values = new ContentValues();
			values.put(Books.BOOK_ID, book.getId());
			values.put(Books.BOOK_TITLE, book.getTitle());
			values.put(Books.BOOK_AUTHOR, book.getAuthor());
			values.put(Books.BOOK_AUTHRO_INTRO, book.getAuthorIntro());
			values.put(Books.BOOK_SUMMARY, book.getDescription());
			values.put(Books.BOOK_IMAGE_URL, book.getImgUrl());

			mContext.getContentResolver().insert(Books.CONTENT_URI, values);

			ContentValues aValues = new ContentValues();
			aValues.put(UserBooks.USER_ID, uid);
			aValues.put(UserBooks.BOOK_ID, book.getId());
			mContext.getContentResolver().insert(
					Users.buildUserBooksUri(uid, book.getId()), aValues);
		}
	}
	
//	private List<Book> getDoubanBooks(UserInfo user) {
//        String accessToken = user.getgetAccessToken();
//        if (accessToken != null) {
//            String uid = mUserId;
//            BookCollection bookCollection = new BookCollection();
//            while (bookCollection.hasMoreBooks()) {
//               return bookCollection.getBooks(getAccessToken(), uid);
//            }
//        }
//	}
}
