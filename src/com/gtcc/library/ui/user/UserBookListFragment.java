package com.gtcc.library.ui.user;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gtcc.library.R;
import com.gtcc.library.entity.Book;
import com.gtcc.library.entity.UserInfo;
import com.gtcc.library.provider.LibraryContract.Users;
import com.gtcc.library.provider.LibraryDatabase.UserBooks;
import com.gtcc.library.ui.BookViewActivity;
import com.gtcc.library.ui.MainActivity;
import com.gtcc.library.util.HttpManager;

/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
public class UserBookListFragment extends ListFragment implements 
	LoaderManager.LoaderCallbacks<Cursor> {
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	private int section;
	
	private static final String[] PROJECTION = new String[] {
        NotePad.Notes._ID, // 0
        NotePad.Notes.COLUMN_NAME_TITLE, // 1
	};

	private List<Book> books;
	
	private TextView mTextView;
	private LoadBooksAsyncTask asyncLoadTask;

	public UserBookListFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		getLoaderManager().initLoader(0, getArguments(), this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.book_list, container, false);
		
		section = getArguments().getInt(ARG_SECTION_NUMBER);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		asyncLoadTask= new LoadBooksAsyncTask();
		asyncLoadTask.execute();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent detailIntent = new Intent(getActivity(), BookViewActivity.class);
		Book subject = books.get(position);
		detailIntent.putExtra("subject", subject);
		startActivity(detailIntent);
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDestroy() {
		if (asyncLoadTask != null) {
			asyncLoadTask.cancel(true);
		}
		
		super.onDestroy();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		MainActivity activity = (MainActivity) getActivity();
		reutrn new CursorLoader(
				getActivity(), 
				Users.buildUserBooksUri(activity.getCurrentUserId(), getStatus()), 
				projection, 
				selection, 
				selectionArgs, 
				sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
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

	private class LoadBooksAsyncTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				MainActivity activity = (MainActivity) getActivity();
				HttpManager httpManager = new HttpManager(activity.getAccessToken());
				UserInfo userInfo = httpManager.getUserInfo();
				books = httpManager.getStaredBooks(userInfo.GetUserId(), UserBookListFragment.this.getStatus());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (isCancelled())
				return;
			
			if (result) {
				
				setListAdapter(new UserBookListAdapter(getActivity(),
						getListView(), books));
			} else {
				Toast.makeText(getActivity(), R.string.load_failed,
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}