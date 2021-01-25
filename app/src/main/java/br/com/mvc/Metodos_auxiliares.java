package br.com.mvc;

import android.content.Context;
import android.location.Location;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

import br.com.enxoval.MenuEnxovalActivity;

public abstract class Metodos_auxiliares {

	public static void mensagem(String msg, Context contexto)
	{
		/*
		AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
		builder.setMessage(msg)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           @Override
				public void onClick(DialogInterface dialog, int id) {
		                return;
		           }
		       });
	AlertDialog alert = builder.create();
	alert.show();
	*/
		Toast toast = Toast.makeText(contexto, msg, Toast.LENGTH_LONG);
		toast.show();
	}

	public static String get_escala(Context context)
	{
		int density= context.getResources().getDisplayMetrics().densityDpi;
		String escala = "";
		switch(density)
		{
			case DisplayMetrics.DENSITY_LOW:
				escala = "ldpi";
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				escala = "mdpi";
				break;
			case DisplayMetrics.DENSITY_HIGH:
				escala =  "hdpi";
				break;
			case DisplayMetrics.DENSITY_XHIGH:
				escala =  "xhdpi";
				break;
			case DisplayMetrics.DENSITY_XXHIGH:
				escala =  "xxhdpi";
				break;
			default:
				escala = "xxhdpi";
				break;
		}
		return escala;
	}

	public static void loadImage(String imagem, Context context, SimpleImageLoadingListener simpleImageLoadingListener)
	{
		String url = "http://www.authenticdesenvolvimento.com.br/imagem/" + imagem + "_" + get_escala(context) + ".png";
		if(!ImageLoader.getInstance().isInited()) {
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
			ImageLoader.getInstance().init(config);
		}
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.loadImage(url, simpleImageLoadingListener);
	}

	public static void loadImage(String imagem, Context context, ImageView imgView, com.squareup.picasso.Callback callback)
	{
		String url = "http://www.authenticdesenvolvimento.com.br/imagem/" + imagem + "_" + get_escala(context) + ".png";
		Picasso.with(context).load(url).into(imgView, callback);
	}

	public static void setCameraPosition(GoogleMap mMap, Location location){
		try {
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
					.zoom(6)                   // Sets the zoom
					.build();                   // Creates a CameraPosition from the builder

			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		}
		catch (Exception e){e.printStackTrace();}
	}


}
