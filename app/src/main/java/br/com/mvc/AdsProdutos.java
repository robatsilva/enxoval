package br.com.mvc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by robinson on 23/08/2016.
 */
public class AdsProdutos {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("imagem")
    @Expose
    public String imagem;
    @SerializedName("link")
    @Expose
    public String link;
    @SerializedName("subcategoria_id")
    @Expose
    public String subcategoriaId;
    @SerializedName("produto_id")
    @Expose
    public String produtoId;

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

    public String getSubcategoriaId() {
        return subcategoriaId;
    }

    public void setSubcategoriaId(String subcategoriaId) {
        this.subcategoriaId = subcategoriaId;
    }

    public String getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(String produtoId) {
        this.produtoId = produtoId;
    }
}
