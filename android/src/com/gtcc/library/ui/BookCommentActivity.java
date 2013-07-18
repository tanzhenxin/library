package com.gtcc.library.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;

public class BookCommentActivity extends SherlockActivity {
	
	public static final String BOOK_TITLE = "title";
	public static final String BOOK_COMMENT = "comment";
	
	private EditText mEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		
		mEditText = (EditText) findViewById(android.R.id.text1);
		
		String bookTitle = getIntent().getExtras().getString(BOOK_TITLE);

		setTitle(getString(R.string.add_review) + bookTitle);
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
			Intent intent = new Intent();
			intent.putExtra(BOOK_COMMENT, mEditText.getText().toString());
			setResult(RESULT_OK, intent);
			finish();
		default:
			return false;
		}
	}

}
