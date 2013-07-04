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
import com.gtcc.library.ui.BookDetailFragment;
import com.gtcc.library.ui.HomeActivity;
import com.gtcc.library.util.ImageCache.ImageCacheParams;
import com.gtcc.library.util.ImageFetcher;
import com.gtcc.library.util.ImageWorker;
import com.gtcc.library.util.Utils;

/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
public class UserBookListFragment extends ListFragment implements 
	LoaderManager.LoaderCallbacks<Cursor> {
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	private static final String IMAGE_CACHE_DIR = "images";
	private int section;

	private UserBookListAdapter mAdapter;
	private ImageFetcher mImageFetcher;
	
	private int mImageWidth;
	private int mImageHeight;
	
    public interface Callbacks {
        public boolean OnBookSelected(String bookId);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public boolean OnBookSelected(String bookId) {
            return true;
        }
    };

    private Callbacks mCallbacks = sDummyCallbacks;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		mAdapter = new UserBookListAdapter(getActivity());
		setListAdapter(mAdapter);
		
		mImageWidth = getResources().getDimensionPixelSize(R.dimen.image_width);
		mImageHeight = getResources().getDimensionPixelSize(R.dimen.image_height);
		
        ImageCacheParams cacheParams = new ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
		
        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), mImageWidth, mImageHeight);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.book_list, container, false);
		
		ListView listView = (ListView) rootView.findViewById(android.R.id.list);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    mImageFetcher.setPauseWork(true);
                } else {
                    mImageFetcher.setPauseWork(false);
                }
			}
		});
		
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
		final Cursor cursor = (Cursor) mAdapter.getItem(position);
		String bookId = cursor.getString(BookQuery.BOOK_ID);
		mCallbacks.OnBookSelected(bookId);
	}
	
    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
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
        if (!(activity instanceof Callbacks)) {
            throw new ClassCastException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
        getActivity().getContentResolver().registerContentObserver(
                Users.CONTENT_URI, true, mObserver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
        getActivity().getContentResolver().unregisterContentObserver(mObserver);
    }
	
	@SuppressLint("NewApi")
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
			
			viewHolder.category.setText("Technical");
			// viewHolder.stars.setText("10");
			// viewHolder.comments.setText("3");

			String imgUrl = cursor.getString(UserBookListFragment.BookQuery.BOOK_IMAGE_URL);
			mImageFetcher.loadImage(imgUrl, viewHolder.image);
//			mImageFetcher.loadImage(imgUrl, viewHolder.image, R.drawable.book);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(R.layout.book_item, null);
			
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.title = (TextView) view.findViewById(R.id.book_title);
			viewHolder.author = (TextView) view.findViewById(R.id.book_author);
			viewHolder.image = (ImageView) view.findViewById(R.id.book_img);
			viewHolder.category = (TextView) view.findViewById(R.id.book_category);
			// viewHolder.stars = (TextView) view.findViewById(R.id.book_stars);
			// viewHolder.comments = (TextView)
			// view.findViewById(R.id.book_comments);

			// TypefaceUtils.setOcticons((TextView) view
			// .findViewById(R.id.icon_star));
			// TypefaceUtils.setOcticons((TextView) view
			// .findViewById(R.id.icon_comment));

			// FangzTypefaceUtils.setTypeface(viewHolder.title);
			// FangzTypefaceUtils.setTypeface(viewHolder.author);
			// FangzTypefaceUtils.setTypeface(viewHolder.category);

			view.setTag(viewHolder);

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

			TextView category;
			TextView stars;
			TextView comments;
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