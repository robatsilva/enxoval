package br.com.mvc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by robinson on 23/08/2016.
 */
public class Ads {
    @SerializedName("ads_id")
    @Expose
    public String id;
    @SerializedName("ads_imagem")
    @Expose
    public String imagem;
    @SerializedName("ads_link")
    @Expose
    public String link;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
