package com.gtcc.library.ui.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.gtcc.library.R;
import com.gtcc.library.entity.UserInfo;
import com.gtcc.library.util.Utils;
import com.gtcc.library.webserviceproxy.WebServiceInfo;
import com.gtcc.library.webserviceproxy.WebServiceUserProxy;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UserRegisterActivity extends FragmentActivity {

	private TextView mUserName;
	private TextView mEmail;
	private TextView mPassword;
	private ImageView mUserImage;
	private Bitmap mUserImageBitmap;

	private ProgressDialog mSpinner;
	private UserRegisterTask mRegisterTask;

	private static final int TAKE_PHOTO = 0;
	private static final int REQUEST_IMAGE = 1;
	private static final int REQUEST_CROP = 2;

	private Uri mPhotoUri;

	private static final String ACTION_IMAGE_CROP = "com.android.camera.action.CROP";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_signup);

		mUserName = (TextView) findViewById(R.id.register_user);
		mEmail = (TextView) findViewById(R.id.register_email);
		mPassword = (TextView) findViewById(R.id.register_password);
		mPassword.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				attempLogin();
				return true;
			}
		});

		final Button registerBtn = (Button) findViewById(R.id.login_signup);
		registerBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				attempLogin();
			}
		});

		mUserImage = (ImageView) findViewById(R.id.user_image);
		mUserImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PickImageFragment fragment = new PickImageFragment();
				fragment.show(getSupportFragmentManager(), "pickImage");
			}
		});

		mSpinner = new ProgressDialog(this);
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage(getString(R.string.register_in_progress));
		mSpinner.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				mSpinner.dismiss();
				if (mRegisterTask != null
						&& mRegisterTask.getStatus() != Status.FINISHED) {
					mRegisterTask.cancel(true);
				}
				return true;
			}
		});

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			this.overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_IMAGE:
				mPhotoUri = data.getData();
				// fall through
			case TAKE_PHOTO:
				cropImage();
				break;
			case REQUEST_CROP:
				mUserImageBitmap = (Bitmap) data.getExtras().get("data");
				if (mUserImageBitmap != null) {
					mUserImage.setImageBitmap(mUserImageBitmap);
				}
				break;
			}
		}
	}

	private void pickImageFromCamera() {
		if (Utils.isIntentAvailable(this, MediaStore.ACTION_IMAGE_CAPTURE)) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			mPhotoUri = Uri
					.fromFile(new File(
							Environment
									.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
							"tmp_avatar_"
									+ String.valueOf(System.currentTimeMillis()
											+ ".jpg")));
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, TAKE_PHOTO);
		} else {
			Toast.makeText(this, R.string.camera_not_found, Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void pickImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		if (Utils.isIntentAvailable(this, intent)) {
			startActivityForResult(intent, REQUEST_IMAGE);
		} else {
			Toast.makeText(this, "Cannot find image gallery",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void cropImage() {
		Intent intent = new Intent(ACTION_IMAGE_CROP);
		intent.setType("image/*");
		List<ResolveInfo> list = getPackageManager().queryIntentActivities(
				intent, 0);

		if (list.size() == 0) {
			Toast.makeText(this, "Cannot find image crop app",
					Toast.LENGTH_SHORT).show();
			return;
		} else {
			intent.setData(mPhotoUri);
			intent.putExtra("outputX", 200);
			intent.putExtra("outputY", 200);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);

			Intent i = new Intent(intent);
			ResolveInfo res = list.get(0);

			i.setComponent(new ComponentName(res.activityInfo.packageName,
					res.activityInfo.name));

			startActivityForResult(i, REQUEST_CROP);
		}
	}

	private void attempLogin() {
		if (Utils.isNetworkConnected(this)) {
			mUserName.setError(null);
			mEmail.setError(null);
			mPassword.setError(null);

			final String userName = mUserName.getText().toString();
			final String email = mEmail.getText().toString();
			final String password = mPassword.getText().toString();

			if (TextUtils.isEmpty(userName)) {
				mUserName.setError(getString(R.string.user_name_not_empty));
				mUserName.requestFocus();
			} else if (TextUtils.isEmpty(email)) {
				mEmail.setError(getString(R.string.user_email_not_empty));
				mEmail.requestFocus();
			} else if (!Utils.isEmailValid(email)) {
				mEmail.setError(getString(R.string.user_email_invalid));
				mEmail.requestFocus();
			} else if (TextUtils.isEmpty(password)) {
				mPassword.setError(getString(R.string.user_password_not_empty));
				mPassword.requestFocus();
			} else {
				mSpinner.show();

				mRegisterTask = new UserRegisterTask();
				mRegisterTask.execute(userName, password, email);
			}
		}
	}

	public static class PickImageFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.login_pick_image).setItems(
					R.array.image_pickers,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								//pickImageFromCamera();
								break;
							case 1:
								//pickImageFromGallery();
								break;
							}
						}

					});
			return builder.create();
		}
	}

	public class UserRegisterTask extends AsyncTask<String, Void, Integer> {

		String userName;
		String password;
		String email;

		@Override
		protected Integer doInBackground(String... params) {
			int ret = WebServiceInfo.OPERATION_FAILED;

			userName = params[0];
			password = params[1];
			email = params[2];

			WebServiceUserProxy webServiceProxy = new WebServiceUserProxy();
			try {
				ret = webServiceProxy.addUser(userName, password,
						email);
				if (ret == WebServiceInfo.OPERATION_SUCCEED && mUserImageBitmap != null) {
					ret = webServiceProxy.uploadUserImage(userName, mUserImageBitmap);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return ret;
		}

		@Override
		protected void onPostExecute(final Integer result) {
			mSpinner.dismiss();

			if (isCancelled())
				return;

			switch (result) {
			case WebServiceInfo.OPERATION_SUCCEED:
				Intent intent = new Intent();
				UserInfo userInfo = new UserInfo(userName, userName, email, password);
				intent.putExtra(UserLoginActivity.LOGIN_USER, userInfo);
				setResult(RESULT_OK, intent);
				Toast.makeText(UserRegisterActivity.this,
						R.string.register_succeed, Toast.LENGTH_SHORT).show();
				finish();
				break;
			case WebServiceInfo.USER_ALREADY_EXISTS:
				Toast.makeText(UserRegisterActivity.this,
						R.string.register_failed_user_exists, Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(UserRegisterActivity.this,
						R.string.register_failed, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
}
