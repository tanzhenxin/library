package com.gtcc.library.ui.library;

import java.io.IOException;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.entity.Book;
import com.gtcc.library.ui.BookListFragment;
import com.gtcc.library.ui.HomeActivity;
import com.gtcc.library.util.HttpManager;
import com.gtcc.library.util.LogUtils;

/**
 * A dummy fragment representing a section of the app, but that simply displays
 * dummy text.
 */
public class LibraryBookListFragment extends BookListFragment {
	public static final String TAG = LogUtils
			.makeLogTag(LibraryBookListFragment.class);

	private List<Book> books;
	private ViewGroup mLoadingIndicator;
	
	private AsyncLoader mAsyncLoader;

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
		View rootView = super.onCreateView(inflater, container, savedInstanceState);
		mLoadingIndicator = (ViewGroup) rootView.findViewById(R.id.loading_progress);
		
		reloadFromArguments(getArguments());
		
		return rootView;
	}
	
	public void reloadFromArguments(Bundle arguments) {
		if (arguments == null) {
			return;
		}
		
		if (mAsyncLoader != null) {
			mAsyncLoader.cancel(true);
		}
		mAsyncLoader = new AsyncLoader();
		
		switch (section) {
		case HomeActivity.TAB_0:
			mAsyncLoader.execute(newBooksLoader);
			break;
		case HomeActivity.TAB_1:
		case HomeActivity.TAB_2:
			break;
		case -1:
			String query = arguments.getString(SearchManager.QUERY);
			mAsyncLoader.execute(newBooksLoader);
			break;
		}
	}

	@Override
	protected String getSelectedBookId(ListView l, View v, int position, long id) {
		Book book = books.get(position);
		return book.getId();
	}
	
	@Override
	protected int getPage() {
		return HomeActivity.PAGE_LIBRARY;
	}

	private interface Loader {
		List<Book> loadBooks() throws IOException;
	}
	
	private Loader newBooksLoader = new Loader() {

		@Override
		public List<Book> loadBooks() throws IOException {
			return HttpManager.getDoubanNewBooks();
		}
	
	};
	
	@Override
	public void onDetach() {
		super.onDetach();
		
		if (mAsyncLoader != null && mAsyncLoader.getStatus() != Status.FINISHED) {
			mAsyncLoader.cancel(true);
		}
	}

	private class AsyncLoader extends AsyncTask<Loader, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Loader... params) {
			Loader loader = params[0];
			try {
				books = loader.loadBooks();
				return true;
			} catch (IOException e) {
				LogUtils.LOGE(TAG, "Unable to load books.");
			}
			return false;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoadingIndicator.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				if (getActivity() == null || isCancelled()) {
					return;
				}
				
				setListAdapter(new LibraryBookListAdapter(getActivity(), books));
			} else {
				Toast.makeText(getActivity(),
						getActivity().getString(R.string.load_failed),
						Toast.LENGTH_SHORT).show();
			}
			
			mLoadingIndicator.setVisibility(View.GONE);
		}
	}

	public class LibraryBookListAdapter extends BaseAdapter {
		private List<Book> books;
		private LayoutInflater mInflater;

		public LibraryBookListAdapter(Context context, List<Book> books) {
			this.books = books;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return books.size();
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
			ViewHolder viewHolder = new ViewHolder();

			if (view == null) {
				view = mInflater.inflate(R.layout.list_item_book, null);

				final TextView title = (TextView) view
						.findViewById(R.id.book_title);
				final TextView author = (TextView) view
						.findViewById(R.id.book_author);
				final ImageView image = (ImageView) view
						.findViewById(R.id.book_img);
				final ImageView like = (ImageView) view
						.findViewById(R.id.book_like);
				final TextView likeCount = (TextView) view
						.findViewById(R.id.book_like_count);

				OnClickListener onclickListener = new OnClickListener() {
					private boolean clicked = false;

					public void onClick(View v) {
						if (!clicked) {
							likeCount.setText(" 1");
							like.setImageResource(R.drawable.ic_like);
						} else {
							likeCount.setText("+1");
							like.setImageResource(R.drawable.ic_unlike);
						}

						like.startAnimation(mApplaudAnimation);
						clicked = !clicked;
					}
				};
				like.setOnClickListener(onclickListener);
				likeCount.setOnClickListener(onclickListener);

				if (i % 2 != 0)
					view.setBackgroundResource(R.drawable.book_list_item_odd_bg);
				else
					view.setBackgroundResource(R.drawable.book_list_item_even_bg);

				viewHolder.title = title;
				viewHolder.author = author;
				viewHolder.image = image;
				viewHolder.like = like;
				viewHolder.likeCount = likeCount;

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			Book book = books.get(i);
			String title = book.getTitle();
			viewHolder.title.setText(title);

			String author = book.getAuthor();
			viewHolder.author.setText(author);

			String imgUrl = book.getImgUrl();
			mImageFetcher.loadImage(imgUrl, viewHolder.image);

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
}