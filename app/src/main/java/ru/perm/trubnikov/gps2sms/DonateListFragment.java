package ru.perm.trubnikov.gps2sms;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

/**
 * To make In-App purchases we use the following
 * using this lib: https://github.com/anjlab/android-inapp-billing-v3
 */

public class DonateListFragment extends ListFragment {

    private static final String LICENSE_KEY = null; // PUT YOUR MERCHANT KEY HERE; // UPD: NO NEED to verify donations
    private BillingProcessor bp;
    private boolean readyToPurchase = false;

    private final String PRODUCT_ID_1 = "donation_1";
    private final String PRODUCT_ID_2 = "donation_2";
    private final String PRODUCT_ID_3 = "donation_3";
    private final String PRODUCT_ID_4 = "donation_4";
    private final String PRODUCT_ID_5 = "donation_5";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bp = new BillingProcessor(getActivity(), LICENSE_KEY, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {
            }

            @Override
            public void onBillingInitialized() {
                readyToPurchase = true;

                String[] donateTitles = new String[]{getString(R.string.donate_title_1),
                        getString(R.string.donate_title_2),
                        getString(R.string.donate_title_3),
                        getString(R.string.donate_title_4),
                        getString(R.string.donate_title_5)
                };

                DonateListAdapter adapter = new DonateListAdapter(
                        getActivity(),
                        new String[]{PRODUCT_ID_1, PRODUCT_ID_2, PRODUCT_ID_3, PRODUCT_ID_4, PRODUCT_ID_5},
                        donateTitles,
                        new String[]{getString(R.string.donate_price_1), // retreiving this from GP might be too slow! Async task ?
                                getString(R.string.donate_price_2),
                                getString(R.string.donate_price_3),
                                getString(R.string.donate_price_4),
                                getString(R.string.donate_price_5)
                        },
                        new Drawable[]{getResources().getDrawable(R.drawable.donate_busride),
                                getResources().getDrawable(R.drawable.donate_hambruger),
                                getResources().getDrawable(R.drawable.donate_beer),
                                getResources().getDrawable(R.drawable.donate_cinema),
                                getResources().getDrawable(R.drawable.donate_party)}
                );

                setListAdapter(adapter);

            }

            @Override
            public void onPurchaseHistoryRestored() {
            }
        });


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        //Toast.makeText(getActivity(), (String) getListAdapter().getItem(position), Toast.LENGTH_SHORT).show();

        if (readyToPurchase) {
            bp.purchase(getActivity(), (String) getListAdapter().getItem(position));
            return;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
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
