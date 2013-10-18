package com.gtcc.library.ui.user;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gtcc.library.R;
import com.gtcc.library.entity.Borrow;
import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.provider.LibraryContract.Users;
import com.gtcc.library.provider.LibraryDatabase.UserBooks;
import com.gtcc.library.ui.BookListFragment;
import com.gtcc.library.ui.HomeActivity;
import com.gtcc.library.util.HttpManager;

import java.util.List;

/**
 * A dummy fragment representing a section of the app, but that simply displays
 * dummy text.
 */
public class UserBookListFragment extends BookListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private UserBookListAdapter mAdapter;
    private List<Borrow> borrowBooks;
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public boolean OnBookSelected(String bookId, int page, int tab) {
            return true;
        }
    };

    private Callbacks mCallbacks = sDummyCallbacks;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        AsyncLoader loader = new AsyncLoader();
        loader.execute(borrowLoader);

        mAdapter = new UserBookListAdapter(getActivity());
        setListAdapter(mAdapter);

		setHasOptionsMenu(true);
	}

    private interface BorrowLoader {
        List<Borrow> loadBooks() throws Exception;
    }
    private BorrowLoader borrowLoader = new BorrowLoader() {

        @Override
        public List<Borrow> loadBooks() throws Exception {
            //SharedPreferences userId = ;

            return HttpManager.webServiceBorrowProxy.getBorrowInfo(((HomeActivity)getActivity()).getUserId());
            //return HttpManager.getDoubanNewBooks();
        }

    };
    private class AsyncLoader extends AsyncTask<BorrowLoader, Void, Boolean> {
        protected Boolean doInBackground(BorrowLoader... params) {
            BorrowLoader loader = params[0];
            try {
                borrowBooks = loader.loadBooks();
                //storeBooks(books);
                return true;
            } catch (Exception e) {

            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            if(result)
                setListAdapter(new UserBorrowAdapter(getActivity(), borrowBooks));
        }
    }

    public class UserBorrowAdapter extends BaseAdapter {
        private List<Borrow> books;
        private LayoutInflater mInflater;

        public UserBorrowAdapter(Context context, List<Borrow> books) {
            if(this.books == null)
            {
                this.books = books;
                mInflater = LayoutInflater.from(context);
            }
        }

        @Override
        public int getCount() {
            return books.size();
        }

        public void clear(){
            this.books.clear();
        }

        @Override
        public Object getItem(int i) {
            return books.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup vg) {

            if (view == null) {
                ViewHolder viewHolder = new ViewHolder();

                view = mInflater.inflate(R.layout.user_book_list, null);

                TextView title = (TextView) view
                        .findViewById(R.id.book_title);

                title.setText(books.get(i).getBookName());

                ImageView image = (ImageView) view
                        .findViewById(R.id.user_book_img);
                String imgUrl = books.get(i).getImgUrl();
                if (imgUrl != null)
                    mImageFetcher.loadImage(imgUrl, image);

                if (i % 2 != 0)
                    view.setBackgroundResource(R.drawable.book_list_item_odd_bg);
                else
                    view.setBackgroundResource(R.drawable.book_list_item_even_bg);
            }

            return view;
        }

        class ViewHolder {
            TextView title;
            ImageView image;
        }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = super.onCreateView(inflater, container,
				savedInstanceState);
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
		String userId = activity.getUserId();
		Uri uri = Users.buildUserBooksUri(userId);
		return new CursorLoader(getActivity(), uri, BookQuery.PROJECTION,
				UserBooks.USE_TYPE + "=?", new String[] { getStatus() },
				Books.DEFAULT_SORT_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
	}

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String bookId = getSelectedBookId(l,v, position, id);
        mCallbacks.OnBookSelected(bookId, getPage(), section);
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
			throw new UnsupportedOperationException("Unknown section: "
					+ section);
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

			String title = cursor
					.getString(UserBookListFragment.BookQuery.BOOK_TITLE);
			viewHolder.title.setText(title);

			String author = cursor
					.getString(UserBookListFragment.BookQuery.BOOK_AUTHOR);
			viewHolder.author.setText(author);

			String imgUrl = cursor
					.getString(UserBookListFragment.BookQuery.BOOK_IMAGE_URL);
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
			viewHolder.likeCount = (TextView) view
					.findViewById(R.id.book_like_count);

			view.setTag(viewHolder);

			OnClickListener onclickListener = new OnClickListener() {
				private boolean clicked = false;

				public void onClick(View v) {
					if (!clicked) {
						viewHolder.likeCount.setText(" 1");
						viewHolder.like.setImageResource(R.drawable.ic_like);
					} else {
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
		}
	}

	public interface BookQuery {
		int _TOKEN = 0;

		public final String[] PROJECTION = new String[] { Books._ID,
				Books.BOOK_ID, Books.BOOK_TITLE, Books.BOOK_AUTHOR,
				Books.BOOK_SUMMARY, Books.BOOK_IMAGE_URL, };

		public int _ID = 0;
		public int BOOK_ID = 1;
		public int BOOK_TITLE = 2;
		public int BOOK_AUTHOR = 3;
		public int BOOK_SUMMARY = 4;
		public int BOOK_IMAGE_URL = 5;
	}
}