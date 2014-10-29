package com.gtcc.library.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.MenuItem;

import com.avos.avoscloud.AVOSCloud;
import com.gtcc.library.R;
import com.gtcc.library.entity.JSONHandler;
import com.gtcc.library.entity.UserInfo;
import com.gtcc.library.provider.LibraryContract;
import com.gtcc.library.provider.LibraryContract.Users;
import com.gtcc.library.sync.BookDataHandler;
import com.gtcc.library.util.Configs;
import com.gtcc.library.util.LogUtils;
import com.gtcc.library.util.PrefUtils;

import java.io.IOException;

public class BaseActivity extends FragmentActivity {

    private static final String TAG = LogUtils.makeLogTag(BaseActivity.class);
	
	public static final String SHARED_PREFERENCE_FILE = "com.gtcc.libary.preference";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	public static final String USER_PASSWORD = "user_password";
	public static final String USER_IMAGE_URL = "user_image_url";

    private Thread mDataBootstrapThread = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loadUserInfo();
        AVOSCloud.initialize(this, Configs.AVOS_API_ID, Configs.AVOS_API_KEY);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
	}

    @Override
    protected void onStart() {
        super.onStart();

        if (!PrefUtils.isDataBootstrapDone(this) && mDataBootstrapThread == null) {
            LogUtils.LOGD(TAG, "One-time data bootstrap is not done yet. Doing now.");
            performDataBootstrap();
        }
    }

    private void performDataBootstrap() {
        final Context context = getApplicationContext();
        LogUtils.LOGD(TAG, "Staring data bootstrap background thread.");
        mDataBootstrapThread = new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtils.LOGD(TAG, "Starting data bootstrap process.");
                try {
                    String bootstrapJson = JSONHandler.parseResource(context, R.raw.bootstrap_data);

                    BookDataHandler dataHandler = new BookDataHandler(context);
                    dataHandler.applyBookData(bootstrapJson, Configs.BOOTSTRAP_DATA_TIMESTAMP);
                    LogUtils.LOGD(TAG, "End of bootstrap -- successful. Marking bootstrap as done.");
                    PrefUtils.markDataBootstrapDone(context);
                    getContentResolver().notifyChange(Uri.parse(LibraryContract.CONTENT_AUTHORITY), null);
                }
                catch (IOException ex) {
                    LogUtils.LOGE(TAG, "*** ERROR DURING BOOTSTRAP! Problem in bootstrap data?");
                    PrefUtils.markDataBootstrapDone(context);
                }

                mDataBootstrapThread = null;
            }
        });
        mDataBootstrapThread.start();
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
    /**
     * Converts an intent into a {@link Bundle} suitable for use as fragment arguments.
     */
    public static Bundle intentToFragmentArguments(Intent intent) {
        Bundle arguments = new Bundle();
        if (intent == null) {
            return arguments;
        }

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
        }

        final Bundle extras = intent.getExtras();
        if (extras != null) {
            arguments.putAll(intent.getExtras());
        }

        return arguments;
    }
	
    /**
     * Converts a fragment arguments bundle into an intent.
     */
    public static Intent fragmentArgumentsToIntent(Bundle arguments) {
        Intent intent = new Intent();
        if (arguments == null) {
            return intent;
        }

        final Uri data = arguments.getParcelable("_uri");
        if (data != null) {
            intent.setData(data);
        }

        intent.putExtras(arguments);
        intent.removeExtra("_uri");
        return intent;
    }
    
	protected void loadUserInfo() {
		UserInfo currentUser = UserInfo.getCurrentUser();
		
		SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);
		currentUser.setUserId(sharedPref.getString(USER_ID, "0"));
		currentUser.setUserName(sharedPref.getString(USER_NAME, null));
		currentUser.setUserPassword(sharedPref.getString(USER_PASSWORD, null));
		currentUser.setUserImageUrl(sharedPref.getString(USER_IMAGE_URL, null));
		currentUser.setAccessToken(sharedPref.getString(ACCESS_TOKEN, null));
	}

	protected void setUserInfo(UserInfo userInfo) {
		UserInfo currentUser = UserInfo.getCurrentUser();
		currentUser.copy(userInfo);
		
		// store user info in shared preferences.
		Editor editor = getSharedPreferences(SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE).edit();
		editor.putString(USER_ID, currentUser.getUserId());
		editor.putString(USER_NAME, currentUser.getUserName());
		
		final String password = currentUser.getUserPassword();
		if (password != null && !TextUtils.isEmpty(password)) {
			editor.putString(USER_PASSWORD, password);
		}
		
		final String imageUrl = currentUser.getUserImageUrl();
		if (imageUrl != null && !TextUtils.isEmpty(imageUrl)) {
			editor.putString(USER_IMAGE_URL, imageUrl);
		}
		
		final String accessToken = currentUser.getAccessToken();
		if (accessToken != null && !TextUtils.isEmpty(accessToken)) {
			editor.putString(ACCESS_TOKEN, accessToken);
		}
		
		editor.commit();
		
		// store user info in content provider
		ContentValues values = new ContentValues();
		values.put(Users.USER_ID, currentUser.getUserId());
		values.put(Users.USER_NAME, currentUser.getUserName());
		values.put(Users.USER_IMAGE_URL, imageUrl);
		getContentResolver().insert(Users.CONTENT_URI, values);
	}
	
	public String getUserId() {
		return UserInfo.getCurrentUser().getUserId();
	}

	protected void clearUserInfo() {
		Editor editor = getSharedPreferences(SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}
}
