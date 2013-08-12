package com.gtcc.library.ui;

import java.io.IOException;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.SearchView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.entity.Book;
import com.gtcc.library.entity.BookCollection;
import com.gtcc.library.entity.UserInfo;
import com.gtcc.library.provider.LibraryContract;
import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.provider.LibraryContract.Users;
import com.gtcc.library.provider.LibraryDatabase.UserBooks;
import com.gtcc.library.ui.library.LibraryPagerAdapter;
import com.gtcc.library.ui.user.UserBookListFragment;
import com.gtcc.library.ui.user.UserLoginActivity;
import com.gtcc.library.ui.user.UserPagerAdapter;
import com.gtcc.library.util.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class HomeActivity extends SlidingFragmentActivity implements
		ActionBar.TabListener, UserBookListFragment.Callbacks {

	public static final int PAGE_USER = 0;
	public static final int PAGE_LIBRARY = 1;
	public static final int PAGE_SETTINGS = 2;

	public static final int TAB_0 = 0;
	public static final int TAB_1 = 1;
	public static final int TAB_2 = 2;

	public static final String ARG_PAGE_NUMBER = "page_number";
	public static final String ARG_SECTION_NUMBER = "section_number";

	private int REQUEST_LOGIN = 1;
	private int SETTINGS = 2;

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
		getSupportMenuInflater().inflate(R.menu.books_list_menu, menu);
		setupSearchMenuItem(menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_search:
            if (!Utils.hasHoneycomb()) {
            	startSearch(null, false, Bundle.EMPTY, false);
                return true;
            }
            break;
		case R.id.menu_refresh:
			triggerRefresh();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupSearchMenuItem(Menu menu) {
		MenuItem searchMenu = menu.findItem(R.id.menu_search);
		if (searchMenu != null && Utils.hasHoneycomb()) {
			SearchView searchView = (SearchView) searchMenu.getActionView();
			if (searchView != null) {
				SearchManager searchManager = (SearchManager) getSystemService(Activity.SEARCH_SERVICE);
				SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
				searchView.setSearchableInfo(info);
			}
		}
	}
	
	private void triggerRefresh() {
		
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
				} else {
					UserInfo userInfo = (UserInfo) data.getExtras()
							.getSerializable(USER_ID);
					mUserId = userInfo.getUserId();
					mUserName = userInfo.getUserName();
					mUserImageUrl = userInfo.getUserImageUrl();
					mAccessToken = data.getExtras().getString(ACCESS_TOKEN);

					new LoadBooksAsyncTask().execute();
				}
				
				storeUserInfo();

				showPage(PAGE_USER);
				break;
			case Activity.RESULT_CANCELED:
				finish();
				break;
			}
		}
		if (requestCode == SETTINGS) {
			switch (resultCode) {
			case Activity.RESULT_FIRST_USER:
				clearUserInfo();
				getContentResolver().delete(LibraryContract.BASE_CONTENT_URI, null, null);
				login();
				break;
			}
		}
	}

	public void showPage(int position) {
		boolean showContent = true;
		
		if (position != mCurrentPage) {
			switch (position) {
			case PAGE_USER:
				if (!hasLogin()) {
					login();
					return;
				}
				else
					showUserHome();
				break;
			case PAGE_LIBRARY:
				showLibrary();
				break;
			case PAGE_SETTINGS:
				showSettings();
				showContent = false;
				break;
			}
		}

		if (showContent) {
			mCurrentPage = position;
			showContent();
		}
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
	
	private void showSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivityForResult(intent, SETTINGS);
	}

	private class LoadBooksAsyncTask extends AsyncTask<Void, Void, Boolean> {

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
