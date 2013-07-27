package com.gtcc.library.ui.user;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.util.Utils;

public class UserRegisterActivity extends SherlockFragmentActivity {

	private TextView mUserName;
	private TextView mEmail;
	private TextView mPassword;
	private ImageView mUserImage;

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

		final Button registerBtn = (Button) findViewById(R.id.login_signup);
		registerBtn.setOnClickListener(onRegisterBtnClicked);

		mUserImage = (ImageView) findViewById(R.id.user_image);
		mUserImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PickImageFragment fragment = new PickImageFragment();
				fragment.show(getSupportFragmentManager(), "pickImage");
			}
		});

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
			case TAKE_PHOTO:
				cropImage();
				break;
			case REQUEST_CROP:
				Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
				if (imageBitmap != null) {
					mUserImage.setImageBitmap(imageBitmap);
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
									+ String.valueOf(System.currentTimeMillis())));
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
			Toast.makeText(this, R.string.camera_not_found, Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void cropImage() {
		Intent intent = new Intent(ACTION_IMAGE_CROP);
		intent.setType("image/*");
		if (Utils.isIntentAvailable(this, intent)) {
			intent.setData(mPhotoUri);
			intent.putExtra("outputX", 200);
			intent.putExtra("outputY", 200);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, REQUEST_CROP);
		} else {
			Toast.makeText(this, R.string.camera_not_found, Toast.LENGTH_SHORT)
					.show();
		}
	}

	private OnClickListener onRegisterBtnClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mUserName.length() == 0) {
				new ErrorInputFragment(R.string.user_name_not_empty).show(
						getSupportFragmentManager(), "");
			} else if (mEmail.length() == 0) {
				new ErrorInputFragment(R.string.user_email_not_empty).show(
						getSupportFragmentManager(), "");
			} else if (mPassword.length() == 0) {
				new ErrorInputFragment(R.string.user_password_not_empty).show(
						getSupportFragmentManager(), "");
			} else {

			}
		}
	};

	class ErrorInputFragment extends DialogFragment {

		private int id;

		public ErrorInputFragment(int id) {
			this.id = id;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					UserRegisterActivity.this);
			builder.setMessage(id).setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}

					});
			return builder.create();
		}

	}

	class PickImageFragment extends DialogFragment {

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
								pickImageFromCamera();
								break;
							case 1:
								pickImageFromGallery();
								break;
							}
						}

					});
			return builder.create();
		}
	}
}
