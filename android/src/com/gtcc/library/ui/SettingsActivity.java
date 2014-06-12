package com.gtcc.library.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.gtcc.library.R;
import com.gtcc.library.entity.UserInfo;
import com.gtcc.library.util.HttpManager;
import com.gtcc.library.util.Utils;
import com.gtcc.library.webserviceproxy.WebServiceInfo;

public class SettingsActivity extends SherlockPreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.layout.activity_settings);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		final Preference editUserPref = findPreference("pref_key_edit_account");
		editUserPref.setSummary(UserInfo.getCurrentUser().getUserName());

		final AlertDialogPreference logoutPreference = (AlertDialogPreference) findPreference("pref_key_log_out");
		logoutPreference.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					Intent intent = new Intent();
					setResult(RESULT_FIRST_USER, intent);
					finish();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					dialog.dismiss();
					break;
				}
			}
		});

		final Preference checkUpdatePref = findPreference("pref_key_check_update");
		checkUpdatePref
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						new VersionChecker().execute();
						return false;
					}
				});

		final Preference versionPreference = findPreference("pref_key_version");
		versionPreference.setSummary(GetVerName(this));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private static String GetVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.gtcc.library", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verName;
	}

	private static int GetVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					"com.gtcc.library", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verCode;
	}

	private class VersionChecker extends AsyncTask<Void, Void, Integer> {

		private ProgressDialog progressDialog;

		@Override
		protected Integer doInBackground(Void... params) {
			return HttpManager.GetServerVerCode();
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(SettingsActivity.this, "",
					getResources().getString(R.string.checking_updates), true);
			progressDialog.show();
		}

		@Override
		protected void onPostExecute(Integer result) {
			progressDialog.dismiss();

			if (isCancelled())
				return;

			if (result == -1) {
				Toast.makeText(SettingsActivity.this, R.string.update_failed,
						Toast.LENGTH_SHORT).show();
			} else {
				int verCode = SettingsActivity
						.GetVerCode(SettingsActivity.this);
				if (result > verCode) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							SettingsActivity.this);
					Dialog alertDialog = builder
							.setMessage(R.string.new_version_found)
							.setPositiveButton(R.string.upgrade,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											Intent browserIntent = new Intent(
													Intent.ACTION_VIEW,
													Uri.parse(WebServiceInfo.SERVER_ROOT
															+ WebServiceInfo.APP_FILE)); 
											startActivity(browserIntent);
										}
									})
							.setNegativeButton(R.string.not_upgrade,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									}).create();
					alertDialog.show();
				} else {
					Toast.makeText(SettingsActivity.this,
							R.string.latest_version, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
