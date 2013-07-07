package com.gtcc.library.ui;

import android.content.Intent;
import android.database.Cursor;
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
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.util.ImageFetcher;
import com.gtcc.library.util.Utils;

public class BookDetailFragment extends SherlockFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private ViewGroup mRootView;
	private ViewGroup mSummaryBlock;
	private ViewGroup mAuthorIntroBlock;
	private ViewGroup mStatusActionBlock;
	private ViewGroup mStatusNowBlock;

	private TextView mTitleView;
	private TextView mAuthorView;
	private TextView mSummaryView;
	private TextView mAuthorIntroView;
	private ImageView mImageView;
	private Button mStatusReading;
	private Button mStatusRead;
	private Button mStatusWish;

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
		// inflater.inflate(R.menu.book_detail_menu, menu);
		//
		// final MenuItem item = menu.findItem(R.id.menu_book_rating);
		// item.getActionView().setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// onOptionsItemSelected(item);
		// }
		// });

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// if (item.getItemId() == R.id.menu_book_rating) {
		// View view = item.getActionView();
		//
		// final TextView plusOne = (TextView)
		// view.findViewById(R.id.plus_one_text);
		// plusOne.startAnimation(mApplaudAnimation);
		//
		// new Handler().postDelayed(new Runnable() {
		// public void run() {
		// // Drawable icon = item.getIcon();
		// // ColorFilter filter = new LightingColorFilter( Color.RED, Color.RED
		// );
		// // icon.setColorFilter(filter);
		//
		// plusOne.setText("1");
		// }
		// }, 1000);
		// return true;
		// }
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_book_detail,
				null);

		mTitleView = (TextView) mRootView.findViewById(R.id.book_title);
		mAuthorView = (TextView) mRootView.findViewById(R.id.book_author);
		mSummaryView = (TextView) mRootView.findViewById(R.id.book_summary);
		mImageView = (ImageView) mRootView.findViewById(R.id.book_img);
		mAuthorIntroView = (TextView) mRootView.findViewById(R.id.author_intro);
		mSummaryBlock = (ViewGroup) mRootView
				.findViewById(R.id.book_summary_block);
		mAuthorIntroBlock = (ViewGroup) mRootView
				.findViewById(R.id.author_intro_block);
		mStatusActionBlock = (ViewGroup) mRootView
				.findViewById(R.id.book_status_action);
		mStatusNowBlock = (ViewGroup) mRootView
				.findViewById(R.id.book_status_now);
		mStatusReading = (Button) mRootView
				.findViewById(R.id.book_status_reading);
		mStatusWish = (Button) mRootView.findViewById(R.id.book_status_wish);
		mStatusRead = (Button) mRootView.findViewById(R.id.book_status_read);
		
		

		// TypefaceUtils.setTypeface(mSummaryView);
		// TypefaceUtils.setTypeface(mAuthorIntroView);

		// mApplaudAnimation = AnimationUtils.loadAnimation(getActivity(),
		// R.anim.dismiss_ani);

		final Animation fadeOutAnimation = AnimationUtils.loadAnimation(
				getActivity(), R.anim.fade_out);
		
		final Animation fadeInAnimation = AnimationUtils.loadAnimation(
				getActivity(), R.anim.fade_in_slowly);
		
		fadeOutAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mStatusActionBlock.setVisibility(View.GONE);
				mStatusNowBlock.setVisibility(View.VISIBLE);
				mStatusNowBlock.startAnimation(fadeInAnimation);
			}
		});

		OnClickListener onclickListener = new OnClickListener() {
			public void onClick(View v) {
				mStatusActionBlock.startAnimation(fadeOutAnimation);
			}
		};

		mStatusReading.setOnClickListener(onclickListener);
		mStatusWish.setOnClickListener(onclickListener);
		mStatusRead.setOnClickListener(onclickListener);
		

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
		if (summary != null && !summary.isEmpty())
			mSummaryView.setText(summary);
		else
			mSummaryBlock.setVisibility(View.GONE);

		String authorIntro = cursor.getString(BookQuery.AUTHOR_INTRO);
		if (authorIntro != null && !authorIntro.isEmpty())
			mAuthorIntroView.setText(authorIntro);
		else
			mAuthorIntroBlock.setVisibility(View.GONE);

		String imgUrl = cursor.getString(BookQuery.BOOK_IMAGE_URL);
		mImageFetcher.loadImage(imgUrl, mImageView, R.drawable.book);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	public interface BookQuery {
		int _TOKEN = 0;

		public final String[] PROJECTION = new String[] { Books._ID,
				Books.BOOK_ID, Books.BOOK_TITLE, Books.BOOK_AUTHOR,
				Books.BOOK_SUMMARY, Books.BOOK_AUTHRO_INTRO,
				Books.BOOK_IMAGE_URL, };

		public int _ID = 0;
		public int BOOK_ID = 1;
		public int BOOK_TITLE = 2;
		public int BOOK_AUTHOR = 3;
		public int BOOK_SUMMARY = 4;
		public int AUTHOR_INTRO = 5;
		public int BOOK_IMAGE_URL = 6;
	}

}