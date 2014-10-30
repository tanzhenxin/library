package com.gtcc.library.sync;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gtcc.library.entity.BookHandler;
import com.gtcc.library.entity.JSONHandler;
import com.gtcc.library.provider.LibraryContract;
import com.gtcc.library.util.LogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TanZA on 10/29/2014.
 */
public class BookDataHandler {
    private static final String TAG = LogUtils.makeLogTag(BookDataHandler.class);
    private static final String DATA_KEY_BOOKS = "books";

    private Context mContext = null;
    private BookHandler mBookHandler = null;

    public BookDataHandler(Context context) {
        mContext = context;
    }

    public void applyBookData(String dataBody, String dataTimestamp) throws IOException {
        LogUtils.LOGD(TAG, "Applying data from bootstrap file, timestamp " + dataTimestamp);
        mBookHandler = new BookHandler(mContext);

        processDataBody(dataBody);

        // produce the necessary content provider operations
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
        LogUtils.LOGD(TAG, "Building content provider operations for books");
        mBookHandler.makeContentProviderOperations(batch);
        LogUtils.LOGD(TAG, "Total content provider operations: " + batch.size());

        // finally, push the changes into the Content Provider
        LogUtils.LOGD(TAG, "Applying " + batch.size() + " content provider operations.");
        try {
            int operations = batch.size();
            if (operations > 0) {
                mContext.getContentResolver().applyBatch(LibraryContract.CONTENT_AUTHORITY, batch);
            }
            LogUtils.LOGD(TAG, "Successfully applied " + operations + " content provider operations.");
        } catch (RemoteException ex) {
            LogUtils.LOGD(TAG, "RemoteException while applying content provider operations.");
            throw new RuntimeException("Error executing content provider batch operation", ex);
        } catch (OperationApplicationException ex) {
            LogUtils.LOGD(TAG, "OperationApplicationException while applying content provider operations.");
            throw new RuntimeException("Error executing content provider batch operation", ex);
        }
    }

    private void processDataBody(String dataBody) {
        JSONObject isonObject = JSON.parseObject(dataBody);
        JSONArray array = isonObject.getJSONArray(DATA_KEY_BOOKS);
        mBookHandler.process(array);
    }
}
