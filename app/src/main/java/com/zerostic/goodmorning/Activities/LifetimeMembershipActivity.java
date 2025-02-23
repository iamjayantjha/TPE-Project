package com.zerostic.goodmorning.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zerostic.goodmorning.R;

@SuppressLint("SetJavaScriptEnabled")
public class LifetimeMembershipActivity extends AppCompatActivity {
    String currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifetime_membership);
        currency = getIntent().getStringExtra("currency");
        WebView mywebView = findViewById(R.id.paymentPage);
        mywebView.setWebViewClient(new WebViewClient());
        if (currency.equals("USD")){
            mywebView.loadUrl("https://rzp.io/l/39sF42bI");
        }else {
            mywebView.loadUrl("https://rzp.io/l/BEFRJ6W");
        }
        WebSettings webSettings= mywebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
}