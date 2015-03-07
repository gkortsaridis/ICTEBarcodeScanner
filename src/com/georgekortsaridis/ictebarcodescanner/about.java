/*******************************************|
 * ICTE Barcode Scanner						|
 * 											|
 * Made By : Kortsaridis George				|
 * AEM     : 598							|
 * 											|
 * about.java								|
 * 											|
 ********************************************/


package com.georgekortsaridis.ictebarcodescanner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class about extends Activity implements OnClickListener{

	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		/*
		 * emfanizo tin othoni tou about
		 * Einai dilomeno sto manifest diaforetika.
		 */
		setContentView(R.layout.activity_about);
	}

	@Override
	public void onClick(View v) {

	}

}