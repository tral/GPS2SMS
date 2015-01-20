package ru.perm.trubnikov.gps2sms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.view.MenuItem;


public class PreferencesLegacyActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Определение темы должно быть ДО super.onCreate и setContentView
        setTheme(DBHelper.determineTheme(this));

        setTitle(R.string.menu_item_settings); // otherwise it's not changed

        super.onCreate(savedInstanceState);

      /*  ShowBackButton();*/

        addPreferencesFromResource(R.xml.settings);

        Preference pref = findPreference("prefAbout");
        pref.setSummary(getString(R.string.pref_about_summary) + " "
                + getString(R.string.version_name));

        // Get the custom preference
        Preference customPref = (Preference) findPreference("prefFav");

        customPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {

                Intent intent = new Intent(PreferencesLegacyActivity.this,
                        ChooseFavActivity.class);
                startActivity(intent);
                return true;
            }

        });

    }

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

}