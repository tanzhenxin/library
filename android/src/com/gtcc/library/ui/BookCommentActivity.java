package com.gtcc.library.ui;

import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.provider.LibraryContract.Comments;

public class BookCommentActivity extends SherlockActivity {

	public static final String USER_ID = "user_id";
	public static final String BOOK_ID = "book_id";
	public static final String BOOK_TITLE = "book_title";
	public static final String REPLY_AUTHOR = "reply_author";
	public static final String REPLY_COMMENT = "reply_comment";

	private EditText mEditText;

	private String mUserId;
	private String mBookId;
	private String mReplyAuthor;
	private String mReplyQuote;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);

		mUserId = getIntent().getExtras().getString(USER_ID);
		mBookId = getIntent().getExtras().getString(BOOK_ID);
		mReplyAuthor = getIntent().getExtras().getString(REPLY_AUTHOR);
		mReplyQuote = getIntent().getExtras().getString(REPLY_COMMENT);
		String bookTitle = getIntent().getExtras().getString(BOOK_TITLE);

		mEditText = (EditText) findViewById(android.R.id.text1);
		if (!TextUtils.isEmpty(bookTitle)) {
			setTitle(getString(R.string.add_review) + bookTitle);
			mEditText.setHint(R.string.add_review_hint);
		} else {
			setTitle(getString(R.string.add_comment) + mReplyAuthor);
			mEditText.setHint(R.string.add_comment_hint);

			TextView quoteCommentContent = (TextView) findViewById(R.id.ref_comment_content);
			quoteCommentContent.setText(mReplyAuthor + getString(R.string.quote) + mReplyQuote);
			quoteCommentContent.setVisibility(View.VISIBLE);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.book_comment_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.send_review:
			final String review = mEditText.getText().toString();
			if (TextUtils.isEmpty(review)) {
				Toast.makeText(this, R.string.comment_not_empty,
						Toast.LENGTH_SHORT).show();
			} else {
				new AsyncTask<Void, Void, Boolean>() {

					@Override
					protected Boolean doInBackground(Void... arg0) {
						// TODO: add sending progress here.
						sendReview(review);
						return true;
					}

					@Override
					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
					}

				}.execute();

				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}
			return true;
		default:
			return false;
		}
	}

	private void sendReview(String review) {
		ContentValues values = new ContentValues();
		values.put(Comments.USER_ID, mUserId);
		values.put(Comments.BOOK_ID, mBookId);
		values.put(Comments.COMMENT, review);
		values.put(Comments.TIMESTAMP, getCurrentTime());

		if (!TextUtils.isEmpty(mReplyAuthor)) {
			values.put(Comments.REPLY_AUTHOR, mReplyAuthor);
			values.put(Comments.REPLY_QUOTE, mReplyQuote);
		}

		getContentResolver().insert(Comments.CONTENT_URI, values);
	}

	private String getCurrentTime() {
		Calendar now = Calendar.getInstance();
		return String.format("%02d", now.get(Calendar.MONTH)) + "-"
				+ String.format("%02d", now.get(Calendar.DAY_OF_MONTH)) + " "
				+ String.format("%02d", now.get(Calendar.HOUR_OF_DAY)) + ":"
				+ String.format("%02d", now.get(Calendar.MINUTE));
	}
}