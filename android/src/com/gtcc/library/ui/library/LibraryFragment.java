package com.gtcc.library.ui.library;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.gtcc.library.R;
import com.gtcc.library.ui.HomeActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class LibraryFragment extends SherlockFragment {

	private ViewPager mViewPager;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (((HomeActivity) getActivity()).getCurrentPage() == HomeActivity.PAGE_LIBRARY) {
			buildActionBarAndViewPagerTitles();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.viewpager_layout, null);
		mViewPager = (ViewPager) view;
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mViewPager.setOnPageChangeListener(onPageChangeListener);
	}

	private void buildActionBarAndViewPagerTitles() {
		final HomeActivity activity = (HomeActivity) getActivity();
		final ActionBar actionBar = activity.getSupportActionBar();

		LibraryPagerAdapter pagerAdapter = new LibraryPagerAdapter(activity);
		mViewPager.setAdapter(pagerAdapter);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.removeAllTabs();

		SimpleTabListener tabListener = new SimpleTabListener(mViewPager);
		for (int i = 0; i < pagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(pagerAdapter.getPageTitle(i))
					.setTabListener(tabListener));
		}

		mViewPager.setCurrentItem(0);
	}

	private ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {

			switch (position) {
			case 0:
				((HomeActivity) getActivity()).getSlidingMenu()
						.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				break;
			default:
				((HomeActivity) getActivity()).getSlidingMenu()
						.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
				break;
			}
			((HomeActivity) getActivity()).getSupportActionBar()
					.setSelectedNavigationItem(position);
		}
	};

	private class SimpleTabListener implements ActionBar.TabListener {

		private ViewPager viewPager;

		public SimpleTabListener(ViewPager viewPager) {
			this.viewPager = viewPager;
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (viewPager != null
					&& viewPager.getCurrentItem() != tab.getPosition())
				viewPager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}
}
