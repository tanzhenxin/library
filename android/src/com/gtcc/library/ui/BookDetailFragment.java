package com.gtcc.library.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.Visibility;
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
import com.gtcc.library.entity.Borrow;
import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.provider.LibraryContract.Comments;
import com.gtcc.library.provider.LibraryContract.Users;
import com.gtcc.library.provider.LibraryDatabase.UserBooks;
import com.gtcc.library.util.HttpManager;
import com.gtcc.library.util.ImageFetcher;
import com.gtcc.library.util.LogUtils;
import com.gtcc.library.util.Utils;
import com.gtcc.library.webserviceproxy.WebServiceInfo;

public class BookDetailFragment extends SherlockFragment {
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
	private ViewGroup mDescriptionBlock;
	private ViewGroup mLoadingIndicator;

	private TextView mTitleView;
	private TextView mAuthorView;
	private TextView mPublisherView;
	private TextView mPublishDateView;
	private TextView mIsbnView;
	private TextView mDescriptionView;
	private ImageView mImageView;
	private TextView mTagView;
	private TextView mStatusView;
	private Button mBorrowReturn;

	private Uri mBookUri;
	private int mPage;
	private String mUserId;
	private Book book;
	private Borrow bookBorrowInfo;

	private ImageFetcher mImageFetcher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BookDetailActivity activity = (BookDetailActivity) getActivity();
		if (activity == null)
			return;

		final Intent intent = activity
				.fragmentArgumentsToIntent(getArguments());
		mBookUri = intent.getData();
		Bundle bundle = intent.getExtras();

		if (mBookUri == null || bundle == null)
			return;

		mPage = bundle.getInt(HomeActivity.ARG_PAGE_NUMBER);
		mUserId = bundle.getString(HomeActivity.USER_ID);

