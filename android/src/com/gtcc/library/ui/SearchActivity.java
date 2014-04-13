package com.gtcc.library.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.widget.SearchView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.provider.LibraryContract.SearchSuggest;
import com.gtcc.library.ui.library.LibraryBookListFragment;
import com.gtcc.library.util.Utils;

public class SearchActivity extends BaseActivity
	implements BookListFragment.Callbacks {
	
	LibraryBookListFragment mBooksFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empty_pane);
		
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
		mBooksFragment.startSearch(intentToFragmentArguments(intent));
		
		addSearchSuggest(query);
	}
	
	private void addSearchSuggest(String query) {
		ContentValues values = new ContentValues();
		values.put(SearchManager.SUGGEST_COLUMN_TEXT_1, query);
		getContentResolver().insert(SearchSuggest.CONTENT_URI, values);
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
		case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
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
	public boolean OnBookSelected(String bookId, int page) {
		Uri sessionUri = Books.buildBookUri(bookId);
		Intent detailIntent = new Intent(Intent.ACTION_VIEW, sessionUri);
		detailIntent.putExtra(HomeActivity.ARG_PAGE_NUMBER, page);
		detailIntent.putExtra(USER_ID, getUserId());
		startActivity(detailIntent);
		
		return true;
	}
}
