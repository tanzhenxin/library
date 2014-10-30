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

    private Context mContext = null;

    private static final String DATA_KEY_BOOKS = "books";

    private static final String[] DATA_KEYS_IN_ORDER = {
            DATA_KEY_BOOKS,
    };

    BookHandler mBookHandler = null;

    HashMap<String, JSONHandler> mHandlerForKey = new HashMap<String, JSONHandler>();

    public BookDataHandler(Context context) {
        mContext = context;
    }

    public void applyBookData(String dataBody, String dataTimestamp) throws IOException {
        LogUtils.LOGD(TAG, "Applying data from bootstrap file, timestamp " + dataTimestamp);
        mHandlerForKey.put(DATA_KEY_BOOKS, mBookHandler = new BookHandler(mContext));

        processDataBody(dataBody);

        // produce the necessary content provider operations
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
        for (String key : DATA_KEYS_IN_ORDER) {
            LogUtils.LOGD(TAG, "Building content provider operations for: " + key);
            mHandlerForKey.get(key).makeContentProviderOperations(batch);
            LogUtils.LOGD(TAG, "Content provider operations so far: " + batch.size());
        }
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
        JSONObject jobj = JSON.parseObject(dataBody);
        JSONArray array = jobj.getJSONArray("books");
        mHandlerForKey.get("books").process(array);
    }
}
