package com.gtcc.library.ui.user;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.gtcc.library.ui.BookListFragment;
import com.gtcc.library.ui.HomeActivity;
import com.gtcc.library.ui.customcontrol.RefreshableListView;
import com.gtcc.library.util.HttpManager;

public class UserBookListFragment extends BookListFragment {

	private List<Borrow> borrowBooks;
	private AsyncLoader mLoader;
	
	private RefreshableListView mListView;
	private TextView mEmptyView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = super.onCreateView(inflater, container,
				savedInstanceState);
		mListView = (RefreshableListView) rootView.findViewById(android.R.id.list);
		mListView.setOnRefreshListener(new RefreshableListView.OnRefreshListener() {
            @Override
            public void onRefreshHeader() {
            }

            @Override
            public void onRefreshFooter(){
            	mListView.onRefreshComplete(true);
            }
        });
		mEmptyView = (TextView) rootView.findViewById(android.R.id.text1);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (((HomeActivity) getActivity()).getCurrentPage() == HomeActivity.PAGE_USER) {
			final HomeActivity activity = (HomeActivity) getActivity();
			final ActionBar actionBar = activity.getSupportActionBar();
			
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			actionBar.removeAllTabs();
		}
	}

	@Override
	protected int getPage() {
		return HomeActivity.PAGE_USER;
	}

	@Override
	public String getSelectedBookId(ListView l, View v, int position, long id) {
		Borrow borrowHistory = borrowBooks.get(position);
		return borrowHistory.getBook().getTag();
	}
	

	@Override
	public void onResume() {
		super.onResume();
		
		mLoader = new AsyncLoader();
		mLoader.execute(borrowLoader);
	}

	@Override
	public void onPause() {
		super.onPause();
		
		if (mLoader != null)
			mLoader.cancel(false);
	}
	
    private interface BorrowLoader {
        List<Borrow> loadBooks() throws Exception;
    }
    private BorrowLoader borrowLoader = new BorrowLoader() {

        @Override
        public List<Borrow> loadBooks() throws Exception {
            return HttpManager.webServiceBorrowProxy.getBorrowInfo(((HomeActivity)getActivity()).getUserId());
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

        protected void onPostExecute(Boolean result) {
            if(!isCancelled())
            {
            	if (result && borrowBooks.size() > 0) {
            		mListView.setVisibility(View.VISIBLE);
            		mEmptyView.setVisibility(View.GONE);
            		
            		setListAdapter(new UserBorrowAdapter(getActivity(), borrowBooks));
            	}
            	else {
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

				if (i % 2 != 0)
					view.setBackgroundResource(R.drawable.book_list_item_odd_bg);
				else
					view.setBackgroundResource(R.drawable.book_list_item_even_bg);

				viewHolder.title = title;
				viewHolder.author = author;
				viewHolder.image = image;
				viewHolder.tag = tag;

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			
			Book book = books.get(i).getBook();
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

			String tag = book.getTag();
			viewHolder.tag.setText(tag);
			
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
		}
    }
}