package br.com.mvc;

import android.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ActAdapter
extends ArrayAdapter<SpinnerClass>
implements Filterable {

private List<SpinnerClass> listaCompleta;
private List<SpinnerClass> resultados;
private Filter meuFiltro;
public static String[] REPLACES = { "a", "e", "i", "o", "u", "c" };
public static Pattern[] PATTERNS = null;
private Context context;

public ActAdapter(
  Context ctx, int layout,
  List<SpinnerClass> textos) { 

  super(ctx, layout, textos);
  this.listaCompleta = textos;
  this.resultados = listaCompleta;
  this.meuFiltro = new MeuFiltro();
  this.context = ctx;
}

@Override
public int getCount() {
  return resultados.size();
}

@Override
public SpinnerClass getItem(int position) {
  if (resultados != null
    && resultados.size() > 0
    && position < resultados.size()){        
    return resultados.get(position);      
  } else {        
    return null;     
  }   
}     

@Override
public View getView(int position, View convertView, ViewGroup parent) {
	LayoutInflater layout_spinner = LayoutInflater.from(context);    	
	
	if(convertView == null)
	{
		convertView = layout_spinner.inflate(br.com.enxoval.R.layout.spinner, null);
	}
	TextView label = (TextView) convertView.findViewById(R.id.text1);
	//TextView label = new TextView(context);
    label.setText(resultados.get(position).getText());
    return label;
}

@Override
public View getDropDownView(int position, View convertView,
        ViewGroup parent) {
	
	TextView label = new TextView(context);
    label.setTextSize(20);
    label.setTextColor(Color.BLACK);
    label.setText(resultados.get(position).getText());
    return label;
}

@Override   
public Filter getFilter() {     
  return meuFiltro;   
}    

private class MeuFiltro extends Filter {
  @Override
  protected FilterResults performFiltering(
    CharSequence constraint) {

    FilterResults filterResults = 
      new FilterResults();

    ArrayList<SpinnerClass> temp = 
      new ArrayList<SpinnerClass>();
        
    if (constraint != null) {
      String term = removeAcentos(
        constraint.toString().trim().toLowerCase());
          
      String placeStr;
      for (SpinnerClass p : listaCompleta) {
        placeStr = removeAcentos(p.toString().toLowerCase());
              
        if ( placeStr.indexOf(term) > -1){
          temp.add(p);
        }
      }
    }
    filterResults.values = temp;
    filterResults.count = temp.size();
    return filterResults;
  }

  @SuppressWarnings("unchecked")
  @Override
protected void publishResults(
    CharSequence contraint,
    FilterResults filterResults) {

    resultados = (ArrayList<SpinnerClass>) 
      filterResults.values;
	
    notifyDataSetChanged();
  }
}
	public static void compilePatterns() {
	PATTERNS = new Pattern[REPLACES.length];
	PATTERNS[0] = Pattern.compile(
	 "[âãáàä]", Pattern.CASE_INSENSITIVE);
	PATTERNS[1] = Pattern.compile(
	 "[éèêë]", Pattern.CASE_INSENSITIVE);
	PATTERNS[2] = Pattern.compile(
	 "[íìîï]", Pattern.CASE_INSENSITIVE);
	PATTERNS[3] = Pattern.compile(
	 "[óòôõö]", Pattern.CASE_INSENSITIVE);
	PATTERNS[4] = Pattern.compile(
	 "[úùûü]", Pattern.CASE_INSENSITIVE);
	PATTERNS[5] = Pattern.compile(
	 "[ç]", Pattern.CASE_INSENSITIVE);
	}

	public static String removeAcentos(String text) {
	if (PATTERNS == null) {
	 compilePatterns();
	}
	
	String result = text;
	for (int i = 0; i < PATTERNS.length; i++) {     
	 Matcher matcher = PATTERNS[i].matcher(result);     
	 result = matcher.replaceAll(REPLACES[i]);   
	}   
	return result.toUpperCase(); 
	} 
}

