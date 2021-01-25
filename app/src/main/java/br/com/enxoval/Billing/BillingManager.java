package br.com.enxoval.Billing;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class BillingManager implements PurchasesUpdatedListener {
    private final BillingClient mBillingClient;
    Activity mActivity;
    BillingUpdatesListener mBillingUpdatesListener;

    boolean mIsServiceConnected;
    private int mBillingClientResponseCode;

    public BillingManager(Activity activity, final BillingUpdatesListener updatesListener) {
        mActivity = activity;
        mBillingUpdatesListener = updatesListener;
        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).build();

        // Start the setup asynchronously.
        // The specified listener is called once setup completes.
        // New purchases are reported through the onPurchasesUpdated() callback
        // of the class specified using the setListener() method above.
        startServiceConnection(new Runnable() {
            @Override
            public void run() {
                // Notify the listener that the billing client is ready.
                mBillingUpdatesListener.onBillingClientSetupFinished();
                // IAB is fully setup. Now get an inventory of stuff the user owns.
                queryPurchases();
            }
        });
    }

    private void startServiceConnection(final Runnable executeOnSuccess) {
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                Log.d(TAG, "Setup finished. Response code: " + billingResponseCode);

                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    mIsServiceConnected = true;
                    if (executeOnSuccess != null) {
                        executeOnSuccess.run();
                    }
                }
                mBillingClientResponseCode = billingResponseCode;
            }

            @Override
            public void onBillingServiceDisconnected() {
                mIsServiceConnected = false;
            }
        });
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        
    }

    public void initiatePurchaseFlow(final String skuId, final ArrayList<String> oldSkus,
                                     final @BillingClient.SkuType String billingType) {
        Runnable purchaseFlowRequest = new Runnable() {
            @Override
            public void run() {
                BillingFlowParams.Builder mParams = BillingFlowParams.newBuilder().
                        setSku(skuId).setType(billingType).setOldSkus(oldSkus);
                mBillingClient.launchBillingFlow(mActivity, mParams.build());
            }
        };

        executeServiceRequest(purchaseFlowRequest);
    }

    public void queryPurchases() {
        Runnable queryToExecute = new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
//                if (areSubscriptionsSupported()) {
//                    Purchase.PurchasesResult subscriptionResult
//                            = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS);
//                    if (subscriptionResult.getResponseCode() == BillingClient.BillingResponse.OK) {
//                        purchasesResult.getPurchasesList().addAll(
//                                subscriptionResult.getPurchasesList());
//                    } else {
//                        // Handle any error response codes.
//                    }
//                } else if (purchasesResult.getResponseCode() == BillingClient.BillingResponse.OK) {
//                    // Skip subscription purchases query as they are not supported.
//                } else {
//                    // Handle any other error response codes.
//                }
                onQueryPurchasesFinished(purchasesResult);
            }
        };

        executeServiceRequest(queryToExecute);
    }

    private void onQueryPurchasesFinished(Purchase.PurchasesResult purchasesResult) {
    }

    private void executeServiceRequest(Runnable runnable) {
        if (mIsServiceConnected) {
            runnable.run();
        } else {
            // If the billing service disconnects, try to reconnect once.
            startServiceConnection(runnable);
        }
    }

}

