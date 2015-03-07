/*******************************************|
f * ICTE Barcode Scanner						|
 * 											|
 * Made By : Kortsaridis George				|
 * AEM     : 598							|
 * 											|
 * getsessions.java							|
 * 											|
 ********************************************/


package com.georgekortsaridis.ictebarcodescanner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class getsessions extends Activity implements OnClickListener{

	AsyncTask<String, Void, String> httptask_lessons;
	SharedPreferences sharedpreferences;
	Editor editor;
	
	Spinner session_spinner;
	Button  sendSession;
	TextView selectedLesson;
	
	int sessions_counter;
	String[] sessions_names;
	String[] sessions_id;
	String LessonID , LessonName;
	String server_address;
	
	String my_username , my_password;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getsession);
        
        
        //Get the previously created intent
        Intent myIntent = getIntent(); 
        LessonID = myIntent.getStringExtra("LessonID"); 
        LessonName = myIntent.getStringExtra("LessonName");
        
        
        //Arxikopoiisi koubion kai spinners
        session_spinner = (Spinner)findViewById(R.id.session_spinner);
        selectedLesson  = (TextView)findViewById(R.id.selectedLesson);
        selectedLesson.setText(LessonName);
        sendSession     = (Button)findViewById(R.id.send_session);
        sendSession.setOnClickListener(this);
        
        //Anoigma ton sharedpreferences gia ta settings
        sharedpreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        
        //Pairnoume tin dieuthinsi tou server apo ta settings
        String temp = sharedpreferences.getString("httpOrHttps", "");
        if(temp.equals("")) temp = "http://";
        server_address = "";
        server_address += temp;
        server_address += sharedpreferences.getString("address", "");
        server_address = server_address.replace(" ", "");
        
        my_username = sharedpreferences.getString("my_username" , "");
        my_password = sharedpreferences.getString("my_password" , "");
        
        
        String my_id = "";
        my_id += my_username;
        my_id += ".";
        my_id += my_password;
        my_id += ".";
        my_id += LessonID;
        Log.i("Tha zitiso tis dialekseis me data", my_id);
        Log.i("Tha tis zitiso apo",server_address);
        //Ksekiname to asingxrono tast gia tin apostoli tou aitimatos
        httptask_lessons = new HttpAsyncTask().execute(server_address,"sessions",my_id);
        
        String x;
		try {
			
			//Pairnoume tin apantisi tou server
			x = httptask_lessons.get();
			if(x.equals("FAIL")){
				//Allios emfanizei minima lanthasmenon stoixeion
				Toast.makeText(getBaseContext(), "WE GOT A FAIL",Toast.LENGTH_LONG).show();
				Intent intent = new Intent(this , MainActivity.class);
			    startActivity(intent);
			}
			else if(x.equals("")){
				Toast.makeText(getBaseContext(), "Empty Response from Server on SESSIONS",Toast.LENGTH_LONG).show(); 
				finish();
			}
			else{
				//Tin xorizoume sta kommata
				String[] separated = x.split(",");
				
				//Kai gia kathe dialeksi, kratame ksexorista ONOMA kai ID
				sessions_counter = separated.length;
				sessions_names = new String[sessions_counter];
				sessions_id = new String[sessions_counter];
				for(int i=0; i<sessions_counter; i++)
				{
					String[] separated2 = separated[i].split(":");
					sessions_names[i] = separated2[1];
					sessions_id[i]    = separated2[0];
					Log.i("Διάλεξη + ID ",sessions_names[i]+" -> "+sessions_id[i]);
				}
				
				//Afou exoun mpei stous pinakes ta onomata, ta prosthetoume sto spinner
				addItemsOnSessionSpinner();
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        
	}

	public void addItemsOnSessionSpinner() {
		List<String> list = new ArrayList<String>();
		for(int i=0; i<sessions_counter; i++)
		{
			list.add(sessions_names[i]);
		}
	
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		session_spinner.setAdapter(dataAdapter);
		
	  }
	
	@Override
	public void onClick(View v) {
		
		//An patithei koumpi, ksekiname tin scanandsend, me parametrous to LessonID kai to SessionID
		Intent myIntent = new Intent(this, scanandsend.class);
		
		for(int i=0; i<sessions_counter; i++)
		{
			if(String.valueOf(session_spinner.getSelectedItem()) == sessions_names[i])
			{
				myIntent.putExtra("LessonID",LessonID);
				myIntent.putExtra("SessionID",sessions_id[i]);
				myIntent.putExtra("OTP","NO");
				myIntent.putExtra("OTPcode","dont_care");
			}
		}
		
		startActivity(myIntent);
	}
	
	
	public static class HttpAsyncTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... urls)
        {
            String temp = BasicHTTP.GET(urls[0], urls[1], urls[2]);
            return temp;
        }
   
    }
	
	
}
