package br.com.enxoval.Billing;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;

import java.util.List;

public interface BillingUpdatesListener {
    void onBillingClientSetupFinished();
    void onConsumeFinished(String token, @BillingClient.BillingResponse int result);
    void onPurchasesUpdated(List<Purchase> purchases);
}
