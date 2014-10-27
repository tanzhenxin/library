package com.gtcc.library.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gtcc.library.R;

import java.util.Calendar;

public class BookCommentActivity extends FragmentActivity {

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
			quoteCommentContent.setText(mReplyAuthor
					+ getString(R.string.quote) + mReplyQuote);
			quoteCommentContent.setVisibility(View.VISIBLE);
		}
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.book_comment_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			confirmExit();
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			confirmExit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void sendReview(String review) {
//		ContentValues values = new ContentValues();
//		values.put(Comments.USER_ID, mUserId);
//		values.put(Comments.BOOK_ID, mBookId);
//		values.put(Comments.COMMENT, review);
//		values.put(Comments.TIMESTAMP, getCurrentTime());
//
//		if (!TextUtils.isEmpty(mReplyAuthor)) {
//			values.put(Comments.REPLY_AUTHOR, mReplyAuthor);
//			values.put(Comments.REPLY_QUOTE, mReplyQuote);
//		}
//
//		getContentResolver().insert(Comments.CONTENT_URI, values);
	}

	private String getCurrentTime() {
		Calendar now = Calendar.getInstance();
		return String.format("%02d", now.get(Calendar.MONTH)) + "-"
				+ String.format("%02d", now.get(Calendar.DAY_OF_MONTH)) + " "
				+ String.format("%02d", now.get(Calendar.HOUR_OF_DAY)) + ":"
				+ String.format("%02d", now.get(Calendar.MINUTE));
	}
	
	private void confirmExit() {
		final String review = mEditText.getText().toString();
		if (TextUtils.isEmpty(review)) {
			finish();
		}
		else {
			new CancelDialogFragment().show(getSupportFragmentManager(), "Exit");
		}
	}

	public static class CancelDialogFragment extends DialogFragment implements
			DialogInterface.OnClickListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.cancel_comment)
					.setMessage(R.string.lost_comment)
					.setPositiveButton(R.string.ok, this)
					.setNegativeButton(R.string.cancel, this);
			return builder.create();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				getActivity().finish();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				dialog.dismiss();
				break;
			}
		}
	}
}
