package ru.perm.trubnikov.gps2sms;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

/**
 * To make In-App purchases we use the following
 * using this lib: https://github.com/anjlab/android-inapp-billing-v3
 */

public class DonateListFragment extends ListFragment {

    private DonateListAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] donateTitles = new String[]{getString(R.string.donate_title_1),
                getString(R.string.donate_title_2),
                getString(R.string.donate_title_3),
                getString(R.string.donate_title_4),
                getString(R.string.donate_title_5)
        };
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        DonateActivity activity = (DonateActivity) getActivity();
        adapter = new DonateListAdapter(
                getActivity(),
                new String[]{activity.getProductId(1), activity.getProductId(2), activity.getProductId(3), activity.getProductId(4), activity.getProductId(5)},
                donateTitles,
                new String[]{"", "", "", "", ""},
                new Drawable[]{getResources().getDrawable(R.drawable.donate_busride),
                        getResources().getDrawable(R.drawable.donate_hambruger),
                        getResources().getDrawable(R.drawable.donate_beer),
                        getResources().getDrawable(R.drawable.donate_cinema),
                        getResources().getDrawable(R.drawable.donate_party)},
                new Integer[]{
                        settings.getInt("prefDonate1", 0),
                        settings.getInt("prefDonate2", 0),
                        settings.getInt("prefDonate3", 0),
                        settings.getInt("prefDonate4", 0),
                        settings.getInt("prefDonate5", 0)
                },
                getResources().getDrawable(R.drawable.donate_owned)
        );

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //Toast.makeText(getActivity(), (String) getListAdapter().getItem(position), Toast.LENGTH_SHORT).show();
        DonateActivity activity = (DonateActivity) getActivity();
        activity.tryToPurchase((String) getListAdapter().getItem(position));
    }

    public void refreshListItemsStatus(int item1, int item2, int item3, int item4, int item5) {
        adapter.setStates(0, item1);
        adapter.setStates(1, item2);
        adapter.setStates(2, item3);
        adapter.setStates(3, item4);
        adapter.setStates(4, item5);
        adapter.notifyDataSetChanged();
    }

    public void refreshListItemsDescs(String val1, String val2, String val3, String val4, String val5) {
        adapter.setDescs(0, val1);
        adapter.setDescs(1, val2);
        adapter.setDescs(2, val3);
        adapter.setDescs(3, val4);
        adapter.setDescs(4, val5);

        adapter.notifyDataSetChanged();
    }

        /*
    private String getPriceText(String productId) {
        try {
            return bp.getPurchaseListingDetails(productId).priceText;
        } catch (Exception e) {
            return "";
        }
    }*/


}
