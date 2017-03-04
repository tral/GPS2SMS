package ru.perm.trubnikov.gps2sms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class PreferencesLegacyActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Определение темы должно быть ДО super.onCreate и setContentView
        setTheme(DBHelper.determineTheme(this));

        setTitle(R.string.menu_item_settings); // otherwise it's not changed

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_checkbox_or_switch);
        addPreferencesFromResource(R.xml.settings);

        ListPreference prefTheme = (ListPreference) findPreference("prefAppTheme");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(PreferencesLegacyActivity.this);
        if (settings.getInt("prefDonate1", 0) > 0 ||
                settings.getInt("prefDonate2", 0) > 0 ||
                settings.getInt("prefDonate3", 0) > 0 ||
                settings.getInt("prefDonate4", 0) > 0 ||
                settings.getInt("prefDonate5", 0) > 0) {
            // Additional themes
            prefTheme.setEntries(new String[]{getString(R.string.app_theme_1),
                    getString(R.string.app_theme_2),
                    getString(R.string.app_theme_3),
                    getString(R.string.app_theme_4),
                    getString(R.string.app_theme_5),
                    getString(R.string.app_theme_6)});
            prefTheme.setEntryValues(new String[]{"1", "2", "3", "4", "5", "6"});
        } else {
            // Default themes
            prefTheme.setEntries(new String[]{getString(R.string.app_theme_1), getString(R.string.app_theme_2)});
            prefTheme.setEntryValues(new String[]{"1", "2"});
        }

        Preference pref = findPreference("prefAbout");
        pref.setSummary(getString(R.string.pref_about_summary) + " "
                + getString(R.string.version_name1                ));

        // Get the custom preference
        Preference customPref = findPreference("prefFav");

        customPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {

                Intent intent = new Intent(PreferencesLegacyActivity.this,
                        ChooseFavActivity.class);
                startActivity(intent);
                return true;
            }

        });

    }

}