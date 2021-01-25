package br.com.enxoval;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import br.com.mvc.EnxovalClient;
import br.com.mvc.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Tracker mTracker;
		AnalyticsApplication application = (AnalyticsApplication) getApplication();
		mTracker = application.getDefaultTracker();
		mTracker.setScreenName(this.getClass().getName());
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());

		setContentView(R.layout.activity_info);
		final EditText email = (EditText) findViewById(R.id.info_txt_email);
		Button comecar = (Button) findViewById(R.id.info_btn_comecar);
		try {
			if(BuildConfig.isPro)
				copySqliteToPro();

			if (Sistema.getStatusConfigSistema(getApplicationContext()) != null)
				if (Sistema.getStatusConfigSistema(getApplicationContext()) == 1) {
                    Intent it;
                    if(BuildConfig.isPro)
                        it = new Intent(InfoActivity.this, CategoriasActivity.class);
					else
					    it = new Intent(InfoActivity.this, TabsActivity.class);
					startActivity(it);
					finish();
					return;
				}
		}
		catch (Exception e){
			e.printStackTrace();
		}

		comecar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!email.getText().toString().isEmpty())
				{
					EnxovalClient client = ServiceGenerator.createService(EnxovalClient.class);
					Call<String> call = client.email(email.getText().toString());
					call.enqueue(new Callback<String>() {
						@Override
						public void onResponse(Call<String> call, Response<String> response) {

						}

						@Override
						public void onFailure(Call<String> call, Throwable t) {

						}
					});
				}
                Intent it;
				if(BuildConfig.isPro)
				    it = new Intent(getApplicationContext(), CategoriasActivity.class);
                else
                    it = new Intent(getApplicationContext(), TabsActivity.class);
                startActivity(it);
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getMenuInflater().inflate(R.menu.info, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {

			case R.id.menu_facebook_info:
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);

				intent.setData(Uri.parse("http://www.facebook.com.br/enxovaldebebe"));
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}

	private void copySqliteToPro(){
		File dbLocal = new File("/data/data/br.com.enxoval.pro/databases/enxoval");
		if(!dbLocal.exists())
			try {
				copy(new File("/data/data/br.com.enxoval/databases/enxoval"), new File("/data/data/br.com.enxoval.pro/databases/enxoval"));
			}
			catch (IOException e){
				e.printStackTrace();
			}
	}
	private void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}
	
}
