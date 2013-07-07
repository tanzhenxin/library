package com.gtcc.library.ui.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gtcc.library.R;
import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.provider.LibraryContract.Users;
import com.gtcc.library.provider.LibraryDatabase.UserBooks;
import com.gtcc.library.ui.BookListFragment;
import com.gtcc.library.ui.HomeActivity;
import com.gtcc.library.util.ImageCache.ImageCacheParams;
import com.gtcc.library.util.ImageFetcher;
import com.gtcc.library.util.ImageWorker;
import com.gtcc.library.util.Utils;

/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
public class UserBookListFragment extends BookListFragment implements 
	LoaderManager.LoaderCallbacks<Cursor> {
	
	private UserBookListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAdapter = new UserBookListAdapter(getActivity());
		setListAdapter(mAdapter);
		
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = super.onCreateView(inflater, container, savedInstanceState);
		getLoaderManager().initLoader(0, getArguments(), this);
		return rootView;
	}
	
	@Override
	protected int getPage() {
		return HomeActivity.PAGE_USER;
	}

	@Override
	public String getSelectedBookId(ListView l, View v, int position, long id) {
		final Cursor cursor = (Cursor) mAdapter.getItem(position);
		return cursor.getString(BookQuery.BOOK_ID);
	}
	
    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getActivity().getContentResolver().registerContentObserver(
                Users.CONTENT_URI, true, mObserver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().getContentResolver().unregisterContentObserver(mObserver);
    }
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		HomeActivity activity = (HomeActivity) getActivity();
		String userId = activity.getCurrentUserId();
		Uri uri = Users.buildUserBooksUri(userId, getStatus());
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
		case HomeActivity.TAB_0:
			return UserBooks.TYPE_READING;
		case HomeActivity.TAB_1:
			return UserBooks.TYPE_WISH;
		case HomeActivity.TAB_2:
			return UserBooks.TYPE_READ;
		default:
			throw new UnsupportedOperationException("Unknown section: " + section);
		}
	}
	
    private final ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (getActivity() == null) {
                return;
            }

            Loader<Cursor> loader = getLoaderManager().getLoader(0);
            if (loader != null) {
                loader.forceLoad();
            }
        }
    };
	
	public class UserBookListAdapter extends CursorAdapter {
		private LayoutInflater mInflater;
		

		public UserBookListAdapter(Context context) {
			super(context, null, false);
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder viewHolder = (ViewHolder) view.getTag();

			String title = cursor.getString(UserBookListFragment.BookQuery.BOOK_TITLE);
			viewHolder.title.setText(title);
			
			String author = cursor.getString(UserBookListFragment.BookQuery.BOOK_AUTHOR);
			viewHolder.author.setText(author);
			
			String imgUrl = cursor.getString(UserBookListFragment.BookQuery.BOOK_IMAGE_URL);
			mImageFetcher.loadImage(imgUrl, viewHolder.image);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(R.layout.list_item_book, null);
			
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.title = (TextView) view.findViewById(R.id.book_title);
			viewHolder.author = (TextView) view.findViewById(R.id.book_author);
			viewHolder.image = (ImageView) view.findViewById(R.id.book_img);
			viewHolder.like = (ImageView) view.findViewById(R.id.book_like);
			viewHolder.likeCount = (TextView) view.findViewById(R.id.book_like_count);

			view.setTag(viewHolder);
			
			OnClickListener onclickListener = new OnClickListener() {
				private boolean clicked = false;
			    public void onClick(View v) {
			    	if (!clicked) {
				    	viewHolder.likeCount.setText(" 1");
				    	viewHolder.like.setImageResource(R.drawable.ic_like);
			    	}
			    	else {
				    	viewHolder.likeCount.setText("+1");
				    	viewHolder.like.setImageResource(R.drawable.ic_unlike);
			    	}
			    	
			    	viewHolder.like.startAnimation(mApplaudAnimation);
			    	clicked = !clicked;
			    }
			};
			viewHolder.like.setOnClickListener(onclickListener);
			viewHolder.likeCount.setOnClickListener(onclickListener);

			if (cursor.getPosition() % 2 != 0)
				view.setBackgroundResource(R.drawable.book_list_item_odd_bg);
			else
				view.setBackgroundResource(R.drawable.book_list_item_even_bg);

			return view;
		}

		class ViewHolder {
			TextView title;
			TextView author;
			ImageView image;

			ImageView like;
			TextView likeCount;
		}
	}
	
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