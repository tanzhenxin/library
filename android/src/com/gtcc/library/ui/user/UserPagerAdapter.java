package com.gtcc.library.ui.user;

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
public class UserPagerAdapter extends FragmentStatePagerAdapter  {
	
	private final Resources resources;

	public UserPagerAdapter(final HomeActivity activity) {
		super(activity.getSupportFragmentManager());
		
		resources = activity.getResources();
	}

	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Return a DummySectionFragment (defined as a static inner class
		// below) with the page number as its lone argument.
		Fragment fragment = new UserBookListFragment();
		Bundle args = new Bundle();
		args.putInt(HomeActivity.ARG_SECTION_NUMBER, position);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		//TODO currently we just show READING
        return 1;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case HomeActivity.TAB_0:
			return resources.getString(R.string.book_reading).toUpperCase(l);
		case HomeActivity.TAB_1:
			return resources.getString(R.string.book_wish).toUpperCase(l);
		case HomeActivity.TAB_2:
			return resources.getString(R.string.book_read).toUpperCase(l);
		}
		return null;
	}
}