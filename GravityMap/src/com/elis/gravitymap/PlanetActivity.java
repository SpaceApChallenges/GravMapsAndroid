package com.elis.gravitymap;

import java.util.Locale;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;

public class PlanetActivity extends ActionBarActivity {
	double weight = 0.0;
	EditText et = null;
	Spinner spinner = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_planet);
		
		 
		spinner = (Spinner) findViewById(R.id.spinner1);
		String[] planets = {"Mercury","Venus","Mars","Jupiter","Saturn","Uranus","Neptune"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item, planets);
		spinner.setAdapter(adapter);
		et = (EditText) findViewById(R.id.editText2);
		final Double[] gravities = {3.703 , 8.872, 3.728, 25.93, 11.19, 9.01, 11.28 };
		 int imageId = getResources().getIdentifier(((String)planets[ (int) spinner.getSelectedItemId()]).toLowerCase(Locale.getDefault()),
                 "drawable", getPackageName());
		 ((ImageView) findViewById(R.id.image_planet)).setImageResource(imageId);
		Button b1 = (Button) findViewById(R.id.buttonCalc);
		b1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				weight = Double.parseDouble(et.getText().toString());
				AlertDialog.Builder builder=new AlertDialog.Builder(getApplicationContext());
				builder.setTitle("Weight");
				builder.setMessage("Your weight is: " + (weight / 9.8067 * gravities[spinner.getSelectedItemPosition()]));
				builder.setCancelable(true);
				AlertDialog alert=builder.create();
				alert.show();
				
			}
		});
		
		
	}

	



}
