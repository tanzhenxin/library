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
import com.gtcc.library.util.CommonAsyncTask;
import com.gtcc.library.util.HttpManager;
import com.gtcc.library.util.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.gtcc.library.ui.zxing.CaptureActivity;

public class HomeActivity extends SlidingFragmentActivity implements
		ActionBar.TabListener, UserBookListFragment.Callbacks {

	public static final int PAGE_USER = 0;
	public static final int PAGE_LIBRARY = 1;
    public static final int PAGE_SCANNER = 2;
	public static final int PAGE_SETTINGS = 3;

	public static final int TAB_0 = 0;
	public static final int TAB_1 = 1;
	public static final int TAB_2 = 2;

	public static final String ARG_PAGE_NUMBER = "page_number";
	public static final String ARG_SECTION_NUMBER = "section_number";

	private int REQUEST_LOGIN = 1;
    private int SCANNER = 2;
	private int SETTINGS = 3;

    private String isbnCode;

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
		setupScanMenuItem(menu);
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
	
	private void setupScanMenuItem(Menu menu) {
        MenuItem scanItem = menu.findItem(R.id.menu_scan);
        if (scanItem != null) {
            scanItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick(MenuItem item){
                    showScanner();
                    return true;
                }
            });
        }
	}
	
	private void triggerRefresh() {
		// TODO
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
				int loginType = data.getExtras().getInt(UserLoginActivity.LOGIN_TYPE);
				UserInfo userInfo = (UserInfo)data.getExtras().getSerializable(UserLoginActivity.LOGIN_USER);
				setUserInfo(userInfo);

				showPage(PAGE_USER);
				break;
			case Activity.RESULT_CANCELED:
				finish();
				break;
			}
		}
        else if (requestCode == SCANNER){
            if (resultCode == RESULT_OK){
                // scan isbn code, open proper detail activity
                isbnCode = data.getExtras().getString("result");
                if (isbnCode != null && isbnCode != ""){
					CommonAsyncTask<Void, Boolean> task = new CommonAsyncTask<Void, Boolean>(this) {
                        @Override
                        protected Boolean doWork(Void... params) throws Exception {
                            Book book = HttpManager.webServiceBookProxy.getBookByISBN(isbnCode);
                            if (book != null)
                                HomeActivity.this.OnBookSelected(book.getBianhao(), HomeActivity.PAGE_USER, HomeActivity.TAB_0);
                            return true;
                        }
                    };
                    task.execute();
                }
            }
        }
		else if (requestCode == SETTINGS) {
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
            case PAGE_SCANNER:
                showScanner();
				showContent = false;
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
		return mUserInfo != null && mUserInfo.getUserId() != "0";
	}
	
	private void login() {
		Intent intent = new Intent(this, UserLoginActivity.class);
		startActivityForResult(intent, REQUEST_LOGIN);
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

    private void showScanner(){
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, SCANNER);
    }
	
	private void showSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivityForResult(intent, SETTINGS);
	}

	@Override
	public boolean OnBookSelected(String bookId, int page, int section) {
		Uri sessionUri = Books.buildBookUri(bookId);
		Intent detailIntent = new Intent(Intent.ACTION_VIEW, sessionUri);
		detailIntent.putExtra(ARG_PAGE_NUMBER, page);
		detailIntent.putExtra(ARG_SECTION_NUMBER, section);
		detailIntent.putExtra(USER_ID, mUserInfo.getUserId());
		startActivity(detailIntent);

		return true;
	}
}
