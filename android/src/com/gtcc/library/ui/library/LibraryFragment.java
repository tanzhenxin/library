package com.gtcc.library.ui.library;

import java.util.Locale;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.gtcc.library.R;
import com.gtcc.library.ui.HomeActivity;
import com.gtcc.library.ui.ViewPagerFragment;

public class LibraryFragment extends ViewPagerFragment {
	
	public static final String ARG_BOOK_CATEOGRY = "book_category";

	@Override
	protected int getPage() {
		return HomeActivity.PAGE_LIBRARY;
	}

	@Override
	protected PagerAdapter getPagerAdapter(FragmentActivity activity) {
		return new LibraryPagerAdapter(activity);
	}
	
	private class LibraryPagerAdapter extends FragmentStatePagerAdapter {
		
		private final Resources resources;
		private final String[] categories = { "E", "F", "M", "S", "T", "Z" };

		public LibraryPagerAdapter(final FragmentActivity activity) {
			super(activity.getSupportFragmentManager());
			
			resources = activity.getResources();
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new LibraryBookListFragment();
			
			Bundle args = new Bundle();
			args.putString(ARG_BOOK_CATEOGRY, categories[position]);
			fragment.setArguments(args);
			
			return fragment;
		}

		@Override
		public int getCount() {
			return 6;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return resources.getString(R.string.library_engineering).toUpperCase(l);
			case 1:
				return resources.getString(R.string.library_languages).toUpperCase(l);
			case 2:
				return resources.getString(R.string.library_management).toUpperCase(l);
			case 3:
				return resources.getString(R.string.library_self).toUpperCase(l);
			case 4:
				return resources.getString(R.string.library_technical).toUpperCase(l);
			case 5:
				return resources.getString(R.string.library_misc).toUpperCase(l);
			}
			return null;
		}
	}
}
