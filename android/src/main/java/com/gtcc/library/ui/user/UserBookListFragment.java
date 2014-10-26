package com.gtcc.library.ui.user;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.gtcc.library.R;
import com.gtcc.library.entity.Book;
import com.gtcc.library.entity.Borrow;
import com.gtcc.library.ui.AbstractBookListFragment;
import com.gtcc.library.ui.HomeActivity;
import com.gtcc.library.ui.customcontrol.RefreshableListView;
import com.gtcc.library.ui.library.LibraryFragment;
import com.gtcc.library.util.HttpManager;
import com.gtcc.library.webserviceproxy.WebServiceInfo;

public class UserBookListFragment extends AbstractBookListFragment {

	private List<Borrow> borrowBooks;
	private AsyncLoader mLoader;

	private ListView mListView;
	private TextView mEmptyView;
	private ViewGroup mLoadingIndicator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = super.onCreateView(inflater, container,
				savedInstanceState);

		mListView = (ListView) rootView.findViewById(android.R.id.list);
		mEmptyView = (TextView) rootView.findViewById(android.R.id.text1);
		mLoadingIndicator = (ViewGroup) rootView
				.findViewById(R.id.loading_progress);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		reloadFromArguments(getArguments());
	}

	@Override
	public void onPause() {
		super.onPause();

		if (mLoader != null && mLoader.getStatus() != Status.FINISHED) {
			mLoader.cancel(true);
		}
	}

	private void reloadFromArguments(Bundle arguments) {
		if (mLoader != null) {
			mLoader.cancel(true);
		}
		mLoader = new AsyncLoader();

		if (arguments != null) {
			final String category = arguments
					.getString(UserFragment.ARG_USER_CATEOGRY);
			if (category == WebServiceInfo.BORROW_METHOD_GET_BORROWED_INFO) {
				mEmptyView.setText(getResources().getText(
						R.string.no_book_borrowing));
				mLoader.execute(mBorrowedLoader);
			} else if (category == WebServiceInfo.BORROW_METHOD_GET_RETURNED_INFO) {
				mEmptyView.setText(getResources().getText(
						R.string.no_book_borrowed));
				mLoader.execute(mReturnedLoader);
			}
		}
	}

	@Override
	protected int getPage() {
		return HomeActivity.PAGE_USER;
	}

	@Override
	public String getSelectedBookId(ListView l, View v, int position, long id) {
		Borrow borrowHistory = borrowBooks.get(position);
		return borrowHistory.getBook().getId();
	}

	private interface BorrowLoader {
		List<Borrow> loadBooks() throws Exception;
	}

	private BorrowLoader mBorrowedLoader = new BorrowLoader() {

		@Override
		public List<Borrow> loadBooks() throws Exception {
			return HttpManager.webServiceBorrowProxy
					.getBorrowedInfo(((HomeActivity) getActivity()).getUserId());
		}

	};

	private BorrowLoader mReturnedLoader = new BorrowLoader() {

		@Override
		public List<Borrow> loadBooks() throws Exception {
			return HttpManager.webServiceBorrowProxy
					.getReturnedInfo(((HomeActivity) getActivity()).getUserId());
		}

	};

	private class AsyncLoader extends AsyncTask<BorrowLoader, Void, Boolean> {
		protected Boolean doInBackground(BorrowLoader... params) {
			BorrowLoader loader = params[0];
			try {
				borrowBooks = loader.loadBooks();
				return true;
			} catch (Exception e) {

			}
			return false;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mLoadingIndicator.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
			mEmptyView.setVisibility(View.GONE);
		}

		protected void onPostExecute(Boolean result) {
			if (!isCancelled()) {
				mLoadingIndicator.setVisibility(View.GONE);

				if (result && borrowBooks.size() > 0) {
					mListView.setVisibility(View.VISIBLE);
					mEmptyView.setVisibility(View.GONE);

					setListAdapter(new UserBorrowAdapter(getActivity(),
							borrowBooks));
				} else {
					mListView.setVisibility(View.GONE);
					mEmptyView.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	public class UserBorrowAdapter extends BaseAdapter {
		private List<Borrow> books;
		private LayoutInflater mInflater;

		public UserBorrowAdapter(Context context, List<Borrow> books) {
			if (this.books == null) {
				this.books = books;
				mInflater = LayoutInflater.from(context);
			}
		}

		@Override
		public int getCount() {
			return books.size();
		}

		public void clear() {
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
			ViewHolder viewHolder = new ViewHolder();

			if (view == null) {
				view = mInflater.inflate(R.layout.list_item_book, null);

				final TextView title = (TextView) view
						.findViewById(R.id.book_title);
				final TextView author = (TextView) view
						.findViewById(R.id.book_author);
				final ImageView image = (ImageView) view
						.findViewById(R.id.book_img);
				final TextView tag = (TextView) view
						.findViewById(R.id.book_tag);
				final TextView status = (TextView) view
						.findViewById(R.id.book_status);

				if (i % 2 != 0)
					view.setBackgroundResource(R.drawable.book_list_item_odd_bg);
				else
					view.setBackgroundResource(R.drawable.book_list_item_even_bg);

				viewHolder.title = title;
				viewHolder.author = author;
				viewHolder.image = image;
				viewHolder.tag = tag;
				viewHolder.status = status;

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			Borrow borrow = books.get(i);
			Book book = borrow.getBook();
			viewHolder.title.setText(book.getTitle());

			String imgUrl = book.getImgUrl();
			if (imgUrl != null)
				mImageFetcher.loadImage(imgUrl, viewHolder.image);

			String author = book.getAuthor();
			if (author != null && !TextUtils.isEmpty(author)) {
				String publisher = book.getPublisher();
				if (publisher != null && !TextUtils.isEmpty(publisher)) {
					author += " / " + publisher;
				}

				String publishDate = book.getPublishDate();
				if (publishDate != null && !TextUtils.isEmpty(publishDate)) {
					author += " / " + publishDate;
				}

				String price = book.getPrice();
				if (price != null && !TextUtils.isEmpty(price)) {
					author += " / " + price;
				}
			}
			viewHolder.author.setText(author);

			String tag = book.getId();
			viewHolder.tag.setText(tag);

			if (borrow.getRealReturnDate().equals("-1")) {
				viewHolder.status.setVisibility(View.VISIBLE);
				String statusText = String.format(
						getResources().getString(R.string.book_due_date),
						borrow.getPlanReturnDate());
				int statusColor = getResources().getColor(
						R.color.body_text_1_positive);

				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date date = null;
				try {
					date = df.parse(borrow.getPlanReturnDate());

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

				viewHolder.status.setText(statusText);
				viewHolder.status.setTextColor(statusColor);
			}

			if (i % 2 != 0)
				view.setBackgroundResource(R.drawable.book_list_item_odd_bg);
			else
				view.setBackgroundResource(R.drawable.book_list_item_even_bg);

			return view;
		}

		class ViewHolder {
			TextView title;
			TextView author;
			ImageView image;
			TextView tag;
			TextView status;
		}
	}
}