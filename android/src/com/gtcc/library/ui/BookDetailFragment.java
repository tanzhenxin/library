package com.gtcc.library.ui;

import java.io.IOException;

import android.app.Activity;
import android.content.ContentValues;
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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.gtcc.library.util.HttpManager;
import com.gtcc.library.util.ImageFetcher;
import com.gtcc.library.util.LogUtils;
import com.gtcc.library.util.Utils;
import com.gtcc.library.webserviceproxy.WebServiceInfo;

public class BookDetailFragment extends SherlockFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = LogUtils
			.makeLogTag(BookDetailFragment.class);

	private final int ADD_REVIEW = 0;
    private final int LOAD_BORROW_RETURN = 1;
    private final int BORROW_BOOK = 2;
    private final int RETURN_BOOK = 3;
    private final int CANNOT_OPERATE = 4;
    private final int LOAD_BOOK_INFO = 5;

    private int mCurrentBookState;

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
    private Button mBorrowReturn;

	private Uri mBookUri;
	private Uri mCommentsUri;
	private int mPage;
	private int mSection;
	private String mUserId;
	private Book book;
    private String borrowedUserId;
    private int borrowResult;

	private ImageFetcher mImageFetcher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BookDetailActivity activity = (BookDetailActivity) getActivity();
		if (activity == null)
			return;
		
		final Intent intent = activity.fragmentArgumentsToIntent(getArguments());
		mBookUri = intent.getData();
		Bundle bundle = intent.getExtras();

		if (mBookUri == null || bundle == null)
			return;

		mCommentsUri = Books.buildCommentUri(Books.getBookId(mBookUri));
		mPage = bundle.getInt(HomeActivity.ARG_PAGE_NUMBER);
		mSection = bundle.getInt(HomeActivity.ARG_SECTION_NUMBER);
		mUserId = bundle.getString(HomeActivity.USER_ID);

		mImageFetcher = Utils.getImageFetcher(getActivity());
		mImageFetcher.setImageFadeIn(false);

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
			intent.putExtra(BookCommentActivity.USER_ID, mUserId);
			intent.putExtra(BookCommentActivity.BOOK_ID, book.getId());
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
        mBorrowReturn = (Button) mRootView.findViewById(R.id.action_borrow_return);
        mBorrowReturn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncLoader().execute(mCurrentBookState);
            }
        });

		setChangeStatusAnimation();
		setClearStatusAnimation();

		// TypefaceUtils.setTypeface(mSummaryView);
		// TypefaceUtils.setTypeface(mAuthorIntroView);

		// mApplaudAnimation = AnimationUtils.loadAnimation(getActivity(),
		// R.anim.dismiss_ani);

		if (mPage == HomeActivity.PAGE_USER) {
			getLoaderManager().restartLoader(BookQuery._TOKEN, null, this);
			getLoaderManager().restartLoader(CommentQuery._TOKEN, null, this);
		} 
		else {
			getLoaderManager().restartLoader(BookQuery._TOKEN, null, this);
			//new AsyncBookLoader().execute(Books.getBookId(mBookUri));
		}

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
		getActivity().getContentResolver().registerContentObserver(
				Comments.CONTENT_URI, true, mObserver);
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
			cursor = new CursorLoader(getActivity(), mBookUri,
					BookQuery.PROJECTION, null, null, Books.DEFAULT_SORT_ORDER);
		} else {
			cursor = new CursorLoader(getActivity(), mCommentsUri,
					CommentQuery.PROJECTION, null, null,
					Comments.DEFAULT_SORT_ORDER);
		}
		return cursor;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (getActivity() == null) {
			return;
		}

		if (loader.getId() == BookQuery._TOKEN) {
			onBookQueryComplete(cursor);
		} else {
			onCommentQueryComplete(cursor);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	private void onBookQueryComplete(Cursor cursor) {
		if (!cursor.moveToFirst()) {
			new AsyncLoader().execute(LOAD_BOOK_INFO);
			return;
		}

		if (book == null)
			book = new Book();
		book.setId(cursor.getString(BookQuery.BOOK_ID));
		book.setBianhao(cursor.getString(BookQuery.BOOK_BIANHAO));
		book.setTitle(cursor.getString(BookQuery.BOOK_TITLE));
		book.setAuthor(cursor.getString(BookQuery.BOOK_AUTHOR));
		book.setAuthorIntro(cursor.getString(BookQuery.BOOK_AUTHRO_INTRO));
		book.setSummary(cursor.getString(BookQuery.BOOK_SUMMARY));
		book.setDescription(cursor.getString(BookQuery.BOOK_DESCRIPTION));
		book.setLanguage(cursor.getString(BookQuery.BOOK_LANGUAGE));
		book.setPrice(cursor.getString(BookQuery.BOOK_PRICE));
		book.setPublishDate(cursor.getString(BookQuery.BOOK_PUBLISH_DATE));
		book.setImgUrl(cursor.getString(BookQuery.BOOK_IMAGE_URL));
		book.setStatus(getCurrentStatus());

		setContentView(book);
        new AsyncLoader().execute(LOAD_BORROW_RETURN);
	}

	private void onCommentQueryComplete(Cursor cursor) {
		final ViewGroup reviewsGroup = (ViewGroup) mRootView
				.findViewById(R.id.book_reviews_block);
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// clear all children from this view group except the first child.
		if (reviewsGroup.getChildCount() > 1) {
			reviewsGroup.removeViews(1, reviewsGroup.getChildCount() - 1);
		}

		boolean hasReviews = false;
		while (cursor.moveToNext()) {
			final String comment = cursor.getString(CommentQuery.COMMENT);
			if (TextUtils.isEmpty(comment)) {
				continue;
			}

			final String userName = cursor.getString(CommentQuery.USER_NAME);
			final String userImageUrl = cursor
					.getString(CommentQuery.USER_IMAGE_URL);
			final String timestamp = cursor.getString(CommentQuery.TIMESTAMP);
			final String mReplyAuthor = cursor
					.getString(CommentQuery.REPLY_AUTHOR);
			final String mReplyQuote = cursor
					.getString(CommentQuery.REPLY_QUOTE);

			final View commentView = inflater.inflate(R.layout.book_comment,
					reviewsGroup, false);
			final ImageView userImageView = (ImageView) commentView
					.findViewById(R.id.user_image);
			final TextView userNameView = (TextView) commentView
					.findViewById(R.id.user_name);
			final TextView commentDateView = (TextView) commentView
					.findViewById(R.id.comment_date);
			final TextView commentContentView = (TextView) commentView
					.findViewById(R.id.comment_content);

			userNameView.setText(userName);
			commentDateView.setText(timestamp);
			commentContentView.setText(comment);
			mImageFetcher.loadThumbnailImage(userImageUrl, userImageView,
					R.drawable.person_image_empty);

			final ImageView replyImageView = (ImageView) commentView
					.findViewById(R.id.comment_reply);
			replyImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),
							BookCommentActivity.class);
					intent.putExtra(BookCommentActivity.USER_ID, mUserId);
					intent.putExtra(BookCommentActivity.BOOK_ID, book.getId());
					intent.putExtra(BookCommentActivity.REPLY_AUTHOR, userName);
					intent.putExtra(BookCommentActivity.REPLY_COMMENT, comment);
					getActivity().startActivityForResult(intent, ADD_REVIEW);
				}
			});

			if (!TextUtils.isEmpty(mReplyAuthor)) {
				final ViewGroup mCommentQuoteBlock = (ViewGroup) commentView
						.findViewById(R.id.quote_comment_block);
				final TextView mCommentQuoteContent = (TextView) commentView
						.findViewById(R.id.quote_comment_content);
				mCommentQuoteContent.setText(mReplyQuote);
				mCommentQuoteBlock.setVisibility(View.VISIBLE);
			}

			if (cursor.isLast()) {
				final ImageView divider = (ImageView) commentView
						.findViewById(R.id.imgDivider);
				divider.setVisibility(View.GONE);
			}

			hasReviews = true;
			reviewsGroup.addView(commentView);
		}

		if (hasReviews) {
			reviewsGroup.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ADD_REVIEW) {
			if (resultCode == Activity.RESULT_OK) {
				Loader<Cursor> loader = getLoaderManager().getLoader(
						CommentQuery._TOKEN);
				if (loader != null) {
					loader.forceLoad();
				}
			}
		}
	}

	private void setContentView(Book book) {
		mTitleView.setText(book.getTitle());
		mAuthorView.setText(book.getAuthor());

		String summary = book.getSummary();
		if (summary != null && !TextUtils.isEmpty(summary))
			mSummaryView.setText(summary);
		else
			mSummaryBlock.setVisibility(View.GONE);

		String authorIntro = book.getAuthorIntro();
		if (authorIntro != null && !TextUtils.isEmpty(authorIntro))
			mAuthorIntroView.setText(authorIntro);
		else
			mAuthorIntroBlock.setVisibility(View.GONE);

		String imgUrl = book.getImgUrl();
		if (imgUrl != null)
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

    private final ContentObserver mObserver = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            if (getActivity() == null) {
                return;
            }

            Loader<Cursor> loader = getLoaderManager().getLoader(
                    CommentQuery._TOKEN);
            if (loader != null) {
                loader.forceLoad();
            }
        }

    };

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

    private void setBorrowReturnState(){
        if (mCurrentBookState == BORROW_BOOK){
            mBorrowReturn.setText(R.string.borrow_this_book);
            mBorrowReturn.setEnabled(true);
        }
        else if (mCurrentBookState == CANNOT_OPERATE){
            mBorrowReturn.setText(String.format(getResources().getString(R.string.lent_to_others), borrowedUserId));
            mBorrowReturn.setEnabled(false);
        }
        else if (mCurrentBookState == RETURN_BOOK) {
            mBorrowReturn.setText(R.string.return_this_book);
            mBorrowReturn.setEnabled(true);
        }
    }

    private class AsyncLoader extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
            int type = params[0];
            try{
                switch (type){
                    case LOAD_BORROW_RETURN:
                        borrowedUserId = HttpManager.webServiceBorrowProxy.checkWhetherBookInBorrow(book.getBianhao());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (borrowedUserId == null){
                                    mCurrentBookState = BORROW_BOOK;
                                }
                                else if (borrowedUserId.equals(mUserId)){
                                    mCurrentBookState = RETURN_BOOK;
                                }
                                else {
                                    mCurrentBookState = CANNOT_OPERATE;
                                }
                                setBorrowReturnState();
                            }
                        });
                        break;
                    case BORROW_BOOK:
                        borrowResult = HttpManager.webServiceBorrowProxy.borrow(mUserId, book.getBianhao());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (borrowResult == WebServiceInfo.OPERATION_SUCCEED){
                                    mCurrentBookState = RETURN_BOOK;
                                    setBorrowReturnState();
                                    Toast.makeText(getActivity(),
                                            getActivity().getString(R.string.operation_succeed),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    case RETURN_BOOK:
                        borrowResult = HttpManager.webServiceBorrowProxy.returnBook(mUserId, book.getBianhao());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (borrowResult == WebServiceInfo.OPERATION_SUCCEED) {
                                    mCurrentBookState = BORROW_BOOK;
                                    setBorrowReturnState();
                                    Toast.makeText(getActivity(),
                                            getActivity().getString(R.string.operation_succeed),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    case LOAD_BOOK_INFO:
                    	book = HttpManager.webServiceBookProxy.getBookByBianHao(Books.getBookId(mBookUri));
                    	if (book == null)
                    		return false;

                    	// save the book to sqlite
                    	ContentValues values = new ContentValues();
        				values.put(Books.BOOK_ID, book.getId());
        				values.put(Books.BOOK_BIANHAO, book.getBianhao());
        				values.put(Books.BOOK_TITLE, book.getTitle());
        				values.put(Books.BOOK_AUTHOR, book.getAuthor());
        				values.put(Books.BOOK_AUTHRO_INTRO, book.getAuthorIntro());
        				values.put(Books.BOOK_SUMMARY, book.getSummary());
        				values.put(Books.BOOK_DESCRIPTION, book.getDescription());
        				values.put(Books.BOOK_LANGUAGE, book.getLanguage());
        				values.put(Books.BOOK_PRICE, book.getPrice());
        				values.put(Books.BOOK_PUBLISH_DATE, book.getPublishDate());
        				values.put(Books.BOOK_IMAGE_URL, book.getImgUrl());
                    	getActivity().getContentResolver().insert(Books.CONTENT_URI, values);
                    	
                    	getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            	setContentView(book);
                                new AsyncLoader().execute(LOAD_BORROW_RETURN);
                            }
                        });
                		
                        break;
                }

                return true;
            }catch (Exception e){
                return false;
            }
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result){

            }
            else {
                Toast.makeText(getActivity(),
                        getActivity().getString(R.string.load_failed),
                        Toast.LENGTH_SHORT).show();
            }
            mLoadingIndicator.setVisibility(View.GONE);
        }
    }

	public interface BookQuery {
		int _TOKEN = 0;

		public final String[] PROJECTION = new String[] { Books._ID,
				Books.BOOK_ID, Books.BOOK_BIANHAO, Books.BOOK_TITLE, 
				Books.BOOK_AUTHOR, Books.BOOK_AUTHRO_INTRO, Books.BOOK_SUMMARY, 
				Books.BOOK_DESCRIPTION, Books.BOOK_LANGUAGE, Books.BOOK_PRICE, 
				Books.BOOK_PUBLISH_DATE, Books.BOOK_IMAGE_URL, };

		public int _ID = 0;
		public int BOOK_ID = 1;
		public int BOOK_BIANHAO = 2;
		public int BOOK_TITLE = 3;
		public int BOOK_AUTHOR = 4;
		public int BOOK_AUTHRO_INTRO = 5;
		public int BOOK_SUMMARY = 6;
		public int BOOK_DESCRIPTION = 7;
		public int BOOK_LANGUAGE = 8;
		public int BOOK_PRICE = 9;
		public int BOOK_PUBLISH_DATE = 10;
		public int BOOK_IMAGE_URL = 11;
	}

	public interface CommentQuery {
		int _TOKEN = 1;

		public final String[] PROJECTION = new String[] { Comments._ID,
				Comments.USER_ID, Comments.COMMENT, Comments.REPLY_AUTHOR,
				Comments.REPLY_QUOTE, Comments.TIMESTAMP, Users.USER_NAME,
				Users.USER_IMAGE_URL, };

		public int _ID = 0;
		public int USER_ID = 1;
		public int COMMENT = 2;
		public int REPLY_AUTHOR = 3;
		public int REPLY_QUOTE = 4;
		public int TIMESTAMP = 5;
		public int USER_NAME = 6;
		public int USER_IMAGE_URL = 7;
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