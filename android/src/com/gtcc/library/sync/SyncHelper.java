package com.gtcc.library.sync;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.os.RemoteException;

import com.gtcc.library.entity.Book;
import com.gtcc.library.provider.LibraryContract;
import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.util.HttpManager;
import com.gtcc.library.util.LogUtils;

public class SyncHelper {

	private static final String TAG = LogUtils.makeLogTag(SyncHelper.class);

	private Context mContext;

	public SyncHelper(Context context) {
		mContext = context;
	}

	public void performSync() throws RemoteException, OperationApplicationException {
		final ContentResolver resolver = mContext.getContentResolver();
		final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
		
		batch.addAll(loadBooks());

		resolver.applyBatch(LibraryContract.CONTENT_AUTHORITY, batch);
	}

	public ArrayList<ContentProviderOperation> loadBooks() {
		final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
		try {
			List<Book> books = HttpManager.webServiceBookProxy.getAllBooks(0, 0);
			for (int i = 0; i < books.size(); ++i) {
				Book book = books.get(i);
				
				ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(Books.CONTENT_URI);
				builder.withValue(Books.BOOK_ID, book.getId());
				builder.withValue(Books.BOOK_TITLE, book.getTitle());
				builder.withValue(Books.BOOK_AUTHOR, book.getAuthor());
				builder.withValue(Books.BOOK_DESCRIPTION, book.getDescription());
				builder.withValue(Books.BOOK_LANGUAGE, book.getLanguage());
				builder.withValue(Books.BOOK_PUBLISHER, book.getPublisher());
				builder.withValue(Books.BOOK_PUBLISH_DATE, book.getPublishDate());
				builder.withValue(Books.BOOK_PRICE, book.getPrice());
				builder.withValue(Books.BOOK_ISBN, book.getISBN());
				builder.withValue(Books.BOOK_IMAGE_URL, book.getImgUrl());
				builder.withValue(Books.BOOK_CATEGORY, book.getCategory());
				
				batch.add(builder.build());
			}
		} catch (Exception e) {
			LogUtils.LOGE(TAG, e.toString());
		}
		
		return batch;
	}
}
