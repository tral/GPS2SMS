package ru.perm.trubnikov.gps2sms;

import android.os.Bundle;
import android.view.MenuItem;

public class ChooseFavActivity extends BaseActivity {

    // ------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTitle(R.string.choose_fav_app);
        super.onCreate(savedInstanceState);

        ShowBackButton();

        // ListView on Fragments
        ChooseFavListFragment fragment = new ChooseFavListFragment();
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


}
