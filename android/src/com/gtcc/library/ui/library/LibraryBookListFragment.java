package com.gtcc.library.ui.library;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gtcc.library.R;
import com.gtcc.library.entity.Book;
import com.gtcc.library.entity.BookCollection;
import com.gtcc.library.ui.BookListFragment;
import com.gtcc.library.ui.HomeActivity;
import com.gtcc.library.ui.customcontrol.RefreshableListView;
import com.gtcc.library.util.HttpManager;
import com.gtcc.library.util.LogUtils;
import com.gtcc.library.webserviceproxy.WebServiceInfo;

/**
 * A dummy fragment representing a section of the app, but that simply displays
 * dummy text.
 */
public class LibraryBookListFragment extends BookListFragment {
	public static final String TAG = LogUtils
			.makeLogTag(LibraryBookListFragment.class);

	private List<Book> books;
	private ViewGroup mLoadingIndicator;
    private RefreshableListView listView;
	
	private AsyncLoader mAsyncLoader;
	
	private SearchBooksLoader mSearchBooksLoader;

    private LibraryBookListAdapter bookListAdapter;

	public LibraryBookListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

        bookListAdapter = new LibraryBookListAdapter(getActivity());
        setListAdapter(bookListAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = super.onCreateView(inflater, container, savedInstanceState);
		mLoadingIndicator = (ViewGroup) rootView.findViewById(R.id.loading_progress);

        listView = (RefreshableListView) rootView.findViewById(android.R.id.list);
        listView.setOnRefreshListener(new RefreshableListView.OnRefreshListener() {
            @Override
            public void onRefreshHeader() {
                /*bookListAdapter.clear();
                bookListAdapter.notifyDataSetChanged();
                reloadFromArguments(getArguments());*/
            }

            @Override
            public void onRefreshFooter(){
                reloadFromArguments(getArguments());
            }
        });
		
		listView.onRefreshFooter();
		
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
			mSearchBooksLoader = new SearchBooksLoader(query);
			mAsyncLoader.execute(mSearchBooksLoader);
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
	
	@Override
	public void onDetach() {
		super.onDetach();
		
		if (mAsyncLoader != null && mAsyncLoader.getStatus() != Status.FINISHED) {
			mAsyncLoader.cancel(true);
		}
	}
	
	private interface Loader {
		List<Book> loadBooks() throws Exception;
	}
	
	private Loader newBooksLoader = new Loader() {

		@Override
		public List<Book> loadBooks() throws Exception {
			return HttpManager.webServiceBookProxy.getAllBooksInList(bookListAdapter.getCount(), WebServiceInfo.LOAD_CAPACITY);
		}
	
	};
	
	class SearchBooksLoader implements Loader {
		
		String query;
		BookCollection loader;
		
		public SearchBooksLoader(String query) {
			this.query = query;
			loader = new BookCollection();
		}

		@Override
		public List<Book> loadBooks() throws IOException {
			if (loader.hasMoreBooks()) {
				return loader.searchBooks(query);
			}
			return null;
		}
	}

	private class AsyncLoader extends AsyncTask<Loader, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Loader... params) {
			Loader loader = params[0];
			try {
				books = loader.loadBooks();
				return true;
			} catch (Exception e) {
				LogUtils.LOGE(TAG, "Unable to load books. Error: " + e.getMessage());
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
                listView.onRefreshComplete(true);
				if (getActivity() == null || isCancelled() || books == null) {
					return;
				}
				
				bookListAdapter.addBooks(books);
			} else {
                listView.onRefreshComplete(false);
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

		public LibraryBookListAdapter(Context context) {
            books = new ArrayList<Book>();
			mInflater = LayoutInflater.from(context);
		}

        public void addBooks(List<Book> newBooks){
            this.books.addAll(newBooks);
        }

        public void clear(){
            this.books.clear();
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
			if (imgUrl != null)
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