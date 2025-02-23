package com.zerostic.goodmorning.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Utils;

public class ClosingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closing);
        Utils.blackIconStatusBar(this, R.color.black);
        new Handler().postDelayed(() -> {
            System.exit(0);
        },1500);
    }
}