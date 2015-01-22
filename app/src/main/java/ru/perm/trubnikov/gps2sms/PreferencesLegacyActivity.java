package ru.perm.trubnikov.gps2sms;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

public class PreferencesLegacyActivity extends PreferenceActivity {

    private BillingProcessor bp;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Определение темы должно быть ДО super.onCreate и setContentView
        setTheme(DBHelper.determineTheme(this));

        setTitle(R.string.menu_item_settings); // otherwise it's not changed

        super.onCreate(savedInstanceState);

      /*  ShowBackButton();*/

        addPreferencesFromResource(R.xml.settings);

        // Default themes
        ListPreference prefTheme = (ListPreference) findPreference("prefAppTheme");
        prefTheme.setEntries(new String[]{getString(R.string.app_theme_1), getString(R.string.app_theme_2)});
        prefTheme.setEntryValues(new String[]{"1", "2"});

        // Additional themes
        bp = new BillingProcessor(PreferencesLegacyActivity.this, null, new BillingProcessor.IBillingHandler() {
            @Override
            public void onBillingInitialized() {
                try {
                    if (bp.isPurchased("donation_1") || bp.isPurchased("donation_2") ||
                            bp.isPurchased("donation_3") || bp.isPurchased("donation_4") ||
                            bp.isPurchased("donation_5")) {
                        ListPreference prefTheme = (ListPreference) findPreference("prefAppTheme");
                        prefTheme.setEntries(new String[]{getString(R.string.app_theme_1),
                                getString(R.string.app_theme_2),
                                getString(R.string.app_theme_3),
                                getString(R.string.app_theme_4),
                                getString(R.string.app_theme_5),
                                getString(R.string.app_theme_6)});
                        prefTheme.setEntryValues(new String[]{"1", "2", "3", "4", "5", "6"});
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {
            }

            @Override
            public void onPurchaseHistoryRestored() {
            }
        });

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

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }


}