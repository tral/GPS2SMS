package ru.perm.trubnikov.gps2sms;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;


@TargetApi(11)
public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        Preference pref = findPreference("prefAbout");
        pref.setSummary(getString(R.string.pref_about_summary) + " "
                + getString(R.string.version_name));


        // Get the custom preference
        Preference customPref = (Preference) findPreference("prefFav");

        customPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {

                Intent intent = new Intent(getActivity(),
                        ChooseFavActivity.class);
                startActivity(intent);
                return true;
            }

        });

    }

}