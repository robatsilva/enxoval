package br.com.enxoval.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Locale;

import br.com.enxoval.MenuEnxovalActivity;
import br.com.enxoval.R;
import br.com.enxoval.SubMenuAdapter;
import br.com.mvc.Db_mvc;
import br.com.mvc.Metodos_auxiliares;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriasFragment extends Fragment {
    ListView lst_categorias;
    ListView lst_sub_categorias;
    Db_mvc database;
    SubMenuAdapter dataAdapter;
    String campos[] = {"cate_descricao"};
    ArrayList<String> categorias;

    public CategoriasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.categorias, container, false);
        inicializa_elementos(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void inicializa_elementos(View v) {
        try
        {
            database = new Db_mvc(Db_mvc.getContext(getActivity()));
            categorias = new ArrayList<String>();
            Cursor c = carrega_categorias();
            if(c == null)
            {
                Metodos_auxiliares.mensagem(getString(R.string.error_init_elements), getActivity());
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
            lst_categorias = (ListView) v.findViewById(R.id.categorias_lst_categorias);
            dataAdapter = new SubMenuAdapter(getActivity(), categorias);
            lst_categorias.setAdapter(dataAdapter);

        }
        catch(Exception e)
        {
            Metodos_auxiliares.mensagem(getString(R.string.error_init_elements) + e.getMessage() + e.toString(), getActivity());
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if(getContext() instanceof MenuEnxovalActivity)
            {
                if(((MenuEnxovalActivity) getContext()).mAdView != null) {
                    ((MenuEnxovalActivity) getContext()).mAdView.resume();
                    ((MenuEnxovalActivity) getContext()).mAdView.setVisibility(View.VISIBLE);
                }
            }
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
            Metodos_auxiliares.mensagem(getString(R.string.error_query) + e.getMessage() + e.toString(), getActivity());
            return null;
        }

    }

}
