package com.zerostic.goodmorning.Activities;

import android.os.Bundle;
import android.os.Vibrator;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zerostic.goodmorning.R;

public class LicensesActivity extends AppCompatActivity {

    String[] libraries = {"AOSP","Razorpay","StoriesProgressView","play-services","picasso","Androidx Lifecycle","Firebase","Blackfizz Eazegraph","Nineoldandroids",
    "play-services-analytics","com.theartofdev.edmodo:android-image-cropper","com.github.bumptech.glide","firebase-messaging",
    "volley","de.hdodenhof:circleimageview","retrofit","com.airbnb.android:lottie","com.github.fornewid:neumorphism","Rhino",
    "multidex","com.squareup.okhttp3:okhttp","androidx.work:work-runtime","androidx.navigation:navigation-fragment","androidx.room:room-runtime",
    "androidx.room:room-compiler","com.jakewharton:butterknife","com.google.android.material:material","com.ncorti:slidetoact:0.9.0"," "};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10,20};
        ListView listView = findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, libraries);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            vibrator.vibrate(pattern, -1);
            Toast.makeText(getApplicationContext(),libraries[position],Toast.LENGTH_SHORT).show();
        });
    }
}