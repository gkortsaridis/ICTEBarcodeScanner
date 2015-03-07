/*******************************************|
 * ICTE Barcode Scanner						|
 * 											|
 * Made By : Kortsaridis George				|
 * AEM     : 598							|
 * 											|
 * loguserpass.java							|
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.georgekortsaridis.ictebarcodescanner.BasicHTTP;

public class loguserpass extends Activity implements OnClickListener{

	AsyncTask<String, Void, String> httptask_lessons;
	Spinner lessons_spinner;
	Button send;
	
	SharedPreferences sharedpreferences;
	
	int lessons_counter;
	String[] lessons_names;
	String[] lessons_id;
	String server_address;
	
	String my_username , my_password;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loguserpass);

        //Anoigoume ta settings kai pairnoume tin dieuthinsi tou server
        sharedpreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String temp = sharedpreferences.getString("httpOrHttps", "");
        if(temp.equals("")) temp = "http://";
        server_address = "";
        server_address += temp;
        server_address += sharedpreferences.getString("address", "");
        server_address = server_address.replace(" ", "");
        
        my_username = sharedpreferences.getString("my_username" , "");
        my_password = sharedpreferences.getString("my_password" , "");
        
        //Arxikopoioume to spinner kai to koumpi
        lessons_spinner  = (Spinner)findViewById(R.id.lessons_spinner);
        send             = (Button)findViewById(R.id.send_lesson);
        send.setOnClickListener(this);
        
        //Ksekiname to asigxrono task gia to aitima
        String my_id = "";
        my_id += my_username;
        my_id += ".";
        my_id += my_password;
        
        Log.i("Tha zitiso ta mathimata me data", my_id);
        Log.i("Tha ta zitiso apo",server_address);
        httptask_lessons = new HttpAsyncTask().execute(server_address,"lessons",my_id);
        
        String x;
		try {
		
			//Lambanoume tin apadisi tou server
			x = httptask_lessons.get();
			
			if(x.equals("FAIL")){
				//Allios emfanizei minima lanthasmenon stoixeion
				Toast.makeText(getBaseContext(), "WE GOT A FAIL",Toast.LENGTH_LONG).show(); 
				Intent intent = new Intent(this , MainActivity.class);
			    startActivity(intent);
			}
			else if(x.equals("")){
				Toast.makeText(getBaseContext(), "Empty Response from server on LESSONS",Toast.LENGTH_LONG).show(); 
				finish();
			}
			else{
				//Tin xorizoume sta kommata
				String[] separated = x.split(",");
				
				//Kai gia kathe mathima, kratame ONOMA kai ID
				lessons_counter = separated.length;
				lessons_names = new String[lessons_counter];
				lessons_id = new String[lessons_counter];
				for(int i=0; i<lessons_counter; i++)
				{
					String[] separated2 = separated[i].split(":");
					lessons_names[i] = separated2[1];
					lessons_id[i]    = separated2[0];
					Log.i("Mathima + ID ",lessons_names[i]+" -> "+lessons_id[i]);
				}
				
				//Afou ta exoume ola stous pinakes, ta bazoume sto spinner
				addItemsOnLessonSpinner();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
        
	}
	
	public void addItemsOnLessonSpinner() {
		List<String> list = new ArrayList<String>();
		for(int i=0; i<lessons_counter; i++)
		{
			list.add(lessons_names[i]);
		}
	
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		lessons_spinner.setAdapter(dataAdapter);
	
	  }

	@Override
	public void onClick(View v) {
		
		//An patithei to koumpi, ksekiame tin getsessions, me parametro to LessonID
		Intent myIntent = new Intent(this, getsessions.class);
		
		for(int i=0; i<lessons_counter; i++)
		{
			if(String.valueOf(lessons_spinner.getSelectedItem()) == lessons_names[i])
			{
				myIntent.putExtra("LessonID",lessons_id[i]);
				myIntent.putExtra("LessonName",lessons_names[i]);
				//Log.i("Pernao to mathima",lessons_names[i]);
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
