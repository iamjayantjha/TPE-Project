package com.zerostic.goodmorning.DismissActivities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Activities.AlarmPreviewActivity;
import com.zerostic.goodmorning.Activities.AlarmRestartActivity;
import com.zerostic.goodmorning.Activities.WeatherActivity;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Data.Alarm;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.Service.AlarmService;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;
import jp.shts.android.storiesprogressview.StoriesProgressView;

/**
 Coded by iamjayantjha
 **/

public class PatternDismissActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener{
    String type,quest,tone;
    int totalQuest,num1,maxQuest;
    final int min = 65;
    final int max = 90;
    String alphabetVal;
    int val;
    int patternClick = 0;
    private StoriesProgressView storiesProgressView;
    MaterialCardView questOne,questTwo,questThree,questFour,questFive,questSix,questSeven,questEight,questNine,questTen;
    TextView valueOne,valueTwo,valueThree,valueFour,valueFive,valueSix,valueSeven,valueEight,valueNine,valueTen;
    RelativeLayout rowOne,rowTwo,rowThree,rowFour,rowFive;
    int hours;
    int minutes;
    String alarmID;
    int previousVal=0, currentVal=0;
    boolean allowRecent = false;
    String TITLE,METHOD ,ALARM_TONE,SNOOZE,snoozeOrder,VOLUME,VIBRATE,WUC,TYPE,mType;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_dismiss);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        Utils.blackIconStatusBar(PatternDismissActivity.this, R.color.background);
        KeyguardManager.KeyguardLock keyguardLock = keyguardLock = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE)).newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();// for 30 minutes time out use 30*60*1000L)
        type = getIntent().getStringExtra("format");
        quest = getIntent().getStringExtra("quest");
        tone = getIntent().getStringExtra("tone");
        mType = getIntent().getStringExtra("type");
        alarmID = getIntent().getStringExtra("id");
        storiesProgressView = findViewById(R.id.timer);
        storiesProgressView.setStoriesCount(1); // <- set stories
        storiesProgressView.setStoryDuration(20000); // <- set a story duration
        storiesProgressView.setStoriesListener(this); // <- set listener
        storiesProgressView.startStories();
        if (mType.equals("alarm")){
            readData();
        }else {
            allowRecent = true;
        }
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        rowOne = findViewById(R.id.rowOne);
        rowTwo = findViewById(R.id.rowTwo);
        rowThree = findViewById(R.id.rowThree);
        rowFour = findViewById(R.id.rowFour);
        rowFive = findViewById(R.id.rowFive);
        valueOne = findViewById(R.id.valueOne);
        valueTwo = findViewById(R.id.valueTwo);
        valueThree = findViewById(R.id.valueThree);
        valueFour = findViewById(R.id.valueFour);
        valueFive = findViewById(R.id.valueFive);
        valueSix = findViewById(R.id.valueSix);
        valueSeven = findViewById(R.id.valueSeven);
        valueEight = findViewById(R.id.valueEight);
        valueNine = findViewById(R.id.valueNine);
        valueTen = findViewById(R.id.valueTen);
        questOne = findViewById(R.id.questOne);
        questTwo = findViewById(R.id.questTwo);
        questThree = findViewById(R.id.questThree);
        questFour = findViewById(R.id.questFour);
        questFive = findViewById(R.id.questFive);
        questSix = findViewById(R.id.questSix);
        questSeven = findViewById(R.id.questSeven);
        questEight = findViewById(R.id.questEight);
        questNine = findViewById(R.id.questNine);
        questTen = findViewById(R.id.questTen);
        if (quest.equals("6")){
            rowFour.setVisibility(View.GONE);
            rowFive.setVisibility(View.GONE);
        }else if (quest.equals("8")){
            rowFive.setVisibility(View.GONE);
        }
        totalQuest = Integer.parseInt(quest);
        if (type.equals("alphabet")){
            num1 = new Random().nextInt((max - min) + 1) + min;
        }else if (type.equals("number")){
            num1 = new Random().nextInt((100 - 10) + 1) + 10;
        }
        if (type.equals("alphabet")&&num1==90){
            num1 = num1-totalQuest;
        }else if (type.equals("alphabet")&&num1>90){
            num1 = num1-totalQuest;
        }else if (type.equals("alphabet")&&(num1+totalQuest)>90){
            num1 = num1-totalQuest;
        }
        int[] values = new int[totalQuest];
        for (int i = 0;i<totalQuest;i++){
            values[i] = num1;
            num1++;
        }
        num1 = num1-values.length;
        Random rand = new Random();

        for (int i = 0; i < values.length; i++) {
            int randomIndex = rand.nextInt(values.length);
            int temp = values[i];
            values[i] = values[randomIndex];
            values[randomIndex] = temp;
        }
        maxQuest = totalQuest-1;
        if (type.equals("alphabet")){
            for (int index = 0; index<=maxQuest;index++){
                if (index == 0){
                    val = values[4];
                    alphabetVal = Character.toString((char) val);
                    valueOne.setText(alphabetVal);
                }else if (index == 1){
                    val = values[3];
                    alphabetVal = Character.toString((char) val);
                    valueTwo.setText(alphabetVal);
                }else if (index == 2){
                    val = values[5];
                    alphabetVal = Character.toString((char) val);
                    valueThree.setText(alphabetVal);
                }else if (index == 3){
                    val = values[1];
                    alphabetVal = Character.toString((char) val);
                    valueFour.setText(alphabetVal);
                }else if (index == 4){
                    val = values[0];
                    alphabetVal = Character.toString((char) val);
                    valueFive.setText(alphabetVal);
                }else if (index == 5){
                    val = values[2];
                    alphabetVal = Character.toString((char) val);
                    valueSix.setText(alphabetVal);
                }else if (index == 6){
                    val = values[7];
                    alphabetVal = Character.toString((char) val);
                    valueSeven.setText(alphabetVal);
                }else if (index == 7){
                    val = values[6];
                    alphabetVal = Character.toString((char) val);
                    valueEight.setText(alphabetVal);
                }else if (index == 8){
                    val = values[9];
                    alphabetVal = Character.toString((char) val);
                    valueNine.setText(alphabetVal);
                }else if (index == 9){
                    val = values[8];
                    alphabetVal = Character.toString((char) val);
                    valueTen.setText(alphabetVal);
                }
            }
            questOne.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getAsciiValue(valueOne.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questOne, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questOne, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questTwo.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getAsciiValue(valueTwo.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questTwo, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questTwo, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questThree.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getAsciiValue(valueThree.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questThree, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questThree, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questFour.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getAsciiValue(valueFour.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questFour, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questFour, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questFive.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getAsciiValue(valueFive.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questFive, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questFive, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questSix.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getAsciiValue(valueSix.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questSix, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questSix, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questSeven.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getAsciiValue(valueSeven.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questSeven, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questSeven, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questEight.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getAsciiValue(valueEight.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questEight, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questEight, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questNine.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getAsciiValue(valueNine.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questNine, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questNine, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questTen.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getAsciiValue(valueTen.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questTen, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questTen, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });

        }
        else if (type.equals("number")){
            for (int index = 0; index<=maxQuest; index++){
                if (index == 0){
                    val = values[4];
                    valueOne.setText(""+val);
                }else if (index == 1){
                    val = values[3];
                    valueTwo.setText(""+val);
                }else if (index == 2){
                    val = values[5];
                    valueThree.setText(""+val);
                }else if (index == 3){
                    val = values[1];
                    valueFour.setText(""+val);
                }else if (index == 4){
                    val = values[0];
                    valueFive.setText(""+val);
                }else if (index == 5){
                    val = values[2];
                    valueSix.setText(""+val);
                }else if (index == 6){
                    val = values[7];
                    valueSeven.setText(""+val);
                }else if (index == 7){
                    val = values[6];
                    valueEight.setText(""+val);
                }else if (index == 8){
                    val = values[9];
                    valueNine.setText(""+val);
                }else if (index == 9){
                    val = values[8];
                    valueTen.setText(""+val);
                }
            }

            questOne.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getNumericValue(valueOne.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questOne, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questOne, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questTwo.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getNumericValue(valueTwo.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questTwo, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questTwo, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questThree.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getNumericValue(valueThree.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questThree, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questThree, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questFour.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getNumericValue(valueFour.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questFour, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questFour, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questFive.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getNumericValue(valueFive.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questFive, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questFive, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questSix.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getNumericValue(valueSix.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questSix, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questSix, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questSeven.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getNumericValue(valueSeven.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questSeven, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questSeven, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questEight.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getNumericValue(valueEight.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questEight, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questEight, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questNine.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getNumericValue(valueNine.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questNine, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questNine, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
            questTen.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                storiesProgressView.reverse();
                previousVal = currentVal;
                currentVal = getNumericValue(valueTen.getText().toString().trim());
                if (((previousVal+1)==currentVal) || (previousVal==0 && currentVal==num1)){
                    changeCardColor(questTen, 0);
                    patternClick++;
                }else {
                    previousVal = 0;
                    currentVal = 0;
                    changeCardColor(questTen, 1);
                    patternClick = 0;
                    new Handler().postDelayed(() -> {
                        changeCardToDefaultColor(questOne, questTwo, questThree, questFour, questFive, questSix, questSeven, questEight, questNine, questTen);
                    }, 1000);
                }
                questOne.setRadius(30);
                completed();
            });
        }
        long time = System.currentTimeMillis() ;
        long mills = Math.abs(time);
        hours = (int) (mills / (1000 * 60 * 60));
        minutes = (int) ((mills / (1000 * 60)) % 60);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


    }

    private int getAsciiValue(String value) {
        return value.charAt(0);
    }

    private int getNumericValue(String value){
        return Integer.parseInt(value);
    }

    private void changeCardColor(MaterialCardView cardView, int i) {
        if (i == 0){
            cardView.setBackgroundColor(ContextCompat.getColor(this, R.color.selected));
        }else {
            cardView.setBackgroundColor(ContextCompat.getColor(this, R.color.error));
        }
        cardView.setRadius(30);
    }

    private void changeCardToDefaultColor(MaterialCardView questOne, MaterialCardView questTwo, MaterialCardView questThree, MaterialCardView questFour, MaterialCardView questFive, MaterialCardView questSix, MaterialCardView questSeven, MaterialCardView questEight, MaterialCardView questNine, MaterialCardView questTen) {
        // Set background color to default
        questOne.setBackgroundColor(ContextCompat.getColor(this, R.color.cardColor));
        questTwo.setBackgroundColor(ContextCompat.getColor(this, R.color.cardColor));
        questThree.setBackgroundColor(ContextCompat.getColor(this, R.color.cardColor));
        questFour.setBackgroundColor(ContextCompat.getColor(this, R.color.cardColor));
        questFive.setBackgroundColor(ContextCompat.getColor(this, R.color.cardColor));
        questSix.setBackgroundColor(ContextCompat.getColor(this, R.color.cardColor));
        questSeven.setBackgroundColor(ContextCompat.getColor(this, R.color.cardColor));
        questEight.setBackgroundColor(ContextCompat.getColor(this, R.color.cardColor));
        questNine.setBackgroundColor(ContextCompat.getColor(this, R.color.cardColor));
        questTen.setBackgroundColor(ContextCompat.getColor(this, R.color.cardColor));
        // Set radius to default
        questOne.setRadius(30);
        questTwo.setRadius(30);
        questThree.setRadius(30);
        questFour.setRadius(30);
        questFive.setRadius(30);
        questSix.setRadius(30);
        questSeven.setRadius(30);
        questEight.setRadius(30);
        questNine.setRadius(30);
        questTen.setRadius(30);
    }

    private void completed(){
        if (patternClick>maxQuest){
            if (mType.equals("alarm")){
                if (TYPE.equals("scheduled")){
                    int wucVal = Integer.parseInt(WUC);
                    if ((wucVal>1)&&(wucVal<=10)){
                        int hour, min;
                        int s = Integer.parseInt(WUC);
                        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        final long[] pattern = {10, 20};
                        vibrator.vibrate(pattern,-1);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.add(Calendar.MINUTE, s);
                        hour = calendar.get(Calendar.HOUR_OF_DAY);
                        min = calendar.get(Calendar.MINUTE);
                        wakeUpCheckAlarmCreation(hour,min);
                    }
                }

                Intent weatherIntent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    weatherIntent = new Intent(PatternDismissActivity.this, WeatherActivity.class);
                    weatherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(weatherIntent);
                    finish();
                }else {
                    PatternDismissActivity.this.finish();
                }
                allowRecent = true;

            }else {
                PatternDismissActivity.this.finish();
            }

        }
    }

    @Override
    public void onNext() {

    }

    @Override
    public void onPrev() {

    }

    @Override
    public void onComplete() {
        allowRecent = true;
        if (mType.equals("alarm")){
            startAlarmService();
            Intent alarmRestart = new Intent(PatternDismissActivity.this, AlarmRestartActivity.class);
            startActivity(alarmRestart);
            PatternDismissActivity.this.finish();
        }else{
            Intent alarmRestart = new Intent(PatternDismissActivity.this, AlarmPreviewActivity.class);
            alarmRestart.putExtra("tone",tone);
            alarmRestart.putExtra("numOfQuest",quest);
            alarmRestart.putExtra("difficulty",type);
            alarmRestart.putExtra("method","Pattern");
            alarmRestart.putExtra("type",mType);
            alarmRestart.putExtra("id",alarmID);
            alarmRestart.putExtra("from","dismiss");
            startActivity(alarmRestart);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
    }
    private void startAlarmService() {
        Intent intentService = new Intent(PatternDismissActivity.this, AlarmService.class);
        intentService.putExtra("id", alarmID);
        intentService.putExtra("act","AlarmRestartActivity");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(intentService);
        } else {
            this.startService(intentService);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!allowRecent){
            //   Toast.makeText(getApplicationContext(),"2",Toast.LENGTH_SHORT).show();
            PatternDismissActivity.this.finish();
            startAlarmService();
            Intent service = new Intent("android.intent.action.MAIN");
            service.setClass(this, AlarmRestartActivity.class);
            service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(service);
        }
    }
    private void readData() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readData(alarmID,db);
        if (cursor.moveToFirst()){
            TITLE = cursor.getString(3);
            METHOD = cursor.getString(4);
            ALARM_TONE = cursor.getString(5);
            SNOOZE = cursor.getString(6);
            snoozeOrder = cursor.getString(7);
            VOLUME = cursor.getString(8);
            VIBRATE = cursor.getString(9);
            WUC = cursor.getString(10);
            TYPE = cursor.getString(11);
        }

    }

    private void wakeUpCheckAlarmCreation(int h, int m){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        dataBaseHelper.getWritableDatabase();
        int ids = Integer.parseInt(alarmID);
        String id = String.valueOf(ids);
        String hour = String.valueOf(h);
        String min = String.valueOf(m);
        boolean result = dataBaseHelper.updateData(id,hour,min,TITLE,METHOD,ALARM_TONE,SNOOZE,"1",VOLUME,VIBRATE,WUC,"wuc");
        if (result){
            scheduleWuc(h,m);
            PatternDismissActivity.this.finish();
        }else {
            Toast.makeText(PatternDismissActivity.this,"There won't be any wake up check",Toast.LENGTH_LONG).show();
        }
    }

    private void scheduleWuc(int hour, int min) {
        Alarm alarmSchedule = new Alarm(
                Integer.parseInt(alarmID),
                hour,
                min,
                ALARM_TONE,
                "WAKE UP CHECK",
                METHOD,
                System.currentTimeMillis(),
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false
        );
        alarmSchedule.scheduleWakeup(getApplicationContext());
    }


}