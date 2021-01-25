package br.com.enxoval;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.mvc.Db_mvc;
import br.com.mvc.Metodos_auxiliares;

public class SubMenuAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater; 
	private List<String> itens;
	Context context;
	HashMap<String, Integer> suca_id = new HashMap<String, Integer>();;
	
	public SubMenuAdapter(Context context, List<String> itens) 
	{
		// TODO Auto-generated constructor stub
		 //Itens do listview
		this.itens = itens; 
		this.context = context;
		//Objeto responspável por pegar o Layout do item.
		mInflater = LayoutInflater.from(context); 
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return itens.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return itens.get(position);
		
	}

	@Override
	public long getItemId(int position) {
		return position;
		
	}

	@Override
	public View getView(final int position, View view, final ViewGroup parent) {
		// TODO Auto-generated method stub
		if(view == null)
			view = mInflater.inflate(R.layout.list_view_sub_menu, null);
		final String item = getItem(position);
		final int position_cate = position;
		TextView txt_item = (TextView) view.findViewById(R.id.list_view_sub_menu_textview1);
		final ListView lst_sub_categorias = (ListView) view.findViewById(R.id.list_view_sub_menu_list_view);
		lst_sub_categorias.setVisibility(View.GONE);
		String campos[] = {"suca_descricao"};
		
		txt_item.setText(item);
		Cursor c = null;
		try
    	{
    		c = carrega_sub_categorias(position);
    		if(c == null)
    		{
    			Metodos_auxiliares.mensagem(context.getString(R.string.error_init_elements), context);
    			return null;	
    		}
    		SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(context, R.layout.lst_view_padrao, carrega_sub_categorias(position) ,campos, new int[] {R.id.list_view_padrao_textview1});
	    	
			lst_sub_categorias.setAdapter(dataAdapter);

			lst_sub_categorias.setOnItemClickListener(new OnItemClickListener() {
	  		  @Override
	  		  public void onItemClick(AdapterView<?> parent, View view,
	  		    int position, long id) {
	  			Intent it = new Intent(context, ProdutosActivity.class);
				it.putExtra("suca_id", String.valueOf(suca_id.get(String.valueOf(position_cate) + String.valueOf(position))));
				it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				  Tracker mTracker = ((AnalyticsApplication) ((MenuEnxovalActivity) context).getApplication()).getDefaultTracker();
				  mTracker.send(new HitBuilders.EventBuilder()
						  .setCategory("Subcategoria")
						  .setAction(String.valueOf(suca_id.get(String.valueOf(position_cate) + String.valueOf(position))))
						  .build());

				context.startActivity(it);
				//((Activity) context).finish();
	  		  }
			});
    	}
		
    	catch(Exception e)
		{
			Metodos_auxiliares.mensagem(context.getString(R.string.error_init_elements) + e.getMessage() + e.toString(), context);
		}
		
		txt_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                ListView lt = (ListView) parent;
                lt.setSelection(position);
                if(lst_sub_categorias.getVisibility() == View.VISIBLE)
					lst_sub_categorias.setVisibility(View.GONE);
				else
				{
					lst_sub_categorias.setVisibility(View.VISIBLE);
					setListViewHeightBasedOnItems(lst_sub_categorias);
				}
			}
		});
		c.close();
		return view;
	}
	
	private Cursor carrega_sub_categorias(int cate_id)
	{
		Cursor c = null;
		try		{    	
						
			Db_mvc database = new Db_mvc(Db_mvc.getContext(context));
			c = database.select("select " + (Locale.getDefault().getLanguage().equals("pt") ? "suca_descricao" : "suca_descricao_en") + " as suca_descricao, suca_ordem, suca_id as _id from subcategorias where suca_cate_id = " + String.valueOf(cate_id + 1) + " order by suca_ordem");
			if(c.moveToFirst())
			{
	    		do
	    		{
	    			suca_id.put(String.valueOf(cate_id) + String.valueOf(c.getPosition()), Integer.parseInt(c.getString(c.getColumnIndex("_id"))));
	    		}while (c.moveToNext());
			}
		}
		catch(Exception e)
		{
			Metodos_auxiliares.mensagem(context.getString(R.string.error_query) + e.getMessage(), context);
			c.close();
			
		}		
		return c;
		
		
	}
	
	public static boolean setListViewHeightBasedOnItems(ListView listView) {

	    ListAdapter listAdapter = listView.getAdapter();
	    if (listAdapter != null) {
	        int numberOfItems = listAdapter.getCount();

	        // Get total height of all items.
	        int totalItemsHeight = 0;
	        for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
	            View item = listAdapter.getView(itemPos, null, listView);
	            item.measure(0, 0);
	            totalItemsHeight += item.getMeasuredHeight();
	        }

	        // Get total height of all item dividers.
	        int totalDividersHeight = listView.getDividerHeight() * 
	                (numberOfItems - 1);

	        // Set list height.
	        ViewGroup.LayoutParams params = listView.getLayoutParams();
	        params.height = totalItemsHeight + totalDividersHeight;
	        listView.setLayoutParams(params);
	        listView.requestLayout();

	        return true;

	    } else {
	        return false;
	    }

	}

}