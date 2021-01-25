package br.com.enxoval;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import br.com.mvc.Metodos_auxiliares;
import br.com.utils.Billing;
import br.com.utils.MyCallback;
import crl.android.pdfwriter.Base;
import crl.android.pdfwriter.PDFWriter;
import crl.android.pdfwriter.PaperSize;
import crl.android.pdfwriter.StandardFonts;


/**
 * Created by robinson on 4/11/17.
 */

public abstract class BaseEnxovalActivity extends AppCompatActivity {
    static String suca_id;
    public ImageView image;
    public LinearLayout linearLayout1;
    public AdView mAdView;
    private BillingClient mBillingClient;
//    public ProgressDialog dialog;


    protected void onCreate(Bundle savedInstanceState, int view_id, int linear_layout_id) {
        super.onCreate(savedInstanceState);
        setContentView(view_id);
        image = new ImageView(this);
        linearLayout1 = (LinearLayout) findViewById(linear_layout_id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Obtain the shared Tracker instance.
        Tracker mTracker;
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(this.getClass().getName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getMenuInflater().inflate(R.menu.main, menu);
        menu.getItem(0).setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent it;

        switch (id) {

            case R.id.menu_facebook:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);

                intent.setData(Uri.parse("http://www.facebook.com.br/enxovaldebebe"));
                startActivity(intent);
                return true;

            case R.id.menu_novo:
                add();
                return true;

            case R.id.menu_item1:
                it = new Intent(getApplicationContext(), ProdutosActivity.class);
                it.putExtra("menu_item1", true);
                startActivity(it);
                return true;

            case R.id.menu_item2:
                it = new Intent(getApplicationContext(), ProdutosActivity.class);
                it.putExtra("menu_item2", true);
                startActivity(it);
                return true;

            case R.id.menu_item7:
                it = new Intent(getApplicationContext(), ProdutosActivity.class);
                it.putExtra("menu_item7", true);
                startActivity(it);
                return true;

            case R.id.menu_item8:
                Metodos_auxiliares.mensagem(getString(R.string.baseEnxoval_about_message), this);
                return true;

            case R.id.menu_item6:
                it = new Intent(getApplicationContext(), ConfiguracoesActivity.class);
                startActivity(it);
                return true;

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                if(!this.getClass().getSimpleName().equals(CategoriasActivity.class.getSimpleName()))
                    finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void add(){
        if(!BuildConfig.isPro){

//            new Billing().purchaseItem("android.test.purchased", "0", R.string.baseEnxoval_dialog_share_title, R.string.baseEnxoval_dialog_add_message, BaseEnxovalActivity.this, new MyCallback(){
            new Billing().purchaseItem("add", "0", R.string.baseEnxoval_dialog_share_title, R.string.baseEnxoval_dialog_add_message, BaseEnxovalActivity.this, new MyCallback(){
                @Override
                public void callBack() {
                    finishAdd();
                }
            });
//            purchaseItem();
            return;
        }
        finishAdd();

    }

    public void finishAdd(){
        Intent it = new Intent(getApplicationContext(), EditarProdutosActivity.class);
        it.putExtra("suca_id", String.valueOf(suca_id));
        startActivityForResult(it, 1);
    }

    public void share(View view) {
        // Verifica se tem permissão de armazenamento
        if (!requestStorePermission()){
            return;
        }

        if(!BuildConfig.isPro){

            new Billing().purchaseItem("share", "0", R.string.baseEnxoval_dialog_share_title, R.string.baseEnxoval_dialog_share_message, BaseEnxovalActivity.this, new MyCallback(){
                @Override
                public void callBack() {
                    finishShare();
                }
            });
//            purchaseItem();
            return;
        }
        finishShare();
    }

    public void outputToFile(String fileName, String pdfContent, String encoding, ProgressDialog dialog) {
        File newFile = new File(Environment.getExternalStorageDirectory() + "/" + fileName);

        try {
            newFile.createNewFile();

            try {
                FileOutputStream pdfFile = new FileOutputStream(newFile);
                pdfFile.write(pdfContent.getBytes(encoding));
                pdfFile.close();
                sharePdf(newFile, dialog);
            } catch(FileNotFoundException e) {
                // ...
            }

        } catch(IOException e) {
            // ...
        }
    }

    private void finishShare(){
        final ProgressDialog _dialog = ProgressDialog.show(this, "",
                this.getString(R.string.loading), true, true);

        PDFWriter writer = new PDFWriter(PaperSize.FOLIO_WIDTH, PaperSize.A4_HEIGHT);

        int x = 20;
        int y = PaperSize.A4_HEIGHT - 20;
        writer.addText(x, y, 20, getString(R.string.pdf_title));
        y -= 60;
        for (Produtos p : ProdutosActivity.produtos
                ) {
            writer.addText(x, y, 15, p.getProduto());

            String string;
            float n = p.getComprado();
            if (n % 1 == 0) {
                string = String.valueOf((int) n);
            } else {
                string = String.valueOf(n).replace(".", ",");
            }

            writer.addText(x, y - 20, 12, getString(R.string.lbl_ja_tenho) + string + getString(R.string.pdf_msg_tip) + p.getSugestao());
            y -= 60;
            if(y <= 30){
                y = PaperSize.A4_HEIGHT - 20;
                writer.newPage();
            }
        }

        outputToFile(getString(R.string.pdf_file_name), writer.asString(), "ISO-8859-1", _dialog);
    }

    public void sharePdf(File file, ProgressDialog dialog){
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        if(file.exists()) {
            intentShareFile.setType("application/pdf");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file.getAbsolutePath()));

            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                    "Sharing File...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

            startActivity(Intent.createChooser(intentShareFile, getString(R.string.pdf_share)));
            dialog.dismiss();
        }
    }

    boolean requestStorePermission(){
        if (ContextCompat.checkSelfPermission(BaseEnxovalActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(BaseEnxovalActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(BaseEnxovalActivity.this)
                        .setTitle(R.string.permission_title)
                        .setMessage(R.string.permission_write_storage)
                        .setPositiveButton(R.string.permission_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(
                                    BaseEnxovalActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    2);
                            }
                        })
                        .show();

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                        BaseEnxovalActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2);

            }
            return false;
        }
        return true;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
