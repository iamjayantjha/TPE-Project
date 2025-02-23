package com.zerostic.goodmorning.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;

import java.util.HashMap;

public class WeatherSettingsActivity extends AppCompatActivity {
    private RadioButton rb1,rb2,rb3;
    private String weather,uid,themeSettings,typeOfSleeper,voiceSettings,weatherSettings,update_prompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_settings);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10,20};
        weather = getIntent().getStringExtra("weather");
        uid = getIntent().getStringExtra("uid");
        themeSettings = getIntent().getStringExtra("theme");
        typeOfSleeper = getIntent().getStringExtra("type_of_sleeper");
        voiceSettings = getIntent().getStringExtra("voice_settings");
        weatherSettings = getIntent().getStringExtra("weather_settings");
        update_prompt = getIntent().getStringExtra("update_prompt");
        switch (weather){
            case "1": rb1.setChecked(true);
            break;
            case "2": rb2.setChecked(true);
                break;
            case "3": rb3.setChecked(true);
                break;
        }
        SharedPreferences preferences1=getSharedPreferences("PREFS",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences1.edit();
        if (isNetworkConnected()){
            rb1.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Settings").child(uid);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", uid);
                hashMap.put("theme", themeSettings);
                hashMap.put("type_of_sleeper", typeOfSleeper);
                hashMap.put("voice_settings", voiceSettings);
                hashMap.put("weather_settings", weatherSettings);
                hashMap.put("weather", "1");
                hashMap.put("update_prompt", update_prompt);
                editor.putString("weather","1");
                editor.apply();
                reference.setValue(hashMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        weather = "1";
                        updateUserSettings();
                    }
                });
            });

            rb2.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Settings").child(uid);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", uid);
                hashMap.put("theme", themeSettings);
                hashMap.put("type_of_sleeper", typeOfSleeper);
                hashMap.put("voice_settings", voiceSettings);
                hashMap.put("weather_settings", weatherSettings);
                hashMap.put("weather", "2");
                hashMap.put("update_prompt", update_prompt);
                editor.putString("weather","2");
                editor.apply();
                reference.setValue(hashMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        weather = "2";
                        updateUserSettings();
                    }
                });
            });

            rb3.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Settings").child(uid);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", uid);
                hashMap.put("theme", themeSettings);
                hashMap.put("type_of_sleeper", typeOfSleeper);
                hashMap.put("voice_settings", voiceSettings);
                hashMap.put("weather_settings", weatherSettings);
                hashMap.put("weather", "3");
                hashMap.put("update_prompt", update_prompt);
                editor.putString("weather","3");
                editor.apply();
                reference.setValue(hashMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        weather = "3";
                        updateUserSettings();
                    }
                });
            });
        }else {
            rb1.setEnabled(false);
            rb2.setEnabled(false);
            rb3.setEnabled(false);
        }

    }

    private void updateUserSettings() {
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        String id = "1";
        boolean result = db.updateUserSettings(id, themeSettings, voiceSettings, weatherSettings, typeOfSleeper, weather,update_prompt);
        if (result){
             Toast.makeText(getApplicationContext(),"Settings changed",Toast.LENGTH_SHORT).show();
        }else {
             Toast.makeText(getApplicationContext(),"Settings not changed",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}