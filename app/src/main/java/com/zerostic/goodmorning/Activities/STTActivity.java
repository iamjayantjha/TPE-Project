package com.zerostic.goodmorning.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.WUCDismissBottomSheet;

public class STTActivity extends AppCompatActivity implements WUCDismissBottomSheet.wucBottomSheetListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stt);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences=getSharedPreferences("method",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("method","Default");
        editor.apply();
        WUCDismissBottomSheet wucDismissBottomSheet = new WUCDismissBottomSheet();
        wucDismissBottomSheet.show(getSupportFragmentManager(),"wucDismissBottomSheet");
        wucDismissBottomSheet.setCancelable(false);
    }

/*    @Override
    public void onClick() {
        Intent weatherIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            weatherIntent = new Intent(STTActivity.this, WeatherActivity.class);
        }else {
            STTActivity.this.finish();
        }
        weatherIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(weatherIntent);
        finish();
    }*/

    @Override
    public void onClick(String isMorning) {

    }
}