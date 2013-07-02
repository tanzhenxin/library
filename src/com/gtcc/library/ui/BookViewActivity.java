package com.gtcc.library.ui;

import com.gtcc.library.R;
import com.gtcc.library.entity.Book;
import com.gtcc.library.util.AsyncImageLoader;
import com.gtcc.library.util.AsyncImageLoader.ImageCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class BookViewActivity extends Activity {
	private TextView txtTitle;
	private TextView txtDescription;
	private TextView txtSummary;
	private ImageView bookImage;
	private RatingBar ratingBar;
	private ProgressDialog dialog;
	private TextView bookStatus;
	private Book book;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.book_detail);
		Bundle extras = getIntent().getExtras();
		book = extras != null ? (Book) extras.getSerializable("subject")
				: null;

		txtTitle = (TextView) findViewById(R.id.book_title);
		txtTitle.setText(book.getTitle());
		
		txtDescription = (TextView) findViewById(R.id.book_description);
		txtDescription.setText(book.getDescription());
		
		txtSummary = (TextView) findViewById(R.id.book_summary);
		txtSummary.setText(book.getSummary());
		
		bookImage = (ImageView) findViewById(R.id.book_img);
		Drawable drawable = AsyncImageLoader.GetInstance().loadDrawable(book.getImgUrl(),
				new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {
						if (bookImage != null) 
							bookImage.setImageDrawable(imageDrawable);
					}
				});

		if (drawable != null) {
			bookImage.setImageDrawable(drawable);
		} else {
			bookImage.setImageResource(R.drawable.book);
		}

		ratingBar = (RatingBar) findViewById(R.id.ratingbar);
		ratingBar.setRating(book.getRating() / 2);
		
		bookStatus = (TextView) findViewById(R.id.book_status);
		bookStatus.setText(book.getStatus());

		dialog = new ProgressDialog(this);
		TextView txtInfo = (TextView) findViewById(R.id.txtInfo);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 1) {
			Bundle extras = data.getExtras();
			String status = extras != null ? (String) extras
					.getSerializable("status") : null;
			String statusDesc = extras != null ? (String) extras
					.getSerializable("statusDesc") : null;
			Float rating = extras != null ? (Float) extras
					.getSerializable("rating") : null;

			String tags = extras != null ? (String) extras
					.getSerializable("tags") : null;

			book.setStatus(status);
			book.setMyRating(rating);
			book.setMyTags(tags);

			bookStatus.setText(statusDesc);
			ratingBar.setRating(rating);
		}
	}

}