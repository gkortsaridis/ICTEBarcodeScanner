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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{


	AsyncTask<String, Void, String> httptask_lessons;
	Button submit , otp;
	String server_address;
	TextView typeOfConnectionText;
	ImageView typeOfConnectionImage;
	
	static SharedPreferences sharedpreferences;
	Editor editor;

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        //Arxikopoio ta koubia, to TextView kai ta EditTexts
        submit = (Button)findViewById(R.id.submit);
        otp    = (Button)findViewById(R.id.otpsubmit);
        typeOfConnectionText = (TextView)findViewById(R.id.type_connection_text);
        typeOfConnectionImage = (ImageView)findViewById(R.id.type_connection_image);
       
     
        
        //Anoigo ta settings kai pairno tin dieuthinsi tou server
        sharedpreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String temp = sharedpreferences.getString("httpOrHttps", "");
        
        if(temp.equals("http://"))
        {
        	typeOfConnectionText.setText("Unsecure Connection Selected");
        	typeOfConnectionImage.setImageResource(R.drawable.unsecure_connection);
        }
        else if(temp.equals("https://") || temp.equals(""))
        {
        	temp = "https://";
        	typeOfConnectionText.setText("Secure Connection Selected");
        	typeOfConnectionImage.setImageResource(R.drawable.secure_connection);
        }
        else
        {
        	typeOfConnectionText.setText(temp);
        }

        server_address = "";
        server_address += temp;
        server_address += sharedpreferences.getString("address", "");
        server_address = server_address.replace(" ", "");
  

        
        //An exei dothei dieuthinsi sta settings
        if( (!server_address.equals("http://")) && (!server_address.equals("https://"))  )
        {	
        	//Energopoio ta koumpia
        	submit.setOnClickListener(this);
        	otp.setOnClickListener(this);
        }
        else
        {
        	//Allios emfanizo katallilo Toast, gia na enimeroso ton xristi
        	Toast.makeText(getBaseContext(), "Παρακαλώ αρχικοποιήστε τις ρυθμισεις της εφαρμογής",Toast.LENGTH_LONG).show(); 
        }
        checkEverything();
  
        
    }

    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//Anoigo ta settings kai pairno tin dieuthinsi tou server
		sharedpreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String temp = sharedpreferences.getString("httpOrHttps", "");
        
        if(temp.equals("http://"))
        {
        	typeOfConnectionText.setText("Unsecure Connection Selected");
        	typeOfConnectionImage.setImageResource(R.drawable.unsecure_connection);
        }
        else if(temp.equals("https://") || temp.equals(""))
        {
        	temp = "https://";
        	typeOfConnectionText.setText("Secure Connection Selected");
        	typeOfConnectionImage.setImageResource(R.drawable.secure_connection);
        }
        else
        {
        	typeOfConnectionText.setText(temp);
        }
        
        server_address = temp;
        server_address += sharedpreferences.getString("address", "");
        server_address = server_address.replace(" ", "");
        
        //An exei rithmistei dieuthinsi apo ta settings
        if((!server_address.equals("http://")) && (!server_address.equals("https://")))
        {	
        	//Energopoio ta koubia
        	submit.setOnClickListener(this);
        	otp.setOnClickListener(this);
        }
	}
    
    @SuppressWarnings("static-access")
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //Dimiourgo tis epiloges ton settings
        menu.add(menu.NONE, 0 ,menu.NONE,"Ρυθμίσεις");
        menu.add(menu.NONE, 1 ,menu.NONE,"About");
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	//Elegxo poia epilogi ton settings exei patithei
    	//kai arxizo to katallilo Activity
    	if(item.getItemId() == 0)
    	{
    		Intent intent = new Intent(this , settings.class);
		    startActivity(intent);
    		return true;
    	}
    	else if(item.getItemId() == 1)
    	{
    		Intent intent = new Intent(this , about.class);
		    startActivity(intent);
    		return true;
    		
    	}
    	return false;
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
    
	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.submit)
		{
			//An patithei to koubi sindesis me username/password
			//dimiourgei to minima pou tha steilei ston server
			String un = sharedpreferences.getString("my_username" , "");
		    String pw = sharedpreferences.getString("my_password" , "");
			
			//Elegxo an ta username kai password periexoun valid xaraktires
			if(checkValidField(un) && checkValidField(pw))
			{
				String toSend = "";
				toSend += un;
				toSend += ".";
				toSend += pw;
				
				
				//Ksekinaei to asingxrono task gia to aitima
				Log.i("Tha steilo to user_pass me data",toSend);
				Log.i("Tha to steilo sto link",server_address);
		        httptask_lessons = new HttpAsyncTask().execute(server_address,"user_pass",toSend);
		        
		        String x;
				try {
					
					//Pairnei tin apadisi
					x = httptask_lessons.get();
					
					Log.i("Apadisi sto login",x);
					
					//Kai elegxei tin apadisi
					if(x.trim().equals("SUCCESS"))
					{
						//An ola einai sosta, ksekinaei to loguserpass
						Log.i("Login with user-pass","SUCCESS");
						Intent intent = new Intent(this , loguserpass.class);
					    startActivity(intent);
					}
					else if(x.equals("")){
						Toast.makeText(getBaseContext(), "Empty Response from Server on USER/PASS LOGIN",Toast.LENGTH_LONG).show(); 
					}
					else
					{
						Log.i("FAIL",x);
						//Allios emfanizei minima lanthasmenon stoixeion
						Toast.makeText(getBaseContext(), "Failed to connect",Toast.LENGTH_LONG).show(); 
					}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
			else
			{
				//Allios emfanizei minima lanthasmenon stoixeion
				Toast.makeText(getBaseContext(), "Το username ή το password περιέχουν μη αποδεκτούς χαρακτήρες",Toast.LENGTH_LONG).show(); 
			}
			
		}	
		else if(v.getId() == R.id.otpsubmit)
		{	
			//An patithei to koubi gia sindesi me OTP
			//Emfanizei to katallilo Dialod Box gia na parei ton kodiko
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	
	        builder.setTitle("Σύνδεση OTP");
	        builder.setMessage("Σύνδεση με κωδικό OTP");
	        final EditText input = new EditText(this);
	        input.setHint("OTP Password");
	        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
	        builder.setView(input);
	        builder.setPositiveButton("Σύνδεση", new DialogInterface.OnClickListener() {
	
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	                
	                //Pairnei ton kodiko
	                String pw = input.getEditableText().toString();
	                
	                //Elegxei an o kodikos exei apagoreumenous xaraktires
	                if(checkValidField(pw))
	                {
	                	
	                	Log.i("Tha steilo to otp request me data",pw);
	                	Log.i("Tha to steilo sto link",server_address);
	                	//Kai ksekinaei to asingxrono task
		                httptask_lessons = new HttpAsyncTask().execute(server_address,"otp",pw);
		                
		                String x;
		        		try {
		        			
		        			//Pairnei tin apadisi tou server
		        			x = httptask_lessons.get();
		        			
		        			//Tin xorizei stis telies
		        			String[] separated = x.split("\\.");
		        			
		        			//Emfanizei einai SUCCESS i FAIL
		        			Log.i("OTP Result : ",separated[0]);
		        			if(separated[0].equals("SUCCESS"))
		        			{
		        				//Kai an einai ola sosta, ksekinaei apeutheias tin scanandsend 
		        				//pou tha labei apo ton server
		        				Intent myIntent = new Intent(getBaseContext(), scanandsend.class);
		        				myIntent.putExtra("SessionID",separated[1]);
		        				myIntent.putExtra("OTP","YES");
		        				myIntent.putExtra("OTPcode",pw);
		        				startActivity(myIntent);
		        			}
		        			else if(x.equals("")){
								Toast.makeText(getBaseContext(), "Empty Response from Server on OTP OGIN",Toast.LENGTH_LONG).show(); 
							}
		        			else
		        			{
		        				//An o kodikos einai lathos
		        				//emfanizei to katallilo minima
		        			    Toast.makeText(getBaseContext(), "OTP not correct",Toast.LENGTH_LONG).show(); 
		        			}
		        			
		        		} catch (InterruptedException e) {
		        			// TODO Auto-generated catch block
		        			e.printStackTrace();
		        		} catch (ExecutionException e) {
		        			// TODO Auto-generated catch block
		        			e.printStackTrace();
		        		}
	        		
	                }
	                else
	                {
	                	//emfanizei to katallilo minima
        			    Toast.makeText(getBaseContext(), "Το πεδίο ΟΤΡ περιέχει μη έγκυρους χαρακτήρες",Toast.LENGTH_LONG).show(); 
        			}	
	                
	            }
			
	        });
	        AlertDialog alert = builder.create();
	        alert.show();
		}
    
	}
	
	public boolean checkValidField(String string)
	{
		char bad[] = {'!','@','#','$','%','^','&','*','(',')'};
		
		for(int i=0; i<string.length(); i++)
		{
			char c = string.charAt(i);	
			for(int j=0; j<bad.length; j++)
				if(c == bad[j]) return false;
				
			
		}
		
		return true;
	}
	
	
	public boolean isConnected()
    {
    	//Elegxei an i siskeui einai sindedemeni sto internet, me WIFI i me Data sindesi
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) 
                return true;
            else
                return false;   
    }

	private boolean appInstalledOrNot(String uri)
	{
		//Elegxei an einai egatestimeno sti siskeui kapoio programma
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed ;
    }
	
	private void checkEverything(){
		
        //An den iparxei sindesi sto internet
        if(!isConnected())
        {
        	//Emfanizo to katallilo Dialod Box
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	
	        builder.setTitle("Δεν υπάρχει σύνδεση Internet");
	        builder.setMessage("Η συσκευή σας δεν είναι συνδεδεμένη στο Internet");
	        builder.setPositiveButton("Ενεργοποίηση WIFI", new DialogInterface.OnClickListener() {
	
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	                Intent settings = new Intent(Settings.ACTION_WIFI_SETTINGS);
	                settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                startActivity(settings);
	            }
			
	        });
	        builder.setNegativeButton("Έξοδος", new DialogInterface.OnClickListener() {
	        	
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	                finish();
	            }
			
	        });
	        AlertDialog alert = builder.create();
	        alert.show();
        }
        else
        {
        	//Allios an einai sindedemenos, elegxei an einai egatestimeno to Barcode Scanner
        	boolean installed  =   appInstalledOrNot("com.google.zxing.client.android");
            if(installed) Log.i("Installed","QR");
            else
            {
            	//An den einai egatestimeno, emfanizo to katallilo Dialog Box
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	
	            builder.setTitle("ZXing QR Code Scanner");
	            builder.setMessage("Για να λειτουργήσει σωστά η εφαρμογή, χρειάζεται η εγκατάσταση του πακέτου QR Code Scanner");
	
	            builder.setPositiveButton("Εγκατάσταση", new DialogInterface.OnClickListener() {
	
	                public void onClick(DialogInterface dialog, int which) {
	                    dialog.dismiss();
	                    
	                    Intent i = new Intent(android.content.Intent.ACTION_VIEW);
	                	i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.zxing.client.android"));
	                	startActivity(i);
	                }
	
	            });
	            
	            builder.setNegativeButton("Έξοδος", new DialogInterface.OnClickListener() {
	
	                @Override
	                public void onClick(DialogInterface dialog, int which) {
	                    dialog.dismiss();
	                    finish();
	                }
	            });
	
	            AlertDialog alert = builder.create();
	            alert.show();
	        	
	        }
        }
		
		
	}
	

	
}
