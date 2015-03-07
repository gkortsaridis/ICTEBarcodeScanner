/*******************************************|
 * ICTE Barcode Scanner						|
 * 											|
 * Made By : Kortsaridis George				|
 * AEM     : 598							|
 * 											|
 * MainActivity.java						|
 * 											|
 ********************************************/

package com.georgekortsaridis.ictebarcodescanner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class scanandsend extends Activity implements OnClickListener{

	AsyncTask<String, Void, String> httptask;
	String lesson;
	String session;
	
	Button scanAgain;
	TextView tv;
	
	SharedPreferences sharedpreferences;
	String server_address;
	
	String my_username , my_password , from_otp , otp_code;
	int seconds_to_wait;
	
	LinearLayout scanandsend;
	Button cancelCountdown,toMenu;
	TextView showRemaining; 
	CountDownTimer countDownTimer;
	
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanandsend);
        
        scanandsend = (LinearLayout)findViewById(R.id.scanandsend_layout);
        
        //Anoigoume ta settings, kai kratame tin dieuthinsi tou server
        sharedpreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String temp = sharedpreferences.getString("httpOrHttps", "");
        if(temp.equals("")) temp = "http://";
        server_address = "";
        server_address += temp;
        server_address += sharedpreferences.getString("address", "");
        server_address = server_address.replace(" ", "");
        
        my_username = sharedpreferences.getString("my_username" , "");
        my_password = sharedpreferences.getString("my_password" , "");
        seconds_to_wait = sharedpreferences.getInt("seconds_to_wait" , 0) * 1000;
        
        
        if(seconds_to_wait >0)
        {	
	        cancelCountdown = new Button(this);
	        cancelCountdown.setId(1);
	        cancelCountdown.setText("Ακύρωση Countdown Timer");
	        cancelCountdown.setLayoutParams(new LayoutParams(
	        		ViewGroup.LayoutParams.FILL_PARENT,
	                ViewGroup.LayoutParams.WRAP_CONTENT));
	        cancelCountdown.setOnClickListener(this);
	        scanandsend.addView(cancelCountdown);
        }
        
        
        toMenu = new Button(this);
        toMenu.setId(2);
        toMenu.setText("Back to MAIN MENU");
        toMenu.setLayoutParams(new LayoutParams(
        		ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        toMenu.setOnClickListener(this);
        scanandsend.addView(toMenu);
        
        showRemaining = new TextView(this);
        showRemaining.setText("Remaining Time");
        showRemaining.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        scanandsend.addView(showRemaining);
        
        //Pairno to mathima pou perastike sto intent
        Intent prev_intent = getIntent();
        lesson  = prev_intent.getStringExtra("LessonID");
        session = prev_intent.getStringExtra("SessionID");
        from_otp = prev_intent.getStringExtra("OTP"); 
        otp_code = prev_intent.getStringExtra("OTPcode");
        Log.i(from_otp,otp_code);
        
        //Arxikopoio to koumpi kai to TextView
        scanAgain = (Button)findViewById(R.id.scanAgain);
        tv        = (TextView)findViewById(R.id.scanresult);
        scanAgain.setOnClickListener(this);
        
        //Ksekinao apeutheias nea sarosi
        Intent intent1 = new Intent("com.google.zxing.client.android.SCAN");
        intent1.putExtra("SCAN_MODE", "QR_CODE_MODE");
        try { startActivityForResult(intent1, 0); }
        catch (ActivityNotFoundException activity)
        { 
			Toast.makeText(getBaseContext(), "Εγκαταστήστε πρώτα το ZXING Barcode Scanner",Toast.LENGTH_LONG).show(); 
        }
        
	}

	@Override
	public void onClick(View v) {
		//An patithei to koumpi, kleino ousiastika auto to Activity
		//kai to ksanarxizo
		if(v.getId() == 1)
		{
			countDownTimer.cancel();
		}
		else if(v.getId() == 2){
			if(seconds_to_wait > 0) countDownTimer.cancel();
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		else
		{
			if(seconds_to_wait > 0) countDownTimer.cancel();
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
   
           if (resultCode == RESULT_OK)
           {
        	   
        	   	//Pairno ta apotelesmata
        	   	String result = intent.getStringExtra("SCAN_RESULT");
              	String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
           
              	//String result = intent.getExtras().getString("la.droid.qr.result");
              	
              	//Ta emfanizo
              	Log.i("Scan result",result);
              	Log.i("Scan format",format);
              	
              	//Log.i("Exv otp???",from_otp);
              	String toSend = "";
              	if(from_otp.equals("NO"))
              	{
	              	//Dimiourgo to string pou tha steilo ston server
	              	Log.i("Den eimai me OTP","OTP");
	              	toSend += my_username;
	              	toSend += ".";
	              	toSend += my_password;
	              	toSend += ".";
	              	toSend += session;
	              	toSend += ".";
	              	toSend += result;
              	}
              	else if(from_otp.equals("YES"))
              	{
              		Log.i("Eimai me OTP","OTP");
              		toSend += "otp";
	              	toSend += ".";
	              	toSend += otp_code;
	              	toSend += ".";
	              	toSend += session;
	              	toSend += ".";
	              	toSend += result;
              	}
              	
              	Log.i("Tha to steilo sto",server_address);
              	Log.i("Tha steilo ton kodiko tou paso me data",toSend);
              	//Ksekinao to asingxrono task gia to aitima
              	httptask = new HttpAsyncTask().execute(server_address,"scan_result",toSend);
              	
              	try {
              		
              		//Pairno tin apadisi
        			String x = httptask.get();
        			
        			if(x.equals(""))
        			{
							Toast.makeText(getBaseContext(), "Empty Response from Server on LOGIN",Toast.LENGTH_LONG).show(); 
					}
        			else
        			{
        				int error;		
        				error = searchForError(x);
        	
        				//Tin emfanizo sto TextView
        				tv.setText(x);
        			
        				if(error == 0)
        				{
	        				//ola ok
	        				MediaPlayer oksound = MediaPlayer.create(getBaseContext(), R.raw.success1);
	        			    oksound.start();
	        			        
	        				tv.setTextColor(Color.GREEN);
	        			}
	        			else if(error == 1)
	        			{
	        				//already exists
	        				MediaPlayer failsound = MediaPlayer.create(getBaseContext(), R.raw.fail1);
	        			    failsound.start();
	        				tv.setTextColor(Color.RED);	
	        			}
	        			else if(error == 2)
	        			{
	        				//session not found , user not found
	        				MediaPlayer failsound = MediaPlayer.create(getBaseContext(), R.raw.fail1);
	        			    failsound.start();
	        				tv.setTextColor(Color.BLUE);	
	        			}
        			}
        			
        		} catch (InterruptedException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (ExecutionException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
              
           	}else if (resultCode == RESULT_CANCELED) {
           		Log.i("App","Scan CANCELLED");
			}
 
			if(seconds_to_wait >0){
				
				countDownTimer = new CountDownTimer(seconds_to_wait, 1000) 
		        {
		        	public void onFinish() 
		             {
		        		showRemaining.setText("Time finished!");
		        		Intent intent = getIntent();
	            		finish();
	            		startActivity(intent);
		             }

					public void onTick(long millisUntilFinished)
		             {
		            	 showRemaining.setText("Time left: "+TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished));
		             }

		        }.start(); 
				
			}
           
		

	}

	public int searchForError(String x)
	{
		//Tin xorizoume sta kommata
		String[] separated = x.split(" ");
		
		for(int i=0; i<separated.length; i++)
		{
			//ola ok
			if(separated[i].equals("SUCCESS") || separated[i].equals("success") || separated[i].equals("Success"))
				return 0;
			else if(separated[i].equals("ERROR") || separated[i].equals("error") || separated[i].equals("Error"))
				return 1; //already exists
			else if(separated[i].equals("FAIL") || separated[i].equals("fail") || separated[i].equals("Fail"))
				return 2; //session not found , user not found
		}
		
		return 4;
		
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
