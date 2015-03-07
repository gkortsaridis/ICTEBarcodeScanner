/*******************************************|
 * ICTE Barcode Scanner						|
 * 											|
 * Made By : Kortsaridis George				|
 * AEM     : 598							|
 * 											|
 * settings.java							|
 * 											|
 ********************************************/

package com.georgekortsaridis.ictebarcodescanner;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class settings extends Activity{

	EditText server_address , my_username , my_password;
	SharedPreferences sharedpreferences;
	Editor editor;
	TextView show_seconds;
	Spinner seconds;
	int posSeconds , posHttp;
	Spinner httpOrHttps;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
      
        
        //Arxikopoio to TextView kai to SeekBar
        server_address = (EditText)findViewById(R.id.server);
        my_username = (EditText)findViewById(R.id.un_text);
        my_password = (EditText)findViewById(R.id.pass_text);
        show_seconds = (TextView)findViewById(R.id.seconds_wait);
        seconds = (Spinner)findViewById(R.id.spinner1);
        httpOrHttps = (Spinner)findViewById(R.id.httpOrHttps);
        
        
        //Anoigo ta settings kai emfanizo tin dieuthinsi
        sharedpreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        posSeconds = sharedpreferences.getInt("seconds_to_wait" , 0);
        String temp = sharedpreferences.getString("httpOrHttps","");
        if(temp.equals("https://") || temp.equals("")) posHttp = 0;
        else posHttp = 1;
        
        
        //Emfanizo tin dieuthinsi tou server , to username kai to password
        server_address.setText(sharedpreferences.getString("address", ""));
        my_username.setText(sharedpreferences.getString("my_username", ""));
        my_password.setText(sharedpreferences.getString("my_password", "")); 

        prepareSpinners();
	}
	
	public void prepareSpinners(){
		List<String> list = new ArrayList<String>();
        list.add("Δεν περιμένω");
        list.add("1 Δευτερόλεπτο");
        list.add("2 Δευτερόλεπτα");
        list.add("3 Δευτερόλεπτα");
        list.add("4 Δευτερόλεπτα");
        list.add("5 Δευτερόλεπτα");
        
        List<String> http = new ArrayList<String>();
        http.add("https://");
        http.add("http://");
         
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                     (this, android.R.layout.simple_spinner_item,list);
                      
        dataAdapter.setDropDownViewResource
                     (android.R.layout.simple_spinner_dropdown_item);
                      
        seconds.setAdapter(dataAdapter);
        seconds.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
   
        seconds.setSelection(posSeconds);
        
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>
        (this, android.R.layout.simple_spinner_item,http);
         
		dataAdapter2.setDropDownViewResource
		        (android.R.layout.simple_spinner_dropdown_item);
		         
		httpOrHttps.setAdapter(dataAdapter2);
		httpOrHttps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			   Object item = parent.getItemAtPosition(pos);
			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		httpOrHttps.setSelection(posHttp);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    
		//An patithei to koumpi BACK
		if (keyCode == KeyEvent.KEYCODE_BACK)
	    {
			int secs =0;
			String spinner_data = seconds.getSelectedItem().toString();
			if(spinner_data.equals("Απενεργοποίηση Timer")){
				secs = 0;
			}else if(spinner_data.equals("1 Δευτερόλεπτο")){
				secs = 1;
			}else if(spinner_data.equals("2 Δευτερόλεπτα")){
				secs = 2;
			}else if(spinner_data.equals("3 Δευτερόλεπτα")){
				secs = 3;
			}else if(spinner_data.equals("4 Δευτερόλεπτα")){
				secs = 4;
			}else if(spinner_data.equals("5 Δευτερόλεπτα")){
				secs = 5;
			}
			
			//Dimiourgo tin full dieuthinsi
	    	String adrs = "";
	    	adrs += server_address.getText().toString();
	    	
	    	//Tin apothikeuo sta settings
	    	Editor editor = sharedpreferences.edit();
	        editor.putString("address", adrs);
	        editor.putString("my_username", my_username.getText().toString());
	        editor.putString("my_password", my_password.getText().toString());
	        editor.putInt("seconds_to_wait" , secs);
	        editor.putString("httpOrHttps" , httpOrHttps.getSelectedItem().toString());
	    	editor.commit();
	    	
	    	//Emfanizo to Toast gia tin epitiximeni allagi
			Toast.makeText(getBaseContext(), "Οι ρυθμίσεις έχουν αλλάξει",Toast.LENGTH_SHORT).show(); 
	    	
			//Kai teliono
	    	finish();
	    	
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
	
	


}
