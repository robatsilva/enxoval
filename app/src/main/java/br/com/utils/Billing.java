package br.com.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import br.com.enxoval.BaseEnxovalActivity;
import br.com.enxoval.R;

import static android.app.Activity.RESULT_CANCELED;

public class Billing{
    static BillingClient mBillingClient;
    ProgressDialog _dialog;
    static String proPrice;

    public Billing() {
        if(mBillingClient != null){
            mBillingClient.endConnection();
        }
        mBillingClient = null;
    }

    public void purchaseItem(String sku, String price, int idTitle, int idMessage, Context context, MyCallback myCallback) {
        Log.d("check flow", "purchaseItem");
        if(!isOnline(context)){
            Snackbar snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content), R.string.verify_conection, Snackbar.LENGTH_SHORT);
            snackbar.show();
            return;
        }

        getBillingClient(sku, price, idTitle, idMessage, context, myCallback);

    }

    private void getBillingClient(final String sku, final String price, final int idTitle, final int idMessage, final Context context, final MyCallback myCallback){
        if(_dialog == null){
            _dialog = ProgressDialog.show(context, "",
                context.getString(R.string.loading), true, true);
        }
        Log.d("check flow", "getBillingClient");
        if(mBillingClient == null) {
            mBillingClient = BillingClient.newBuilder(context)
                    .setListener(new PurchasesUpdatedListener() {
                        @Override
                        public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
                            Log.d("check flow", "onPurchasesUpdated" + responseCode);
                            if (responseCode == BillingClient.BillingResponse.OK
                                    && purchases != null) {
                                Log.d("check flow", "onPurchasesUpdated if");
                                myCallback.callBack();
                                _dialog.dismiss();
                            } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
                                Log.d("check flow", "USER_CANCELED");
                                _dialog.dismiss();
                            } else {
                                Log.d("check flow", "onPurchasesUpdated else");
                                _dialog.dismiss();
                                new AlertDialog.Builder(context)
                                        .setTitle(R.string.baseEnxoval_dialog_purchase_error_title)
                                        .setMessage(R.string.baseEnxoval_dialog_purchase_error_msg)
                                        .setPositiveButton(R.string.baseEnxoval_dialog_purchase_error_try_again, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                _dialog.show();
                                                purchaseItem(sku, price, idTitle, idMessage, context, myCallback);
                                            }
                                        })
                                        .setNegativeButton(context.getString(R.string.baseEnxoval_dialog_share_download) + " " + proPrice, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=br.com.enxoval.pro")));
                                                } catch (ActivityNotFoundException anfe) {
                                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=br.com.enxoval.pro")));
                                                }
                                            }
                                        })
                                        .setNeutralButton(R.string.btn_not_now, null)
                                        .show();
                            }
                        }
                    }).build();

        }
        connectBillingClient(sku, price, idTitle, idMessage, mBillingClient, context, myCallback);
    }

    private void connectBillingClient(final String sku, final String price, final int idTitle, final int idMessage, final BillingClient mBillingClient, final Context context, final MyCallback myCallback){
        if(mBillingClient.isReady()){
            getDetail(sku, price, idTitle, idMessage, context, myCallback);
            Log.d("check flow", "isReady");
            return;
        }
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {

                Log.d("check flow", "onBillingSetupFinished" + billingResponseCode);

                getDetail(sku, price, idTitle, idMessage, context, myCallback);

            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d("check flow", "onBillingServiceDisconnected");
                _dialog.dismiss();
            }
        });
    }

    public void getDetail(final String _sku, final String _price, final int idTitle, final int idMessage, final Context context, final MyCallback myCallback) {
        List skuList = new ArrayList<String>();
        skuList.add(_sku);
        skuList.add("always_share");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                        if (responseCode == BillingClient.BillingResponse.OK
                                && skuDetailsList != null) {

                            String price = "3,99";
                            for (SkuDetails skuDetails : skuDetailsList) {
                                if (_sku.equals(skuDetails.getSku())) {
                                    price = skuDetails.getPrice();
                                }
                                if("always_share".equals(skuDetails.getSku())){
                                    proPrice = skuDetails.getPrice();
                                }
                            }
                            getPurchases(_sku, price, idTitle, idMessage, mBillingClient, context, myCallback);
                        }
                    }
                });
    }


    private void getPurchases(final String _sku, final String price, int idTitle, int idMessage, final BillingClient mBillingClient, final Context context, final MyCallback myCallback){
        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        Log.d("check flow", "getPurchases");
        try{
            if(purchasesResult != null && purchasesResult.getPurchasesList().size() > 0){
                for (Purchase purchase : purchasesResult.getPurchasesList()) {
//                    if(purchase.getSku().equalsIgnoreCase("android.test.purchased")) {
                    if(purchase.getSku().equalsIgnoreCase(_sku)) {
                        Log.d("check flow", "getPurchases if");
//                        mBillingClient.consumeAsync(purchase.getPurchaseToken(), new ConsumeResponseListener() {
//                            @Override
//                            public void onConsumeResponse(int responseCode, String purchaseToken) {
//                                myCallback.callBack();
//                                Log.d("check flow", "onConsumeResponse");
//                                _dialog.dismiss();
//                            }
//                        });
                        myCallback.callBack();
                        _dialog.dismiss();
                        return;
                    }
                }
            }
        } catch (Exception e){}


        // SE PASSOU PELO FOR E NÃO ENCONTROU O ITEM, ENTÃO PERGUNTA SE DESEJA COMPRAR
        question(_sku, price, mBillingClient, idTitle, idMessage, context, myCallback);

    }

    private void question(final String _sku, final String price, final BillingClient mBillingClient, int idTitle, int idMessage, final Context context, final MyCallback myCallback){
        Log.d("check flow", "question");
        _dialog.dismiss();
        new AlertDialog.Builder(context)
                .setTitle(idTitle)
                .setMessage(idMessage)
                .setPositiveButton((context.getString(R.string.baseEnxoval_dialog_btn_buy_now)) + " " + price, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onBillingClientConnected(_sku, mBillingClient, context, myCallback);
                    }
                })
                .setNegativeButton(context.getString(R.string.baseEnxoval_dialog_share_download) + " " + proPrice, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=br.com.enxoval.pro")));
                        } catch (ActivityNotFoundException anfe) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=br.com.enxoval.pro")));
                        }
                    }
                })
                .setNeutralButton(R.string.btn_not_now, null)
                .show();
        return;
    }

    private void onBillingClientConnected(String _sku, BillingClient mBillingClient, final Context context, MyCallback myCallback){
        _dialog.show();

        Log.d("check flow", "onBillingClientConnected");
        // The billing client is ready. You can query purchases here.
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSku(_sku)
                .setType(BillingClient.SkuType.INAPP)
                .build();
        int responseCode = mBillingClient.launchBillingFlow((Activity) context, flowParams);
        if(responseCode == 7) {
            myCallback.callBack();
            Log.d("check flow", "onBillingClientConnected if 2");
            _dialog.dismiss();

        }
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}

