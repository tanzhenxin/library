package com.gtcc.library.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.gtcc.library.R;

public class BookDetailActivity extends BaseActivity {
	
	private Fragment mFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empty_pane);

		if (getIntent().hasExtra(Intent.EXTRA_TITLE)) {
			setTitle(getIntent().getStringExtra(Intent.EXTRA_TITLE));
		}

		final String customTitle = getIntent().getStringExtra(
				Intent.EXTRA_TITLE);
		setTitle(customTitle != null ? customTitle : getTitle());

		if (savedInstanceState == null) {
			mFragment = new BookDetailFragment();
			mFragment.setArguments(intentToFragmentArguments(getIntent()));
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, mFragment, "single_pane")
					.commit();
		} else {
			mFragment = getSupportFragmentManager().findFragmentByTag(
					"single_pane");
		}
		
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
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
