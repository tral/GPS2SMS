package ru.perm.trubnikov.gps2sms;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class MyCoordsActivity extends Activity {

	// ------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// Определение темы должно быть ДО super.onCreate и setContentView
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		setTheme(sharedPrefs.getString("prefAppTheme", "1").equalsIgnoreCase("1") ? R.style.AppTheme_Dark : R.style.AppTheme_Light);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycoords);
		
		/*
		Button btn = (Button) findViewById(R.id.button1);
		btn.requestFocus();
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});*/

	}

	// ------------------------------------------------------------------------------------------

}
