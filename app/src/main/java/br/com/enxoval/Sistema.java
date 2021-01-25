package br.com.enxoval;

import android.content.Context;
import android.database.Cursor;

import br.com.mvc.Db_mvc;

public abstract class Sistema{
	static Db_mvc database;	
	public static String getSexo(Context context) throws Exception {
		String sexo;		 
		database = new Db_mvc(Db_mvc.getContext(context));
		Cursor c = database.select("select sexo from sistema");
		c.moveToFirst();
		sexo = c.getString(c.getColumnIndex("sexo"));
		c.close();
		return sexo;
	}
	public static void setSexo(String sexo, Context context) {
		database = new Db_mvc(Db_mvc.getContext(context));
		database.query("update sistema set " +
				"sexo = '" + sexo + "'");
	}
	public static String getMesNascimento(Context context) throws Exception {
		
		String mes_nascimento;
		database = new Db_mvc(Db_mvc.getContext(context));
		Cursor c = database.select("select mes_nascimento from sistema");
		c.moveToFirst();
		mes_nascimento = c.getString(c.getColumnIndex("mes_nascimento"));
		c.close();
		return mes_nascimento;
	}
	public static void setMesNascimento(String mes_nascimento, Context context) {
		database = new Db_mvc(Db_mvc.getContext(context));
		database.query("update sistema set " +
				"mes_nascimento = '" + mes_nascimento + "'");
	}
	public static Integer getCidade(Context context) throws Exception {
		Integer cidade;		 
		database = new Db_mvc(Db_mvc.getContext(context));
		Cursor c = database.select("select cidade from sistema");
		c.moveToFirst();
		cidade = Integer.parseInt(c.getString(c.getColumnIndex("cidade")));
		c.close();
		return cidade;
	}
	public static void setCidade(Integer cidade, Context context) {
		database = new Db_mvc(Db_mvc.getContext(context));
		database.query("update sistema set " +
				"cidade = '" + cidade + "'");
	}
	public static Integer getEstado(Context context) throws Exception {
		Integer estado;		 
		database = new Db_mvc(Db_mvc.getContext(context));
		Cursor c = database.select("select estado from sistema");
		c.moveToFirst();
		estado = Integer.parseInt(c.getString(c.getColumnIndex("estado")));
		c.close();
		return estado;
	}
	public static void setEstado(Integer estado, Context context) {
		database = new Db_mvc(Db_mvc.getContext(context));
		database.query("update sistema set " +
				"estado = '" + estado + "'");
	}
	
	public static Integer getStatusConfigSistema(Context context) throws Exception {
		Integer status_config_sistema;		 
		database = new Db_mvc(Db_mvc.getContext(context));
		Cursor c = database.select("select status_config_sistema from sistema");
		c.moveToFirst();
		status_config_sistema = Integer.parseInt(c.getString(c.getColumnIndex("status_config_sistema")));
		c.close();
		return status_config_sistema;
	}
	public static void setStatusConfigSistema(Integer status_config_sistema, Context context) {
		database = new Db_mvc(Db_mvc.getContext(context));
		database.query("update sistema set " +
				"status_config_sistema = '" + status_config_sistema + "'");
	}
	
	
}
