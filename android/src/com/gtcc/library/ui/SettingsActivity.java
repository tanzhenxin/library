package com.gtcc.library.ui;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.gtcc.library.R;

public class SettingsActivity extends SherlockPreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.layout.activity_settings);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

}
