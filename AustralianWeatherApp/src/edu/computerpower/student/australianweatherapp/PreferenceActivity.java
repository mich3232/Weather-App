package edu.computerpower.student.australianweatherapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

public class PreferenceActivity extends ActionBarActivity {

	private final static String PREFSETTINGS = "usersettings";
	private final static int PREF_MODE_PRIVATE = 0;
	private final static String PREFKEY_CITY = "city";
	private final static String PREFKEY_MEASUREMENT = "measurement";
	private final static String PREFKEY_FONT_COLOUR = "fontcolour";
	String Melbourne;
	String Metric;
	String White;
	Spinner city;
	Spinner measure;
	Spinner fontcol;
	Button savepref;
	Button getweather;
	TextView select;
	SharedPreferences sharedpreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preference);
		
		city = (Spinner)findViewById(R.id.city);
		measure = (Spinner)findViewById(R.id.measurement);
		fontcol = (Spinner)findViewById(R.id.fontcolour);
		savepref = (Button)findViewById(R.id.btnsavepref);
		getweather = (Button)findViewById(R.id.btngetweather);
		select = (TextView)findViewById(R.id.selection);
		
		Melbourne = "Melbourne";
	    Metric = "Metric";
	    White = "White";
		
	    sharedpreferences = getSharedPreferences(PREFSETTINGS, PREF_MODE_PRIVATE);
		
		String city1 = sharedpreferences.getString(PREFKEY_CITY, Melbourne);
		String measure1 = sharedpreferences.getString(PREFKEY_MEASUREMENT, Metric);
		String fontcolour = sharedpreferences.getString(PREFKEY_FONT_COLOUR, White);
		
		select.setText("Your Selected City: " + city1 + "\n" + "Your Selected Measurement: " + measure1 + "\n" + "Your Selected Text Colour: " + fontcolour);
		
		
		savepref.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				String c = city.getItemAtPosition(city.getSelectedItemPosition()).toString();
				String m = measure.getItemAtPosition(measure.getSelectedItemPosition()).toString();
				String fc = fontcol.getItemAtPosition(fontcol.getSelectedItemPosition()).toString();
				
				SharedPreferences.Editor editor = sharedpreferences.edit();
				
				editor.putString(PREFKEY_CITY, c);
	            editor.putString(PREFKEY_MEASUREMENT, m);
	            editor.putString(PREFKEY_FONT_COLOUR, fc);
	            editor.commit();
	            Toast.makeText(PreferenceActivity.this, "City: " + c + " Measurement: " + m + " Font Colour: " + fc + " were saved as your user settings.", Toast.LENGTH_LONG).show();
			}
		});
	}
	
	
	public void GetWeather(View view){
		Intent intent = new Intent(this, GetWeatherActivity.class);
		startActivity(intent);
		}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preference, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
