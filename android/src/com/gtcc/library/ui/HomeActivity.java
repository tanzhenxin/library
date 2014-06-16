package com.gtcc.library.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.widget.SearchView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.entity.Book;
import com.gtcc.library.entity.Borrow;
import com.gtcc.library.entity.UserInfo;
import com.gtcc.library.provider.LibraryContract;
import com.gtcc.library.provider.LibraryContract.Books;
import com.gtcc.library.sync.SyncHelper;
import com.gtcc.library.ui.library.LibraryFragment;
import com.gtcc.library.ui.user.UserBookListFragment;
import com.gtcc.library.ui.user.UserFragment;
import com.gtcc.library.ui.user.UserLoginActivity;
import com.gtcc.library.ui.zxing.CaptureActivity;
import com.gtcc.library.util.CommonAsyncTask;
import com.gtcc.library.util.HttpManager;
import com.gtcc.library.util.LogUtils;
import com.gtcc.library.util.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class HomeActivity extends SlidingFragmentActivity implements
		UserBookListFragment.Callbacks {
	
	private static final String TAG = LogUtils.makeLogTag(HomeActivity.class);

	public static final int PAGE_USER = 0;
	public static final int PAGE_LIBRARY = 1;
	public static final int PAGE_SCANNER = 2;
	public static final int PAGE_SETTINGS = 3;

	public static final String ARG_PAGE_NUMBER = "page_number";
	public static final String CURRENT_INDEX = "currentIndex";
	public static final String BOOK_ISBN = "isbn";

	private int REQUEST_LOGIN = 1;
	private int SCANNER = 2;
	private int SETTINGS = 3;

	private int mCurrentPage = -1;

	public HomeActivity() {
		super(R.string.app_name);
	}

	private final int GET_RETURN_DATE = 1;

	private class AsyncLoader extends CommonAsyncTask<Integer, Boolean> {
		@Override
		public Boolean doWork(Integer... params) throws Exception {
			int type = params[0];
			switch (type) {
			case GET_RETURN_DATE:
				List<Borrow> bookBorrowInfos = HttpManager.webServiceBorrowProxy
						.getBorrowedInfo(HomeActivity.this.getUserId());
				for (Iterator i = bookBorrowInfos.iterator(); i.hasNext();) {
					Borrow bookBorrowInfo = (Borrow) i.next();

					Calendar today = Calendar.getInstance();
					Calendar returnDate = Calendar.getInstance();
					today.add(Calendar.DATE, -3);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					java.util.Date date = sdf.parse(bookBorrowInfo
							.getPlanReturnDate());
					returnDate.setTime(date);
					if (today.before(returnDate)) {
						i.remove();
					}
				}
				if (!bookBorrowInfos.isEmpty()) {
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							HomeActivity.this)
							.setSmallIcon(R.drawable.ic_user_center)
							.setContentTitle(
									getString(R.string.expire_book_tile))
							.setContentText(
									getString(R.string.expire_book_tile));
					Intent resultIntent = new Intent(HomeActivity.this,
							HomeActivity.class);
					resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent resultPendingIntent = PendingIntent
							.getActivity(HomeActivity.this, 0, resultIntent,
									PendingIntent.FLAG_UPDATE_CURRENT);
					mBuilder.setContentIntent(resultPendingIntent);
					mBuilder.setAutoCancel(true);
					int mNotificationId = 001;
					NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					mNotifyMgr.notify(mNotificationId, mBuilder.build());
				}
				break;
			}
			return true;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empty_pane);

		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		if (savedInstanceState != null) {
			mCurrentPage = savedInstanceState.getInt(CURRENT_INDEX);
		}

		if (mCurrentPage == -1) {
			SharedPreferences sharedPref = getSharedPreferences(
					SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);
			mCurrentPage = sharedPref.getInt(CURRENT_INDEX, -1);
		}

		if (mCurrentPage == -1) {
			mCurrentPage = PAGE_LIBRARY;
		}

		showPage(mCurrentPage);
		
		if (savedInstanceState == null) {
			triggerRefresh();
		}

//		if (isFirstLoad == true && hasLogin()) {
//			new AsyncLoader().execute(GET_RETURN_DATE);
//			isFirstLoad = false;
//		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(CURRENT_INDEX, mCurrentPage);
	}

	@Override
	protected void onPause() {
		super.onPause();

		Editor editor = getSharedPreferences(SHARED_PREFERENCE_FILE,
				Context.MODE_PRIVATE).edit();
		editor.putInt(CURRENT_INDEX, mCurrentPage);
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.books_list_menu, menu);
		setupSearchMenuItem(menu);
		setupScanMenuItem(menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_search:
			if (!Utils.hasHoneycomb()) {
				startSearch(null, false, Bundle.EMPTY, false);
				return true;
			}
			break;
		case R.id.menu_refresh:
			triggerRefresh();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupSearchMenuItem(Menu menu) {
		MenuItem searchMenu = menu.findItem(R.id.menu_search);
		if (searchMenu != null && Utils.hasHoneycomb()) {
			SearchView searchView = (SearchView) searchMenu.getActionView();
			if (searchView != null) {
				SearchManager searchManager = (SearchManager) getSystemService(Activity.SEARCH_SERVICE);
				SearchableInfo info = searchManager
						.getSearchableInfo(getComponentName());
				searchView.setSearchableInfo(info);
			}
		}
	}

	private void setupScanMenuItem(Menu menu) {
		MenuItem scanItem = menu.findItem(R.id.menu_scan);
		if (scanItem != null) {
			scanItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					showScanner();
					return true;
				}
			});
		}
	}

	private void triggerRefresh() {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				boolean operationSucceed = false;
				try {
					new SyncHelper(HomeActivity.this).performSync();
					operationSucceed = true;
				} catch (RemoteException e) {
					LogUtils.LOGE(TAG, e.toString());
				} catch (OperationApplicationException e) {
					LogUtils.LOGE(TAG, e.toString());
				}
				
				return operationSucceed;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (!result) {
					Toast.makeText(HomeActivity.this, "Failed to get books.", Toast.LENGTH_SHORT).show();
				}
			}
			
		}.execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_LOGIN) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				int loginType = data.getExtras().getInt(
						UserLoginActivity.LOGIN_TYPE);
				UserInfo userInfo = (UserInfo) data.getExtras()
						.getSerializable(UserLoginActivity.LOGIN_USER);
				setUserInfo(userInfo);

				showPage(PAGE_LIBRARY);
				break;
			case Activity.RESULT_CANCELED:
				finish();
				break;
			}
		} else if (requestCode == SCANNER) {
			if (resultCode == RESULT_OK) {
				final String isbnCode = data.getExtras().getString("result");
				if (isbnCode != null && isbnCode != "") {
					CommonAsyncTask<Void, List<Book>> task = new CommonAsyncTask<Void, List<Book>>(
							this) {
						@Override
						protected List<Book> doWork(Void... params)
								throws Exception {
							return HttpManager.webServiceBookProxy
									.getBookListByISBN(isbnCode);
						}

						@Override
						protected void onResult(List<Book> result) {
							if (result != null && !result.isEmpty()) {
								if (result.size() == 1) {
									HomeActivity.this.OnBookSelected(
											result.get(0).getId(),
											HomeActivity.this.mCurrentPage);
								} else {
									Intent intent = new Intent(
											HomeActivity.this,
											ScanBookListActivity.class);
									intent.putExtra(BOOK_ISBN, isbnCode);
									startActivity(intent);
								}

							} else {
								Toast.makeText(HomeActivity.this,
										R.string.no_book_found,
										Toast.LENGTH_SHORT).show();
							}
						}
					};
					task.execute();
				}
			}
		} else if (requestCode == SETTINGS) {
			switch (resultCode) {
			case Activity.RESULT_FIRST_USER:
				clearUserInfo();
				getContentResolver().delete(LibraryContract.BASE_CONTENT_URI,
						null, null);
				login();
				break;
			}
		}
	}

	public void showPage(int position) {
		if (!hasLogin()) {
			login();
			return;
		}

		switch (position) {
		case PAGE_USER:
			mCurrentPage = PAGE_USER;
			showUserHome();
			showContent();
			break;
		case PAGE_LIBRARY:
			mCurrentPage = PAGE_LIBRARY;
			showLibrary();
			showContent();
			break;
		case PAGE_SCANNER:
			showScanner();
			break;
		case PAGE_SETTINGS:
			showSettings();
			break;
		}
	}

	public int getCurrentPage() {
		return mCurrentPage;
	}

	public boolean hasLogin() {
		return true;
//		String userId = getUserId();
//		return userId != null && userId != "0";
	}

	private void login() {
		Intent intent = new Intent(this, UserLoginActivity.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}

	private void showUserHome() {
		Fragment fragment = this.getSupportFragmentManager().findFragmentById(
				R.id.fragment_container);
		if (fragment == null || !(fragment instanceof UserBookListFragment)) {
			fragment = new UserFragment();
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.fragment_container, fragment);
			transaction.commit();
		}

		setTitle(R.string.user_center);
	}

	private void showLibrary() {
		Fragment fragment = this.getSupportFragmentManager().findFragmentById(
				R.id.fragment_container);
		if (fragment == null || !(fragment instanceof LibraryFragment)) {
			fragment = new LibraryFragment();
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.fragment_container, fragment);
			transaction.commit();
		}

		setTitle(R.string.book_library);
	}

	private void showScanner() {
		Intent intent = new Intent(this, CaptureActivity.class);
		startActivityForResult(intent, SCANNER);
	}

	private void showSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivityForResult(intent, SETTINGS);
	}

	@Override
	public boolean OnBookSelected(String bookId, int page) {
		Uri sessionUri = Books.buildBookUri(bookId);
		Intent detailIntent = new Intent(Intent.ACTION_VIEW, sessionUri);
		detailIntent.putExtra(ARG_PAGE_NUMBER, page);
		detailIntent.putExtra(USER_ID, getUserId());
		startActivity(detailIntent);

		return true;
	}
}
