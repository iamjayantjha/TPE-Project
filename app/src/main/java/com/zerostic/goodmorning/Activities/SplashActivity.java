package com.zerostic.goodmorning.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;

/**
 Coded by iamjayantjha
 **/

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;
    boolean isSettingsAvailable;
    String theme;
    ImageView gmLogo;
    private Animation animation,animation2;
    TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        gmLogo = findViewById(R.id.gmLogo);
        appName = findViewById(R.id.appName);
        mCurrentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        readUserSettings();
        int SPLASH_DISPLAY_LENGTH = 1150;
        Utils.blackIconStatusBar(SplashActivity.this, R.color.background);
        new Handler().postDelayed(() -> {
            appName.setVisibility(View.VISIBLE);
            animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_move);
            animation2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein_slide_up);
            gmLogo.startAnimation(animation);
            appName.startAnimation(animation2);
        },550);
        if (mCurrentUser==null){
            new Handler().postDelayed(() -> {
                Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SplashActivity.this.startActivity(mainIntent);
                overridePendingTransition(R.anim.splash_in, R.anim.splash_out);
                SplashActivity.this.finish();
            }, SPLASH_DISPLAY_LENGTH);
        }else {
            new Handler().postDelayed(() -> {
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SplashActivity.this.startActivity(mainIntent);
                overridePendingTransition(R.anim.splash_in, R.anim.splash_out);
                SplashActivity.this.finish();
            }, SPLASH_DISPLAY_LENGTH);
        }
    }

    private void readUserSettings(){
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = db.readUserSettings("1",sqLiteDatabase);
        if (cursor.moveToFirst()){
            theme = cursor.getString(1);
            isSettingsAvailable = true;
        }
        else {
            isSettingsAvailable = false;
        }
    }
}