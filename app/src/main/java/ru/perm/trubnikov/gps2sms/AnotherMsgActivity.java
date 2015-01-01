package ru.perm.trubnikov.gps2sms;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AnotherMsgActivity extends Activity {

	// ------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// Определение темы должно быть ДО super.onCreate и setContentView
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		setTheme(sharedPrefs.getString("prefAppTheme", "1").equalsIgnoreCase(
				"1") ? R.style.AppTheme_Dark : R.style.AppTheme_Light);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_another_msg);

		ShowBackButton();

		Button btn = (Button) findViewById(R.id.button1);
		btn.requestFocus();
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	// ------------------------------------------------------------------------------------------

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@TargetApi(11)
	public void ShowBackButton() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// call something for API Level 11+
            getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

}
