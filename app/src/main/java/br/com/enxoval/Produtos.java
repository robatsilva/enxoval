package br.com.enxoval;

import android.content.Context;
import android.database.Cursor;

import java.util.Locale;

import br.com.mvc.Db_mvc;
import br.com.mvc.Metodos_auxiliares;

public class Produtos {
	private String produto;
	private int sugestao;
	private float comprado;
	private long id;
	private Integer suca_id;
	static Db_mvc database;
	
	
	public static Cursor getProdutosSubcategoria(Context context, String suca_id) throws Exception
	{		
		database = new Db_mvc(Db_mvc.getContext(context));
		Cursor c = null;
		c = database.select("select " + (Locale.getDefault().getLanguage().equals("pt") ? "prod_nome" : "prod_nome_en") + " as prod_nome, prod_qtd_comprada, " + Sistema.getMesNascimento(context) + ", prod_id as _id from produtos where (prod_sexo = 'U' or prod_sexo = '" + Sistema.getSexo(context) + "') and prod_suca_id = " + suca_id + " order by prod_nome");
		return c;
	}
	
	public static Cursor getProdutosTodos(Context context, String suca_id, String nome_produto) throws Exception
	{		
		database = new Db_mvc(Db_mvc.getContext(context));
		Cursor c = null;
		c = database.select("select " + (Locale.getDefault().getLanguage().equals("pt") ? "prod_nome" : "prod_nome_en") + " as prod_nome, prod_qtd_comprada, " + Sistema.getMesNascimento(context) + ", prod_id as _id from produtos where (prod_sexo = 'U' or prod_sexo = '" + Sistema.getSexo(context) + "') and prod_nome like '%" + nome_produto + "%' order by prod_nome");
		return c;
	}
	
	public static Cursor getProdutosTenho(Context context) throws Exception
	{		
		database = new Db_mvc(Db_mvc.getContext(context));
		Cursor c = null;
		c = database.select("select " + (Locale.getDefault().getLanguage().equals("pt") ? "prod_nome" : "prod_nome_en") + " as prod_nome, prod_qtd_comprada, " + Sistema.getMesNascimento(context) + ", prod_id as _id from produtos where (prod_sexo = 'U' or prod_sexo = '" + Sistema.getSexo(context) + "') and prod_qtd_comprada >= " + Sistema.getMesNascimento(context) + " order by prod_nome");
		return c;
	}
	
	public static Cursor getProdutosFalta(Context context) throws Exception
	{		
		database = new Db_mvc(Db_mvc.getContext(context));
		Cursor c = null;
		c = database.select("select " + (Locale.getDefault().getLanguage().equals("pt") ? "prod_nome" : "prod_nome_en") + " as prod_nome, prod_qtd_comprada, " + Sistema.getMesNascimento(context) + ", prod_id as _id from produtos where (prod_sexo = 'U' or prod_sexo = '" + Sistema.getSexo(context) + "') and prod_qtd_comprada < " + Sistema.getMesNascimento(context) + " order by prod_nome");
		return c;
	}
	
	public String getProduto() {
		return produto;
	}
	public void setProduto(String produto) {
		this.produto = produto;
	}
	public int getSugestao() {
		return sugestao;
	}
	public void setSugestao(int sugestao) {
		this.sugestao = sugestao;
	}
	public float getComprado() {
		return comprado;
	}
	public void setComprado(float d) {
		this.comprado = d;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Integer getSuca_id() {
		return suca_id;
	}

	public void setSuca_id(Integer suca_id) {
		this.suca_id = suca_id;
	}
	
	public void soma_comprado(Context context, double d)
	{
		database = new Db_mvc(Db_mvc.getContext(context));
		try {
			database.query("update produtos set prod_qtd_comprada = " + String.valueOf(d) + " where prod_id = " + String.valueOf(this.getId()));
		} catch (Exception e) {
			Metodos_auxiliares.mensagem(context.getString(R.string.error_update_qtt), context);
		}
	}

	public static String novo_produto(Context context, Produtos p)
	{
		database = new Db_mvc(Db_mvc.getContext(context));
		try {
			database.query("insert into produtos (prod_nome, prod_nome_en, prod_descricao, prod_sexo, prod_frio, prod_calor, prod_qtd_comprada, prod_suca_id) values (" +
                    "'"+p.getProduto()+"', '"+p.getProduto()+"', '', 'U', "+String.valueOf(p.getSugestao())+", "+String.valueOf(p.getSugestao())+", "+String.valueOf(p.getComprado())+", "+String.valueOf(p.getSuca_id())+")");
			return context.getString(R.string.success_add_product);

		} catch (Exception e) {
			return context.getString(R.string.error_add_product);
		}
	}

	public static String editar_produto(Context context, Produtos p)
	{
		database = new Db_mvc(Db_mvc.getContext(context));
		try {
			database.query("update produtos set prod_nome = '"+p.getProduto()
					+"', prod_nome_en = "+p.getProduto()
					+"', prod_qtd_comprada = "+String.valueOf(p.getComprado())
					+", prod_frio = " +String.valueOf(p.getSugestao())
					+", prod_calor = " +String.valueOf(p.getSugestao())
					+" where prod_id = "+String.valueOf(p.getId()));
			return context.getString(R.string.success_edit_product);

		} catch (Exception e) {
			return context.getString(R.string.error_edit_product);
		}
	}


	public Boolean excluir(Context context, Produtos p) {
		database = new Db_mvc(Db_mvc.getContext(context));
		try {
			database.query("delete from produtos where prod_id = "+String.valueOf(p.getId()));
			return true;


		} catch (Exception e) {
			return false;
		}
	}
}
