package com.gtcc.library.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.gtcc.library.R;
import com.gtcc.library.entity.Book;
import com.gtcc.library.entity.Borrow;
import com.gtcc.library.provider.LibraryContract;
import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.util.ImageFetcher;
import com.gtcc.library.util.LogUtils;
import com.gtcc.library.util.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>{
	private static final String TAG = LogUtils
			.makeLogTag(BookDetailFragment.class);

	private final int ADD_REVIEW = 0;

	private ViewGroup mRootView;
	private ViewGroup mDescriptionBlock;

	private TextView mTitleView;
	private TextView mAuthorView;
	private TextView mPublisherView;
	private TextView mPublishDateView;
	private TextView mIsbnView;
	private TextView mDescriptionView;
	private ImageView mImageView;
	private TextView mTagView;
	private TextView mStatusView;
	private Button mBorrowReturnButton;

	private Bundle mArguments;
	private Book mBook;
	private Borrow mBookBorrowInfo;

	private ImageFetcher mImageFetcher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mImageFetcher = Utils.getImageFetcher(getActivity());
		mImageFetcher.setImageFadeIn(false);

		setHasOptionsMenu(true);
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
		mBorrowReturnButton = (Button) mRootView
				.findViewById(R.id.action_borrow_return);
		mBorrowReturnButton.setEnabled(false);

		return mRootView;
	}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reloadFromArguments(getArguments());
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
            intent.putExtra(BookCommentActivity.USER_ID, ((BaseActivity) getActivity()).getUserId());
            intent.putExtra(BookCommentActivity.BOOK_ID, mBook.getObjectId());
            intent.putExtra(BookCommentActivity.BOOK_TITLE, mBook.getTitle());
            getActivity().startActivityForResult(intent, ADD_REVIEW);
        }
        return super.onOptionsItemSelected(item);
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

    public void reloadFromArguments(Bundle arguments) {
        final Intent intent = ((BaseActivity)getActivity())
                .fragmentArgumentsToIntent(getArguments());
        final Uri uri = intent.getData();
        if (uri == null)
            return;

        mArguments = arguments;
        reloadBooksData();
    }

    public void reloadBooksData() {
        getLoaderManager().restartLoader(BookDetailQuery._TOKEN, mArguments, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final Intent intent = BaseActivity.fragmentArgumentsToIntent(mArguments);
        final Uri uri = intent.getData();
        return new CursorLoader(getActivity(), uri, BookDetailQuery.PROJECTION,
                null, null, Books.DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (getActivity() == null)
            return;

        if (data != null && data.moveToNext()) {
            mBook = new Book();
            mBook.setObjectId(data.getString(BookDetailQuery.BOOK_ID));
            mBook.setTag(data.getString(BookDetailQuery.BOOK_TAG));
            mBook.setTitle(data.getString(BookDetailQuery.BOOK_TITLE));
            mBook.setAuthor(data.getString(BookDetailQuery.BOOK_AUTHOR));
            mBook.setDescription(data.getString(BookDetailQuery.BOOK_DESCRIPTION));
            mBook.setImageUrl(data.getString(BookDetailQuery.BOOK_IMAGE_URL));
            mBook.setPrice(data.getString(BookDetailQuery.BOOK_PRICE));
            mBook.setIsbn(data.getString(BookDetailQuery.BOOK_ISBN));
            mBook.setPublisher(data.getString(BookDetailQuery.BOOK_PUBLISHER));
            mBook.setPublishedDate(data.getString(BookDetailQuery.BOOK_PUBLISH_DATE));
            mBook.setPrintLength(data.getInt(BookDetailQuery.BOOK_PRINT_LENGTH));
            mBook.setCategory(data.getString(BookDetailQuery.BOOK_CATEGORY));

            setContentView(mBook);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setContentView(Book book) {
		mTitleView.setText(book.getTitle());

		String author = book.getAuthor();
		mAuthorView.setText(author);

		String publisher = book.getPublisher();
		mPublisherView.setText(publisher);

		String publishDate = book.getPublishedDate();
		mPublishDateView.setText(publishDate);

		String isbn = book.getIsbn();
		mIsbnView.setText(isbn);

		String bookDesc = book.getDescription();
		if (bookDesc != null && !TextUtils.isEmpty(bookDesc))
			mDescriptionView.setText(bookDesc);
		else
			mDescriptionBlock.setVisibility(View.GONE);

		String imgUrl = book.getImageUrl();
		if (imgUrl != null)
			mImageFetcher.loadImage(imgUrl, mImageView, R.drawable.book);

		mTagView.setText(book.getTag());

        getBorrowReturnState();
	}

    private void getBorrowReturnState() {
        AVQuery<AVObject> query = new AVQuery<>("BorrowHistory");
        query.whereEqualTo("bookTag", mBook.getTag());
        query.whereEqualTo("realReturnDate", "-1");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
                if (e != null) {
                    Log.e(TAG, "Failed to get book borrow info.", e);
                } else {
                    if (avObjects.size() == 1) {
                        AVObject obj = avObjects.get(0);
                        newBorrowInfo(obj);
                    }

                    setBorrowReturnState();
                }
            }
        });
    }

    private void newBorrowInfo(AVObject obj) {
        mBookBorrowInfo = new Borrow();
        mBookBorrowInfo.setBook(mBook);
        mBookBorrowInfo.setObjectId(obj.getObjectId());
        mBookBorrowInfo.setStartBorrowDate(obj.getString("startBorrowDate"));
        mBookBorrowInfo.setPlanReturnDate(obj.getString("planReturnDate"));
        mBookBorrowInfo.setRealReturnDate(obj.getString("realReturnDate"));
        mBookBorrowInfo.setUsername((obj.getString("username")));
    }

	private void setBorrowReturnState() {
        if (mBookBorrowInfo == null) {
			mBorrowReturnButton.setText(R.string.borrow_this_book);
            mBorrowReturnButton.setEnabled(true);
            mBorrowReturnButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    borrowBook();
                }
            });
			mStatusView.setVisibility(View.GONE);
		} else if (mBookBorrowInfo.getUsername().equalsIgnoreCase(
                ((BaseActivity)getActivity()).getUserId())) {
            mBorrowReturnButton.setText(R.string.return_this_book);
            mBorrowReturnButton.setEnabled(true);
            mBorrowReturnButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    returnBook();
                }
            });
            mStatusView.setVisibility(View.VISIBLE);

            String statusText = String.format(
                    getResources().getString(R.string.book_due_date),
                    mBookBorrowInfo.getPlanReturnDate());
            int statusColor = getResources().getColor(
                    R.color.body_text_1_positive);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = df.parse(mBookBorrowInfo.getPlanReturnDate());

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
		} else {
            mBorrowReturnButton.setVisibility(View.GONE);
            mStatusView.setVisibility(View.VISIBLE);
            mStatusView.setTextColor(getResources().getColor(
                    R.color.body_text_disabled));
            mStatusView.setText(String.format(
                    getResources().getString(R.string.lent_to_others),
                    mBookBorrowInfo.getUsername()));
		}
	}

    private void borrowBook() {
        if (mBookBorrowInfo != null)
            return;

        final AVObject obj = new AVObject("BorrowHistory");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar startBorrowDate = Calendar.getInstance();
        Calendar planReturnDate = startBorrowDate;
        planReturnDate.add(Calendar.MONTH, 1);

        obj.put("username", ((BaseActivity)getActivity()).getUserId());
        obj.put("startBorrowDate", dateFormat.format(startBorrowDate.getTime()));
        obj.put("planReturnDate", dateFormat.format(planReturnDate.getTime()));
        obj.put("realReturnDate", "-1");

        obj.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    Log.e(TAG, "Failed to borrow book.", e);
                } else {
                    newBorrowInfo(obj);
                    setBorrowReturnState();
                }
            }
        });
    }

    private void returnBook() {
        if (mBookBorrowInfo == null)
            return;

        AVQuery<AVObject> query = new AVQuery<>("BorrowHistory");
        query.getInBackground(mBookBorrowInfo.getObjectId(), new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e != null) {
                    Log.e(TAG, "Failed to get borrow info.", e);
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar realReturnDate = Calendar.getInstance();
                    avObject.put("realReturnDate", dateFormat.format(realReturnDate.getTime()));
                    avObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e != null) {
                                Log.e(TAG, "Failed to return book.", e);
                            } else {
                                mBookBorrowInfo = null;
                                setBorrowReturnState();
                            }
                        }
                    });
                }
            }
        });
    }

    public interface BookDetailQuery {
        int _TOKEN = 1;

        String[] PROJECTION = {
                BaseColumns._ID,
                LibraryContract.BookColumns.BOOK_ID,
                LibraryContract.BookColumns.BOOK_TAG,
                LibraryContract.BookColumns.BOOK_TITLE,
                LibraryContract.BookColumns.BOOK_AUTHOR,
                LibraryContract.BookColumns.BOOK_DESCRIPTION,
                LibraryContract.BookColumns.BOOK_PUBLISHER,
                LibraryContract.BookColumns.BOOK_PUBLISH_DATE,
                LibraryContract.BookColumns.BOOK_PRICE,
                LibraryContract.BookColumns.BOOK_ISBN,
                LibraryContract.BookColumns.BOOK_IMAGE_URL,
                LibraryContract.BookColumns.BOOK_PRINT_LENGTH,
                LibraryContract.BookColumns.BOOK_CATEGORY,
        };

        int _ID = 0;
        int BOOK_ID = 1;
        int BOOK_TAG = 2;
        int BOOK_TITLE = 3;
        int BOOK_AUTHOR = 4;
        int BOOK_DESCRIPTION = 5;
        int BOOK_PUBLISHER = 6;
        int BOOK_PUBLISH_DATE = 7;
        int BOOK_PRICE = 8;
        int BOOK_ISBN = 9;
        int BOOK_IMAGE_URL = 10;
        int BOOK_PRINT_LENGTH = 11;
        int BOOK_CATEGORY = 12;
    }
}