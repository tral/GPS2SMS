package ru.perm.trubnikov.gps2sms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

public class TestActivity extends Activity {

    // SAMPLE APP CONSTANTS
    private static final String ACTIVITY_NUMBER = "activity_num";
    private static final String LOG_TAG = "iabv3";

    // PRODUCT & SUBSCRIPTION IDS
    private static final String PRODUCT_ID = "android.test.purchased";
    private static final String SUBSCRIPTION_ID = "android.test.purchased";
    private static final String LICENSE_KEY = null; // PUT YOUR MERCHANT KEY HERE; // WE DO NOT NEED TO VERIFY OUR DONATIONS

    private BillingProcessor bp;
    private boolean readyToPurchase = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        TextView title = (TextView) findViewById(R.id.titleTextView);
        title.setText(String.format("TITLE", getIntent().getIntExtra(ACTIVITY_NUMBER, 1)));

        bp = new BillingProcessor(this, LICENSE_KEY, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
                showToast("onProductPurchased: " + productId);
                bp.loadOwnedPurchasesFromGoogle();
                updateTextViews();
            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {
                showToast("onBillingError: " + Integer.toString(errorCode));
                bp.loadOwnedPurchasesFromGoogle();
                updateTextViews();
            }

            @Override
            public void onBillingInitialized() {
                showToast("onBillingInitialized");
                readyToPurchase = true;
                updateTextViews();
            }

            @Override
            public void onPurchaseHistoryRestored() {
                showToast("onPurchaseHistoryRestored");
                for (String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for (String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                updateTextViews();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        updateTextViews();
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateTextViews() {
        TextView text = (TextView) findViewById(R.id.productIdTextView);
        text.setText(String.format("%s is%s purchased", PRODUCT_ID, bp.isPurchased(PRODUCT_ID) ? "" : " not"));
        text = (TextView) findViewById(R.id.subscriptionIdTextView);
        text.setText(String.format("%s is%s subscribed", SUBSCRIPTION_ID, bp.isSubscribed(SUBSCRIPTION_ID) ? "" : " not"));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void onClick(View v) {
        if (!readyToPurchase) {
            showToast("Billing not initialized.");
            return;
        }
        switch (v.getId()) {
            case R.id.purchaseButton:
                bp.purchase(this, PRODUCT_ID);
                break;
            case R.id.consumeButton:
                Boolean consumed = bp.consumePurchase(PRODUCT_ID);
                updateTextViews();
                if (consumed)
                    showToast("Successfully consumed");

                consumed = bp.consumePurchase("donation_1");
                if (consumed) showToast("Successfully consumed (donation_1)");
                consumed = bp.consumePurchase("donation_2");
                if (consumed) showToast("Successfully consumed (donation_2)");
                consumed = bp.consumePurchase("donation_3");
                if (consumed) showToast("Successfully consumed (donation_3)");
                consumed = bp.consumePurchase("donation_4");
                if (consumed) showToast("Successfully consumed (donation_4)");
                consumed = bp.consumePurchase("donation_5");
                if (consumed) showToast("Successfully consumed (donation_5)");

                break;
            case R.id.productDetailsButton:
                SkuDetails sku = bp.getPurchaseListingDetails(PRODUCT_ID);
                showToast(sku != null ? sku.toString() : "Failed to load SKU details");
                break;
            case R.id.subscribeButton:
                bp.subscribe(this, SUBSCRIPTION_ID);
                break;
            case R.id.updateSubscriptionsButton:
                if (bp.loadOwnedPurchasesFromGoogle()) {
                    showToast("Subscriptions updated.");
                    updateTextViews();
                }
                break;
            case R.id.subsDetailsButton:
                SkuDetails subs = bp.getSubscriptionListingDetails(SUBSCRIPTION_ID);
                showToast(subs != null ? subs.toString() : "Failed to load subscription details");
                break;
            case R.id.launchMoreButton:
                startActivity(new Intent(this, MainActivity.class).putExtra(ACTIVITY_NUMBER, getIntent().getIntExtra(ACTIVITY_NUMBER, 1) + 1));
                break;
            default:
                break;
        }
    }

}
