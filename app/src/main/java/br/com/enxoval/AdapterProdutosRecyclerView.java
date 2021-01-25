package br.com.enxoval;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import br.com.mvc.AdsProdutos;
import br.com.mvc.EnxovalClient;
import br.com.mvc.Metodos_auxiliares;
import br.com.mvc.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by robinson on 05/03/2017.
 */

public class AdapterProdutosRecyclerView extends RecyclerView.Adapter {
    private List<Produtos>   produtos;
    private HashMap<Long, Bitmap> ads_images_bmp = new HashMap<>();
    private Context context;
    private List<AdsProdutos> adsProdutos;
    private LayoutInflater mInflater;
    private Tracker mTracker;


    public AdapterProdutosRecyclerView(List<Produtos> produtos, List<AdsProdutos> adsProdutos, Context context){
        this.produtos = produtos;
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.adsProdutos = adsProdutos;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.lst_view_produtos, parent, false);

        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ViewHolder myHolder = (ViewHolder) holder;
        final Produtos produto = produtos.get(position);

        myHolder.lly_editar.setVisibility(View.GONE);
        myHolder.lly_botoes.setVisibility(View.GONE);
        myHolder.lly_opcao.setVisibility(View.VISIBLE);
        myHolder.btn_editar.setVisibility(View.VISIBLE);
        myHolder.ad_produto_image.setVisibility(View.GONE);

        myHolder.txt_produto.setText(produto.getProduto());
        myHolder.txt_sugestao.setText(String.valueOf(produto.getSugestao()));

        String string;
        float n = produto.getComprado();
        if (n % 1 == 0) {
            string = String.valueOf((int) n);
        } else {
            string = String.valueOf(n).replace(".", ",");
        }
        myHolder.txt_comprado.setText(string);
        myHolder.txt_qtd_comprado.setText(string);

