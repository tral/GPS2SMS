package ru.perm.trubnikov.gps2sms;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

@TargetApi(11)
public class PreferencesActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTitle(R.string.menu_item_settings); // otherwise it's not changed

        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
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