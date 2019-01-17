package edu.computerpower.student.australianweatherapp;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import edu.computerpower.student.australianweatherapp.weatherobjects.List;
import edu.computerpower.student.australianweatherapp.weatherobjects.Main;
import edu.computerpower.student.australianweatherapp.weatherobjects.RootObject;
import edu.computerpower.student.australianweatherapp.weatherobjects.Weather;

public class GetWeatherActivity extends ActionBarActivity {
	//declares cl;ass varriables
	private final static String PREFSETTINGS = "usersettings";
	private final static int PREF_MODE_PRIVATE = 0;
    private final static String PREFKEY_CITY = "city";
    private final static String PREFKEY_MEASUREMENT = "measurement";
    private final static String PREFKEY_FONT_COLOUR = "fontcolour";
    
    String Melbourne;
    String Metric ;
    String White ;
    String city1;
    String measure1;
    String fontcolour;
    SharedPreferences sharedpreferences;
	TextView dateTime;
	TextView userSettings;
	TextView forecast;
	Calendar c;
	int textviews;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_weather);
		
		dateTime = (TextView)findViewById(R.id.dateTime);
		
		//sets date and time string		
		c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        dateTime.setText("Current Date and Time : "+ "\n" + formattedDate); //displays date and time
        //initialises variables
        Melbourne = "Melbourne";
        Metric = "Metric";
        White = "White";
        
        userSettings = (TextView)findViewById(R.id.userSettings);
        sharedpreferences = getSharedPreferences(PREFSETTINGS, PREF_MODE_PRIVATE);
        //initialises variables to the sharedpreferences
        city1 = sharedpreferences.getString(PREFKEY_CITY, Melbourne);
        measure1 = sharedpreferences.getString(PREFKEY_MEASUREMENT, Metric);
        fontcolour = sharedpreferences.getString(PREFKEY_FONT_COLOUR, White);
        //sets city choice 
        if (city1 != null) {
            userSettings.setText("Your current city is set to " + city1);
        } else {
        	city1 = Melbourne;
            userSettings.setText("You do not have a city chosen the default city is set to " + city1 +". You can change this by navigating to the user preference setting page.");
        }
       //calls colour chooser method
        colourChooser();
        
        //calls checkWebconnection and displays message if no connection
        boolean val = this.checkWebConnection();
		if (val == true) {
		
		} else {
			Toast.makeText(GetWeatherActivity.this, "No Internet Connection, please try again shortly",Toast.LENGTH_LONG).show();	
		}
	//calls for execution of ForecastForCityTask class tasks
		new ForecastForCityTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.get_weather, menu);
		return true;
		}
	//sets what happens when settings is clicked
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 int id = item.getItemId();
	        switch (id) {
	            case R.id.action_settings:
	                Intent intent = new Intent(this, PreferenceActivity.class);
	                startActivity(intent);
	                return true;
	            default:

	                //noinspection SimplifiableIfStatement
	                if (id == R.id.action_settings) {
	                    return true;
	                }

	                return super.onOptionsItemSelected(item);}
	        }
	
	//checks for web connection
	public boolean checkWebConnection() {
		ConnectivityManager mgr = (ConnectivityManager)
			getSystemService(Context.CONNECTIVITY_SERVICE);
		
			NetworkInfo info = mgr.getActiveNetworkInfo();
		
			if (info.isConnectedOrConnecting()) {
				if (info.isConnected()) {
					return true;
				}
				return false;
			}
			return false;
		}
	//sets textcolour according to default colour or shared preference set colour
	public void colourChooser(){
		int textviews[]={R.id.userSettings, R.id.dateTime, R.id.temp, R.id.wforecast, R.id.forecast, R.id.timetxt, R.id.timetxt1, R.id.timetxt2, R.id.timetxt3, R.id.timetxt4, R.id.timetxt5, R.id.timetxt6, R.id.timetxt7};
		for(int i=0;i<textviews.length;i++)
			if(fontcolour.equals("Blue"))//if blue is chosen set text to blue
				((TextView)findViewById(textviews[i])).setTextColor(Color.BLUE);
			else
				if(fontcolour.equals("Red"))//if red is chosen set text to red
					((TextView)findViewById(textviews[i])).setTextColor(Color.RED);
			else				
				if(fontcolour.equals("White"))//if white is chosen set text to white
					((TextView)findViewById(textviews[i])).setTextColor(Color.WHITE);
			else				
				if(fontcolour.equals("Yellow"))//if yellow is chosen set text to white
					((TextView)findViewById(textviews[i])).setTextColor(Color.YELLOW);
			else				
				if(fontcolour.equals("Green"))//if green is chosen set text to white
					((TextView)findViewById(textviews[i])).setTextColor(Color.GREEN);
			else
			((TextView)findViewById(textviews[i])).setTextColor(Color.WHITE);//if no choose made set text to white
		}
	
		
	public class ForecastForCityTask extends AsyncTask<String, Integer,
	        String> {
		//Declares variables
		
		DateTimeFormatter dateTimeFormatterTo;
		 private final String SERVICE_ADDRESS_START =
				 "http://api.openweathermap.org/data/2.5/forecast?q=" + city1 + ",au";
		 private final String SERVICE_ADDRESS_END = "&units=" + measure1 + "&APPID=3cce97a96d9979833d08904c9082e8e2" ;	 
		 JSONObject jObj = new JSONObject();//declare a new jsonObject
		 	
			@Override
			protected String doInBackground(String... params) {
				//declares variables
				 String serviceAddress = SERVICE_ADDRESS_START + 
						 SERVICE_ADDRESS_END;
				 
				 Log.d("service address", serviceAddress );
				 StringBuilder response = new StringBuilder();
				 DefaultHttpClient client = new DefaultHttpClient();
				 HttpGet httpGet = new HttpGet(serviceAddress);
				 //get json string
				 try {
					 HttpResponse execute = client.execute(httpGet);
					 InputStream content = execute.getEntity().getContent();
					 BufferedReader buffer = new BufferedReader(new
					 InputStreamReader(content));
				 
					 String bufferString = "";
				
				while ((bufferString = buffer.readLine()) != null) {
				 	response.append(bufferString);
				 }
				 
			} catch (Exception e) {
				 e.printStackTrace();
			}
				 
			return response.toString();//returns json string
		}
			
			@Override
			protected void onPostExecute(String result) {
				
				//declares and initialises method scope variablies
				String displayTime= "";
				String displayString = "";
				String displayIcon = "";
				String temp = "";
				String min_temp = "";
				String max_temp = "";
				String time = "";
				String icons = "";
				double day_min_temp = 50.0;
				double day_max_temp = 0.0;
				String fTimeZoneId = "UTC";
				String tTimeZoneId = "Australia/" + city1;
				String dateTimePattern = "yyyy-MM-dd HH:mm:ss";
				
				Log.d("result", result);
				forecast = (TextView)findViewById(R.id.wforecast);
				 
				//deserialise json to gson
				Gson gson = new Gson();
				RootObject rootObject = gson.fromJson(result, RootObject.class);
				//iterate through gson result for rootObject and get top 8 results
				for(int i=0; i < 8; i++){
					
					temp = rootObject.getList().get(0).getMain().getTemp().toString();//set temp variable
					min_temp = rootObject.getList().get(i).getMain().getTempMin().toString();//set min_temp variable
					double minValue = Double.parseDouble(min_temp);
					if( minValue < day_min_temp)
						day_min_temp = minValue;
					
					max_temp = rootObject.getList().get(i).getMain().getTempMax().toString();//set max_temp variable
					double maxValue = Double.parseDouble(max_temp);
					if( maxValue > day_max_temp)
						day_max_temp = maxValue;
					
					displayString = "Temperature: " + temp + "\n" + "Minimum temperature: " + day_min_temp + "\n" + "Maximum temperature: " + day_max_temp + "\n";//sets display string
					
					time = rootObject.getList().get(i).getDtTxt().toString();//sets time 
					icons = "img" + rootObject.getList().get(i).getWeather().get(0).getIcon().toString();//sets icons
					displayTime = time;//sets display time
					displayIcon = icons;//sets display icon
					Log.d("displayTime", displayTime);//logs displayTime
					Log.d("displayIcon", displayIcon);//logs displayIcon
					
					//makes an array of textviews and sets them to displaytime
					int texttime[] = {R.id.timetxt, R.id.timetxt1, R.id.timetxt2,R.id.timetxt3, R.id.timetxt4, R.id.timetxt5,R.id.timetxt6, R.id.timetxt7};
					String fTimeString = displayTime;
					((TextView)findViewById(texttime[i])).setText(convertTimeString(fTimeString, fTimeZoneId, tTimeZoneId, dateTimePattern));
					
					//makes an array of imageviews and sets them to displayIcon
					int imageviews[] = {R.id.icon, R.id.icon1, R.id.icon2, R.id.icon3, R.id.icon4, R.id.icon5, R.id.icon6, R.id.icon7};
					int image = getResources().getIdentifier(displayIcon, "drawable", getPackageName());
					ImageView iV = (ImageView)findViewById(imageviews[i]);
					iV.setImageResource(image);
				}	
				//sets forecast text to the displayString
				
				forecast.setText(displayString);
				Log.d("DisplayString", displayString);//logs displayString
				
			}
			
			public String convertTimeString(String fTimeString, String fTimeZoneId,
					String tTimeZoneId, String dateTimePattern) {
				// method to convert from UTC to EST
				Log.d("fTimeZoneId",fTimeZoneId);
				Log.d("tTimeZoneId",tTimeZoneId);
				Log.d("dateTimePattern",dateTimePattern);
				Log.d("fTimeString",fTimeString);
				DateTimeZone fromZoneDateTimeZone = DateTimeZone.forID(fTimeZoneId);
				Log.d("fromZoneDateTimeZone",fromZoneDateTimeZone.toString() );
				DateTimeZone toZoneDateTimeZone = DateTimeZone.forID(tTimeZoneId);
				Log.d("toZoneDateTimeZone",toZoneDateTimeZone.toString());
				DateTimeFormatter dateTimeFormatterFromTime = DateTimeFormat.forPattern(dateTimePattern).withZone(fromZoneDateTimeZone);
				Log.d("dateTimeFormatterFromTime",dateTimeFormatterFromTime.toString());
				DateTime fromTimeDateTime = dateTimeFormatterFromTime.parseDateTime(fTimeString);
				Log.d("fromTimeDateTime",fromTimeDateTime.toString());
				
				DateTime toTimeDateTime = fromTimeDateTime.toDateTime(toZoneDateTimeZone);
				Log.d("toTimeDateTime", toTimeDateTime.toString());
				String dateTimePattern1 = "h aa";
				String Mid = "Midnight";
				String Noon = "Noon";
				DateTimeFormatter dateTimeFormatterTo = DateTimeFormat.forPattern(dateTimePattern1).withZone(toZoneDateTimeZone);
				Log.d("dateTimeFormatterTo",dateTimeFormatterTo.toString());
				//sets 12 o'clock to noon or midnight
				String convertedTime = toTimeDateTime.toString(dateTimeFormatterTo);
				Log.d("ConvertedTime", convertedTime);
				if(convertedTime.equals("12 PM"))
					convertedTime = Noon;
				Log.d("Noon", Noon);
				
				if(convertedTime.equals("12 AM"))
					convertedTime = Mid;	
				Log.d("Mid", Mid);
				
				return convertedTime;//returns the converted time
				
				}
			}
		}

	

	 
			



	

	

	
