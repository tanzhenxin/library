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
		
		return batch;
	}
}
