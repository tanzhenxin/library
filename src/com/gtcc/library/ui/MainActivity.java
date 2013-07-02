package com.gtcc.library.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.gtcc.library.R;
import com.gtcc.library.ui.library.LibraryPagerAdapter;
import com.gtcc.library.ui.user.UserLoginActivity;
import com.gtcc.library.ui.user.UserPagerAdapter;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends BaseActivity implements
		ActionBar.TabListener {
	
	public static final int PAGE_USER = 0;
	public static final int PAGE_LIBRARY = 1;
	public static final int PAGE_SETTINGS = 2;
	
	public static final String SHPREF_KEY_ACCESS_TOKEN = "Access_Token";
	private String accessToken;
	
	private int REQUEST_LOGIN = 1;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	private int mCurrentPage = -1;
	
	public MainActivity() {
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
							getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
							break;
						default:
							getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
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

				showUserHome();
				break;
			}
		}
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
			actionBar.addTab(actionBar.newTab()
					.setText(pagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		setTitle(R.string.book_library);
		mViewPager.setCurrentItem(0);
	}
}
