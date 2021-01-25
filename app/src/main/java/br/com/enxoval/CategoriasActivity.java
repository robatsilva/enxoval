package br.com.enxoval;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Locale;

import br.com.mvc.Db_mvc;
import br.com.mvc.Metodos_auxiliares;


public class CategoriasActivity extends MenuEnxovalActivity {
	ListView lst_categorias;
	ListView lst_sub_categorias;
	Db_mvc database = new Db_mvc(Db_mvc.getContext(this));
	SubMenuAdapter dataAdapter;
	String campos[] = {"cate_descricao"};
	ArrayList <String> categorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState, R.layout.categorias, R.id.categorias_lly_principal);

        try
        {
            if(Sistema.getStatusConfigSistema(getApplicationContext()) != null)
                if(Sistema.getStatusConfigSistema(getApplicationContext()) == 1)
                {
                    inicializa_elementos();
                    return;
                }
            Intent it = new Intent(CategoriasActivity.this, ConfigBebeActivity.class);
            startActivity(it);
            finish();
        }
        catch(Exception e)
        {
            Intent it = new Intent(CategoriasActivity.this, ConfigBebeActivity.class);
            startActivity(it);
            finish();
        }
    }
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        return true;
    }
    @SuppressWarnings("deprecation")
	private void inicializa_elementos() {
    	try
    	{
    		categorias = new ArrayList<String>();
    		Cursor c = carrega_categorias();
    		if(c == null)
    		{
    			Metodos_auxiliares.mensagem(getString(R.string.error_init_elements), getApplicationContext());
    			return;
    		}
    		if(c.moveToFirst())
    		{
    			do
    			{
    				categorias.add(c.getString(c.getColumnIndex("cate_descricao")));
    			}while (c.moveToNext());
    		}
    		c.close();
	    	lst_categorias = (ListView) findViewById(R.id.categorias_lst_categorias);	    	
	    	dataAdapter = new SubMenuAdapter(CategoriasActivity.this, categorias);
	    	lst_categorias.setAdapter(dataAdapter);
	    	
    	}
    	catch(Exception e)
		{
			Metodos_auxiliares.mensagem(getString(R.string.error_init_elements) + e.getMessage() + e.toString(), getApplicationContext());
		}
    	
    }
    
    private Cursor carrega_categorias()
    {
    	try
    	{
    		return database.select("select " + (Locale.getDefault().getLanguage().equals("pt") ? "cate_descricao" : "cate_descricao_en") + " as cate_descricao, cate_id as _id from categorias");
    	}
    	catch(Exception e)
    	{
    		Metodos_auxiliares.mensagem(getString(R.string.error_query) + e.getMessage() + e.toString(), getApplicationContext());
    		return null;
    	}
    	
    }
}
