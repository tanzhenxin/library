package com.gtcc.library.sync;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.os.RemoteException;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
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
            AVQuery<AVObject> query = new AVQuery<AVObject>("Book");
            List<AVObject> avObjects = query.find();
			for (int i = 0; i < avObjects.size(); ++i) {
				AVObject book = avObjects.get(i);
				
				ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(Books.CONTENT_URI);
				builder.withValue(Books.BOOK_ID, book.getString("tag"));
				builder.withValue(Books.BOOK_TITLE, book.getString("title"));
				builder.withValue(Books.BOOK_AUTHOR, book.getString("author"));
				builder.withValue(Books.BOOK_DESCRIPTION, book.getString("description"));
				builder.withValue(Books.BOOK_LANGUAGE, book.getString("language"));
				builder.withValue(Books.BOOK_PUBLISHER, book.getString("publisher"));
				builder.withValue(Books.BOOK_PUBLISH_DATE, book.getString("publishDate"));
				builder.withValue(Books.BOOK_PRICE, book.getString("price"));
				builder.withValue(Books.BOOK_ISBN, book.getString("ISBN"));
				builder.withValue(Books.BOOK_IMAGE_URL, book.getString("imageUrl"));
				builder.withValue(Books.BOOK_CATEGORY, book.getString("tag").substring(0, 1));
				
				batch.add(builder.build());
			}
		} catch (Exception e) {
			LogUtils.LOGE(TAG, e.toString());
		}
		
		return batch;
	}
}
