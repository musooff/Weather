package com.id20140921.weather;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends Activity implements LocationListener {

    protected LocationManager locationManager;
    protected Location lastKnownLocation;
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    TextView tx_location,tx_weather,tx_temp,tx_time,tx_max_temp,tx_min_temp,tx_rain_prob;
    SwipeRefreshLayout ml;

    ImageView im_weather,im_refresh;

    Integer[] images = {
            R.drawable.chanceflurries,
            R.drawable.chancerain,
            R.drawable.chancesleet,
            R.drawable.chancesnow,
            R.drawable.chancetstorms,
            R.drawable.clear,
            R.drawable.cloudy,
            R.drawable.flurries,
            R.drawable.fog,
            R.drawable.hazy,
            R.drawable.mostlycloudy,
            R.drawable.mostlysunny,
            R.drawable.partlycloudy,
            R.drawable.partlysunny,
            R.drawable.rain,
            R.drawable.sleet,
            R.drawable.snow,
            R.drawable.sunny,
            R.drawable.tstorms,
            R.drawable.unknown
    };
    String[] names = {
            "Chance of Flurries",
            "Chance of Rain",
            "Chance of Sleet",
            "Chance of Snow",
            "Chance of Thunderstorms",
            "Clear",
            "Cloudy",
            "Flurries",
            "Fog",
            "Haze",
            "Mostly Cloudy",
            "Mostly Sunny",
            "Partly Cloudy",
            "Partly Sunny",
            "Rain",
            "Sleet",
            "Snow",
            "Sunny",
            "Thunderstorms",
            "Unknown"
    };

    String[] f_time;
    Integer[] white_images;
    String[] f_temp;
    String[] f_pop;

    String[] f_day;
    Integer[] colorful_images;
    String[] f_day_temp_max;
    String[] f_day_temp_min;
    String[] f_day_pop;

    GridView forecast_day,forecast_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("WeatherInfo",0);
        editor = sharedPreferences.edit();
        ml = (SwipeRefreshLayout) findViewById(R.id.main_layout);

        tx_location = (TextView) findViewById(R.id.textView_city_name);
        tx_weather = (TextView)findViewById(R.id.textView_weather_condition);
        tx_temp = (TextView)findViewById(R.id.textView_temparature);
        tx_time = (TextView)findViewById(R.id.textView_time);
        tx_max_temp = (TextView)findViewById(R.id.textView_max_temp);
        tx_min_temp = (TextView)findViewById(R.id.textView_min_temp);
        tx_rain_prob = (TextView)findViewById(R.id.textView_rain_prob);

        im_weather = (ImageView)findViewById(R.id.imageView_condition);
        im_refresh = (ImageView)findViewById(R.id.imageView_refresh);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TWO_MINUTES, 10, this);
        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        f_time = new String[]{"1PM","2PM","3PM","4PM","5PM"};
        white_images = new Integer[]{images[6],images[6],images[6],images[6],images[6]};
        f_temp = new String[]{"13","13","13","13","13"};
        f_pop = new String[]{"20","20","20","20","20"};


        f_day = new String[]{"Day 1", "Day 2", "Day3"};
        colorful_images = new Integer[]{images[6],images[6],images[6]};
        f_day_temp_max = new String[]{"15","15","15"};
        f_day_temp_min = new String[]{"-3","-3","-3"};
        f_day_pop = new String[]{"20","20","20"};





        forecast_time = (GridView) findViewById(R.id.forecase_time);
        forecast_time.setAdapter(new ListAdapterTime(this, f_time, white_images, f_temp,f_pop));

        forecast_day = (GridView) findViewById(R.id.forecase_day);
        forecast_day.setAdapter(new ListAdapterDay(this, f_day, colorful_images, f_day_temp_max, f_day_temp_min,f_day_pop));

        ml.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                ml.setRefreshing(true);
                if (isNetworkAvailable()){
                    new GetWundergroundData().execute();
                }
                else {
                    updateALL(sharedPreferences.getString("last_wunder",getString(R.string.wunder_data)));
                    Toast.makeText(MainActivity.this,"Please, Check your network connection.",Toast.LENGTH_SHORT).show();
                }
                ml.setRefreshing(false);
            }
        });

        im_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetworkAvailable()){
                    new GetWundergroundData().execute();
                }
                else {
                    updateALL(sharedPreferences.getString("last_wunder",getString(R.string.wunder_data)));
                    Toast.makeText(MainActivity.this,"Please, Check your network connection.",Toast.LENGTH_SHORT).show();
                }
            }
        });


        if (isNetworkAvailable()){
            new GetWundergroundData().execute();
        }
        else {
            updateALL(sharedPreferences.getString("last_wunder",getString(R.string.wunder_data)));
            Toast.makeText(this,"Please, Check your network connection.",Toast.LENGTH_SHORT).show();
        }
    }

    private String readStream(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();

        String line;

        try{
            while ((line = reader.readLine())!=null){
                stringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isBetterLocation(location,lastKnownLocation)){
            lastKnownLocation = location;
            /*
            new GetCity().execute();
            new GetWeather().execute();
            new GetForecast().execute();
            forecast_day.setAdapter(new ListAdapterDay(MainActivity.this, f_day, colorful_images, f_day_temp_max, f_day_temp_min,f_day_pop));
            forecast_time.setAdapter(new ListAdapterTime(MainActivity.this, f_time, white_images, f_temp,f_pop));
            */
            if (isNetworkAvailable()){
                new GetWundergroundData().execute();
            }
            else {
                updateALL(sharedPreferences.getString("last_wunder",getString(R.string.wunder_data)));
                Toast.makeText(this,"Please, Check your network connection.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }
        return false;
    }

    private class GetWundergroundData extends AsyncTask<Object,Object,String>{
        @Override
        protected String doInBackground(Object... objects) {
            String result = null;
            try {
                URL url = new URL("http://api.wunderground.com/api/065b8af3d8f177ce/forecast/geolookup/conditions/hourly/q/"+lastKnownLocation.getLatitude()+","+lastKnownLocation.getLongitude()+".json");
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                result = readStream(in);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            editor.putString("last_wunder",s);
            editor.apply();
            updateALL(sharedPreferences.getString("last_wunder",getString(R.string.wunder_data)));
        }
    }

    private void updateALL(String wund){
        try {
            JSONObject jsonObject = new JSONObject(wund);
            JSONObject json_location = jsonObject.getJSONObject("location");

            /*Update Location tx*/

            tx_location.setText(json_location.getString("city"));

            /*Update Weather Conditions*/

            JSONObject co = jsonObject.getJSONObject("current_observation");
            tx_weather.setText(co.getString("weather"));
            tx_temp.setText(co.getString("temp_c")+"°");
            tx_time.setText(co.getString("local_time_rfc822").split(" ")[4]);
            String cond = co.getString("weather");
            if (Arrays.asList(names).contains(cond)){
                im_weather.setImageResource(images[Arrays.asList(names).indexOf(cond)]);
            }

            if (cond.equals("Chance of Rain") || cond.equals("Rain")){
                ml.setBackgroundResource(R.drawable.back_rainy);
            }
            else if ((cond.equals("Flurries")) || cond.equals("Sleet") || cond.equals("Snow")){
                ml.setBackgroundResource(R.drawable.back_snow);
            }
            else if ((co.equals("Mostly Sunny")) || cond.equals("Partly Sunny") || cond.equals("Sunny")){
                ml.setBackgroundResource(R.drawable.back_sunny);
            }
            else{
                ml.setBackgroundResource(R.drawable.back_cloudy);
            }

            /*Update next Weather conditions of next 3 days and todays max&min*/

            JSONObject json_forecast = jsonObject.getJSONObject("forecast");
            JSONObject json_simpleforecast = json_forecast.getJSONObject("simpleforecast");
            JSONArray json_forecastday = json_simpleforecast.getJSONArray("forecastday");
            for (int i = 0;i<4;i++){
                JSONObject json_per = json_forecastday.getJSONObject(i);
                JSONObject json_date = json_per.getJSONObject("date");
                JSONObject json_high = json_per.getJSONObject("high");
                JSONObject json_low = json_per.getJSONObject("low");

                if (i == 0){
                    tx_max_temp.setText(json_high.getString("celsius"));
                    tx_min_temp.setText(json_low.getString("celsius"));
                    tx_rain_prob.setText(json_per.getString("pop")+"%");
                }
                else {

                    f_day[i-1] = json_date.getString("weekday");
                    f_day_temp_max[i-1] = json_high.getString("celsius");
                    f_day_temp_min[i-1] = json_low.getString("celsius");
                    f_day_pop[i-1] = json_per.getString("pop");
                    String cond2 = json_per.getString("conditions");
                    if (Arrays.asList(names).contains(cond2)){
                        colorful_images[i-1] = images[Arrays.asList(names).indexOf(cond2)];
                    }
                }
            }

            /*Update Weather Conditions of next 5 hours*/
            JSONArray json_hourly_forecast = jsonObject.getJSONArray("hourly_forecast");
            for (int i = 0; i<5;i++){
                JSONObject json_time = json_hourly_forecast.getJSONObject(i);
                JSONObject json_FCTTIME = json_time.getJSONObject("FCTTIME");
                JSONObject json_temp = json_time.getJSONObject("temp");
                f_time[i] = json_FCTTIME.getString("civil");
                f_temp[i] = json_temp.getString("metric");
                f_pop[i] = json_time.getString("pop");

                String cond3 = json_time.getString("condition");
                if (Arrays.asList(names).contains(cond3)){
                    white_images[i] = images[Arrays.asList(names).indexOf(cond3)];
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        forecast_day.setAdapter(new ListAdapterDay(MainActivity.this, f_day, colorful_images, f_day_temp_max, f_day_temp_min,f_day_pop));
        forecast_time.setAdapter(new ListAdapterTime(MainActivity.this, f_time, white_images, f_temp,f_pop));

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
