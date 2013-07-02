package com.gtcc.library.ui.user;

import java.util.List;

import com.gtcc.library.R;
import com.gtcc.library.entity.Book;
import com.gtcc.library.util.AsyncImageLoader;
import com.gtcc.library.util.AsyncImageLoader.ImageCallback;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UserBookListAdapter extends BaseAdapter {
	private List<Book> books;
	private LayoutInflater mInflater;
	private ListView listView;
	Resources resources;

	public UserBookListAdapter(Context context, ListView listView, List<Book> books) {
		this.listView = listView;
		this.books = books;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		resources = context.getResources();
	}

	public int getCount() {
		return books.size();
	}

	public Object getItem(int i) {
		return books.get(i);
	}

	public long getItemId(int i) {
		return i;
	}

	public View getView(int i, View view, ViewGroup vg) {
		ViewHolder viewHolder = null;
		if (view == null) {
			view = mInflater.inflate(R.layout.book_item, null);
			
//	        TypefaceUtils.setOcticons((TextView) view
//	                .findViewById(R.id.icon_star));
//	        TypefaceUtils.setOcticons((TextView) view
//	                .findViewById(R.id.icon_comment));
			
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) view.findViewById(R.id.book_title);
			viewHolder.author = (TextView) view.findViewById(R.id.book_author);
			viewHolder.image = (ImageView) view.findViewById(R.id.book_img);
			viewHolder.category = (TextView) view.findViewById(R.id.book_category);
//			viewHolder.stars = (TextView) view.findViewById(R.id.book_stars);
//			viewHolder.comments = (TextView) view.findViewById(R.id.book_comments);
			
//			FangzTypefaceUtils.setTypeface(viewHolder.title);
//			FangzTypefaceUtils.setTypeface(viewHolder.author);
//			FangzTypefaceUtils.setTypeface(viewHolder.category);
			
			view.setTag(viewHolder);
			
			if (i % 2 != 0) 
				view.setBackgroundResource(R.drawable.book_list_item_odd_bg);
			else
				view.setBackgroundResource(R.drawable.book_list_item_even_bg);
		} 
		else {
			viewHolder = (ViewHolder)view.getTag();
		}
		
		Book book = books.get(i);
		

		viewHolder.title.setText(book.getTitle());
		viewHolder.author.setText(book.getDescription());
		viewHolder.category.setText("Technical");
//		viewHolder.stars.setText("10");
//		viewHolder.comments.setText("3");

		String imgUrl = book.getImgUrl();
		final ImageView imgBook = viewHolder.image;
		Drawable drawable = AsyncImageLoader.GetInstance().loadDrawable(imgUrl,
				new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {
						imgBook.setImageDrawable(imageDrawable);
					}
				});

		if (drawable != null) {
			imgBook.setImageDrawable(drawable);
		} else {
			imgBook.setImageResource(R.drawable.book);
		}

		return view;
	}
	
	static class ViewHolder {
		TextView title;
		TextView author;
		ImageView image;
		
		TextView category;
		TextView stars;
		TextView comments;
	}
}