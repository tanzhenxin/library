package com.gtcc.library.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.entity.UserInfo;
import com.gtcc.library.provider.LibraryContract.Users;

public class BaseActivity extends SherlockFragmentActivity {
	
	protected CharSequence mTitle;
	
	public static final String SHARED_PREFERENCE_FILE = "com.gtcc.libary.preference";
	
	public static final String ACCESS_TOKEN = "access_token";
	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	public static final String USER_PASSWORD = "user_password";
	public static final String USER_IMAGE_URL = "user_image_url";
	
	protected UserInfo mUserInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loadUserInfo();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
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
    public Bundle intentToFragmentArguments(Intent intent) {
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
    public Intent fragmentArgumentsToIntent(Bundle arguments) {
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
		SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);
		
		final String userId = sharedPref.getString(USER_ID, "0");
		final String userName = sharedPref.getString(USER_NAME, null);
		final String userPassword = sharedPref.getString(USER_PASSWORD, null);
		final String userImageUrl = sharedPref.getString(USER_IMAGE_URL, null);
		final String accessToken = sharedPref.getString(ACCESS_TOKEN, null);
		
		mUserInfo = new UserInfo(userId, userName, userPassword, userImageUrl, accessToken);
	}

	protected void setUserInfo(UserInfo userInfo) {
		mUserInfo = userInfo;
		
		// store user info in shared preferences.
		Editor editor = getSharedPreferences(SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE).edit();
		
		editor.putString(USER_ID, mUserInfo.getUserId());
		editor.putString(USER_NAME, mUserInfo.getUserName());
		
		final String password = mUserInfo.getUserPassword();
		if (password != null && !TextUtils.isEmpty(password)) {
			editor.putString(USER_PASSWORD, password);
		}
		
		final String imageUrl = mUserInfo.getUserImageUrl();
		if (imageUrl != null && !TextUtils.isEmpty(imageUrl)) {
			editor.putString(USER_IMAGE_URL, imageUrl);
		}
		
		final String accessToken = mUserInfo.getAccessToken();
		if (accessToken != null && !TextUtils.isEmpty(accessToken)) {
			editor.putString(ACCESS_TOKEN, accessToken);
		}
		
		editor.commit();
		
		// store user info in content provider
		ContentValues values = new ContentValues();
		values.put(Users.USER_ID, mUserInfo.getUserId());
		values.put(Users.USER_NAME, mUserInfo.getUserName());
		values.put(Users.USER_IMAGE_URL, imageUrl);
		getContentResolver().insert(Users.CONTENT_URI, values);
	}
	
	public String getUserId() {
		return mUserInfo.getUserId();
	}

	protected void clearUserInfo() {
		Editor editor = getSharedPreferences(SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}
}
