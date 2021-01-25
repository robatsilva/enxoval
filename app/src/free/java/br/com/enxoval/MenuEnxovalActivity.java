package br.com.enxoval;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.mvc.Ads;
import br.com.mvc.EnxovalClient;
import br.com.mvc.Metodos_auxiliares;
import br.com.mvc.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class MenuEnxovalActivity extends BaseEnxovalActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	static List<Ads> ads;
	public ImageView image;
	private Integer i_ads = 1;

	public static Location location;
	public static Timer timerAtual;
	public static TimerTask task;
	private final Handler handler = new Handler();
    private static boolean canShowVersaoProAnuncio = true;

	private static InterstitialAd interstitialAd;


	GoogleApiClient mGoogleApiClient;

	protected void onCreate(Bundle savedInstanceState, int view_id, int linear_layout_id) {
		super.onCreate(savedInstanceState, view_id, linear_layout_id);

		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId("ca-app-pub-8027025344367466/2242228233");
//
		initGoogleApiCliente();
	}

	private void requestNewInterstitial()
	{
		if(interstitialAd == null)
			return;
		AdRequest adRequest;
		if(BuildConfig.DEBUG)
			 adRequest = new AdRequest.Builder()
					.addTestDevice("3764AA3EF5333D624DB18AAA5187FA00")
					.build();
		else
			adRequest = new AdRequest.Builder()
					.build();
//
		interstitialAd.loadAd(adRequest);
	}

	private void initGoogleApiCliente() {
		if (ActivityCompat.checkSelfPermission(MenuEnxovalActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(
					MenuEnxovalActivity.this,
					new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
					0);
		}
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(MenuEnxovalActivity.this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(LocationServices.API)
					.build();
		}
	}

	@Override
	public void onStart() {
        if(!(mGoogleApiClient == null))
		    mGoogleApiClient.connect();
		super.onStart();
	}

	@Override
	public void onStop() {
        if(!(mGoogleApiClient == null))
		    mGoogleApiClient.disconnect();
		super.onStop();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		setMyLocation();
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		setLocation(null);
	}

	@Override
	public void onConnectionSuspended(int i) {
		setLocation(null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Resume the AdView.
		requestNewInterstitial();

		if(mAdView != null)
			mAdView.resume();
		else {
			try {
				if (ads != null && ads.size() > 0) {
					ativaTimer();
				}
			}catch (Exception e){}
		}
	}

	@Override
	public void onPause() {
		if(mAdView != null)
			mAdView.pause();
		else {
            if (null != task)
                task.cancel();
            if (timerAtual != null) {
                timerAtual.cancel();
                timerAtual.purge();
            }
                //try {linearLayout1.removeView(image);} catch (Exception e){}
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		// Destroy the AdView.
		if(mAdView != null)
			mAdView.destroy();
		super.onDestroy();
	}

	private void cria_ads() {
		try {
			if ((ads == null || ads.size() == 0) && mAdView == null) {
				accessWebService();
			}
		}
		catch (Exception e)
		{}
	}

	// Async Task to access the web
	public void accessWebService() {
		EnxovalClient client = ServiceGenerator.createService(EnxovalClient.class);
		Call<List<Ads>> call = client.getAds(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
		call.enqueue(new Callback<List<Ads>>() {
			@Override
			public void onResponse(Call<List<Ads>> call, Response<List<Ads>> response) {
				if(response.body() != null && response.body().size() > 0) {
					i_ads = 1;
					if (timerAtual != null)
						timerAtual.cancel();
					ads = response.body();
					try {
						ativaTimer();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else
					cria_admob("banner");
			}

			@Override
			public void onFailure(Call<List<Ads>> call, Throwable t) {
				cria_admob("banner");
			}
		});
	}

	private void ativaTimer() throws Exception{
		if(ads.size() == 0)
			return;

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		image.setLayoutParams(params);
		if(image.getParent() == null)
			linearLayout1.addView(image);

		if(ads.size() == 1) {
			setImage();
			return;
		}
		task = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						try {
							i_ads++;
							if (i_ads > ads.size()) {
								i_ads = 1;
							}
							setImage();

						}catch (Exception e) {
							return;
						}
						//linearLayout1.addView(image);
					}
				});
			}
		};

		timerAtual = new Timer();
		timerAtual.schedule(task, 2000, 10000);
	}

	public void setImage(){
		final int ad_atual = i_ads - 1;
		Metodos_auxiliares.loadImage(ads.get(ad_atual).getImagem(), MenuEnxovalActivity.this, new SimpleImageLoadingListener(){
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				image.setImageBitmap(loadedImage);

				image.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						conta_clique(String.valueOf(ads.get(ad_atual).getId()));
						AlertDialog.Builder builder = new AlertDialog.Builder(MenuEnxovalActivity.this);
						builder.setTitle(R.string.menuEnxoval_link_title);
						builder.setItems(R.array.menu_poup_up_ad_produto, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if(which == 0)
								{
									Intent i=new Intent(android.content.Intent.ACTION_SEND);
									i.setType("text/plain");
									i.putExtra(android.content.Intent.EXTRA_SUBJECT,getString(R.string.menuEnxoval_share_product_title));
									i.putExtra(android.content.Intent.EXTRA_TEXT, ads.get(ad_atual).getLink());
									startActivity(Intent.createChooser(i,getString(R.string.menuEnxoval_share_product_title2)));
									Tracker mTracker;
									mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
									mTracker.send(new HitBuilders.EventBuilder()
											.setCategory("Share_ad")
											.setAction("Click share ad - " + ads.get(ad_atual).getId())
											.build());
								}

								if(which == 1)
								{
									try {

										String uri = Uri.parse(ads.get(ad_atual).getLink()).toString();
										if (uri.equals(""))
											return;

										Intent intent = new Intent();
										intent.setAction(Intent.ACTION_VIEW);
										intent.addCategory(Intent.CATEGORY_BROWSABLE);

										intent.setData(Uri.parse(uri));
										startActivity(intent);

										conta_clique(ads.get(ad_atual).getId());
									} catch (Exception e) {
										return;
									}
								}

							}
						});

						//builder.setInverseBackgroundForced(true);
						builder.create();
						builder.show();
					}
				});
				conta_impressao(ads.get(ad_atual).getId());
			}
		});
	}

	private void cria_admob(String adSize)
	{
		try {
			if (!isOnline())
				return;

            switch (adSize){
                case "banner":
					mAdView = new AdView(this);
					mAdView.setAdUnitId("ca-app-pub-8027025344367466/3021603033");
					// Create an ad request.
					AdRequest.Builder adRequestBuilder = new AdRequest
							.Builder();
                    mAdView.setAdSize(AdSize.SMART_BANNER);

					if(BuildConfig.DEBUG)
                    	// Optionally populate the ad request builder.
                    	adRequestBuilder.addTestDevice("3764AA3EF5333D624DB18AAA5187FA00");

                    // Start loading the ad.
                    mAdView.loadAd(adRequestBuilder.build());

                    mAdView.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                        }

                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            super.onAdFailedToLoad(errorCode);
                        }

                        @Override
                        public void onAdLeftApplication() {
                            super.onAdLeftApplication();
                        }

                        @Override
                        public void onAdOpened() {
                            super.onAdOpened();
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            // Add the AdView to the view hierarchy.
                            try{linearLayout1.addView(mAdView);}catch (Exception e){}
                        }
                    });
                    break;

                case "full":
					if(interstitialAd.isLoaded())
						interstitialAd.show();
                    break;
            }



		}
		catch (Exception e){}
	}

	public void conta_clique(final String id) {
		EnxovalClient client = ServiceGenerator.createService(EnxovalClient.class);
		Call<List<Ads>> call = client.countClicAd(id);
		call.enqueue(new Callback<List<Ads>>() {
			@Override
			public void onResponse(Call<List<Ads>> call, Response<List<Ads>> response) {

			}

			@Override
			public void onFailure(Call<List<Ads>> call, Throwable t) {

			}
		});
	}

	public void conta_impressao(final String id)
	{
		EnxovalClient client = ServiceGenerator.createService(EnxovalClient.class);
		Call<List<Ads>> call = client.countImpressaoAd(id);
		call.enqueue(new Callback<List<Ads>>() {
			@Override
			public void onResponse(Call<List<Ads>> call, Response<List<Ads>> response) {

			}

			@Override
			public void onFailure(Call<List<Ads>> call, Throwable t) {

			}
		});
	}

	public void setLocation(Location location) {
		if(location == null) {
			location = new Location("");
			location.setLongitude(0);
			location.setLatitude(0);
		}
		this.location = location;
		cria_ads();
	}

	@Override
	public void onBackPressed() {
		SharedPreferences settings = getSharedPreferences("Preferences", 0);
        if(canShowVersaoProAnuncio) {
            new AlertDialog.Builder(MenuEnxovalActivity.this)
                    .setTitle(R.string.menuEnxoval_dialog_back_title)
                    .setMessage(R.string.menuEnxoval_dialog_back_msg)
                    .setNeutralButton(R.string.btn_maybe, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            canShowVersaoProAnuncio = false;
                            MenuEnxovalActivity.this.onSuperBackPressed();
							if(isOnline())
								cria_admob("full");
                        }
                    })
                    .setPositiveButton(R.string.menuEnxoval_dialog_back_btn_buy, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
							try {
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=br.com.enxoval.pro")));
							} catch (ActivityNotFoundException anfe) {
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=br.com.enxoval.pro")));
							}

							canShowVersaoProAnuncio = false;
                            MenuEnxovalActivity.this.onSuperBackPressed();
                        }
                    })
					.show();
        }else {
            if(isOnline())
                cria_admob("full");

			MenuEnxovalActivity.this.onSuperBackPressed();
        }

		/*if(settings.getBoolean("versao_pro_gastos", true)) {
			new AlertDialog.Builder(MenuEnxovalActivity.this)
					.setTitle("Controle seus gastos")
					.setMessage("Marque também o valor dos produtos baixando a versão Pro!")
					.setNeutralButton("Talvez depois", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences settings = getSharedPreferences("Preferences", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("versao_pro_gastos", false);
                            editor.putBoolean("versao_pro_anuncio", true);
                            //Confirma a gravação dos dados
                            editor.commit();
                            MenuEnxovalActivity.this.onSuperBackPressed();
						}
					})
					.setPositiveButton("Baixar", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							try {
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=br.com.enxoval.pro")));
							} catch (android.content.ActivityNotFoundException anfe) {
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=br.com.enxoval.pro")));
							}

							SharedPreferences settings = getSharedPreferences("Preferences", 0);
							SharedPreferences.Editor editor = settings.edit();
							editor.putBoolean("versao_pro_gastos", false);
							editor.putBoolean("versao_pro_anuncio", true);
							//Confirma a gravação dos dados
							editor.commit();
							MenuEnxovalActivity.this.onSuperBackPressed();
						}
					})
					.show();
		}
		else
		{
			if(settings.getBoolean("versao_pro_anuncio", true)) {
				new AlertDialog.Builder(MenuEnxovalActivity.this)
						.setTitle("Versão sem anúncios")
						.setMessage("Adquira a versão Pro e livre-se dos anúncios")
						.setNeutralButton("Talvez depois", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences settings = getSharedPreferences("Preferences", 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("versao_pro_gastos", true);
                                editor.putBoolean("versao_pro_anuncio", false);
                                //Confirma a gravação dos dados
                                editor.commit();
                                MenuEnxovalActivity.this.onSuperBackPressed();
							}
						})
						.setPositiveButton("Baixar", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
								try {
									startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=br.com.enxoval.pro")));
								} catch (android.content.ActivityNotFoundException anfe) {
									startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=br.com.enxoval.pro")));
								}

								SharedPreferences settings = getSharedPreferences("Preferences", 0);
								SharedPreferences.Editor editor = settings.edit();
								editor.putBoolean("versao_pro_gastos", true);
								editor.putBoolean("versao_pro_anuncio", false);
								//Confirma a gravação dos dados
								editor.commit();
								MenuEnxovalActivity.this.onSuperBackPressed();
							}
						})
						.show();
			}else
				MenuEnxovalActivity.this.onSuperBackPressed();
		}*/
	}

	public void onSuperBackPressed(){
		super.onBackPressed();
	}

	private void setMyLocation() {
		if (ActivityCompat.checkSelfPermission(MenuEnxovalActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MenuEnxovalActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		Location location = LocationServices.FusedLocationApi.getLastLocation(
				mGoogleApiClient);

		setLocation(location);
        if(this instanceof TabsActivity)
            Metodos_auxiliares.setCameraPosition(TabsActivity.getmMap(), location);
	}

    @Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == 0) {
			if (permissions.length > 0 &&
					permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION &&
					grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				setMyLocation();

			} else
				setLocation(location);
		}
		if(requestCode == 2) {
			if (permissions.length > 0 &&
					permissions[0].equals(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
					grantResults[0] == PackageManager.PERMISSION_DENIED) {
				boolean showRationale = false;
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
					showRationale = shouldShowRequestPermissionRationale(permissions[0]);
					if (!showRationale) {
						new AlertDialog.Builder(MenuEnxovalActivity.this)
								.setTitle(R.string.permission_title)
								.setMessage(R.string.menuEnxoval_dialog_permission_msg)
								.setNeutralButton(R.string.btn_no, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
									}
								})
								.setPositiveButton(R.string.btn_config, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
												Uri.fromParts("package", getPackageName(), null));
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										startActivity(intent);
									}
								})
								.show();
						return;
					}
				}
			} else {
				share(null);
			}
		}

	}
}