        myHolder.btn_editar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.recyclerView_dialog_menu_title);
                builder.setItems(R.array.menu_poup_up_editar_produto, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0)
                        {
                            myHolder.txt_comprado.setText(myHolder.txt_qtd_comprado.getText());

                            myHolder.lly_opcao.setVisibility(View.GONE);
                            myHolder.lly_editar.setVisibility(View.VISIBLE);
                            myHolder.btn_editar.setVisibility(View.GONE);
                            myHolder.lly_botoes.setVisibility(View.VISIBLE);
                            //txt_comprado.setFocusable(true);
                            myHolder.txt_comprado.requestFocus();
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(myHolder.txt_comprado, InputMethodManager.SHOW_IMPLICIT);

                            //ListView lt = (ListView) parent;
                            //lt.setSelection(position);
                        }

                        if(which == 1)
                        {
                            Intent it = new Intent(context, EditarProdutosActivity.class);
                            it.putExtra("id", String.valueOf(produto.getId()));
                            it.putExtra("nome", produto.getProduto());
                            it.putExtra("sugestao", String.valueOf(produto.getSugestao()));
                            it.putExtra("ja_tenho", String.valueOf(produto.getComprado()));
                            it.putExtra("label", context.getString(R.string.recyclerView_edit_product));
                            context.startActivity(it);
                            return;
                        }
                        if(which == 2)
                        {
                            AlertDialog alerta;
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(R.string.atention);
                            builder.setMessage(R.string.dialog_delete_product_msg);
                            builder.setPositiveButton(R.string.bnt_yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    if(produto.excluir(context, produto)) {

                                        try {
                                            produtos.remove(position);
                                            notifyDataSetChanged();
                                            //lt.removeView(outra_view);
                                        }
                                        catch (Exception e)
                                        {

                                        }
                                        Metodos_auxiliares.mensagem(context.getString(R.string.dialog_delete_product_success), context);
                                    }
                                    else
                                        Metodos_auxiliares.mensagem(context.getString(R.string.error_delete), context);
                                }
                            });
                            builder.setNegativeButton(context.getString(R.string.btn_no), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    return;
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        }
                    }
                });

                builder.setInverseBackgroundForced(true);
                builder.create();
                builder.show();
            }
        });

        myHolder.txt_comprado.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        myHolder.btn_salvar.performClick();
                    }
                    return true;
                }
                return false;
            }
        });

        myHolder.btn_salvar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myHolder.lly_opcao.setVisibility(View.VISIBLE);
                myHolder.lly_editar.setVisibility(View.GONE);
                myHolder.btn_editar.setVisibility(View.VISIBLE);
                myHolder.lly_botoes.setVisibility(View.GONE);

                float number = 0;
                try
                {
                    number = Float.valueOf(myHolder.txt_comprado.getText().toString().replace(',','.'));
                    produto.soma_comprado(mInflater.getContext(), number);
                    produto.setComprado(number);
                    myHolder.txt_qtd_comprado.setText(myHolder.txt_comprado.getText().toString());
                    InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(myHolder.txt_comprado.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                catch(NumberFormatException e){
                    Metodos_auxiliares.mensagem(context.getString(R.string.error_edit_value), context);
                }

            }
        });

        myHolder.btn_cancelar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myHolder.lly_opcao.setVisibility(View.VISIBLE);
                myHolder.lly_editar.setVisibility(View.GONE);
                myHolder.btn_editar.setVisibility(View.VISIBLE);
                myHolder.lly_botoes.setVisibility(View.GONE);
                InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(myHolder.txt_comprado.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        myHolder.btn_mais.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    // TODO Auto-generated method stub
                    //produto.soma_comprado(mInflater.getContext(), produto.getComprado() + 1);
                    //produto.setComprado(produto.getComprado() + 1);

                    String string;
                    float n = Float.valueOf(myHolder.txt_comprado.getText().toString().replace(",", "."));
                    n = n + 1;
                    if (n % 1 == 0) {
                        string = String.valueOf((int) n);
                    } else {
                        string = String.valueOf(n).replace(".", ",");
                    }
                    myHolder.txt_comprado.setText(string);
                    //txt_qtd_comprado.setText(string);
                    //txt_comprado.setText((String.valueOf(produto.getComprado())).replace(".", ","));
                }
                catch (Exception e){}
            }
        });

        myHolder.btn_menos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //produto.soma_comprado(mInflater.getContext(), produto.getComprado() - 1);
                //produto.setComprado(produto.getComprado() - 1);

                String string;
                float n = Float.valueOf(myHolder.txt_comprado.getText().toString().replace(",", "."));
                n = n - 1;
                if (n % 1 == 0) {
                    string = String.valueOf((int) n);
                } else {
                    string = String.valueOf(n).replace(".", ",");
                }
                myHolder.txt_comprado.setText(string);
                //txt_qtd_comprado.setText(string);
                //txt_comprado.setText(String.valueOf(produto.getComprado()).replace(".", ",").replace(",0",""));
            }
        });

        myHolder.txt_comprado.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

				/*float number = 0;
				try
				{
					number = Float.valueOf(s.toString().replace(',','.'));
					produto.soma_comprado(mInflater.getContext(), number);
					produto.setComprado(number);
				}
				catch(NumberFormatException e){
					return;
				}			*/
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub


            }
        });

        if(adsProdutos == null) {
            return;
        }

        for (final AdsProdutos ad_produto : adsProdutos) {
            if (ad_produto.getProdutoId().equalsIgnoreCase(String.valueOf(produto.getId()))) {
                Metodos_auxiliares.loadImage(ad_produto.getImagem(), context, myHolder.ad_produto_image, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        contaImpressao(ad_produto.getId());
                        myHolder.ad_produto_image.setVisibility(View.VISIBLE);

                        myHolder.ad_produto_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle(R.string.RecyclerView_share_item_title);
                                builder.setItems(R.array.menu_poup_up_ad_produto, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            Intent i = new Intent(android.content.Intent.ACTION_SEND);
                                            i.setType("text/plain");
                                            i.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.recyclerView_share_item_subject));
                                            i.putExtra(android.content.Intent.EXTRA_TEXT, ad_produto.getLink());
                                            context.startActivity(Intent.createChooser(i, context.getString(R.string.RecyclerView_share_item_title)));

                                            mTracker = ((AnalyticsApplication) ((ProdutosActivity) context).getApplication()).getDefaultTracker();
                                            mTracker.send(new HitBuilders.EventBuilder()
                                                    .setCategory("Share_ad_produto")
                                                    .setAction("Click share ad_produto - " + produto.getId())
                                                    .build());
                                        }

                                        if (which == 1) {
                                            try {
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ad_produto.getLink()));
                                                context.startActivity(browserIntent);
                                                countClick(ad_produto.getId());

                                            } catch (Exception e) {
                                            }
                                        }

                                    }
                                });

                                builder.setInverseBackgroundForced(true);
                                builder.create();
                                builder.show();
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        myHolder.ad_produto_image.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_produto;
        TextView txt_sugestao;
        final TextView txt_qtd_comprado;
        final TextView txt_comprado;
        Button btn_mais;
        final Button btn_menos;
        final ImageButton btn_editar;
        final ImageButton btn_salvar;
        final ImageButton btn_cancelar;
        final LinearLayout lly_opcao;
        final LinearLayout lly_editar;
        final LinearLayout lly_botoes;
        final ImageView ad_produto_image;

        public ViewHolder(View view) {
            super(view);
            txt_produto = (TextView) view.findViewById(R.id.lst_view_produtos_txt_produto);
            txt_sugestao = (TextView) view.findViewById(R.id.lst_view_produtos_txt_sugestao);
            txt_qtd_comprado = (TextView) view.findViewById(R.id.lst_view_produtos_lbl_qtd_comprado);
            txt_comprado = (TextView) view.findViewById(R.id.lst_view_produtos_txt_comprado);
            btn_mais = (Button) view.findViewById(R.id.lst_view_produtos_btn_mais);
            btn_menos = (Button) view.findViewById(R.id.lst_view_produtos_btn_menos);
            btn_editar = (ImageButton) view.findViewById(R.id.lst_view_produtos_btn_editar);
            lly_opcao = (LinearLayout) view.findViewById(R.id.lst_view_produtos_lly_opcao);
            lly_editar = (LinearLayout) view.findViewById(R.id.lst_view_produtos_lly_editar);
            lly_botoes = (LinearLayout) view.findViewById(R.id.lst_view_produtos_lly_botoes);
            btn_salvar = (ImageButton) view.findViewById(R.id.lst_view_produtos_btn_salvar);
            btn_cancelar = (ImageButton) view.findViewById(R.id.lst_view_produtos_btn_cancelar);
            ad_produto_image = (ImageView) view.findViewById(R.id.produto_ad_produto);
        }
    }

    private void countClick(String id)
    {
        EnxovalClient client = ServiceGenerator.createService(EnxovalClient.class);
        Call<List<AdsProdutos>> call = client.countClicAdProduto(id);
        call.enqueue(new Callback<List<AdsProdutos>>() {
            @Override
            public void onResponse(Call<List<AdsProdutos>> call, Response<List<AdsProdutos>> response) {

            }

            @Override
            public void onFailure(Call<List<AdsProdutos>> call, Throwable t) {

            }
        });
    }

    private void contaImpressao(String id)
    {
        EnxovalClient client = ServiceGenerator.createService(EnxovalClient.class);
        Call<List<AdsProdutos>> call = client.countImpressaoAdProduto(id);
        call.enqueue(new Callback<List<AdsProdutos>>() {
            @Override
            public void onResponse(Call<List<AdsProdutos>> call, Response<List<AdsProdutos>> response) {
            }

            @Override
            public void onFailure(Call<List<AdsProdutos>> call, Throwable t) {

            }
        });
    }
}
