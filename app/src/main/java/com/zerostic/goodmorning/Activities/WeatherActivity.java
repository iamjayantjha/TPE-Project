package com.zerostic.goodmorning.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Main;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Application.WeatherAPI;
import com.zerostic.goodmorning.Application.WeatherData;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;
import com.zerostic.goodmorning.BuildConfig;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 Coded by iamjayantjha
 **/

public class WeatherActivity extends AppCompatActivity {
    TextView dsc, tv, humidityText, lat, lon,locationText,time,todaysDay,sunriseTimeTV,sunsetTimeTV,windTV;
    EditText locationEditText;
    MaterialButton weather;
    String apikey = BuildConfig.WEATHER_API_KEY;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    String latitude,longitude;
    String param1="",loc="", name;
    ImageView dscIcon,locationIcon,more;
    int hours,minutes;
    String notation="AM",today;
    int day;
    String min,voiceSettings;
    RelativeLayout rl1;
    long sunrise,sunset,dayLength;
    LottieAnimationView lottieAnimationView;
    Dialog dialog;
    MaterialCardView locationCard,sunriseCard,sunsetCard;
    boolean visible = false, toSpeak, isSettingsAvailable;
    private TextToSpeech tts;
    TextView dayLengthTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        readUserData();
        readUserSettings();
        dsc = findViewById(R.id.dsc);
        tv = findViewById(R.id.temperature);
        locationEditText = findViewById(R.id.location);
        dayLengthTV = findViewById(R.id.dayLength);
        weather = findViewById(R.id.weather);
        humidityText = findViewById(R.id.humidity);
        windTV = findViewById(R.id.wind);
        dscIcon = findViewById(R.id.dscIcon);
        lat = findViewById(R.id.latitude);
        lon = findViewById(R.id.longitude);
        lottieAnimationView = findViewById(R.id.animationView);
        time= findViewById(R.id.time);
        rl1 = findViewById(R.id.rl1);
        sunriseTimeTV = findViewById(R.id.sunriseTime);
        sunsetTimeTV = findViewById(R.id.sunsetTime);
        locationCard = findViewById(R.id.locationCard);
        todaysDay = findViewById(R.id.day);
        locationIcon = findViewById(R.id.locationIcon);
        locationText = findViewById(R.id.locationText);
        sunriseCard = findViewById(R.id.sunriseCard);
        sunsetCard = findViewById(R.id.sunsetCard);
        more = findViewById(R.id.more);
        Calendar rightNow = Calendar.getInstance();
        hours = rightNow.get(Calendar.HOUR_OF_DAY);
        minutes = rightNow.get(Calendar.MINUTE);
        dialog = new Dialog(WeatherActivity.this);
        dialog.setContentView(R.layout.weather_search_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        Button okay = dialog.findViewById(R.id.okayBtn);
        Button cancel = dialog.findViewById(R.id.cancelBtn);
        TextView title = dialog.findViewById(R.id.headerText);
        TextView message = dialog.findViewById(R.id.confirmText);
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS){
                int result = tts.setLanguage(Locale.ENGLISH);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                    Toast.makeText(getApplicationContext(),"Language is not supported",Toast.LENGTH_SHORT).show();
                    toSpeak = false;
                }else {
                    toSpeak = true;

                }
            }
        });
        if (minutes<=9){
            min = "0"+minutes;
        }else {
            min = String.valueOf(minutes);
        }
        if (hours == 0){
            time.setText("12:"+min+" "+notation);
        }else {
            if (hours>12){
                hours = hours-12;
                notation = "PM";
            }
            time.setText(hours+":"+min+" "+notation);
        }
        day = rightNow.get(Calendar.DAY_OF_WEEK);
        switch (day){
            case 1: today = "Sunday";
                break;

            case 2: today = "Monday";
                break;

            case 3: today = "Tuesday";
                break;

            case 4: today = "Wednesday";
                break;

            case 5: today = "Thursday";
                break;

            case 6: today = "Friday";
                break;

            case 7: today = "Saturday";
                break;
        }
        Date c = rightNow.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM", Locale.getDefault());
        String formattedDate = df.format(c);
        todaysDay.setText(today+", "+formattedDate);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if ((ActivityCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ID);
        } else {
            weather.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                if (isNetworkConnected()){
                    getWeather();
                    getInformation();
                }else {
                    Toast.makeText(getApplicationContext(),"Internet is not connected can't get weather right now.", Toast.LENGTH_SHORT).show();
                }
                locationText.setText(locationEditText.getText().toString().trim());
            });

        }

        okay.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            if (visible){
                locationCard.setVisibility(View.GONE);
                weather.setVisibility(View.GONE);
                visible = false;
            }else {
                locationCard.setVisibility(View.VISIBLE);
                weather.setVisibility(View.VISIBLE);
                visible = true;
            }
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            dialog.dismiss();
        });

        more.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            if (visible){
                title.setVisibility(View.VISIBLE);
                title.setText(R.string.weather_dialog_title_toinvisible);
                message.setVisibility(View.VISIBLE);
                message.setText(R.string.weather_dialog_message_toinvisible);
            }else {
                title.setVisibility(View.VISIBLE);
                title.setText(R.string.weather_dialog_title);
                message.setVisibility(View.VISIBLE);
                message.setText(R.string.weather_dialog_message);
            }
            dialog.show();
        });

    }

    private void getWeather() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherAPI myApi = retrofit.create(WeatherAPI.class);
        Call<WeatherData> weatherDataCall = myApi.getWeather(locationEditText.getText().toString().trim(), apikey);
        weatherDataCall.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(@NotNull Call<WeatherData> call, @NotNull Response<WeatherData> response) {
                if (response.code() == 404) {
                    Toast.makeText(getApplicationContext(), "Location Not Found", Toast.LENGTH_SHORT).show();
                }else if (response.code() == 400){
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"We are getting weather forecast for your location please wait.",Toast.LENGTH_SHORT).show();
                 }else if (!(response.isSuccessful())) {
                    Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_SHORT).show();
                } else {
                    lottieAnimationView.setVisibility(View.GONE);
                    WeatherData myData = response.body();
                    assert myData != null;
                    Main main = myData.getMain();
                   // Double temp = main.getTemp();
                  //  int temperature = (int) (temp - 273.15);
                    int humidity = main.getHumidity();
                   // tv.setText(String.valueOf(temperature) + "°");
                    humidityText.setText(humidity+"%");
                    SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("humidity",humidityText.getText().toString().trim());
                    editor.apply();
                }

            }

            @Override
            public void onFailure(@NotNull Call<WeatherData> call, @NotNull Throwable t) {
               // Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Unable to get weather forecast for your location. \nMake sure you are connected to the Internet", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void getInformation() {
        String tempUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + locationEditText.getText().toString().trim() + "&appid="+BuildConfig.WEATHER_API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("weather");
                JSONObject object = jsonArray.getJSONObject(0);
                String description = object.getString("main");
                String naturalDescription = getNaturalDescription(description);
                String icon = object.getString("icon");
                JSONObject mainObject = jsonObject.getJSONObject("main");
                String temp = mainObject.getString("feels_like");
                double mainTemperature = Double.parseDouble(temp);
                int temperature = (int) (mainTemperature - 273.15);
                tv.setText(temperature + "°");
                if (!tv.getText().equals("") && (voiceSettings.equals("1") || voiceSettings.equals("3") || voiceSettings.isEmpty())){
                    String dsc = description;
                    if (description.equals("Rain")){
                        dsc = "Raining";
                    }
                    String speech;
                    if (locationEditText.getText().toString().trim().equalsIgnoreCase(DataBaseHelper.getUserLocation(WeatherActivity.this)) || DataBaseHelper.getUserLocation(WeatherActivity.this).contains(locationEditText.getText().toString().trim())) {
                        // Default weather information
                        speech = String.format(Locale.getDefault(),"%s, the weather is %d degrees, and it's %s outside.", name, temperature, naturalDescription);
                    } else {
                        // Weather information for a specific location
                        String location = locationEditText.getText().toString().trim();
                        speech = String.format(Locale.getDefault(),"%s, the weather in %s is %d degrees, and it's %s here.", name, location, temperature, naturalDescription);
                    }
                    say(speech);
                }
                String iconUrl = "https://openweathermap.org/img/w/" + icon + ".png";
                JSONObject wind = jsonObject.getJSONObject("wind");
                String speed = wind.getString("speed");
                JSONObject sys = jsonObject.getJSONObject("sys");
                String rise = sys.getString("sunrise");
                String set = sys.getString("sunset");
                sunrise = Long.parseLong(rise);
                sunset = Long.parseLong(set);
                Log.i("Sunrise Sunset",sunrise+"");
                dayLength = sunset - sunrise;
                windTV.setText(speed);
                long seconds = Math.abs(dayLength);
                long hours = seconds / 3600;
                long minutes = (seconds % 3600) / 60;
                dayLengthTV.setText("Day Length "+hours + "h:" + minutes+"m");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    String sunriseTime = formattedTime(Instant.ofEpochSecond(sunrise));
                    String sunsetTime = formattedTime(Instant.ofEpochSecond(sunset));
                    sunriseCard.setVisibility(View.VISIBLE);
                    sunsetCard.setVisibility(View.VISIBLE);
                    sunriseTimeTV.setText(sunriseTime);
                    sunsetTimeTV.setText(sunsetTime);
                    SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("sunset",sunsetTimeTV.getText().toString().trim());
                    editor.putString("sunrise",sunriseTimeTV.getText().toString().trim());
                    editor.apply();
                }
                dsc.setText(description);
                SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("temperature",tv.getText().toString().trim());
                editor.putString("dsc",dsc.getText().toString().trim());
                editor.putString("speed",speed);
                editor.apply();
                Picasso.get().load(iconUrl).into(dscIcon);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> //Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_LONG).show());
                Toast.makeText(getApplicationContext(), "Unable to get weather forecast for your location. \nMake sure you are connected to the Internet", Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    private void getLastLocation() {

        if (isLocationEnabled()) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ID);
            }
            mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if (location == null) {
                    requestNewLocationData();
                    //Toast.makeText(getApplicationContext(),"Unable to get your location please try again",Toast.LENGTH_LONG).show();
                } else {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            assert addresses != null;
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            city = city+", "+country;
            SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("city",city);
            editor.apply();
            saveData(city);
            if (locationEditText.getText().toString().trim().isEmpty()){
                locationEditText.setText(city);
                locationText.setText(city);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
                    lat.setText(location.getLatitude() + "");
                    latitude = location.getLatitude() + "";
                    lon.setText(location.getLongitude() + "");
                    longitude = location.getLongitude() + "";
                }
            });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }

    private void saveData(String location) {
        try (DataBaseHelper db = new DataBaseHelper(this)) {
            String id = "1";
            Cursor res = db.getLocationData();
            if (res != null && res.getCount() > 0) {
                if (param1 == null || param1.isEmpty()) {
                    db.insertLocation(id, location);
                } else {
                    db.updateLocation(id, location);
                }
            } else {
                db.insertLocation(id, location);
            }
        }

    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (checkPermissions()) {
            //getLastLocation();
            if (loc == null || loc.isEmpty()){
                readLocation();
                SharedPreferences preferences1=getSharedPreferences("PREFS",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences1.edit();
                editor.putString("city",loc);
                editor.apply();
                locationEditText.setText(loc);
                locationText.setText(loc);
            }else {
                SharedPreferences preferences1=getSharedPreferences("PREFS",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences1.edit();
                editor.putString("city",loc);
                editor.apply();
                locationEditText.setText(loc);
                locationText.setText(loc);
                if (isNetworkConnected()){
                    getWeather();
                }else
                {
                    SharedPreferences preferences = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                    String weather = preferences.getString("temperature", "");
                    String city = preferences.getString("city", "");
                    String description = preferences.getString("dsc", "");
                    String sunset = preferences.getString("sunset", "");
                    String sunrise = preferences.getString("sunrise", "");
                    String humidity = preferences.getString("humidity", "");
                    String speed = preferences.getString("speed", "");
                    windTV.setText(speed);
                    sunsetCard.setVisibility(View.VISIBLE);
                    sunriseCard.setVisibility(View.VISIBLE);
                    tv.setText(weather);
                    locationText.setText(city);
                    sunsetTimeTV.setText(sunset);
                    sunriseTimeTV.setText(sunrise);
                    humidityText.setText(humidity);
                    dsc.setText(description);

                }
               // getInformation();
            }
        }else {
            requestPermissions();
        }

        Calendar rightNow = Calendar.getInstance();
        hours = rightNow.get(Calendar.HOUR_OF_DAY);
        minutes = rightNow.get(Calendar.MINUTE);
        if (minutes<=9){
            min = "0"+minutes;
        }else {
            min = String.valueOf(minutes);
        }
        if (hours == 0){
            time.setText("12:"+min+" "+notation);
        }else {
            if (hours>12){
                hours = hours-12;
                notation = "PM";
            }
            time.setText(hours+":"+min+" "+notation);
        }


    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
       // LocationRequest mLocationRequest = new LocationRequest();
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat.setText("Latitude: " + mLastLocation.getLatitude() + "");
            latitude = String.valueOf(mLastLocation.getLatitude());
            lon.setText("Longitude: " + mLastLocation.getLongitude() + "");
            longitude = String.valueOf(mLastLocation.getLongitude());

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                String city = addresses.get(0).getLocality();
                SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("city",city);
                editor.apply();
                saveData(city);
                if (locationEditText.getText().toString().trim().equals("")){
                    locationEditText.setText(city);
                    locationText.setText(city);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }


    @Override
    protected void onStart() {
        super.onStart();
       /* ZoneId zone = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            zone = ZoneId.systemDefault();
            // System.out.println(zone);
            //Toast.makeText(getApplicationContext(),zone+"",Toast.LENGTH_SHORT).show();
            z = zone.toString();
            Toast.makeText(getApplicationContext(),z,Toast.LENGTH_SHORT).show();
        }*/

        if (checkPermissions()){
            readLocation();
            getLastLocation();
            if (loc == null ||loc.isEmpty()){
                readLocation();
                SharedPreferences preferences1=getSharedPreferences("PREFS",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences1.edit();
                editor.putString("city",loc);
                editor.apply();
                locationEditText.setText(loc);
                locationText.setText(loc);
                if (isNetworkConnected()){
                    getWeather();
                }else
                {
                    SharedPreferences preferences = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                    String weather = preferences.getString("temperature", "");
                    String city = preferences.getString("city", "");
                    String sunset = preferences.getString("sunset", "");
                    String sunrise = preferences.getString("sunrise", "");
                    String humidity = preferences.getString("humidity", "");
                    String speed = preferences.getString("speed", "");
                    windTV.setText(speed);
                    sunsetCard.setVisibility(View.VISIBLE);
                    sunriseCard.setVisibility(View.VISIBLE);
                    tv.setText(weather);
                    locationText.setText(city);
                    sunsetTimeTV.setText(sunset);
                    sunriseTimeTV.setText(sunrise);
                    humidityText.setText(humidity);
                }
               // getInformation();

            }else {
                SharedPreferences preferences1=getSharedPreferences("PREFS",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences1.edit();
                editor.putString("city",loc);
                editor.apply();
                locationEditText.setText(loc);
                locationText.setText(loc);
                if (isNetworkConnected()){
                    getWeather();
                    getInformation();
                }else
                {
                    SharedPreferences preferences = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                    String weather = preferences.getString("temperature", "");
                    String city = preferences.getString("city", "");
                    String sunset = preferences.getString("sunset", "");
                    String sunrise = preferences.getString("sunrise", "");
                    String humidity = preferences.getString("humidity", "");
                    String speed = preferences.getString("speed", "");
                    windTV.setText(speed);
                    sunsetCard.setVisibility(View.VISIBLE);
                    sunriseCard.setVisibility(View.VISIBLE);
                    tv.setText(weather);
                    locationText.setText(city);
                    sunsetTimeTV.setText(sunset);
                    sunriseTimeTV.setText(sunrise);
                    humidityText.setText(humidity);
                }
            }
        }else {
            requestPermissions();
        }

        Calendar rightNow = Calendar.getInstance();
        hours = rightNow.get(Calendar.HOUR_OF_DAY);
        minutes = rightNow.get(Calendar.MINUTE);
        if ((hours>=5)&&(hours<=18)){
            rl1.setBackgroundResource(R.drawable.rlbg);
            Utils.weatherActivityStatusBar(WeatherActivity.this, R.color.weatherstatus);
        }else {
            rl1.setBackgroundResource(R.drawable.rlbgnight);
            Utils.weatherActivityStatusBar(WeatherActivity.this, R.color.newcol);
        }

    }

    private void readLocation(){
        DataBaseHelper db = new DataBaseHelper(this);
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = db.readLocation("1",sqLiteDatabase);
        if (cursor.moveToFirst()){
            param1 = cursor.getString(0);
            loc = cursor.getString(1);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String formattedTime(Instant time){
        String z;
          ZoneId zone = null;
            zone = ZoneId.systemDefault();
            z = zone.toString();
          //  Toast.makeText(this,z,Toast.LENGTH_SHORT).show();
        final DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("h:mm a", Locale.ENGLISH)
                .withZone(ZoneId.of(z));
        return formatter.format(time);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        WeatherActivity.this.finish();
    }

    private void say(String speakText) {
        tts.setPitch(1.005f);
        tts.setSpeechRate(1);
        tts.speak(speakText, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    protected void onDestroy() {
        if (tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void readUserData(){
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = db.readUserData("1",sqLiteDatabase);
        if (cursor.moveToFirst()){
            name = cursor.getString(1);
        }
    }

    private void readUserSettings(){
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = db.readUserSettings("1",sqLiteDatabase);
        if (cursor.moveToFirst()){
            voiceSettings = cursor.getString(2);
            isSettingsAvailable = true;
        }
        else {
            isSettingsAvailable = false;
        }
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    // Method to map API descriptions to more natural-sounding descriptions
    private String getNaturalDescription(String apiDescription) {
        switch (apiDescription.toLowerCase()) {
            case "clouds":
                return "cloudy";
            case "rain":
                return "raining";
            case "clear":
                return "clear skies";
            case "snow":
                return "snowing";
            case "thunderstorm":
                return "thunderstorming";
            case "drizzle":
                return "drizzling";
            case "mist":
                return "misty";
            case "haze":
                return "hazy";
            case "fog":
                return "foggy";
            case "smoke":
                return "smoky";
            case "dust":
                return "dusty";
            case "sand":
                return "sandy";
            case "ash":
                return "ashy";
            case "squall":
                return "squally";
            case "tornado":
                return "tornado-like";
            default:
                return apiDescription;
        }
    }


}





