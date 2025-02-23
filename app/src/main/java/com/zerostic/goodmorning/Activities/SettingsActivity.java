package com.zerostic.goodmorning.Activities;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 Coded by iamjayantjha
 **/

public class SettingsActivity extends AppCompatActivity {
    TextView userName, userEmail;
    String name,email,imageURI,fName, weather,update_prompt,country,currency,verification,themeSettings,voiceSettings,weatherSettings,typeOfSleeper;
    CircleImageView profilePicture;
    MaterialCardView profileCard;
    RelativeLayout rl2,rl3,rl4,rl5,inviteRl;
    boolean isDataAvailable = true,isSettingsAvailable;
    Dialog dialog;
    ImageView verified;
    Button okay;
    SwitchCompat themeSwitch;
    private ImageView zerostic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Utils.blackIconStatusBar(SettingsActivity.this, R.color.background);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        rl2 = findViewById(R.id.payment);
        rl3 = findViewById(R.id.weather);
        rl4 = findViewById(R.id.contact);
        rl5 = findViewById(R.id.speech);
        zerostic = findViewById(R.id.zerostic);
        inviteRl = findViewById(R.id.invite);
        readUserData();
        readUserSettings();
        profilePicture = findViewById(R.id.profilePicture);
        profileCard = findViewById(R.id.profileCard);
        verified = findViewById(R.id.verified);
        themeSwitch = findViewById(R.id.themeSwitch);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10,20};
        readUserData();
        //getCurrency(country);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        themeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(pattern, -1);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Settings").child(uid);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", uid);
                hashMap.put("theme", themeSettings);
                hashMap.put("type_of_sleeper", typeOfSleeper);
                hashMap.put("voice_settings", voiceSettings);
                hashMap.put("weather_settings", weatherSettings);
                hashMap.put("weather", weather);
                hashMap.put("update_prompt", update_prompt);
                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()){
                            updateUserSettings();
                        }
                    }
                });
            }
        });
        dialog = new Dialog(SettingsActivity.this);
        dialog.setContentView(R.layout.permission_dialog);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        okay = dialog.findViewById(R.id.okayBtn);
        TextView title = dialog.findViewById(R.id.headerText);
        TextView message = dialog.findViewById(R.id.confirmText);
        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        title.setText(R.string.dataUnavailableTitle);
        message.setText(R.string.dataUnavailable);
        if (isDataAvailable){
            userName.setText(name);
            userEmail.setText(email);
            if (imageURI.equals("")||imageURI.isEmpty()){
                Glide.with(SettingsActivity.this).load(R.mipmap.ic_profilepicture).into(profilePicture);
            }else {
                Glide.with(SettingsActivity.this).load(imageURI).into(profilePicture);
            }
            if (verification.equals("Unverified")){
                verified.setImageResource(R.drawable.ic_unverified);
            }else if (verification.equals("Verified")){
                verified.setImageResource(R.drawable.ic_verified);
            }
            profileCard.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                Intent profileIntent = new Intent(SettingsActivity.this, ProfileActivity.class);
                /*Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(profilePicture, "profile");*/
                profileIntent.putExtra("imageURI",imageURI);
               // ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SettingsActivity.this, pairs);
                startActivity(profileIntent);//,options.toBundle());
            });
            rl2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibrator.vibrate(pattern,-1);
                    Intent paymentIntent = new Intent(SettingsActivity.this, PaymentActivity.class);
                    paymentIntent.putExtra("name",fName);
                    paymentIntent.putExtra("email",email);
                    paymentIntent.putExtra("image",imageURI);
                    paymentIntent.putExtra("currency",currency);
                    startActivity(paymentIntent);
                }
            });
            rl3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibrator.vibrate(pattern,-1);
                    Intent weatherIntent = new Intent(SettingsActivity.this, WeatherSettingsActivity.class);
                    weatherIntent.putExtra("weather",weather);
                    weatherIntent.putExtra("uid",uid);
                    weatherIntent.putExtra("theme", themeSettings);
                    weatherIntent.putExtra("type_of_sleeper", typeOfSleeper);
                    weatherIntent.putExtra("voice_settings", voiceSettings);
                    weatherIntent.putExtra("weather_settings", weatherSettings);
                    weatherIntent.putExtra("update_prompt",update_prompt);
                    startActivity(weatherIntent);
                }
            });
            rl4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibrator.vibrate(pattern,-1);
                    Intent contactIntent = new Intent(SettingsActivity.this, ContactUsActivity.class);
                    contactIntent.putExtra("name",fName);
                    startActivity(contactIntent);
                }
            });
            rl5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibrator.vibrate(pattern,-1);
                    Intent speechIntent = new Intent(SettingsActivity.this, SpeechActivity.class);
                    speechIntent.putExtra("name",fName);
                    speechIntent.putExtra("theme",themeSettings);
                    speechIntent.putExtra("weatherSettings",weatherSettings);
                    speechIntent.putExtra("weather",weather);
                    speechIntent.putExtra("sleeper",typeOfSleeper);
                    speechIntent.putExtra("voice",voiceSettings);
                    speechIntent.putExtra("update_prompt",update_prompt);
                    startActivity(speechIntent);
                }
            });

            inviteRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibrator.vibrate(pattern, -1);
                    Intent inviteIntent = new Intent(SettingsActivity.this, InviteFriendsActivity.class);
                    inviteIntent.putExtra("name",fName);
                    startActivity(inviteIntent);
                }
            });

            zerostic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibrator.vibrate(pattern,-1);
                    Intent paymentIntent = new Intent(SettingsActivity.this, AboutZerosticActivity.class);
                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(zerostic, "zerostic");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SettingsActivity.this, pairs);
                    startActivity(paymentIntent,options.toBundle());
                }
            });
        }else {
            dialog.show();
            okay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibrator.vibrate(pattern,-1);
                    FirebaseAuth.getInstance().signOut();
                    Intent appIntro = new Intent(SettingsActivity.this, SplashActivity.class);
                    appIntro.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(appIntro);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    finish();
                }
            });
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        SettingsActivity.this.finish();
    }

    private void readUserData(){
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = db.readUserData("1",sqLiteDatabase);
        if (cursor.moveToFirst()){
           fName = cursor.getString(1);
           String lName = cursor.getString(2);
           email = cursor.getString(4);
           country = cursor.getString(5);
           imageURI = cursor.getString(7);
           verification = cursor.getString(9);
           name = fName+" "+lName;
        }
        else {
            isDataAvailable = false;
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onStart() {
        super.onStart();
        readUserData();
        readUserSettings();
        if (isSettingsAvailable){
            if (themeSettings.equals("2")){
                themeSwitch.setChecked(true);
            }else {
                themeSwitch.setChecked(false);
            }
        }else {
            themeSettings = "2";
            voiceSettings = "1";
            weatherSettings = "1";
            typeOfSleeper = "1";
            weather = "1";
            update_prompt = "1";
            updateUserSettings();
        }
        if (!isNetworkConnected()){
            Toast.makeText(getApplicationContext(),"Internet not connected",Toast.LENGTH_SHORT).show();

        }
    }
    private void getCurrency(String country){
        switch (country){
            case "INDIA": currency = "INR";
            break;
            default: currency = "USD";
            break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        readUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        readUserData();
    }

    private void readUserSettings(){
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = db.readUserSettings("1",sqLiteDatabase);
        if (cursor.moveToFirst()){
            themeSettings = cursor.getString(1);
            voiceSettings = cursor.getString(2);
            weatherSettings = cursor.getString(3);
            typeOfSleeper = cursor.getString(4);
            weather = cursor.getString(5);
            update_prompt = cursor.getString(6);
            isSettingsAvailable = true;
        }
        else {
            isSettingsAvailable = false;
        }
    }

    private void updateUserSettings() {
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        String id = "1";
        boolean result = db.updateUserSettings(id, themeSettings, voiceSettings, weatherSettings, typeOfSleeper, weather,update_prompt);
        /*if (result){
             Toast.makeText(getApplicationContext(),"Data retrieved successfully",Toast.LENGTH_SHORT).show();
        }else {
             Toast.makeText(getApplicationContext(),"Error in getting your data",Toast.LENGTH_SHORT).show();
        }*/
    }
}