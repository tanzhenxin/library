package com.gtcc.library.ui;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.gtcc.library.R;
import com.gtcc.library.entity.Book;
import com.gtcc.library.entity.BookCollection;
import com.gtcc.library.entity.UserInfo;
import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.provider.LibraryContract.Users;
import com.gtcc.library.provider.LibraryDatabase.UserBooks;
import com.gtcc.library.ui.library.LibraryPagerAdapter;
import com.gtcc.library.ui.user.UserBookListFragment;
import com.gtcc.library.ui.user.UserLoginActivity;
import com.gtcc.library.ui.user.UserOAuth2LoginActivity;
import com.gtcc.library.ui.user.UserPagerAdapter;
import com.gtcc.library.util.HttpManager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class HomeActivity extends BaseActivity implements
		ActionBar.TabListener, UserBookListFragment.Callbacks {

	public static final int PAGE_USER = 0;
	public static final int PAGE_LIBRARY = 1;
	public static final int PAGE_SETTINGS = 2;

	public static final int TAB_0 = 0;
	public static final int TAB_1 = 1;
	public static final int TAB_2 = 2;

	public static final String ARG_PAGE_NUMBER = "page_number";
	public static final String ARG_SECTION_NUMBER = "section_number";

	public static final String ACCESS_TOKEN = "access_token";
	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	public static final String USER_PASSWORD = "user_password";
	public static final String USER_IMAGE_URL = "user_image_url";
	
	private String mAccessToken;
	private String mUserId;
	private String mUserName;
	private String mUserPassword;
	private String mUserImageUrl;

	private int REQUEST_LOGIN = 1;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private int mCurrentPage = -1;

	public HomeActivity() {
		super(R.string.app_name);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set up the ViewPager with the sections adapter.
		mViewPager = new ViewPager(this);
		mViewPager.setId("VP".hashCode());
		setContentView(mViewPager);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						switch (position) {
						case 0:
							getSlidingMenu().setTouchModeAbove(
									SlidingMenu.TOUCHMODE_FULLSCREEN);
							break;
						default:
							getSlidingMenu().setTouchModeAbove(
									SlidingMenu.TOUCHMODE_MARGIN);
							break;
						}
						actionBar.setSelectedNavigationItem(position);
					}
				});

		loadUserInfo();
		showPage(PAGE_USER);

		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_LOGIN) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				int loginType = data.getExtras().getInt(
						UserLoginActivity.LOGIN_TYPE);
				if (loginType == UserLoginActivity.LOGIN_NORMAL) {
					mUserId = data.getExtras().getString(USER_ID);
					mUserName = data.getExtras().getString(USER_NAME);
					mUserPassword = data.getExtras().getString(USER_PASSWORD);

					Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
					editor.putString(USER_ID, mUserId);
					editor.putString(USER_NAME, mUserName);
					editor.putString(USER_PASSWORD, mUserPassword);
					editor.commit();
				} else {
					UserInfo userInfo = (UserInfo) data.getExtras()
							.getSerializable(USER_ID);
					mUserId = userInfo.getUserId();
					mUserName = userInfo.getUserName();
					mUserImageUrl = userInfo.getUserImageUrl();
					mAccessToken = data.getExtras().getString(ACCESS_TOKEN);

					Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
					editor.putString(USER_ID, mUserId);
					editor.putString(USER_NAME, mUserName);
					editor.putString(USER_IMAGE_URL, mUserImageUrl);
					editor.putString(ACCESS_TOKEN, mAccessToken);
					editor.commit();

					new LoadBooksAsyncTask().execute();
				}
				
				storeUserInfo();

				showUserHome();
				break;
			case Activity.RESULT_CANCELED:
				finish();
				break;
			}
		}
	}
	
	private void loadUserInfo() {
		mUserId = getPreferences(Context.MODE_PRIVATE).getString(USER_ID, "0");
		mUserName = getPreferences(Context.MODE_PRIVATE).getString(USER_NAME, null);
		mUserPassword = getPreferences(Context.MODE_PRIVATE).getString(USER_PASSWORD, null);
		mUserImageUrl = getPreferences(Context.MODE_PRIVATE).getString(USER_IMAGE_URL, null);
		mAccessToken = getPreferences(Context.MODE_PRIVATE).getString(ACCESS_TOKEN, null);
	}

	private void storeUserInfo() {
		ContentValues values = new ContentValues();
		values.put(Users.USER_ID, mUserId);
		values.put(Users.USER_NAME, mUserName);
		values.put(Users.USER_IMAGE_URL, mUserImageUrl);
		getContentResolver().insert(Users.CONTENT_URI, values);
	}

	public void showPage(int position) {
		if (position != mCurrentPage) {
			switch (position) {
			case PAGE_USER:
				if (!hasLogin())
					login();
				else
					showUserHome();
				break;
			case PAGE_LIBRARY:
				showLibrary();
				break;
			case PAGE_SETTINGS:
				break;
			}
		}

		mCurrentPage = position;
		showContent();
	}
	
	public boolean hasLogin() {
		return mUserId != "0";
	}
	
	private void login() {
		Intent intent = new Intent(this, UserLoginActivity.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}
	
	public String getUserId() {
		return mUserId;
	}

	private String getAccessToken() {
		if (mAccessToken == null) {
			mAccessToken = getPreferences(Context.MODE_PRIVATE).getString(
					ACCESS_TOKEN, null);
		}
		return mAccessToken;
	}

	private void showUserHome() {
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		UserPagerAdapter pagerAdapter = new UserPagerAdapter(this);
		mViewPager.setAdapter(pagerAdapter);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.removeAllTabs();

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < pagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar
					.addTab(actionBar.newTab()
							.setText(pagerAdapter.getPageTitle(i))
							.setTabListener(this));
		}

		setTitle(R.string.user_center);
		mViewPager.setCurrentItem(0);
	}

	private void showLibrary() {
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		LibraryPagerAdapter pagerAdapter = new LibraryPagerAdapter(this);
		mViewPager.setAdapter(pagerAdapter);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.removeAllTabs();

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < pagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar
					.addTab(actionBar.newTab()
							.setText(pagerAdapter.getPageTitle(i))
							.setTabListener(this));
		}

		setTitle(R.string.book_library);
		mViewPager.setCurrentItem(0);
	}

	private class LoadBooksAsyncTask extends AsyncTask<Void, Void, Boolean> {

		@SuppressLint("NewApi")
		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				String accessToken = getAccessToken();
				if (accessToken != null) {
					String uid = mUserId;
					BookCollection bookCollection = new BookCollection();
					while (bookCollection.hasMoreBooks()) {
						List<Book> books = bookCollection.getBooks(
								getAccessToken(), uid);
						storeBooks(uid, books);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			return true;
		}

		private void storeBooks(String uid, List<Book> books) {
			for (Book book : books) {
				ContentValues values = new ContentValues();
				values.put(Books.BOOK_ID, book.getId());
				values.put(Books.BOOK_TITLE, book.getTitle());
				values.put(Books.BOOK_AUTHOR, book.getAuthor());
				values.put(Books.BOOK_AUTHRO_INTRO, book.getAuthorIntro());
				values.put(Books.BOOK_SUMMARY, book.getSummary());
				values.put(Books.BOOK_IMAGE_URL, book.getImgUrl());

				getContentResolver().insert(Books.CONTENT_URI, values);

				ContentValues aValues = new ContentValues();
				aValues.put(UserBooks.USER_ID, uid);
				aValues.put(UserBooks.BOOK_ID, book.getId());
				aValues.put(UserBooks.USE_TYPE, book.getStatus());
				getContentResolver().insert(
						Users.buildUserBooksUri(uid, book.getId()), aValues);
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (isCancelled())
				return;

			if (!result) {
				Toast.makeText(HomeActivity.this, R.string.load_failed,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean OnBookSelected(String bookId, int page, int section) {
		Uri sessionUri = Books.buildBookUri(bookId);
		Intent detailIntent = new Intent(Intent.ACTION_VIEW, sessionUri);
		detailIntent.putExtra(ARG_PAGE_NUMBER, page);
		detailIntent.putExtra(ARG_SECTION_NUMBER, section);
		detailIntent.putExtra(USER_ID, mUserId);
		startActivity(detailIntent);

		return true;
	}
}
