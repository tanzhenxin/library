package com.gtcc.library.ui.library;

import java.util.Locale;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.gtcc.library.R;
import com.gtcc.library.ui.HomeActivity;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class LibraryPagerAdapter extends FragmentPagerAdapter {
	
	private final Resources resources;

	public LibraryPagerAdapter(final HomeActivity activity) {
		super(activity.getSupportFragmentManager());
		
		resources = activity.getResources();
	}

	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Return a DummySectionFragment (defined as a static inner class
		// below) with the page number as its lone argument.
		Fragment fragment = new LibraryFragment();
		Bundle args = new Bundle();
		args.putInt(LibraryFragment.ARG_SECTION_NUMBER, position + 1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		return 4;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return resources.getString(R.string.library_technical).toUpperCase(l);
		case 1:
			return resources.getString(R.string.library_self).toUpperCase(l);
		case 2:
			return resources.getString(R.string.library_english).toUpperCase(l);
		case 3:
			return resources.getString(R.string.library_misc).toUpperCase(l);
		}
		return null;
	}
}