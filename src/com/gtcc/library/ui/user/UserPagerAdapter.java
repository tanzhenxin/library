package com.gtcc.library.ui.user;

import java.util.Locale;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.gtcc.library.R;
import com.gtcc.library.ui.MainActivity;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class UserPagerAdapter extends FragmentPagerAdapter {
	
	private final Resources resources;

	public UserPagerAdapter(final MainActivity activity) {
		super(activity.getSupportFragmentManager());
		
		resources = activity.getResources();
	}

	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Return a DummySectionFragment (defined as a static inner class
		// below) with the page number as its lone argument.
		Fragment fragment = new UserFragment();
		Bundle args = new Bundle();
		args.putInt(UserFragment.ARG_SECTION_NUMBER, position + 1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		// Show 3 total pages.
		return 4;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return resources.getString(R.string.user_borrowing).toUpperCase(l);
		case 1:
			return resources.getString(R.string.user_borrowed).toUpperCase(l);
		case 2:
			return resources.getString(R.string.user_wanted).toUpperCase(l);
		case 3:
			return resources.getString(R.string.user_donated).toUpperCase(l);
		}
		return null;
	}
}