package br.com.mvc;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinnerAdapter extends ArrayAdapter<SpinnerClass>{

    private Context context;
    private SpinnerClass[] myObjs;

    public SpinnerAdapter(Context context, int textViewResourceId,
    	SpinnerClass[] myObjs) {
        super(context, textViewResourceId, myObjs);
        this.context = context;
        this.myObjs = myObjs;
    }

    @Override
	public int getCount(){
       return myObjs.length;
    }

    @Override
	public SpinnerClass getItem(int position){
       return myObjs[position];
    }

    @Override
	public long getItemId(int position){
       return position;
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
        label.setText(myObjs[position].getText());
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
            ViewGroup parent) {
    	
//    	LayoutInflater inflater=getLayoutInflater();
//    	View row=inflater.inflate(br.com.enxoval.R.layout.spinner_dropdown, parent, false);
//    	TextView label=(TextView)row.findViewById(R.id.text1);
    	//TextView label = (TextView) convertView.findViewById(R.id.text1);
    	TextView label = new TextView(context);
        label.setTextSize(20);
        label.setText(myObjs[position].getText());
        return label;
    }
}
