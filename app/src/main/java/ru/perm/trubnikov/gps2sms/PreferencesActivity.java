package ru.perm.trubnikov.gps2sms;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

@TargetApi(11)
public class PreferencesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Определение темы должно быть ДО super.onCreate и setContentView
        setTheme(DBHelper.determineTheme(this));

        setTitle(R.string.menu_item_settings); // otherwise it's not changed

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pref_with_actionbar);
        getFragmentManager().beginTransaction().replace(R.id.preference_container,
                new PrefsFragment()).commit();

        ShowBackButton();



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

    public void ShowBackButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // call something for API Level 11+
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

}