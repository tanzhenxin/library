package com.gtcc.library.ui.library;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
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
		
		startLoad();
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.books_list_menu, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
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
	
	private void startLoad() {
		switch (section) {
		case HomeActivity.TAB_0:
			new AsyncLoader().execute(newBooksLoader);
			break;
		default:
			break;
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
		protected void onPostExecute(Boolean result) {
			if (result) {
				setListAdapter(new LibraryBookListAdapter(getActivity(), books));
			} else {
				Toast.makeText(getActivity(),
						getActivity().getString(R.string.load_failed),
						Toast.LENGTH_SHORT).show();
			}
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