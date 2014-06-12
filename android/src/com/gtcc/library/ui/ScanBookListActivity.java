package com.gtcc.library.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.ui.library.LibraryBookListFragment;

public class ScanBookListActivity extends BaseActivity implements
		AbstractBookListFragment.Callbacks {
	
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
		
		Bundle args = new Bundle();
		args.putString(HomeActivity.BOOK_ISBN, getIntent().getStringExtra(HomeActivity.BOOK_ISBN));
		mBooksFragment.setArguments(args);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
		}
		return super.onOptionsItemSelected(item);
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
