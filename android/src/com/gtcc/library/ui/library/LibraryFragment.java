package com.gtcc.library.ui.library;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;

import com.gtcc.library.R;
import com.gtcc.library.provider.LibraryContract;
import com.gtcc.library.ui.BaseActivity;
import com.gtcc.library.ui.HomeActivity;
import com.gtcc.library.ui.ViewPagerFragment;
import com.gtcc.library.ui.library.LibraryBookListFragment.BooksQuery;

public class LibraryFragment extends ViewPagerFragment {
	
	@Override
	protected int getPage() {
		return HomeActivity.PAGE_LIBRARY;
	}

	@Override
	protected PagerAdapter getPagerAdapter(FragmentActivity activity) {
		return new LibraryPagerAdapter(activity);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
        activity.getContentResolver().registerContentObserver(
                LibraryContract.Books.CONTENT_URI, true, mObserver);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
	}
	
    private final ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (getActivity() == null) {
                return;
            }

            Loader<Cursor> loader = getLoaderManager().getLoader(BooksQuery._TOKEN);
            if (loader != null) {
                loader.forceLoad();
            }
        }
    };

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

			Intent intent = new Intent();
			intent.setData(LibraryContract.Books.buildCategoryUri(categories[position]));
			fragment.setArguments(BaseActivity.intentToFragmentArguments(intent));
			
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