		mImageFetcher = Utils.getImageFetcher(getActivity());
		mImageFetcher.setImageFadeIn(false);

		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// inflater.inflate(R.menu.book_detail_menu, menu);
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
		mPublisherView = (TextView) mRootView.findViewById(R.id.book_publisher);
		mPublishDateView = (TextView) mRootView
				.findViewById(R.id.book_publish_date);
		mIsbnView = (TextView) mRootView.findViewById(R.id.book_isbn);
		mDescriptionView = (TextView) mRootView.findViewById(R.id.book_summary);
		mImageView = (ImageView) mRootView.findViewById(R.id.book_img);
		mDescriptionBlock = (ViewGroup) mRootView
				.findViewById(R.id.book_summary_block);
		mTagView = (TextView) mRootView.findViewById(R.id.book_tag);
		mStatusView = (TextView) mRootView.findViewById(R.id.book_status);
		mLoadingIndicator = (ViewGroup) mRootView
				.findViewById(R.id.loading_progress);
		mBorrowReturn = (Button) mRootView
				.findViewById(R.id.action_borrow_return);
		mBorrowReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				new AsyncLoader().execute(mCurrentBookState);
			}
		});

		new AsyncLoader().execute(LOAD_BOOK_INFO);

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

	private void setContentView(Book book) {
		mTitleView.setText(book.getTitle());

		String author = book.getAuthor();
		mAuthorView.setText(author);

		String publisher = book.getPublisher();
		mPublisherView.setText(publisher);

		String publishDate = book.getPublishDate();
		mPublishDateView.setText(publishDate);

		String isbn = book.getISBN();
		mIsbnView.setText(isbn);

		String bookDesc = book.getDescription();
		if (bookDesc != null && !TextUtils.isEmpty(bookDesc))
			mDescriptionView.setText(bookDesc);
		else
			mDescriptionBlock.setVisibility(View.GONE);

		String imgUrl = book.getImgUrl();
		if (imgUrl != null)
			mImageFetcher.loadImage(imgUrl, mImageView, R.drawable.book);

		mTagView.setText(book.getTag());
	}

	private void setBorrowReturnState() {
		if (mCurrentBookState == BORROW_BOOK) {
			mBorrowReturn.setText(R.string.borrow_this_book);

			mStatusView.setVisibility(View.GONE);
		} else if (mCurrentBookState == CANNOT_OPERATE) {
			mBorrowReturn.setVisibility(View.GONE);

			mStatusView.setVisibility(View.VISIBLE);
			mStatusView.setTextColor(getResources().getColor(
					R.color.body_text_disabled));
			mStatusView.setText(String.format(
					getResources().getString(R.string.lent_to_others),
					bookBorrowInfo.getUserName()));
		} else if (mCurrentBookState == RETURN_BOOK) {
			mBorrowReturn.setText(R.string.return_this_book);

			if (bookBorrowInfo != null) {
				mStatusView.setVisibility(View.VISIBLE);
			
				String statusText = String.format(
						getResources().getString(R.string.book_due_date),
						bookBorrowInfo.getPlanReturnDate());
				int statusColor = getResources().getColor(
						R.color.body_text_1_positive);

				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date date = null;
				try {
					date = df.parse(bookBorrowInfo.getPlanReturnDate());

					Date currentDate = new Date();
					long diffDays = (date.getTime() - currentDate.getTime())
							/ (24 * 60 * 60 * 1000);
					if (diffDays < 0) {
						statusText = String.format(
								getResources().getString(
										R.string.book_expired_date), -diffDays);
						statusColor = getResources().getColor(
								R.color.body_text_1_negative);
					} else if (diffDays <= 5) {
						statusText = String
								.format(getResources().getString(
										R.string.book_remaining_date), diffDays);
						statusColor = getResources().getColor(
								R.color.body_text_1_middle);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}

				mStatusView.setText(statusText);
				mStatusView.setTextColor(statusColor);
			}
		}
	}

	private class AsyncLoader extends AsyncTask<Integer, Void, Boolean> {
		private int borrowResult;

		@Override
		protected Boolean doInBackground(Integer... params) {
			int type = params[0];
			try {
				switch (type) {
				case LOAD_BORROW_RETURN:
					bookBorrowInfo = HttpManager.webServiceBorrowProxy
							.checkWhetherBookInBorrow(book.getTag());
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (bookBorrowInfo == null) {
								mCurrentBookState = BORROW_BOOK;
							} else if (bookBorrowInfo.getUserName().equalsIgnoreCase(
									mUserId)) {
								mCurrentBookState = RETURN_BOOK;
							} else {
								mCurrentBookState = CANNOT_OPERATE;
							}
							setBorrowReturnState();
						}
					});
					break;
				case BORROW_BOOK:
					borrowResult = HttpManager.webServiceBorrowProxy.borrow(
							mUserId, book.getTag());
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (borrowResult == WebServiceInfo.OPERATION_SUCCEED) {
								mCurrentBookState = RETURN_BOOK;
								setBorrowReturnState();
								Toast.makeText(
										getActivity(),
										getActivity().getString(
												R.string.operation_succeed),
										Toast.LENGTH_SHORT).show();
							} else if (borrowResult == WebServiceInfo.BORROWED_BOOK_EXCCEED_3) {
								Toast.makeText(
										getActivity(),
										getActivity().getString(
												R.string.borrowed_book_excceed_3),
										Toast.LENGTH_LONG).show();
							}
						}
					});
					break;
				case RETURN_BOOK:
					borrowResult = HttpManager.webServiceBorrowProxy
							.returnBook(mUserId, book.getTag());
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (borrowResult == WebServiceInfo.OPERATION_SUCCEED) {
								mCurrentBookState = BORROW_BOOK;
								setBorrowReturnState();
								Toast.makeText(
										getActivity(),
										getActivity().getString(
												R.string.operation_succeed),
										Toast.LENGTH_SHORT).show();
							}
						}
					});
					break;
				case LOAD_BOOK_INFO:
					book = HttpManager.webServiceBookProxy
							.getBookByBianHao(Books.getBookId(mBookUri));
					if (book == null)
						return false;

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
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mBorrowReturn.setEnabled(false);
			mLoadingIndicator.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {

			} else {
				Toast.makeText(getActivity(),
						getActivity().getString(R.string.load_failed),
						Toast.LENGTH_SHORT).show();
			}
			mBorrowReturn.setEnabled(true);
			mLoadingIndicator.setVisibility(View.GONE);
		}
	}
}