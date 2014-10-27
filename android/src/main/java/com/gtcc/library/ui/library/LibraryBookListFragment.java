package com.gtcc.library.ui.library;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gtcc.library.R;
import com.gtcc.library.provider.LibraryContract;
import com.gtcc.library.ui.AbstractBookListFragment;
import com.gtcc.library.ui.BaseActivity;
import com.gtcc.library.ui.HomeActivity;
import com.gtcc.library.util.LogUtils;

public class LibraryBookListFragment extends AbstractBookListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	public static final String TAG = LogUtils
			.makeLogTag(LibraryBookListFragment.class);

	private ViewGroup mLoadingIndicator;
	private ListView listView;
	private TextView mTextView;

	private CursorAdapter mAdapter;
	private int mBookQueryToken;

	public LibraryBookListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = super.onCreateView(inflater, container,
				savedInstanceState);

		mLoadingIndicator = (ViewGroup) rootView
				.findViewById(R.id.loading_progress);
		mTextView = (TextView) rootView.findViewById(android.R.id.text1);
		listView = (ListView) rootView.findViewById(android.R.id.list);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		reloadFromArguments(getArguments());
	}

	public void reloadFromArguments(Bundle arguments) {
		setListAdapter(null);

		final Intent intent = BaseActivity
				.fragmentArgumentsToIntent(arguments);
		final Uri uri = intent.getData();

		if (uri == null) {
			return;
		}
		
		mAdapter = new BooksAdapter(getActivity());
		setListAdapter(mAdapter);

		getLoaderManager().restartLoader(BooksQuery._TOKEN, arguments, this);
	}

	@Override
	protected String getSelectedBookId(ListView l, View v, int position, long id) {
		Cursor cursor = (Cursor) mAdapter.getItem(position);
		return cursor.getString(cursor
				.getColumnIndex(LibraryContract.BookColumns.BOOK_ID));
	}

	@Override
	protected int getPage() {
		return HomeActivity.PAGE_LIBRARY;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		final Intent intent = BaseActivity.fragmentArgumentsToIntent(data);
		final Uri uri = intent.getData();
		
		return new CursorLoader(getActivity(), uri, BooksQuery.PROJECTION, null, null, LibraryContract.Books.DEFAULT_SORT_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (getActivity() == null) {
			return;
		}
		
		mAdapter.changeCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	public class BooksAdapter extends CursorAdapter {
		public BooksAdapter(Context context) {
			super(context, null, false);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return getActivity().getLayoutInflater().inflate(
					R.layout.list_item_book, parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder viewHolder = new ViewHolder();

			if (view.getTag() == null) {
				final TextView title = (TextView) view
						.findViewById(R.id.book_title);
				final TextView author = (TextView) view
						.findViewById(R.id.book_author);
				final ImageView image = (ImageView) view
						.findViewById(R.id.book_img);
				final TextView tag = (TextView) view
						.findViewById(R.id.book_tag);

				if (cursor.getPosition() % 2 != 0)
					view.setBackgroundResource(R.drawable.book_list_item_odd_bg);
				else
					view.setBackgroundResource(R.drawable.book_list_item_even_bg);

				viewHolder.title = title;
				viewHolder.author = author;
				viewHolder.image = image;
				viewHolder.tag = tag;

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			String title = cursor.getString(BooksQuery.BOOK_TITLE);
			viewHolder.title.setText(title);

			String author = cursor.getString(BooksQuery.BOOK_AUTHOR);
			String publisher = cursor.getString(BooksQuery.BOOK_PUBLISHER);
			String publishDate = cursor.getString(BooksQuery.BOOK_PUBLISH_DATE);
			String price = cursor.getString(BooksQuery.BOOK_PRICE);
			if (author != null && !TextUtils.isEmpty(author)) {
				if (publisher != null && !TextUtils.isEmpty(publisher)) {
					author += " / " + publisher;
				}

				if (publishDate != null && !TextUtils.isEmpty(publishDate)) {
					author += " / " + publishDate;
				}

				if (price != null && !TextUtils.isEmpty(price)) {
					author += " / " + price;
				}
			}
			viewHolder.author.setText(author);

			String imgUrl = cursor.getString(BooksQuery.BOOK_IMAGE_URL);
			if (imgUrl != null)
				mImageFetcher.loadImage(imgUrl, viewHolder.image);

			String tag = cursor.getString(BooksQuery.BOOK_ID);
			viewHolder.tag.setText(tag);
		}

		class ViewHolder {
			TextView title;
			TextView author;
			ImageView image;
			TextView tag;
		}
	}

	public interface BooksQuery {
		int _TOKEN = 0x1;

		String[] PROJECTION = { BaseColumns._ID,
				LibraryContract.BookColumns.BOOK_ID,
				LibraryContract.BookColumns.BOOK_TITLE,
				LibraryContract.BookColumns.BOOK_AUTHOR,
				LibraryContract.BookColumns.BOOK_PUBLISHER,
				LibraryContract.BookColumns.BOOK_PUBLISH_DATE,
				LibraryContract.BookColumns.BOOK_PRICE,
				LibraryContract.BookColumns.BOOK_IMAGE_URL };

		int _ID = 0;
		int BOOK_ID = 1;
		int BOOK_TITLE = 2;
		int BOOK_AUTHOR = 3;
		int BOOK_PUBLISHER = 4;
		int BOOK_PUBLISH_DATE = 5;
		int BOOK_PRICE = 6;
		int BOOK_IMAGE_URL = 7;
	}
}