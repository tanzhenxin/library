package com.gtcc.library.ui.library;

import java.util.Locale;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.gtcc.library.R;
import com.gtcc.library.ui.HomeActivity;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class LibraryPagerAdapter extends FragmentStatePagerAdapter {
	
	private final Resources resources;

	public LibraryPagerAdapter(final HomeActivity activity) {
		super(activity.getSupportFragmentManager());
		
		resources = activity.getResources();
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = new LibraryBookListFragment();
		Bundle args = new Bundle();
		args.putInt(HomeActivity.ARG_SECTION_NUMBER, position);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return resources.getString(R.string.book_new_arrival).toUpperCase(l);
		case 1:
			return resources.getString(R.string.book_hotest).toUpperCase(l);
		case 2:
			return resources.getString(R.string.book_category).toUpperCase(l);
		}
		return null;
	}
}