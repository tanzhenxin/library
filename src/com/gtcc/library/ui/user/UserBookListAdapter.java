package com.gtcc.library.ui.user;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gtcc.library.R;
import com.gtcc.library.util.AsyncImageLoader;
import com.gtcc.library.util.AsyncImageLoader.ImageCallback;

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

	static class ViewHolder {
		TextView title;
		TextView author;
		ImageView image;

		TextView category;
		TextView stars;
		TextView comments;
	}
}