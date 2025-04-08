package com.zerostic.goodmorning.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zerostic.goodmorning.Adapter.SectionPagerAdapter;
import com.zerostic.goodmorning.BuildConfig;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.AlarmsList.AlarmsListFragment;
import com.zerostic.goodmorning.Application.Users;
import com.zerostic.goodmorning.Application.UsersSettings;
import com.zerostic.goodmorning.Application.Users_Subscription;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;
import com.zerostic.goodmorning.Service.AlarmService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 2323;
    int PERMISSION_ID = 44, count = 0;
    FusedLocationProviderClient mFusedLocationClient;
    String param1="",loc="",param2="",param3="",theme,paid,renewalDate="",currency,plan="", quote, name, update_prompt="", voiceSettings, weatherSettings, sleeper, weather, titleText = "Location Request";
    EditText locationEditText;
    ImageView weatherData;
    Dialog dialog;
    Vibrator vibrator;
    boolean isSettingsAvailable = true;
    boolean isPaid;
    boolean isDataAvailable;
    final long[] pattern = {10,20};
    FirebaseUser mCurrentUser;
    DatabaseReference reference;
    Button okay;
    TextView navigation;
    //String apiKey = BuildConfig.API_KEY_GEMINI;
    ViewPager viewPager;
    SectionPagerAdapter sectionsPagerAdapter;
    boolean dialogShown = false;
    boolean freemiumMsgShown;
    long sunrise,sunset,dayLength;
    ImageView button1, icon2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.mainPage);
        sectionsPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        Utils.blackIconStatusBar(MainActivity.this, R.color.background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Utils.navigationBar(MainActivity.this, R.color.background);
        }
        weatherData = findViewById(R.id.weatherData);
        weatherData.setOnClickListener(view12 -> {
            vibrator.vibrate(pattern, -1);
            Intent settingsIntent = null;
            settingsIntent = new Intent(this, WeatherActivity.class);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(settingsIntent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        FirebaseAnalytics.getInstance(this);
        navigation = findViewById(R.id.navigation);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationEditText = findViewById(R.id.locationEditText);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.permission_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        okay = dialog.findViewById(R.id.okayBtn);
        button1 = findViewById(R.id.button1);
        icon2 = findViewById(R.id.icon2);
      //  Generative AI Model
       /* GenerativeModel gm = new GenerativeModel( "gemini-1.5-flash",BuildConfig.GEMINI_API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);*/
        TextView title = dialog.findViewById(R.id.headerText);
        TextView message = dialog.findViewById(R.id.confirmText);
        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        if (checkPermissions()){
        title.setText(R.string.permission);
        message.setText(R.string.display_over_other_apps);
        }
        else {
            title.setText(titleText);
            message.setText("We require location permission in order to provide you weather forecast of your location. You can turn this off in settings.\nWe don't save your location with us or share it with any other third party.");
        }
       allPermissionChecks();
        ImageView settings = findViewById(R.id.settings);
        settings.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        });

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancelAll();
        if(getIntent().hasExtra("yes")){
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
        }
        MaterialCardView addAlarm = findViewById(R.id.addAlarm);
        button1.setOnClickListener(view13 -> {
            vibrator.vibrate(pattern, -1);
            Intent open = new Intent(this, CreateAlarmActivity.class);
            open.putExtra("method","Default");
            open.putExtra("tone","0");
            open.putExtra("wuc","Off");
            open.putExtra("act","main");
            startActivity(open);
            SharedPreferences preferences1 = getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences1.edit();
            editor.putString("snooze","0");
            editor.putString("title","Alarm");
            editor.putString("ringsIn","Rings in 24 hours");
            editor.apply();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        icon2.setOnClickListener(v->{
            vibrator.vibrate(pattern, -1);
            Intent intent = new Intent(MainActivity.this, SleepDataActivity.class);
            startActivity(intent);
        });
        SharedPreferences preferences = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        name = preferences.getString("name","User");
        count = preferences.getInt("count",0);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new AlarmsListFragment()).commit();

//       FirebaseCrashlytics.getInstance().setUserId(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
       
       /* Get FCm Token */
       /*Token FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isComplete()){
                String token = task.getResult();
                Log.e("token", "Token -> "+token);
            }
        });*/

    }


    @Override
    protected void onStart() {
        super.onStart();
        Calendar rightNow = Calendar.getInstance();
        Date c = rightNow.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd", Locale.getDefault());
        String formattedDate = df.format(c);
      /*  if ((mCurrentUser!=null)){
            changeUserInfo();
            changeUserSettings();
            //changeUserPayDetails();
            //readUserData();
//            readUserSettings();
            requestNotificationPermission();
            SharedPreferences preferences = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
            freemiumMsgShown = preferences.getBoolean("freemium", false);
            //Toast.makeText(getApplicationContext(), ""+ freemiumMsgShown, Toast.LENGTH_SHORT).show();
            if (!freemiumMsgShown){
                TextView title = dialog.findViewById(R.id.headerText);
                TextView message = dialog.findViewById(R.id.confirmText);
                title.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                Button okay = dialog.findViewById(R.id.okayBtn);
                message.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                dialog.setCancelable(false);
                dialog.show();
                message.setText("Good Morning is now free!\nNo Ads, No Subscription.\nJust Good Mornings.\nAll Yours");
                okay.setOnClickListener(v -> {
                    vibrator.vibrate(pattern, -1);
                    dialog.dismiss();
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putBoolean("freemium",true);
                    editor.apply();
                    message.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                });
            }
            new Handler().postDelayed(() -> navigation.setText(R.string.swipe), 1000);
            new Handler().postDelayed(() -> {
                quote = getQuote(formattedDate);
                navigation.setText(quote);
            }, 5000);
            new Handler().postDelayed(() -> navigation.setText(R.string.swipe), 10000);
            new Handler().postDelayed(() -> {
                quote = getQuote(formattedDate);
                navigation.setText(quote);
            }, 15000);
            checkUpdates();
        }
        else {
            Intent startIntent = new Intent(MainActivity.this, LoginActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startIntent);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            finish();
        }*/

    }

    private void checkUpdates() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                Dialog updateDialog = new Dialog(MainActivity.this);
                updateDialog.setContentView(R.layout.update_prompt_dialog);
                Objects.requireNonNull(updateDialog.getWindow()).setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
                updateDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                updateDialog.setCancelable(true);
                Button notNow = updateDialog.findViewById(R.id.notNow);
                Button okay = updateDialog.findViewById(R.id.okayBtn);
                TextView title = updateDialog.findViewById(R.id.headerText);
                TextView message = updateDialog.findViewById(R.id.confirmText);
                title.setVisibility(View.VISIBLE);
                message.setVisibility(View.VISIBLE);
                title.setText(R.string.update_available);
                message.setText(R.string.update_available_message);
                okay.setOnClickListener(v -> {
                    vibrator.vibrate(pattern, -1);
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, 101);
                    } catch (IntentSender.SendIntentException ignored) {

                    }
                    updateDialog.dismiss();

                });
                notNow.setOnClickListener(v -> {
                    vibrator.vibrate(pattern, -1);
                    updateDialog.dismiss();
                });
            }
        });
    }

    private void requestNotificationPermission() {
        // Check if the notification is enabled or not.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        boolean isEnabled = notificationManager.areNotificationsEnabled();
        if (!isEnabled) {
            // If the notification is not enabled then redirect to the notification setting
            // to enable it.
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.permission_dialog);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            TextView header = dialog.findViewById(R.id.headerText);
            TextView message = dialog.findViewById(R.id.confirmText);
            header.setText(R.string.notification_permission_heading);
            message.setText(R.string.notification_permission_message);
            header.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
            Button okay = dialog.findViewById(R.id.okayBtn);
            dialog.show();
            okay.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                dialog.dismiss();
                Intent intent;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                }else {
                    intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.fromParts("package", getPackageName(), null));
                }
                startActivity(intent);
            });
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent closing = new Intent(MainActivity.this, ClosingActivity.class);
        startActivity(closing);
        finish();
    }

    private void updateUserSettings(UsersSettings usersSettings) {
        try (UserDatabaseHelper db = new UserDatabaseHelper(this)) {
            String id = "1";
            db.updateUserSettings(id, usersSettings.getTheme(), usersSettings.getVoice_settings(), usersSettings.getWeather_settings(), usersSettings.getType_of_sleeper(), usersSettings.getWeather(), usersSettings.getUpdate_prompt());
        }
    }

    private void insertUserSettings(UsersSettings usersSettings) {
        try (UserDatabaseHelper db = new UserDatabaseHelper(this)) {
            String id = "1";
            db.insertUserSettings(id, usersSettings.getTheme(), usersSettings.getVoice_settings(), usersSettings.getWeather_settings(), usersSettings.getType_of_sleeper(), usersSettings.getWeather(), usersSettings.getUpdate_prompt());
        }
    }

    private void updateUserData(Users users) {
        try (UserDatabaseHelper db = new UserDatabaseHelper(this)) {
            String id = "1";
            db.updateUserData(id, users.getFirst_name(), users.getLast_name(), users.getPhone_number(), users.getEmail(), users.getCountry(), users.getPayment_status(), users.getImageUri(), users.getAccount_open_date(), users.getVerification());
        }
    }

    private void insertUserData(Users user) {
        boolean result;
        try (UserDatabaseHelper db = new UserDatabaseHelper(this)) {
            String id = "1";
            result = db.insertUserData(id, user.getFirst_name(), user.getLast_name(), user.getPhone_number(), user.getEmail(), user.getCountry(), user.getPayment_status(), user.getImageUri(), user.getAccount_open_date(), user.getVerification());
        }
        if (result){
            Toast.makeText(getApplicationContext(),"Welcome back "+user.getFirst_name(),Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(),"Unable to get your data", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    
    private void getLastLocation() {
        Dialog locationDialog = new Dialog(MainActivity.this);
        locationDialog.setContentView(R.layout.update_prompt_dialog);
        Objects.requireNonNull(locationDialog.getWindow()).setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
        locationDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        locationDialog.setCancelable(true);
        Button okayBtn = locationDialog.findViewById(R.id.okayBtn);
        Button stopBtn = locationDialog.findViewById(R.id.notNow);
        TextView title = locationDialog.findViewById(R.id.headerText);
        TextView message = locationDialog.findViewById(R.id.confirmText);
        String stopBtnText = "Don't need weather";
        stopBtn.setText(stopBtnText);
        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        title.setText(titleText);
        String locationWarning = "You won't be able to check weather if location is turned off.";
        message.setText(locationWarning);

        if (isLocationEnabled()) {
            SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean("weatherEnabled",true);
            editor.apply();
            if (locationDialog.isShowing()){
                locationDialog.dismiss();
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ID);
            }
            mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if (location == null) {
                    requestNewLocationData();
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
                        editor.putString("city",city);
                        editor.apply();
                        saveData(city);
                        if (locationEditText.getText().toString().trim().isEmpty()){
                            locationEditText.setText(city);
                        }
                    } catch (IOException ignored) {
                    }
                }
            });
        } else {
            if (!dialogShown){
                //locationDialog.show();
                locationDialog.setOnCancelListener(dialog -> {
                    SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putBoolean("weatherEnabled",false);
                    editor.apply();
                    Toast.makeText(getApplicationContext(),"Weather disabled\nYou won't be able to check weather updates",Toast.LENGTH_SHORT).show();
                    dialogShown = true;
                });
            }
        }
        okayBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            // Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            locationDialog.dismiss();
            dialogShown = true;
        });
        stopBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern,-1);
            SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean("weatherEnabled",false);
            editor.apply();
            dialogShown = true;
            locationDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Weather disabled\nYou won't be able to check weather updates",Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private void saveData(String location) {
        try (DataBaseHelper db = new DataBaseHelper(this)) {
            String id = "1";
            Cursor res = db.getLocationData();
            if (res != null && res.getCount() > 0) {
                db.updateLocation(id, location);
            } else {
                db.insertLocation(id, location);
            }
        }

    }

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

            try {
                assert mLastLocation != null;
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                assert addresses != null;
                String city = addresses.get(0).getLocality();
                SharedPreferences preferences=getSharedPreferences("city",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("city",city);
                editor.apply();
                saveData(city);
                if (locationEditText.getText().toString().trim().isEmpty()){
                    locationEditText.setText(city);

                }

            } catch (IOException ignored) {
            }
        }
    };

    private void readLocation(){
        Cursor cursor;
        try (DataBaseHelper db = new DataBaseHelper(this)) {
            SQLiteDatabase sqLiteDatabase;
            sqLiteDatabase = db.getReadableDatabase();
            cursor = db.readLocation("1", sqLiteDatabase);
            while (cursor.moveToNext()){
                loc = cursor.getString(1);
            }
            locationEditText.setText(loc);
            if (isNetworkConnected()){
                getInformation();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        freemiumMsgShown = preferences.getBoolean("freemium", false);
        if (checkPermissions()) {
            readLocation();
            getLastLocation();
            if (loc == null || loc.isEmpty()){
                readLocation();
            }
            locationEditText.setText(loc);
        }else {
           okay.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                requestPermissions();
                requestNotificationPermission();
                dialog.dismiss();
            });
        }
    }

    private void getInformation() {
        String tempUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + locationEditText.getText().toString().trim() + "&appid="+BuildConfig.WEATHER_API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("weather");
                JSONObject object = jsonArray.getJSONObject(0);
                String description = object.getString("main");
                String icon = object.getString("icon");
                JSONObject mainObject = jsonObject.getJSONObject("main");
                String temp = mainObject.getString("feels_like");
                double mainTemperature = Double.parseDouble(temp);
                int temperature = (int) (mainTemperature - 273.15);
                String iconUrl = "https://openweathermap.org/img/w/" + icon + ".png";
                JSONObject wind = jsonObject.getJSONObject("wind");
                String speed = wind.getString("speed");
                JSONObject sys = jsonObject.getJSONObject("sys");
                String rise = sys.getString("sunrise");
                String set = sys.getString("sunset");
                sunrise = Long.parseLong(rise);
                sunset = Long.parseLong(set);
                dayLength = sunset - sunrise;
                long seconds = Math.abs(dayLength);
                long currentTimeInSeconds = System.currentTimeMillis() / 1000;
                if (currentTimeInSeconds > sunrise && currentTimeInSeconds < sunset){
                    weatherData.setImageResource(R.drawable.sun);
                }else {
                    weatherData.setImageResource(R.drawable.moon);
                }
              //  weatherData.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> //Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_LONG).show());
                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show());
                //Toast.makeText(getApplicationContext(), "Unable to get weather forecast for your location. \nMake sure you are connected to the Internet", Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void readUserData(){
     Cursor cursor;
     try (UserDatabaseHelper db = new UserDatabaseHelper(this)) {
         SQLiteDatabase sqLiteDatabase;
         sqLiteDatabase = db.getReadableDatabase();
         cursor = db.readUserData("1", sqLiteDatabase);
     }
     if (cursor.moveToFirst()){
         name = cursor.getString(1);
         param2 = cursor.getString(0);
         paid = cursor.getString(6);
         isDataAvailable = true;
         SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
         SharedPreferences.Editor editor=preferences.edit();
         editor.putString("name",name);
         editor.apply();
     }else {
         isDataAvailable = false;
     }
 }

 private void readUserSettings(){
     Cursor cursor;
     try (UserDatabaseHelper db = new UserDatabaseHelper(this)) {
         SQLiteDatabase sqLiteDatabase;
         sqLiteDatabase = db.getReadableDatabase();
         cursor = db.readUserSettings("1", sqLiteDatabase);
     }
     if (cursor.moveToFirst()){
         param3 = cursor.getString(0);
         theme = cursor.getString(1);
         isSettingsAvailable = true;
         update_prompt = cursor.getString(6);
         voiceSettings = cursor.getString(2);
         weatherSettings = cursor.getString(3);
         sleeper = cursor.getString(4);
         weather = cursor.getString(5);
     }
     else {
         isSettingsAvailable = false;
     }
 }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void updateVerification(Users users){
        if (isNetworkConnected()){
            if ((mCurrentUser.isEmailVerified())&&(users.getVerification().equals("Unverified"))){
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mCurrentUser.getUid());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", users.getId());
                hashMap.put("first_name", users.getFirst_name());
                hashMap.put("last_name", users.getLast_name());
                hashMap.put("email", users.getEmail());
                hashMap.put("phone_number", users.getPhone_number());
                hashMap.put("payment_status", users.getPayment_status());
                hashMap.put("country",users.getCountry());
                hashMap.put("account_open_date",users.getAccount_open_date());
                hashMap.put("password",users.getPassword());
                hashMap.put("imageUri",users.getImageUri());
                hashMap.put("terms_of_service",users.getTerms_of_service());
                hashMap.put("verification","Verified");
                reference.setValue(hashMap).addOnCompleteListener(task -> {

                });
            }
        }
    }

    private void readPayDetails() {
        Cursor cursor;
        try (UserDatabaseHelper dataBaseHelper = new UserDatabaseHelper(this)) {
            SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
            cursor = dataBaseHelper.readPaymentData("1", db);
        }
        if (cursor.moveToFirst()){
            isPaid = true;
            plan = cursor.getString(1);
            renewalDate = cursor.getString(3);
            currency = cursor.getString(4);
        }
    }

    private void changeUserInfo(){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(mCurrentUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (getApplicationContext() == null){
                    return;
                }
                Users user = snapshot.getValue(Users.class);
                    updateVerification(user);
                assert user != null;
                    if (!UserDatabaseHelper.doesUserDetailsExist(MainActivity.this)){
                        insertUserData(user);
                    }else {
                        updateUserData(user);
                    }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void  changeUserSettings(){
        reference = FirebaseDatabase.getInstance().getReference("Users Settings").child(mCurrentUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (getApplicationContext() == null){
                    return;
                }
                UsersSettings usersSettings = snapshot.getValue(UsersSettings.class);
                assert usersSettings != null;
                    if (UserDatabaseHelper.doesUserSettingsExist(MainActivity.this)){
                        updateUserSettings(usersSettings);
                    }else {
                        insertUserSettings(usersSettings);
                    }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void allPermissionChecks(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if (!Settings.canDrawOverlays(this)){
                dialog.show();
                okay.setOnClickListener(v -> {
                    vibration();
                    Intent permission = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    ActivityCompat.startActivityForResult(this,permission,ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE,Bundle.EMPTY);
                    dialog.dismiss();
                });
            }else{
                if (checkPermissions()){
                    dialog.dismiss();
                    readLocation();
                    getLastLocation();
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_SETTINGS)
                            != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 101);
                    }
                    if (loc == null || loc.isEmpty()){
                        getLastLocation();
                        readLocation();

                    }
                    locationEditText.setText(loc);
                    if (isNetworkConnected()){
                        getInformation();
                    }
                }else {
                    dialog.show();
                    okay.setOnClickListener(v -> {
                        vibration();
                        requestPermissions();
                        dialog.dismiss();
                    });
                }
            }
        }
        else {
            if (checkPermissions()){
                dialog.dismiss();
                readLocation();
                getLastLocation();
                if (loc == null || loc.isEmpty()){
                    getLastLocation();
                    readLocation();

                }
                locationEditText.setText(loc);
                if (isNetworkConnected()){
                    getInformation();
                }
            }else {
                dialog.show();
                okay.setOnClickListener(v -> {
                    vibrator.vibrate(pattern,-1);
                    requestPermissions();
                    dialog.dismiss();
                });
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM}, 101);
            }
        }
    }

    private String getQuote(String date){
        String quote="";
        switch (date){
            case "01":
                quote =  "Arise awake and stop not till the goal is reached.";
                break;
            case "02":
                quote =  "Look for opportunities in every change in your life.";
                break;
            case "03":
                quote =  "Strive not to be a success, but rather to be of value.";
                break;
            case "04":
                quote =  "The mind is everything. What you think you become.";
                break;
            case "05":
                quote =  "The problem is you think you have time.";
                break;
            case "06":
                quote =  "An unexamined life is not worth living.";
                break;
            case "07":
                quote =  "Either you run the day, or the day runs you.";
                break;
            case "08":
                quote =  "Believe you can and you’re halfway there.";
                break;
            case "09":
                quote =  "The best revenge is massive success.";
                break;
            case "10":
                quote =  "Everything has beauty, but not everyone can see.";
                break;
            case "11":
                quote =  "Fall seven times and stand up eight.";
                break;
            case "12":
                quote =  "You become what you believe.";
                break;
            case "13":
                quote =  "Dream big and dare to fail.";
                break;
            case "14":
                quote =  "If you can dream it, you can achieve it.";
                break;
            case "15":
                quote =  "In life we can have results or reasons.";
                break;
            case "16":
                quote =  "The deed is everything the glory is nothing.";
                break;
            case "17":
                quote =  "Sky is the limit.";
                break;
            case "18":
                quote =  "Struggle and you will be successful.";
                break;
            case "19":
                quote =  "Leaders are like eagles they fly alone.";
                break;
            case "20":
                quote =  "The race for quality has no finish line.";
                break;
            case "21":
                quote =  "Team work leads you to success.";
                break;
            case "22":
                quote =  "Peace is what the whole world needs.";
                break;
            case "23":
                quote =  "Energy is eternal delight.";
                break;
            case "24":
                quote =  "Luck favours the brave.";
                break;
            case "25":
                quote =  "The one who runs wins the race.";
                break;
            case "26":
                quote =  "Action speaks louder than words.";
                break;
            case "27":
                quote =  "Don’t choose, Get chosen.";
                break;
            case "28":
                quote =  "Don’t raise your voice, Improve your argument.";
                break;
            case "29":
                quote =  "Failure is not a person, it’s an event.";
                break;
            case "30":
                quote =  "The light heart lives long.";
                break;
            case "31":
                quote =  "Your life is your message.";
                break;
        }
        return quote;
    }

    private void vibration(){
        vibrator.vibrate(pattern, -1);
    }
}