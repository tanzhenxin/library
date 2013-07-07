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
import com.gtcc.library.ui.user.UserPagerAdapter;
import com.gtcc.library.util.HttpManager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class HomeActivity extends BaseActivity implements 
	ActionBar.TabListener, UserBookListFragment.Callbacks {

	public static final int PAGE_USER = 0;
	public static final int PAGE_LIBRARY = 1;
	public static final int PAGE_SETTINGS = 2;
	
	public static final int USER_READING = 0;
	public static final int USER_WISH = 1;
	public static final int USER_READ = 2;
	
	public static final String ARG_SECTION_NUMBER = "section_number";

	public static final String SHPREF_KEY_ACCESS_TOKEN = "Access_Token";
	public static final String SHPREF_KEY_USER_ID = "User_Id";
	private String accessToken;
	private String currentUserId;

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
				accessToken = data.getExtras().getString(
						SHPREF_KEY_ACCESS_TOKEN);

				Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
				editor.putString(SHPREF_KEY_ACCESS_TOKEN, accessToken);
				editor.commit();
				
				UserInfo userInfo = (UserInfo) data.getExtras().getSerializable(SHPREF_KEY_USER_ID);
				storeUserInfo(userInfo);

				new LoadBooksAsyncTask().execute();

				showUserHome();
				break;
			}
		}
	}
	
	private void storeUserInfo(UserInfo userInfo) {
		String currentUserId = userInfo.getUserId();

		Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
		editor.putString(SHPREF_KEY_USER_ID, currentUserId);
		editor.commit();

		ContentValues values = new ContentValues();
		values.put(Users.USER_ID, userInfo.getUserId());
		values.put(Users.USER_NAME, userInfo.getUserName());
		values.put(Users.USER_IMAGE_URL, userInfo.getUserImageUrl());
		getContentResolver().insert(Users.CONTENT_URI, values);
	}

	public void showPage(int position) {
		if (position != mCurrentPage) {
			switch (position) {
			case PAGE_USER:
				if (!hasAccessToken())
					requestAccessToken();
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

	public String getAccessToken() {
		if (!hasAccessToken())
			requestAccessToken();

		return accessToken;
	}

	public String getCurrentUserId() {
		if (currentUserId == null) {
			currentUserId = getPreferences(Context.MODE_PRIVATE).getString(
					SHPREF_KEY_USER_ID, null);
		}
		return currentUserId == null ? "0" : currentUserId;
	}

	private Boolean hasAccessToken() {
		accessToken = getPreferences(Context.MODE_PRIVATE).getString(
				SHPREF_KEY_ACCESS_TOKEN, null);
		return accessToken != null;
	}

	private void requestAccessToken() {
		Intent intent = new Intent(this, UserLoginActivity.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}

	private void showUserHome() {
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		UserPagerAdapter mSectionsPagerAdapter = new UserPagerAdapter(this);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.removeAllTabs();

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
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
				HttpManager httpManager = new HttpManager(getAccessToken());

				String uid = getCurrentUserId();
				BookCollection bookCollection = new BookCollection();
				while (bookCollection.hasMoreBooks()) {
					List<Book> books = bookCollection.getBooks(httpManager,	uid);
					storeBooks(uid, books);
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
						Users.buildUserBooksUri(uid,
								book.getStatus()), aValues);
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
	public boolean OnBookSelected(String bookId, int section) {
        Uri sessionUri = Books.buildBookUri(bookId);
        Intent detailIntent = new Intent(Intent.ACTION_VIEW, sessionUri);
        detailIntent.putExtra(ARG_SECTION_NUMBER, section);
		startActivity(detailIntent);
		
		return true;
	}
}
