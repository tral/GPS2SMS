package ru.perm.trubnikov.gps2sms;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.view.MenuItem;

public class UserSettingActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ShowBackButton();
		addPreferencesFromResource(R.xml.settings);

		this.initSummaries(this.getPreferenceScreen());

	}

	private void setSummary(Preference pref) {

		// react on type or key
		if (pref instanceof Preference) {
			// ListPreference listPref = (ListPreference) pref;
			// pref.setSummary(listPref.getEntry());
			// Log.d("seagull", "EXCEPTION! " + pref.getKey());
			if (pref.getKey().equalsIgnoreCase("prefAbout")) {
				pref.setSummary(getString(R.string.pref_about_summary) + " "
						+ getString(R.string.version_name));
			}
		}
	}

	private void initSummaries(PreferenceGroup pg) {
		for (int i = 0; i < pg.getPreferenceCount(); ++i) {
			Preference p = pg.getPreference(i);
			if (p instanceof PreferenceGroup)
				this.initSummaries((PreferenceGroup) p); // recursion
			else
				this.setSummary(p);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
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