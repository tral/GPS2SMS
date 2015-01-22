package ru.perm.trubnikov.gps2sms;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;


@TargetApi(11)
public class PrefsFragment extends PreferenceFragment {

    private BillingProcessor bp;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        // Default themes
        ListPreference prefTheme = (ListPreference) findPreference("prefAppTheme");
        prefTheme.setEntries(new String[]{getString(R.string.app_theme_1), getString(R.string.app_theme_2)});
        prefTheme.setEntryValues(new String[]{"1", "2"});

        // Additional themes
        bp = new BillingProcessor(getActivity(), null, new BillingProcessor.IBillingHandler() {
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
        }

        );

        Preference pref = findPreference("prefAbout");
        pref.setSummary(getString(R.string.pref_about_summary) + " " + getString(R.string.version_name));

        // Get the custom preference
        Preference customPref = findPreference("prefFav");

        customPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {

                Intent intent = new Intent(getActivity(),
                        ChooseFavActivity.class);
                startActivity(intent);
                return true;
            }

        });

    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }

}