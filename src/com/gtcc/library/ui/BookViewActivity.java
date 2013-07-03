package com.gtcc.library.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gtcc.library.R;
import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.provider.LibraryContract.Users;
import com.gtcc.library.ui.user.UserBookListFragment;
import com.gtcc.library.util.AsyncImageLoader;
import com.gtcc.library.util.AsyncImageLoader.ImageCallback;
import com.gtcc.library.util.ImageFetcher;
import com.gtcc.library.util.Utils;

public class BookViewActivity extends FragmentActivity implements
	LoaderManager.LoaderCallbacks<Cursor> {
	private TextView mTitleView;
	private TextView mAuthorView;
	private TextView mSummaryView;
	private ImageView mImageView;
	private RatingBar mRatingBar;
	private ProgressDialog mDialog;
	private TextView mBookStatus;
	
	private String mBookId;
	
	private ImageFetcher mImageFetcher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.book_detail);
		Bundle extras = getIntent().getExtras();
		mBookId = extras != null ? extras.getString("bookId")
				: null;
		if (mBookId == null) 
			return;
		
        LoaderManager manager = getSupportLoaderManager();

        mImageFetcher = Utils.getImageFetcher(this);
        mImageFetcher.setImageFadeIn(true);
        
        getSupportLoaderManager().initLoader(0, null, this);

		mTitleView = (TextView) findViewById(R.id.book_title);
		mAuthorView = (TextView) findViewById(R.id.book_description);
		mSummaryView = (TextView) findViewById(R.id.book_summary);
		mImageView = (ImageView) findViewById(R.id.book_img);
		mRatingBar = (RatingBar) findViewById(R.id.ratingbar);
		mBookStatus = (TextView) findViewById(R.id.book_status);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 1) {
			Bundle extras = data.getExtras();
			String status = extras != null ? (String) extras
					.getSerializable("status") : null;
			String statusDesc = extras != null ? (String) extras
					.getSerializable("statusDesc") : null;
			Float rating = extras != null ? (Float) extras
					.getSerializable("rating") : null;

			String tags = extras != null ? (String) extras
					.getSerializable("tags") : null;

			mBookStatus.setText(statusDesc);
			mRatingBar.setRating(rating);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Uri uri = Books.buildBookUri(mBookId);
		return new CursorLoader(
				this, 
				uri, 
				BookQuery.PROJECTION, 
				null, 
				null, 
				Books.DEFAULT_SORT_ORDER);
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
		
		String imgUrl = cursor.getString(UserBookListFragment.BookQuery.BOOK_IMAGE_URL);
		mImageFetcher.loadImage(imgUrl, mImageView, R.drawable.book);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) { }
	
	public interface BookQuery {
		int _TOKEN = 0;
		
		public final String[] PROJECTION = new String[] {
			Books._ID,
			Books.BOOK_ID,
			Books.BOOK_TITLE,
			Books.BOOK_AUTHOR,
			Books.BOOK_SUMMARY,
			Books.BOOK_IMAGE_URL,
		};
		
		public int _ID = 0;
		public int BOOK_ID = 1;
		public int BOOK_TITLE = 2;
		public int BOOK_AUTHOR = 3;
		public int BOOK_SUMMARY = 4;
		public int BOOK_IMAGE_URL = 5;
	}

}