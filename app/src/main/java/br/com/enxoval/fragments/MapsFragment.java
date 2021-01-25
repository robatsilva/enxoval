package br.com.enxoval.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.HashMap;
import java.util.List;

import br.com.enxoval.AnalyticsApplication;
import br.com.enxoval.MenuEnxovalActivity;
import br.com.enxoval.R;
import br.com.enxoval.TabsActivity;
import br.com.mvc.EnxovalClient;
import br.com.mvc.Marcadores;
import br.com.mvc.Metodos_auxiliares;
import br.com.mvc.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap mMap;
    Bitmap bmp;
    List<Marcadores> marcadores;
    HashMap<String, Marcadores> mapMarcadores;
    private float zoom_old = 0;
    private Tracker mTracker;

    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_maps, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mapView = (MapView) view.findViewById(R.id.map);
//        mapView.onCreate(savedInstanceState);
//        mapView.onResume();
//        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        TabsActivity.setmMap(mMap);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);

        getMarkers();
        if(MenuEnxovalActivity.location == null)
            return;

        Metodos_auxiliares.setCameraPosition(mMap, MenuEnxovalActivity.location);
    }

    private void setListeners() {
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                setMarkers();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                bmp = null;
                ImageLoader imageLoader = ImageLoader.getInstance();
                String uri;

                if(mapMarcadores.get(marker.getTitle()).imagemUrlMarcador != null && !mapMarcadores.get(marker.getTitle()).imagemUrlMarcador.isEmpty())
                {
                    uri = mapMarcadores.get(marker.getTitle()).imagemUrlMarcador;
                    imageLoader.loadImage(uri, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            bmp = loadedImage;
                            marker.hideInfoWindow();
                            marker.showInfoWindow();
                        }
                    });
                }
                mMap.setPadding(0, dpToPx(200), 0, 0);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Marker")
                        .setAction("Click marker - " + mapMarcadores.get(marker.getTitle()).idMarcador)
                        .build());
                countClickMarker(mapMarcadores.get(marker.getTitle()).idMarcador);
                return false;
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(final Marker marker) {

                Marcadores marcador = mapMarcadores.get(marker.getTitle());
                View myContentView = getActivity().getLayoutInflater().inflate(
                        R.layout.janela_marcador, null);
                final ImageView imagem_loja = ((ImageView) myContentView
                        .findViewById(R.id.janela_marcador_imagem));
                TextView nome_loja = ((TextView) myContentView
                        .findViewById(R.id.janela_marcdor_nome_loja));
                TextView endereco_loja = ((TextView) myContentView
                        .findViewById(R.id.janela_marcdor_endereco_loja));
                TextView telefone_loja = ((TextView) myContentView
                        .findViewById(R.id.janela_marcdor_telefone_loja));
                TextView descricao_loja = ((TextView) myContentView
                        .findViewById(R.id.janela_marcdor_descricao_loja));
                TextView promocao_loja = ((TextView) myContentView
                        .findViewById(R.id.janela_marcdor_promocao_loja));
                TextView url_loja = ((TextView) myContentView
                        .findViewById(R.id.janela_marcdor_url_loja));
                TextView label_promocao = ((TextView) myContentView
                        .findViewById(R.id.janela_marcdor_label_projocao));
                TextView label_url = ((TextView) myContentView
                        .findViewById(R.id.janela_marcdor_label_url));

                imagem_loja.setImageBitmap(bmp);
                nome_loja.setText(marcador.nomeMarcador);
                endereco_loja.setText(marcador.enderecoMarcador);
                if(marcador.telefoneMarcador != null && !marcador.telefoneMarcador.isEmpty())
                    telefone_loja.setText(marcador.telefoneMarcador);
                if(marcador.telefoneMarcador != null && !marcador.telefoneMarcador.isEmpty())
                    telefone_loja.setText(telefone_loja.getText().toString() + " / " + marcador.emailMarcdor);
                descricao_loja.setText(marcador.descricaoMarcador);
                if(marcador.promocaoMarcador != null && !marcador.promocaoMarcador.isEmpty()) {
                    label_promocao.setVisibility(View.VISIBLE);
                    promocao_loja.setVisibility(View.VISIBLE);
                    promocao_loja.setText(marcador.promocaoMarcador);
                }
                if(marcador.urlMarcador != null && !marcador.urlMarcador.isEmpty()){
                    label_url.setVisibility(View.VISIBLE);
                    url_loja.setVisibility(View.VISIBLE);
                    url_loja.setText(marcador.urlMarcador);
                }

                return myContentView;
            }

            @Override
            public View getInfoContents(final Marker marker) {
                return null;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Marcadores marcador = mapMarcadores.get(marker.getTitle());

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("MarkerWindow")
                        .setAction("Click marker window - " + marcador.idMarcador)
                        .build());

                countClickMarkerWindow(mapMarcadores.get(marker.getTitle()).idMarcador);

                try{
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(marcador.urlMarcador));
                    startActivity(browserIntent);
                }catch(Exception e){}
            }
        });

    }
    private void getMarkers() {
        EnxovalClient client = ServiceGenerator.createService(EnxovalClient.class);
        Call<List<Marcadores>> call = client.getMarcadores();
        call.enqueue(new Callback<List<Marcadores>>() {
            @Override
            public void onResponse(Call<List<Marcadores>> call, Response<List<Marcadores>> response) {
                try {
                    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).build();
                    ImageLoader.getInstance().init(config);

                    marcadores = response.body();
                    mapMarcadores = new HashMap<String, Marcadores>();
                    mapMarcadores.clear();
                    for (Marcadores marcador : marcadores) {
                        mapMarcadores.put(marcador.idMarcador, marcador);
                    }
                    setListeners();
                    setMarkers();
                }
                catch (Exception e){}
            }

            @Override
            public void onFailure(Call<List<Marcadores>> call, Throwable t) {

            }
        });
    }

    private void countClickMarker(String markerId)
    {
        EnxovalClient client = ServiceGenerator.createService(EnxovalClient.class);
        Call<List<Marcadores>> call = client.countClickMarker(markerId);
        call.enqueue(new Callback<List<Marcadores>>() {
            @Override
            public void onResponse(Call<List<Marcadores>> call, Response<List<Marcadores>> response) {

            }

            @Override
            public void onFailure(Call<List<Marcadores>> call, Throwable t) {

            }
        });
    }

    private void countClickMarkerWindow(String markerId)
    {
        EnxovalClient client = ServiceGenerator.createService(EnxovalClient.class);
        Call<List<Marcadores>> call = client.countClickMarkerWindow(markerId);
        call.enqueue(new Callback<List<Marcadores>>() {
            @Override
            public void onResponse(Call<List<Marcadores>> call, Response<List<Marcadores>> response) {
            }

            @Override
            public void onFailure(Call<List<Marcadores>> call, Throwable t) {

            }
        });
    }

    private void setMarkers()
    {
        if(marcadores == null)
            return;
        if(zoom_old == mMap.getCameraPosition().zoom)
            return;

        zoom_old = mMap.getCameraPosition().zoom;
        mMap.clear();
        for (Marcadores marcador : marcadores) {
            if(mMap.getCameraPosition().zoom >= Float.parseFloat(marcador.zoomMarcador)) {
                BitmapDescriptor icon;
                if(marcador.promocaoMarcador == null || marcador.promocaoMarcador.equalsIgnoreCase(""))
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_body);
                else
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_body_discount);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(marcador.latitudeMarcador), Double.parseDouble(marcador.longitudeMarcador)))
                        .title(marcador.idMarcador)
                        .icon(icon)
                );
            }
        }
    }
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if(isVisibleToUser) {
                mTracker = ((AnalyticsApplication) getActivity().getApplication()).getDefaultTracker();
                mTracker.setScreenName("Maps");
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                isOnline();
            }
            /*if(getContext() instanceof MenuEnxovalActivity)
            {
                if(((MenuEnxovalActivity) getContext()).mAdView != null) {
                    ((MenuEnxovalActivity) getContext()).mAdView.pause();
                    ((MenuEnxovalActivity) getContext()).mAdView.setVisibility(GONE);
                }
                else {
                    if (null != ((MenuEnxovalActivity) getContext()).task)
                        ((MenuEnxovalActivity) getContext()).task.cancel();
                    if (((MenuEnxovalActivity) getContext()).timerAtual != null) {
                        ((MenuEnxovalActivity) getContext()).timerAtual.cancel();
                        ((MenuEnxovalActivity) getContext()).timerAtual.purge();
                    //}
                    //try {((MenuEnxovalActivity) getContext()).linearLayout1.removeView(((MenuEnxovalActivity) getContext()).image);} catch (Exception e){}
                }
            }*/
        }
    }

    public void isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(!(netInfo != null && netInfo.isConnectedOrConnecting()))
            Toast.makeText(getActivity(), R.string.verify_conection, Toast.LENGTH_SHORT).show();
        else
        {
            if(marcadores == null && mMap != null)
                getMarkers();
        }
    }
}
