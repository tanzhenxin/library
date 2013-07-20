package com.gtcc.library.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;

public class BookDetailActivity extends SherlockFragmentActivity {
	
	private Fragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_detail);

		if (getIntent().hasExtra(Intent.EXTRA_TITLE)) {
			setTitle(getIntent().getStringExtra(Intent.EXTRA_TITLE));
		}

		final String customTitle = getIntent().getStringExtra(
				Intent.EXTRA_TITLE);
		setTitle(customTitle != null ? customTitle : getTitle());

		if (savedInstanceState == null) {
			mFragment = new BookDetailFragment();
			mFragment.setArguments(BaseActivity.intentToFragmentArguments(getIntent()));
			getSupportFragmentManager().beginTransaction()
					.add(R.id.root_container, mFragment, "single_pane")
					.commit();
		} else {
			mFragment = getSupportFragmentManager().findFragmentByTag(
					"single_pane");
		}
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent parentIntent = new Intent(this, HomeActivity.class);
            NavUtils.navigateUpTo(this, parentIntent);
            
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (mFragment != null) {
			mFragment.onActivityResult(requestCode, resultCode, data);
		}
	}

	public Fragment getFragment() {
		return mFragment;
	}
}
