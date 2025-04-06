package com.zerostic.goodmorning.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.R;

import org.checkerframework.checker.units.qual.A;

public class SleepDataActivity extends AppCompatActivity {
    MaterialCardView sleepStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sleep_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sleepStats = findViewById(R.id.sleepStats);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Utils.blackIconStatusBar(this, R.color.background);
            Utils.navigationBar(this, R.color.background);
        }

        sleepStats.setOnClickListener(v -> {
            startActivity(new Intent(this, SleepStatsActivity.class));
        });
    }
}