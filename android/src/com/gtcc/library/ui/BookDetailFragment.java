package com.gtcc.library.ui;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.entity.Book;
import com.gtcc.library.entity.BookCollection;
import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.provider.LibraryContract.Comments;
import com.gtcc.library.provider.LibraryContract.Users;
import com.gtcc.library.provider.LibraryDatabase.UserBooks;
import com.gtcc.library.util.ImageFetcher;
import com.gtcc.library.util.LogUtils;
import com.gtcc.library.util.Utils;

public class BookDetailFragment extends SherlockFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = LogUtils
			.makeLogTag(BookDetailFragment.class);
	
	private int ADD_REVIEW = 0;

	private ViewGroup mRootView;
	private ViewGroup mSummaryBlock;
	private ViewGroup mAuthorIntroBlock;
	private ViewGroup mStatusActionBlock;
	private ViewGroup mStatusNowBlock;
	private ViewGroup mLoadingIndicator;

	private TextView mTitleView;
	private TextView mAuthorView;
	private TextView mSummaryView;
	private TextView mAuthorIntroView;
	private ImageView mImageView;
	private Button mStatusReading;
	private Button mStatusRead;
	private Button mStatusWish;
	private TextView mBookStatusText;
	
	private Uri mBookUri;
	private Uri mCommentsUri;
	private int mPage;
	private int mSection;
	private String mUserId;
	private Book book;

	private ImageFetcher mImageFetcher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = BaseActivity
				.fragmentArgumentsToIntent(getArguments());
		mBookUri = intent.getData();
		Bundle bundle = intent.getExtras();

		if (mBookUri == null || bundle == null)
			return;

		mCommentsUri = Books.buildCommentUri(Books.getBookId(mBookUri));
		mPage = bundle.getInt(HomeActivity.ARG_PAGE_NUMBER);
		mSection = bundle.getInt(HomeActivity.ARG_SECTION_NUMBER);
		mUserId = bundle.getString(HomeActivity.USER_ID);

		mImageFetcher = Utils.getImageFetcher(getActivity());
		mImageFetcher.setImageFadeIn(true);

		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.book_detail_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (item.getItemId() == R.id.add_review) {
			Intent intent = new Intent(getActivity(), BookCommentActivity.class);
			intent.putExtra(BookCommentActivity.BOOK_TITLE, book.getTitle());
			getActivity().startActivityForResult(intent, ADD_REVIEW);
		}
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
		mBookStatusText = (TextView) mRootView
				.findViewById(R.id.book_status_text);
		mLoadingIndicator = (ViewGroup) mRootView
				.findViewById(R.id.loading_progress);

		setChangeStatusAnimation();
		setClearStatusAnimation();

		// TypefaceUtils.setTypeface(mSummaryView);
		// TypefaceUtils.setTypeface(mAuthorIntroView);

		// mApplaudAnimation = AnimationUtils.loadAnimation(getActivity(),
		// R.anim.dismiss_ani);

		if (mPage == HomeActivity.PAGE_USER)
		{
			getLoaderManager().initLoader(BookQuery._TOKEN, null, this);
			getLoaderManager().initLoader(CommentQuery._TOKEN, null, this);
		}
		else
			new AsyncBookLoader().execute(Books.getBookId(mBookUri));

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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		getActivity().getContentResolver().registerContentObserver(mCommentsUri, true, mObserver);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		CursorLoader cursor = null;
		if (id == BookQuery._TOKEN) {
			cursor = new CursorLoader(getActivity(), mBookUri, BookQuery.PROJECTION,
				null, null, Books.DEFAULT_SORT_ORDER);
		}
		else {
			cursor = new CursorLoader(getActivity(), mCommentsUri, CommentQuery.PROJECTION, 
					null, null, Comments.DEFAULT_SORT_ORDER);
		}
		return cursor;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (!cursor.moveToFirst()) {
			return;
		}
		
		book = new Book();

		String title = cursor.getString(BookQuery.BOOK_TITLE);
		book.setTitle(title);
		String author = cursor.getString(BookQuery.BOOK_AUTHOR);
		book.SetAuthor(author);
		String summary = cursor.getString(BookQuery.BOOK_SUMMARY);
		book.setSummary(summary);
		String authorIntro = cursor.getString(BookQuery.AUTHOR_INTRO);
		book.setAuthorIntro(authorIntro);
		String imgUrl = cursor.getString(BookQuery.BOOK_IMAGE_URL);
		book.setImgUrl(imgUrl);
		String status = getCurrentStatus();
		book.setStatus(status);

		setContentView(book);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == ADD_REVIEW) {
			if (resultCode == Activity.RESULT_OK) {
				
			}
		}
	}

	private void setContentView(Book book) {
		mTitleView.setText(book.getTitle());
		mAuthorView.setText(book.getAuthor());

		String summary = book.getSummary();
		if (summary != null && !summary.isEmpty())
			mSummaryView.setText(summary);
		else
			mSummaryBlock.setVisibility(View.GONE);

		String authorIntro = book.getAuthorIntro();
		if (authorIntro != null && !authorIntro.isEmpty())
			mAuthorIntroView.setText(authorIntro);
		else
			mAuthorIntroBlock.setVisibility(View.GONE);

		String imgUrl = book.getImgUrl();
		mImageFetcher.loadImage(imgUrl, mImageView, R.drawable.book);

		String status = book.getStatus();
		if (status != null) {
			mStatusActionBlock.setVisibility(View.GONE);
			mStatusNowBlock.setVisibility(View.VISIBLE);
			mBookStatusText.setText(status);
		} else {
			mStatusActionBlock.setVisibility(View.VISIBLE);
			mStatusNowBlock.setVisibility(View.GONE);
		}
	}

	private void setChangeStatusAnimation() {
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
				mBookStatusText.setText(getCurrentStatus());
				mStatusNowBlock.setVisibility(View.VISIBLE);
				mStatusNowBlock.startAnimation(fadeInAnimation);
			}
		});

		mStatusReading.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mStatusActionBlock.startAnimation(fadeOutAnimation);
				mSection = HomeActivity.TAB_0;
			}
		});
		mStatusWish.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mStatusActionBlock.startAnimation(fadeOutAnimation);
				mSection = HomeActivity.TAB_1;
			}
		});
		mStatusRead.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mStatusActionBlock.startAnimation(fadeOutAnimation);
				mSection = HomeActivity.TAB_2;
			}
		});
	}

	private void setClearStatusAnimation() {
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
				mStatusNowBlock.setVisibility(View.GONE);
				mStatusActionBlock.setVisibility(View.VISIBLE);
				mStatusActionBlock.startAnimation(fadeInAnimation);
			}
		});

		OnClickListener onclickListener = new OnClickListener() {
			public void onClick(View v) {
				mStatusNowBlock.startAnimation(fadeOutAnimation);
			}
		};

		mStatusNowBlock.setOnClickListener(onclickListener);
	}

	private String getCurrentStatus() {
		switch (mSection) {
		case HomeActivity.TAB_0:
			return getActivity().getString(R.string.book_reading_full);
		case HomeActivity.TAB_1:
			return getActivity().getString(R.string.book_wish_full);
		case HomeActivity.TAB_2:
			return getActivity().getString(R.string.book_read_full);
		default:
			return null;
		}
	}

	private class AsyncBookLoader extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			String bookId = params[0];
			try {
				book = BookCollection.getBook(bookId);
			} catch (IOException e) {
				LogUtils.LOGE(TAG, "Unable to get book detail");
				return false;
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result) {
				setContentView(book);
			} else {
				Toast.makeText(getActivity(), R.string.load_failed,
						Toast.LENGTH_SHORT).show();
			}

			mLoadingIndicator.setVisibility(View.GONE);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoadingIndicator.setVisibility(View.VISIBLE);
		}
	}
	
	private final ContentObserver mObserver = new ContentObserver(new Handler()) {

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			
			if (getActivity() == null) {
				return;
			}
			
			Loader<Cursor> loader = getLoaderManager().getLoader(CommentQuery._TOKEN);
			if (loader != null) {
				loader.forceLoad();
			}
		}
		
	};

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
	
	public interface CommentQuery {
		int _TOKEN = 1;
		
		public final String[] PROJECTION = new String[] {
				Comments._ID,
				Comments.USER_ID,
				Comments.REPLY_TO,
				Comments.TIMESTAMP,
				Users.USER_NAME,
				Users.USER_IMAGE_URL,
		};
		
		public int _ID = 0;
		public int USER_ID = 1;
		public int REPLY_TO = 2;
		public int TIMESTAMP = 3;
		public int USER_NAME = 4;
		public int USER_IMAGE_URL = 5;
	}

	public interface UserBookQuery {
		int _TOKEN = 1;

		public final String[] PROJECTION = new String[] { UserBooks.USER_ID,
				UserBooks.BOOK_ID, UserBooks.USE_TYPE, };

		public int USER_ID = 0;
		public int BOOK_ID = 1;
		public int USE_TYPE = 2;
	}
}