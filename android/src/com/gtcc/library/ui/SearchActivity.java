package com.gtcc.library.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.widget.SearchView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.ui.library.LibraryBookListFragment;
import com.gtcc.library.util.Utils;

public class SearchActivity extends BaseActivity
	implements BookListFragment.Callbacks {
	
	LibraryBookListFragment mBooksFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		FragmentManager fm = getSupportFragmentManager();
		mBooksFragment = (LibraryBookListFragment) fm.findFragmentById(R.id.fragment_container);
		if (mBooksFragment == null) {
			mBooksFragment = new LibraryBookListFragment();
			fm.beginTransaction()
				.add(R.id.fragment_container, mBooksFragment)
				.commit();
		}
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		onNewIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		String query = intent.getStringExtra(SearchManager.QUERY);
		
		setTitle(Html.fromHtml(getString(R.string.title_search_query, query)));
		mBooksFragment.reloadFromArguments(intentToFragmentArguments(intent));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.search, menu);
        setupSearchMenuItem(menu);
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
				searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                    @Override
//                    public boolean onQueryTextSubmit(String s) {
//                        ReflectionUtils.tryInvoke(searchItem, "collapseActionView");
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onQueryTextChange(String s) {
//                        return false;
//                    }
//                });
//                searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
//                    @Override
//                    public boolean onSuggestionSelect(int i) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onSuggestionClick(int i) {
//                        ReflectionUtils.tryInvoke(searchItem, "collapseActionView");
//                        return false;
//                    }
//                });
			}
		}
	}

	@Override
	public boolean OnBookSelected(String bookId, int page, int tab) {
		// TODO Auto-generated method stub
		return false;
	}
}
