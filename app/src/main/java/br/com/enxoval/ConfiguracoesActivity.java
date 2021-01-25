package br.com.enxoval;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;

import br.com.mvc.Metodos_auxiliares;
import br.com.mvc.SpinnerAdapter;
import br.com.mvc.SpinnerClass;

public class ConfiguracoesActivity extends MenuEnxovalActivity {
	Spinner spn_sexo;
	Spinner spn_mes_nascimento;
	Button salvar;
	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) 
	    {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.configuracoes);
			inicializa_elementos();
			listeners();
			try {
				carrega_sexo();
				carrega_mes_nascimento();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Metodos_auxiliares.mensagem(getString(R.string.error_config), this);
			}
	    }	 
	 
	 private void inicializa_elementos() {
		 spn_sexo = (Spinner) findViewById(R.id.configuracoes_spn_sexo);
		 spn_mes_nascimento = (Spinner) findViewById(R.id.configuracoes_spn_mes_nascimento);
		 salvar = (Button) findViewById(R.id.configuracoes_btn_salvar);
	}


	private void carrega_sexo() {
		//spinner	            
	        SpinnerClass[] sexo = {
	        	new SpinnerClass(getString(R.string.config_bebe_menino), 1),
		        new SpinnerClass(getString(R.string.config_bebe_menina), 2),
		        new SpinnerClass(getString(R.string.config_bebe_nao_definido), 3)
	        };
	        SpinnerAdapter sexo_adapter = new SpinnerAdapter(this, R.layout.spinner, sexo);
	        //meses_adapter.setDropDownViewResource(R.layout.spinner_dropdown); 
	        spn_sexo.setAdapter(sexo_adapter);
	        int id = 0;
	        try {
				switch (Sistema.getSexo(this)) {
				case "M":
					id = 0;				
					break;
				case "F":
					id = 1;
					break;
				case "U":
					id = 2;
					break;
				default:
					break;
				}
				spn_sexo.setSelection(id);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Metodos_auxiliares.mensagem(getString(R.string.erro_select_gender_baby) + e.getMessage() + e.toString(), this);
			}
			
	}
	 
	private void carrega_mes_nascimento() {
		//spinner               
        SpinnerClass[] meses = {
        	new SpinnerClass(getString(R.string.spinner_clima_calor), 1),
	        new SpinnerClass(getString(R.string.spinner_clima_frio), 2)
	        
        };
        SpinnerAdapter meses_adapter = new SpinnerAdapter(this, R.layout.spinner, meses);
        spn_mes_nascimento.setAdapter(meses_adapter);
        int mes = 0;
        try {
			switch (Sistema.getMesNascimento(this))
			{
			case "prod_calor":
				mes = 1;
				break;						
			case "prod_frio":
				mes = 2;
				break;	
			}			
			spn_mes_nascimento.setSelection(mes - 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Metodos_auxiliares.mensagem(getString(R.string.error_clima), this);
		}
	}
	private void listeners() {
			 salvar.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					salva_sexo();
					salva_mes();
					finish();
				}

				private void salva_mes() {
					SpinnerClass mes = (SpinnerClass) spn_mes_nascimento.getSelectedItem();
					
					switch (mes.getValue())
					{
					case 1:
						Sistema.setMesNascimento("prod_calor", getApplicationContext());
						break;						
					case 2:
						Sistema.setMesNascimento("prod_frio", getApplicationContext());
						break;					
					}
					
				}

				private void salva_sexo() {
					SpinnerClass sexo = (SpinnerClass) spn_sexo.getSelectedItem();
					
					switch (sexo.getValue())
					{
					case 1:
						Sistema.setSexo("M", getApplicationContext());
						break;						
					case 2:
						Sistema.setSexo("F", getApplicationContext());
						break;						
					case 3:
						Sistema.setSexo("U", getApplicationContext());
						break;
					}
				}
			});
	}
}
