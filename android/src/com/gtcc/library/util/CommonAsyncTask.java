package com.gtcc.library.util;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.gtcc.library.R;

import java.io.IOException;

/**
 * Created by ShenCV on 9/23/13.
 */
public abstract class CommonAsyncTask<TParam, TResult> extends AsyncTask<TParam, Integer, TResult> {
    private Context mContext;
    private Exception exception;

    public CommonAsyncTask(){
    }

    public CommonAsyncTask(Context context){
        mContext = context;
    }
    
    @Override
    protected TResult doInBackground(TParam... params){
        try{
            doWork(params);
        }
        catch (Exception e) {
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(TResult result){
        if (isCancelled())
            return;

        if (exception == null){
            onResult(result);
        }
        else{
            onError(exception);
        }
    }

    protected void onError(Exception ex){
    	if(mContext != null) {
    		// show load failed by default
            Toast.makeText(mContext,
                    mContext.getString(R.string.load_failed),
                    Toast.LENGTH_SHORT).show();
    	}
    }

    // operation successful
    protected void onResult(TResult result){
        // do nothing, can be override
    }

    // do work
    protected abstract TResult doWork(TParam... params) throws Exception;
}
