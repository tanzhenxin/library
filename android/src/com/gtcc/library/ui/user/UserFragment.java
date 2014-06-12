package com.gtcc.library.ui.user;

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
import com.gtcc.library.webserviceproxy.WebServiceInfo;

public class UserFragment extends ViewPagerFragment {
	
	public static final String ARG_USER_CATEOGRY = "user_category";

	@Override
	protected int getPage() {
		return HomeActivity.PAGE_USER;
	}

	@Override
	protected PagerAdapter getPagerAdapter(FragmentActivity activity) {
		return new UserPagerAdapter(activity);
	}

	private class UserPagerAdapter extends FragmentStatePagerAdapter {
		private final Resources resources;
		private final String[] categories = {
				WebServiceInfo.BORROW_METHOD_GET_BORROW_INFO,
				WebServiceInfo.BORROW_METHOD_GET_BORROWED_INFO };

		public UserPagerAdapter(final FragmentActivity activity) {
			super(activity.getSupportFragmentManager());

			resources = activity.getResources();
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new UserBookListFragment();

			Bundle args = new Bundle();
			args.putString(ARG_USER_CATEOGRY, categories[position]);
			fragment.setArguments(args);

			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return resources.getString(R.string.user_borrowing)
						.toUpperCase(l);
			case 1:
				return resources.getString(R.string.user_borrowed)
						.toUpperCase(l);
			}
			return null;
		}
	}
}
