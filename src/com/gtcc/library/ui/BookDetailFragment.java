package com.gtcc.library.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.ui.user.UserBookListFragment;
import com.gtcc.library.util.ImageFetcher;
import com.gtcc.library.util.Utils;

public class BookDetailFragment extends SherlockFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private ViewGroup mRootView;

	private TextView mTitleView;
	private TextView mAuthorView;
	private TextView mSummaryView;
	private ImageView mImageView;
	private RatingBar mRatingBar;
	private ProgressDialog mDialog;
	private TextView mBookStatus;

	private TextView mPlusOne;
	private Animation mApplaudAnimation;

	private Uri mBookUri;

	private ImageFetcher mImageFetcher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = BaseActivity
				.fragmentArgumentsToIntent(getArguments());
		mBookUri = intent.getData();

		if (mBookUri == null)
			return;

		mImageFetcher = Utils.getImageFetcher(getActivity());
		mImageFetcher.setImageFadeIn(true);

		setHasOptionsMenu(true);

		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.book_detail_menu, menu);

		final MenuItem item = menu.findItem(R.id.menu_book_rating);
		item.getActionView().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onOptionsItemSelected(item);
			}
		});

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (item.getItemId() == R.id.menu_book_rating) {
			View view = item.getActionView();
			
			final TextView plusOne = (TextView) view.findViewById(R.id.plus_one_text);
			plusOne.startAnimation(mApplaudAnimation);
			
			new Handler().postDelayed(new Runnable() {
				public void run() {
//					Drawable icon = item.getIcon();
//					ColorFilter filter = new LightingColorFilter( Color.RED, Color.RED ); 
//					icon.setColorFilter(filter);
					
					plusOne.setText("1");
				}
			}, 1000);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_book_detail,
				null);

		mTitleView = (TextView) mRootView.findViewById(R.id.book_title);
		mAuthorView = (TextView) mRootView.findViewById(R.id.book_description);
		mSummaryView = (TextView) mRootView.findViewById(R.id.book_summary);
		mImageView = (ImageView) mRootView.findViewById(R.id.book_img);
		mRatingBar = (RatingBar) mRootView.findViewById(R.id.ratingbar);
		mBookStatus = (TextView) mRootView.findViewById(R.id.book_status);

		mApplaudAnimation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.dismiss_ani);

		return mRootView;
	}

	@Override
	public void onPause() {
		super.onPause();
		mImageFetcher.flushCache();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mImageFetcher.closeCache();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), mBookUri, BookQuery.PROJECTION,
				null, null, Books.DEFAULT_SORT_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (!cursor.moveToFirst()) {
			return;
		}

		String title = cursor.getString(BookQuery.BOOK_TITLE);
		mTitleView.setText(title);

		String author = cursor.getString(BookQuery.BOOK_AUTHOR);
		mAuthorView.setText(author);

		String summary = cursor.getString(BookQuery.BOOK_SUMMARY);
		mSummaryView.setText(summary);

		String imgUrl = cursor
				.getString(UserBookListFragment.BookQuery.BOOK_IMAGE_URL);
		mImageFetcher.loadImage(imgUrl, mImageView, R.drawable.book);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	public interface BookQuery {
		int _TOKEN = 0;

		public final String[] PROJECTION = new String[] { Books._ID,
				Books.BOOK_ID, Books.BOOK_TITLE, Books.BOOK_AUTHOR,
				Books.BOOK_SUMMARY, Books.BOOK_IMAGE_URL, };

		public int _ID = 0;
		public int BOOK_ID = 1;
		public int BOOK_TITLE = 2;
		public int BOOK_AUTHOR = 3;
		public int BOOK_SUMMARY = 4;
		public int BOOK_IMAGE_URL = 5;
	}

}