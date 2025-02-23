package com.zerostic.goodmorning.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;

import java.util.HashMap;

public class SpeechActivity extends AppCompatActivity {
    private String voiceSettings,theme,weatherSettings,sleeper,weather,update_prompt,name;
    private boolean isSettingsAvailable;
    Dialog dialog;
    Button okay;
    TextView title, message;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        name = getIntent().getStringExtra("name");
        theme = getIntent().getStringExtra("theme");
        weatherSettings = getIntent().getStringExtra("weatherSettings");
        weather = getIntent().getStringExtra("weather");
        sleeper = getIntent().getStringExtra("sleeper");
        voiceSettings = getIntent().getStringExtra("voice");
        update_prompt = getIntent().getStringExtra("update_prompt");
        TextView voiceSettingsHeading = findViewById(R.id.voiceSettingsHeading);
        RadioButton rb1 = findViewById(R.id.rb1);
        RadioButton rb2 = findViewById(R.id.rb2);
        RadioButton rb3 = findViewById(R.id.rb3);
        RadioButton rb4 = findViewById(R.id.rb4);
        dialog = new Dialog(SpeechActivity.this);
        dialog.setContentView(R.layout.permission_dialog);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        okay = dialog.findViewById(R.id.okayBtn);
        title = dialog.findViewById(R.id.headerText);
        message = dialog.findViewById(R.id.confirmText);
        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        switch (voiceSettings){
            case "1": rb1.setChecked(true);
            break;
            case "2": rb2.setChecked(true);
            break;
            case "3": rb3.setChecked(true);
            break;
            case "4": rb4.setChecked(true);
            break;
        }
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10,20};
        okay.setOnClickListener(v1 -> {
            vibrator.vibrate(pattern, -1);
            count++;
            dialog.dismiss();
        });
        if (isNetworkConnected()){
            rb1.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                if (sleeper.equals("3") && count<1){
                    title.setText("Recommendation");
                    message.setText("Hey, "+name+" we recommend that you use voice just at weather as we can see you are a heavy sleeper. But, if you still want voice for everything you can change your settings.");
                    dialog.show();
                    switch (voiceSettings){
                        case "1": rb1.setChecked(true);
                            break;
                        case "2": rb2.setChecked(true);
                            break;
                        case "3": rb3.setChecked(true);
                            break;
                        case "4": rb4.setChecked(true);
                            break;
                    }
                }else{
                    voiceSettings = "1";
                    updateUserSetings(voiceSettings);
                }

            });
            rb2.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                if (sleeper.equals("3") && count<1){
                    title.setText("Recommendation");
                    message.setText("Hey, "+name+" we recommend that you use voice just at weather as we can see you are a heavy sleeper. But, if you still want voice for everything you can change your settings.");
                    dialog.show();
                    switch (voiceSettings){
                        case "1": rb1.setChecked(true);
                            break;
                        case "2": rb2.setChecked(true);
                            break;
                        case "3": rb3.setChecked(true);
                            break;
                        case "4": rb4.setChecked(true);
                            break;
                    }
                }else {
                    voiceSettings = "2";
                    updateUserSetings(voiceSettings);
                }

            });
            rb3.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                voiceSettings = "3";
                updateUserSetings(voiceSettings);

            });
            rb4.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                voiceSettings = "4";
                updateUserSetings(voiceSettings);
            });
        }else {
            rb1.setEnabled(false);
            rb2.setEnabled(false);
            rb3.setEnabled(false);
            rb4.setEnabled(false);
        }
        String voiceVal = name+" change your voice following the below steps";
        voiceSettingsHeading.setText(voiceVal);
    }

    private void updateUserSetings(String voiceSettings) {
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        boolean result = db.updateUserSettings("1",theme,voiceSettings,weatherSettings,sleeper,weather,update_prompt);
        if (result){
            updateOnFirebase();
        }else {
            Toast.makeText(getApplicationContext(),"Settings have not been changed.",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateOnFirebase() {
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Settings").child(mCurrentUser.getUid());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", mCurrentUser.getUid());
        hashMap.put("theme", theme);
        hashMap.put("weather", weather);
        hashMap.put("weather_settings", weatherSettings);
        hashMap.put("type_of_sleeper", sleeper);
        hashMap.put("voice_settings", voiceSettings);
        hashMap.put("update_prompt", update_prompt);
        reference.setValue(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(getApplicationContext(),"Settings have been changed successfully.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}