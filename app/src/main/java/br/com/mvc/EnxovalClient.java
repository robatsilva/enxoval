package br.com.mvc;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by robinson on 23/08/2016.
 */
public interface EnxovalClient {
    @GET("ads/listar")
    Call<List<Ads>> getAds(@Query("latitude") String latitudo, @Query("longitude") String longitude);
    @POST("ads/contarClique/{id}")
    Call<List<Ads>> countClicAd(@Path("id") String id);
    @POST("ads/contarImpressao/{id}")
    Call<List<Ads>> countImpressaoAd(@Path("id") String id);

    @GET("marcadores/listar")
    Call<List<Marcadores>> getMarcadores();

    @POST("marcadores/contarClique/{id}")
    Call<List<Marcadores>> countClickMarker(@Path("id") String id);

    @POST("marcadores/contarCliqueJanela/{id}")
    Call<List<Marcadores>> countClickMarkerWindow(@Path("id") String id);

    @GET("adProdutos/listar/{id}")
    Call<List<AdsProdutos>> getAdsProdutos(@Path("id") String id);

    @POST("adProdutos/contarClique/{id}")
    Call<List<AdsProdutos>> countClicAdProduto(@Path("id") String id);
    @POST("adProdutos/contarImpressao/{id}")
    Call<List<AdsProdutos>> countImpressaoAdProduto(@Path("id") String id);

    @FormUrlEncoded
    @POST("sistema/email")
    Call<String> email(@Field("email") String email);
}
