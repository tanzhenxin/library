package com.gtcc.library.ui.user;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gtcc.library.R;
import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.provider.LibraryContract.Users;
import com.gtcc.library.provider.LibraryDatabase.UserBooks;
import com.gtcc.library.ui.BookViewActivity;
import com.gtcc.library.ui.MainActivity;

/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
public class UserBookListFragment extends ListFragment implements 
	LoaderManager.LoaderCallbacks<Cursor> {
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	private int section;

	private UserBookListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		mAdapter = new UserBookListAdapter(getActivity());
		setListAdapter(mAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.book_list, container, false);
		
		section = getArguments().getInt(ARG_SECTION_NUMBER);
		getLoaderManager().initLoader(0, getArguments(), this);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		final Cursor cursor = (Cursor) mAdapter.getItem(position);
		String bookId = cursor.getString(BookQuery.BOOK_ID);
		Uri uri = Books.buildBookUri(bookId);
		
		Intent detailIntent = new Intent(getActivity(), BookViewActivity.class);
		detailIntent.putExtra("uri", bookId);
		startActivity(detailIntent);
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		MainActivity activity = (MainActivity) getActivity();
		Uri uri = Users.buildUserBooksUri(activity.getCurrentUserId(), getStatus());
		return new CursorLoader(
				getActivity(), 
				uri, 
				BookQuery.PROJECTION, 
				null, 
				null, 
				Books.DEFAULT_SORT_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}
	
	private String getStatus() {
		switch (section) {
		case 1:
			return UserBooks.TYPE_READING;
		case 2:
			return UserBooks.TYPE_READ;
		case 3:
			return UserBooks.TYPE_WISH;
		case 4:
			return UserBooks.TYPE_DONATE;
		default:
			return "";
		}
	}
	
//    private final ContentObserver mObserver = new ContentObserver(new Handler()) {
//        @Override
//        public void onChange(boolean selfChange) {
//            if (getActivity() == null) {
//                return;
//            }
//
//            Loader<Cursor> loader = getLoaderManager().getLoader(mSessionQueryToken);
//            if (loader != null) {
//                loader.forceLoad();
//            }
//        }
//    };
	
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