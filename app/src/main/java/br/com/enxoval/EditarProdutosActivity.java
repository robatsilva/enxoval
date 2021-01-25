package br.com.enxoval;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import br.com.mvc.Metodos_auxiliares;


public class EditarProdutosActivity extends MenuEnxovalActivity {
    EditText nome;
    EditText sugestao;
    EditText ja_tenho;
    Integer id = 0;
    Integer suca_id = 0;
    Button salvar;
    Button cancelar;
    TextView label;
    Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.editar_produtos_activity, R.id.lly_editar_produtos);
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.editar_produtos_activity);
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3500); // As I am using LENGTH_LONG in Toast
                    EditarProdutosActivity.this.finish();
                } catch (Exception e) {
                }
            }
        };
        try {
            nome = (EditText) findViewById(R.id.editar_produtos_activity_nome_produto);
            sugestao = (EditText) findViewById(R.id.editar_produtos_activity_sugestao);
            ja_tenho = (EditText) findViewById(R.id.editar_produtos_activity_ja_tenho);
            salvar = (Button) findViewById(R.id.editar_produtos_btn_salvar);
            cancelar = (Button) findViewById(R.id.editar_produtos_btn_cancelar);
            label = (TextView) findViewById(R.id.editar_produtos_activity_label_1);
            Bundle bundle = getIntent().getExtras();


            if(getIntent().getExtras().containsKey("id")) {
                label.setText(bundle.getString("label"));
                nome.setText(bundle.getString("nome"));

                sugestao.setText(bundle.getString("sugestao"));

                String string;
                float n = Float.parseFloat(bundle.getString("ja_tenho"));
                if (n % 1 == 0) {
                    string = String.valueOf((int) n);
                } else {
                    string = String.valueOf(n).replace(".", ",");
                }
                ja_tenho.setText(string);

                id = Integer.parseInt(bundle.getString("id"));
            }
            else if(getIntent().getExtras().containsKey("suca_id")) {
                suca_id = Integer.parseInt(bundle.getString("suca_id"));
            }
            else
            {
                Metodos_auxiliares.mensagem(getString(R.string.error_define_product), this);
                finish();
            }
        }
        catch (Exception e)
        {
            Metodos_auxiliares.mensagem(getString(R.string.error_load_products), this);
        }
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditarProdutosActivity.this.finish();
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Produtos p = new Produtos();
                if (nome.getText().toString().equals("")) {
                    Metodos_auxiliares.mensagem(getString(R.string.error_empty_name), EditarProdutosActivity.this);
                    return;
                }

                if (sugestao.getText().toString().equals("")) {
                    Metodos_auxiliares.mensagem(getString(R.string.error_invalid_tip), EditarProdutosActivity.this);
                    return;
                }

                float number = 0;
                try {
                    number = Float.valueOf(ja_tenho.getText().toString().replace(',', '.'));
                    p.setProduto(nome.getText().toString());
                    p.setComprado(number);
                    p.setSugestao(Integer.parseInt(sugestao.getText().toString()));
                    p.setId(id);
                    p.setSuca_id(suca_id);
                    if (id == 0 && suca_id > 0)
                        Metodos_auxiliares.mensagem(Produtos.novo_produto(EditarProdutosActivity.this, p), EditarProdutosActivity.this);
                    else if (id > 0)
                        Metodos_auxiliares.mensagem(Produtos.editar_produto(EditarProdutosActivity.this, p), EditarProdutosActivity.this);
                    setResult(Activity.RESULT_OK);
                    finish();
                } catch (NumberFormatException e) {
                    Metodos_auxiliares.mensagem(getString(R.string.error_invalid_have), EditarProdutosActivity.this);
                    return;
                }
            }
        });
    }
}
