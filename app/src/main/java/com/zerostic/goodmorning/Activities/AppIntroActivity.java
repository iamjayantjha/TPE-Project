package com.zerostic.goodmorning.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.card.MaterialCardView;
import com.zerostic.goodmorning.Adapter.SliderAdapter;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Utils;

/**
Coded by iamjayantjha
**/

public class AppIntroActivity extends AppCompatActivity {
    ViewPager mSlider;
    LinearLayout mDotsLayout;
    SliderAdapter sliderAdapter;
    TextView[] mDots;
    MaterialCardView previousBtn, nextBtn;
    int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);
        Utils.blackIconStatusBar(AppIntroActivity.this, R.color.background);
        mSlider = findViewById(R.id.slider);
        mDotsLayout = findViewById(R.id.dots);
        sliderAdapter = new SliderAdapter(this);
        mSlider.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        mSlider.addOnPageChangeListener(viewListener);
        previousBtn = findViewById(R.id.previousBtn);
        nextBtn = findViewById(R.id.nextBtn);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        nextBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern,-1);
            int pageValue = currentPage +1;
            mSlider.setCurrentItem(pageValue);
            if (pageValue == mDots.length){
                Intent login = new Intent(AppIntroActivity.this, LoginActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(login);
                finish();
            }
        });
        previousBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern,-1);
            mSlider.setCurrentItem(currentPage -1);
        });
    }

    public void addDotsIndicator(int position){
        mDots = new TextView[3];
        mDotsLayout.removeAllViews();
        for (int i =0; i < mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorPrimary));
            mDotsLayout.addView(mDots[i]);
        }

        if (mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.dots));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            currentPage = position;
            if (position == 0){
                previousBtn.setEnabled(false);
                previousBtn.setVisibility(View.GONE);
                nextBtn.setEnabled(true);
                nextBtn.setVisibility(View.VISIBLE);
            }else if (position == 1){
                previousBtn.setEnabled(true);
                previousBtn.setVisibility(View.VISIBLE);
            } else {
                previousBtn.setEnabled(true);
                previousBtn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}