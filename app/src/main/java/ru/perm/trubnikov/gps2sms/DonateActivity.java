package ru.perm.trubnikov.gps2sms;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.anjlab.android.iab.v3.SkuDetails;


public class DonateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ShowBackButton();

        setContentView(R.layout.activity_donate);

        // ListView on Fragments
        DonateListFragment fragment = new DonateListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frgmCont, fragment).commit();



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

/*
    private void updateTextViews() {
        TextView text = (TextView)findViewById(R.id.productIdTextView);
        text.setText(String.format("%s is%s purchased", PRODUCT_ID1, bp.isPurchased(PRODUCT_ID1) ? "" : " not"));
        text = (TextView)findViewById(R.id.subscriptionIdTextView);
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
                bp.purchase(this,PRODUCT_ID1);
                break;
            case R.id.consumeButton:
                Boolean consumed = bp.consumePurchase(PRODUCT_ID1);
                updateTextViews();
                if (consumed)
                    showToast("Successfully consumed");
                break;
            case R.id.productDetailsButton:
                SkuDetails sku = bp.getPurchaseListingDetails(PRODUCT_ID1);
                showToast(sku != null ? sku.toString() : "Failed to load SKU details");
                break;
            case R.id.subscribeButton:
                bp.subscribe(this,SUBSCRIPTION_ID);
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
                startActivity(new Intent(this, DonateActivity.class).putExtra(ACTIVITY_NUMBER, getIntent().getIntExtra(ACTIVITY_NUMBER, 1) + 1));
                break;
            default:
                break;
        }
    }

*/


}

