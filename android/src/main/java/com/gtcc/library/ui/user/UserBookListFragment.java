package com.gtcc.library.ui.user;

import android.content.Context;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.gtcc.library.R;
import com.gtcc.library.entity.Book;
import com.gtcc.library.entity.Borrow;
import com.gtcc.library.ui.AbstractBookListFragment;
import com.gtcc.library.ui.BaseActivity;
import com.gtcc.library.ui.HomeActivity;
import com.gtcc.library.util.HttpManager;
import com.gtcc.library.util.LogUtils;
import com.gtcc.library.webserviceproxy.WebServiceInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserBookListFragment extends AbstractBookListFragment {
    private static final String TAG =
            LogUtils.makeLogTag(UserBookListFragment.class);

	private List<Borrow> borrowBooks;

	private ListView mListView;
	private TextView mEmptyView;
	private ViewGroup mLoadingIndicator;

    private UserBorrowAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = super.onCreateView(inflater, container,
				savedInstanceState);

		mListView = (ListView) rootView.findViewById(android.R.id.list);
		mEmptyView = (TextView) rootView.findViewById(android.R.id.text1);
		mLoadingIndicator = (ViewGroup) rootView
				.findViewById(R.id.loading_progress);

        mAdapter = new UserBorrowAdapter(getActivity());
        setListAdapter(mAdapter);

        reloadFromArguments(getArguments());

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
	}

	private void reloadFromArguments(Bundle arguments) {
		if (arguments != null) {
			final String category = arguments
					.getString(UserFragment.ARG_USER_CATEOGRY);
			if (category == WebServiceInfo.BORROW_METHOD_GET_BORROWED_INFO) {
				mEmptyView.setText(getResources().getText(
                        R.string.no_book_borrowing));
                getBorrowedBooks();
			} else if (category == WebServiceInfo.BORROW_METHOD_GET_RETURNED_INFO) {
				mEmptyView.setText(getResources().getText(
                        R.string.no_book_borrowed));
                getBorrowedBooks();
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
		return borrowHistory.getBook().getObjectId();
	}

    private void getBorrowedBooks() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);

        mAdapter.clear();
        mAdapter.notifyDataSetInvalidated();

        AVQuery<AVObject> query = new AVQuery<>("BorrowHistory");
        query.whereEqualTo("username", ((BaseActivity) getActivity()).getUserId());
        query.whereEqualTo("realReturnDate", "-1");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
                if (e != null) {
                    Log.e(TAG, "Failed to get borrowed books.", e);
                } else {
                    if (avObjects.size() == 0) {
                        mLoadingIndicator.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        for (AVObject obj : avObjects) {
                            newBorrowInfo(obj);
                        }
                    }
                }
            }
        });
    }

    private void newBorrowInfo(AVObject obj) {
        final Borrow borrow = new Borrow();
        borrow.setObjectId(obj.getObjectId());
        borrow.setStartBorrowDate(obj.getString("startBorrowDate"));
        borrow.setPlanReturnDate(obj.getString("planReturnDate"));
        borrow.setRealReturnDate(obj.getString("realReturnDate"));
        borrow.setUsername((obj.getString("username")));
        borrow.setBookTag(obj.getString("bookTag"));

        AVQuery<AVObject> query = new AVQuery<>("Book");
        query.whereEqualTo("tag", borrow.getBookTag());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
                if (e != null) {
                    Log.e(TAG, "Failed to get book by tag.", e);
                } else {
                    if (avObjects.size() == 1) {
                        AVObject bookObj = avObjects.get(0);
                        Book book = new Book();
                        book.setObjectId(bookObj.getString("objectId"));
                        book.setTag(bookObj.getString("tag"));
                        book.setTitle(bookObj.getString("title"));
                        book.setAuthor(bookObj.getString("author"));
                        book.setDescription(bookObj.getString("description"));
                        book.setImageUrl(bookObj.getString("imageUrl"));
                        book.setPrice(bookObj.getString("price"));
                        book.setIsbn(bookObj.getString("ISBN"));
                        book.setPublisher(bookObj.getString("publisher"));
                        book.setPublishedDate(bookObj.getString("publishDate"));
                        book.setPrintLength(bookObj.getInt("printLength"));
                        book.setCategory(bookObj.getString(book.getTag().substring(0, 1)));
                        borrow.setBook(book);

                        mAdapter.addBooks(borrow);
                        mAdapter.notifyDataSetInvalidated();

                        mLoadingIndicator.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

	public class UserBorrowAdapter extends BaseAdapter {
		private List<Borrow> books;
		private LayoutInflater mInflater;

		public UserBorrowAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
            books = new ArrayList<>();
		}

        public void addBooks(Borrow borrow) {
            this.books.add(borrow);
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

			String imgUrl = book.getImageUrl();
			if (imgUrl != null)
				mImageFetcher.loadImage(imgUrl, viewHolder.image);

			String author = book.getAuthor();
			if (author != null && !TextUtils.isEmpty(author)) {
				String publisher = book.getPublisher();
				if (publisher != null && !TextUtils.isEmpty(publisher)) {
					author += " / " + publisher;
				}

				String publishDate = book.getPublishedDate();
				if (publishDate != null && !TextUtils.isEmpty(publishDate)) {
					author += " / " + publishDate;
				}

				String price = book.getPrice();
				if (price != null && !TextUtils.isEmpty(price)) {
					author += " / " + price;
				}
			}
			viewHolder.author.setText(author);

			String tag = book.getObjectId();
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