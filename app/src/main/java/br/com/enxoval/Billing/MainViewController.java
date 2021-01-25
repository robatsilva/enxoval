package br.com.enxoval.Billing;

import android.app.Activity;

import com.android.billingclient.api.Purchase;

import java.util.List;

public class MainViewController {
    Activity mActivity;

    public MainViewController(Activity mActivity) {
        this.mActivity = mActivity;
    }

    private class UpdateListener implements BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {
//            mActivity.onBillingManagerSetupFinished();
        }

        @Override
        public void onConsumeFinished(String token, int result) {

        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchases) {

        }
    }
}
