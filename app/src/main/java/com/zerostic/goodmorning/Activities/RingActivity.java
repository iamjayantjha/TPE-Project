package com.zerostic.goodmorning.Activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.ncorti.slidetoact.SlideToActView;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.CreateAlarm.CreateAlarmViewModel;
import com.zerostic.goodmorning.Data.Alarm;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;
import com.zerostic.goodmorning.DismissActivities.BarCodeDismissActivity;
import com.zerostic.goodmorning.DismissActivities.MathsDismissActivity;
import com.zerostic.goodmorning.DismissActivities.PatternDismissActivity;
import com.zerostic.goodmorning.DismissActivities.ShakeDismissActivity;
import com.zerostic.goodmorning.DismissActivities.TextDismissActivity;
import com.zerostic.goodmorning.DismissActivities.WalkDismissActivity;
import com.zerostic.goodmorning.Service.AlarmService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 Coded by iamjayantjha
 **/

public class RingActivity extends Activity{
    SlideToActView dismiss, snoozeBtn;
    ImageView clock;
    private CreateAlarmViewModel createAlarmViewModel;
    TextView title, method, tone;
    int hours;
    int minutes;
    DataBaseHelper dataBaseHelper;
    SQLiteDatabase sqLiteDatabase;
    String TITLE,theme,weather,typeOfSleeper, METHOD,id,startTime, ALARM_TONE, numOfQuest, difficulty, SNOOZE, snoozeOrder, VOLUME, VIBRATE, WUC, TYPE, alarmID, methodText, oldHour, oldMin,sun,mon,tue,wed,thu,fri,sat;
    boolean isWuc = false, isSnooze = false,isSettingsAvailable, isRecurring,isSun = false, isMon = false, isTue = false, isWed = false, isThu = false, isFri = false, isSat = false, allowRecent = false;
    int i;
    PowerManager.WakeLock wakeLock;
    PowerManager powerManager;
    private CountDownTimer countDownTimer;

    AudioManager audioManager;
    private static final long START_TIME_IN_MILLIS = 3600000;
    private long TIME_LEFT_IN_MILLIS = START_TIME_IN_MILLIS;
    Alarm alarmScheduleRepeating;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
        createAlarmViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(CreateAlarmViewModel.class);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        Utils.blackIconStatusBar(RingActivity.this, R.color.background);
        title = findViewById(R.id.title);
        method = findViewById(R.id.method);
        tone = findViewById(R.id.tone);
        clock = findViewById(R.id.activity_ring_clock);
        dismiss = findViewById(R.id.activity_ring_dismiss);
        snoozeBtn = findViewById(R.id.snoozeBtn);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire(10*60*1000L /*10 minutes*/);
        SharedPreferences preferences = this.getSharedPreferences("id", Context.MODE_PRIVATE);
        alarmID = preferences.getString("id","");
        readData();
        readMathsMethod();
        checkSnooze();
        readHrMin();
        readUserSettings();
        readRecurringData();
        updateVolume();
        if (isSettingsAvailable){
            if (theme.equals("1")){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else if (theme.equals("2")){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
        setTime();
        preferences = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        weather = preferences.getString("weather","1");
        if (isSnooze) {
            if (i == 1) {
                snoozeBtn.setOnSlideCompleteListener(slideToActView -> {
                    i++;
                    int hour, min;
                    int s = Integer.parseInt(SNOOZE);
                    vibrator.vibrate(pattern, -1);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.MINUTE, s);
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    min = calendar.get(Calendar.MINUTE);
                    saveData(hour, min);
                    Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                    getApplicationContext().stopService(intentService);
                    allowRecent = true;
                    finish();
                    //Toast.makeText(RingActivity.this, "Snoozed for " + s + " minutes", Toast.LENGTH_SHORT).show();
                });

                dismiss.setOnSlideCompleteListener(slideToActView -> {
                    vibrator.vibrate(pattern, -1);
                    if (!isRecurring){
                        changeAlarmData();
                    }
                    updateTrackerTime();
                    Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                    getApplicationContext().stopService(intentService);
                    allowRecent = true;
                    saveOldData();
                //    resetTimer();
                    if (weather.equals("1")){
                        Intent weatherIntent = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            weatherIntent = new Intent(RingActivity.this, WeatherActivity.class);
                        }else {
                            RingActivity.this.finish();
                        }
                        assert weatherIntent != null;
                        weatherIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(weatherIntent);
                        finish();
                    }
                    if (method.getText().toString().equals("Maths")) {
                        Intent mathsIntent = new Intent(RingActivity.this, MathsDismissActivity.class);
                        mathsIntent.putExtra("numOfQuest", numOfQuest);
                        mathsIntent.putExtra("difficulty", difficulty);
                        mathsIntent.putExtra("tone", ALARM_TONE);
                        mathsIntent.putExtra("type", "alarm");
                        mathsIntent.putExtra("id", alarmID);
                        mathsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mathsIntent);
                        finish();
                    } else if (method.getText().toString().equals("Shake")) {
                        Intent shakeIntent = new Intent(RingActivity.this, ShakeDismissActivity.class);
                        shakeIntent.putExtra("numOfQuest", numOfQuest);
                        shakeIntent.putExtra("difficulty", difficulty);
                        shakeIntent.putExtra("tone", ALARM_TONE);
                        shakeIntent.putExtra("type", "alarm");
                        shakeIntent.putExtra("id", alarmID);
                        shakeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(shakeIntent);
                        finish();
                    } else if (method.getText().toString().equals("Bar Code")) {
                        Intent barcodeIntent = new Intent(RingActivity.this, BarCodeDismissActivity.class);
                        barcodeIntent.putExtra("code", numOfQuest);
                        barcodeIntent.putExtra("tone", ALARM_TONE);
                        barcodeIntent.putExtra("type", "alarm");
                        barcodeIntent.putExtra("id", alarmID);
                        barcodeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(barcodeIntent);
                        finish();
                    } else if (method.getText().toString().equals("Walk")) {
                        Intent walkIntent = new Intent(RingActivity.this, WalkDismissActivity.class);
                        walkIntent.putExtra("steps", numOfQuest);
                        walkIntent.putExtra("tone", ALARM_TONE);
                        walkIntent.putExtra("type", "alarm");
                        walkIntent.putExtra("id", alarmID);
                        walkIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(walkIntent);
                        finish();
                    } else if (method.getText().toString().equals("Pattern")) {
                        Intent patternIntent = new Intent(RingActivity.this, PatternDismissActivity.class);
                        patternIntent.putExtra("quest", numOfQuest);
                        patternIntent.putExtra("format", difficulty);
                        patternIntent.putExtra("tone", ALARM_TONE);
                        patternIntent.putExtra("type", "alarm");
                        patternIntent.putExtra("id", alarmID);
                        patternIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(patternIntent);
                        finish();
                    } else if (method.getText().toString().equals("Text")) {
                        Intent textIntent = new Intent(RingActivity.this, TextDismissActivity.class);
                        textIntent.putExtra("quest", numOfQuest);
                        textIntent.putExtra("format", difficulty);
                        textIntent.putExtra("tone", ALARM_TONE);
                        textIntent.putExtra("type", "alarm");
                        textIntent.putExtra("id", alarmID);
                        textIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(textIntent);
                        finish();
                    } else if (method.getText().toString().equals("Challenge Mode")) {
                        //int methodDecision = new Random().nextInt(2);
//                        switch (methodDecision){
                        challengeData();
//                        }

                    } else {
                        if (TYPE.equals("scheduled")) {
                            int wucVal = Integer.parseInt(WUC);
                            if ((wucVal > 1) && (wucVal <= 10)) {
                                int hour, min;
                                int s = Integer.parseInt(WUC);
                                vibrator.vibrate(pattern, -1);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                calendar.add(Calendar.MINUTE, s);
                                hour = calendar.get(Calendar.HOUR_OF_DAY);
                                min = calendar.get(Calendar.MINUTE);
                                wakeUpCheckAlarmCreation(hour, min);
                            }else {
                                RingActivity.this.finish();
                            }
                        }
                    }
                });


            } else {
                snoozeBtn.setVisibility(View.GONE);
                dismiss.setOnSlideCompleteListener(slideToActView -> {
                    vibrator.vibrate(pattern, -1);
                    if (!isRecurring){
                        changeAlarmData();
                    }
                    updateTrackerTime();
                    Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                    getApplicationContext().stopService(intentService);
                    allowRecent = true;
                    saveOldData();
                    //resetTimer();
                    if (weather.equals("1")){
                        Intent weatherIntent;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            weatherIntent = new Intent(RingActivity.this, WeatherActivity.class);
                            weatherIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(weatherIntent);
                            finish();
                        }else {
                            RingActivity.this.finish();
                        }

                    }
                    if (method.getText().toString().equals("Maths")) {
                        Intent mathsIntent = new Intent(RingActivity.this, MathsDismissActivity.class);
                        mathsIntent.putExtra("numOfQuest", numOfQuest);
                        mathsIntent.putExtra("difficulty", difficulty);
                        mathsIntent.putExtra("tone", ALARM_TONE);
                        mathsIntent.putExtra("type", "alarm");
                        mathsIntent.putExtra("id", alarmID);
                        mathsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mathsIntent);
                        finish();
                    } else if (method.getText().toString().equals("Shake")) {
                        Intent shakeIntent = new Intent(RingActivity.this, ShakeDismissActivity.class);
                        shakeIntent.putExtra("numOfQuest", numOfQuest);
                        shakeIntent.putExtra("difficulty", difficulty);
                        shakeIntent.putExtra("tone", ALARM_TONE);
                        shakeIntent.putExtra("type", "alarm");
                        shakeIntent.putExtra("id", alarmID);
                        shakeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(shakeIntent);
                        finish();
                    } else if (method.getText().toString().equals("Bar Code")) {
                        Intent barcodeIntent = new Intent(RingActivity.this, BarCodeDismissActivity.class);
                        barcodeIntent.putExtra("code", numOfQuest);
                        barcodeIntent.putExtra("tone", ALARM_TONE);
                        barcodeIntent.putExtra("type", "alarm");
                        barcodeIntent.putExtra("id", alarmID);
                        barcodeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(barcodeIntent);
                        finish();
                    } else if (method.getText().toString().equals("Walk")) {
                        Intent walkIntent = new Intent(RingActivity.this, WalkDismissActivity.class);
                        walkIntent.putExtra("steps", numOfQuest);
                        walkIntent.putExtra("tone", ALARM_TONE);
                        walkIntent.putExtra("type", "alarm");
                        walkIntent.putExtra("id", alarmID);
                        walkIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(walkIntent);
                        finish();
                    } else if (method.getText().toString().equals("Pattern")) {
                        Intent patternIntent = new Intent(RingActivity.this, PatternDismissActivity.class);
                        patternIntent.putExtra("quest", numOfQuest);
                        patternIntent.putExtra("format", difficulty);
                        patternIntent.putExtra("tone", ALARM_TONE);
                        patternIntent.putExtra("type", "alarm");
                        patternIntent.putExtra("id", alarmID);
                        patternIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(patternIntent);
                        finish();
                    } else if (method.getText().toString().equals("Text")) {
                        Intent textIntent = new Intent(RingActivity.this, TextDismissActivity.class);
                        textIntent.putExtra("quest", numOfQuest);
                        textIntent.putExtra("format", difficulty);
                        textIntent.putExtra("tone", ALARM_TONE);
                        textIntent.putExtra("type", "alarm");
                        textIntent.putExtra("id", alarmID);
                        textIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(textIntent);
                        finish();
                    } else if (method.getText().toString().equals("Challenge Mode")) {
                   //     int methodDecision = new Random().nextInt(2);
//                        switch (methodDecision){
                        challengeData();
//                        }

                    } else {
                        if (TYPE.equals("scheduled")) {
                            int wucVal = Integer.parseInt(WUC);
                            if ((wucVal > 1) && (wucVal <= 10)) {
                                int hour, min;
                                int s = Integer.parseInt(WUC);
                                vibrator.vibrate(pattern, -1);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                calendar.add(Calendar.MINUTE, s);
                                hour = calendar.get(Calendar.HOUR_OF_DAY);
                                min = calendar.get(Calendar.MINUTE);
                                wakeUpCheckAlarmCreation(hour, min);
                            }else {
                                RingActivity.this.finish();
                            }
                        }
                    }
                });
            }
        } else {
            snoozeBtn.setVisibility(View.GONE);
            dismiss.setOnSlideCompleteListener(slideToActView -> {
                vibrator.vibrate(pattern, -1);
                if (!isRecurring){
                    changeAlarmData();
                  //  Toast.makeText(RingActivity.this, "1", Toast.LENGTH_SHORT).show();
                }
              //  Toast.makeText(RingActivity.this, "2", Toast.LENGTH_SHORT).show();
                updateTrackerTime();
                Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                getApplicationContext().stopService(intentService);
                allowRecent = true;
               // resetTimer();
                if (weather.equals("1")){
                    Intent weatherIntent = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        weatherIntent = new Intent(RingActivity.this, WeatherActivity.class);
                    }else {
                        RingActivity.this.finish();
                    }
                    assert weatherIntent != null;
                    weatherIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(weatherIntent);
                    finish();
                   // Toast.makeText(RingActivity.this, "3", Toast.LENGTH_SHORT).show();
                }/*else if (weather.equals("2") && h<11){
                    Intent weatherIntent = new Intent(RingActivity.this, WeatherActivity.class);
                    weatherIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(weatherIntent);
                    RingActivity.this.finish();
                }*/
                if (method.getText().toString().equals("Maths")) {
                    Intent mathsIntent = new Intent(RingActivity.this, MathsDismissActivity.class);
                    mathsIntent.putExtra("numOfQuest", numOfQuest);
                    mathsIntent.putExtra("difficulty", difficulty);
                    mathsIntent.putExtra("tone", ALARM_TONE);
                    mathsIntent.putExtra("type", "alarm");
                    mathsIntent.putExtra("id", alarmID);
                    mathsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mathsIntent);
                    finish();
                } else if (method.getText().toString().equals("Shake")) {
                    Intent shakeIntent = new Intent(RingActivity.this, ShakeDismissActivity.class);
                    shakeIntent.putExtra("numOfQuest", numOfQuest);
                    shakeIntent.putExtra("difficulty", difficulty);
                    shakeIntent.putExtra("tone", ALARM_TONE);
                    shakeIntent.putExtra("type", "alarm");
                    shakeIntent.putExtra("id", alarmID);
                    shakeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(shakeIntent);
                    finish();
                } else if (method.getText().toString().equals("Bar Code")) {
                    Intent barcodeIntent = new Intent(RingActivity.this, BarCodeDismissActivity.class);
                    barcodeIntent.putExtra("code", numOfQuest);
                    barcodeIntent.putExtra("tone", ALARM_TONE);
                    barcodeIntent.putExtra("type", "alarm");
                    barcodeIntent.putExtra("id", alarmID);
                    barcodeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(barcodeIntent);
                    finish();
                } else if (method.getText().toString().equals("Walk")) {
                    Intent walkIntent = new Intent(RingActivity.this, WalkDismissActivity.class);
                    walkIntent.putExtra("steps", numOfQuest);
                    walkIntent.putExtra("tone", ALARM_TONE);
                    walkIntent.putExtra("type", "alarm");
                    walkIntent.putExtra("id", alarmID);
                    walkIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(walkIntent);
                    finish();
                } else if (method.getText().toString().equals("Pattern")) {
                    Intent patternIntent = new Intent(RingActivity.this, PatternDismissActivity.class);
                    patternIntent.putExtra("quest", numOfQuest);
                    patternIntent.putExtra("format", difficulty);
                    patternIntent.putExtra("tone", ALARM_TONE);
                    patternIntent.putExtra("type", "alarm");
                    patternIntent.putExtra("id", alarmID);
                    patternIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(patternIntent);
                    finish();
                } else if (method.getText().toString().equals("Text")) {
                    Intent textIntent = new Intent(RingActivity.this, TextDismissActivity.class);
                    textIntent.putExtra("quest", numOfQuest);
                    textIntent.putExtra("format", difficulty);
                    textIntent.putExtra("tone", ALARM_TONE);
                    textIntent.putExtra("type", "alarm");
                    textIntent.putExtra("id", alarmID);
                    textIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(textIntent);
                    finish();
                } else if (method.getText().toString().equals("Challenge Mode")) {
//                        switch (methodDecision){
                    challengeData();
//                        }

                } else {
                    if (TYPE.equals("scheduled")) {
                        int wucVal = Integer.parseInt(WUC);
                        if ((wucVal > 1) && (wucVal <= 10)) {
                            int hour, min;
                            int s = Integer.parseInt(WUC);
                            vibrator.vibrate(pattern, -1);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.add(Calendar.MINUTE, s);
                            hour = calendar.get(Calendar.HOUR_OF_DAY);
                            min = calendar.get(Calendar.MINUTE);
                            wakeUpCheckAlarmCreation(hour, min);
                        }else {
                            RingActivity.this.finish();
                        }
                    }
                }
            });
        }
        long time = System.currentTimeMillis();
        long mills = Math.abs(time);
        hours = (int) (mills / (1000 * 60 * 60));
        minutes = (int) ((mills / (1000 * 60)) % 60);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

    //    animateClock();
    Animation animation =  AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_jump);
    clock.startAnimation(animation);
    }

    private void changeAlarmData() {
        Alarm alarm = new Alarm(Integer.parseInt(alarmID),
                Integer.parseInt(oldHour),
                Integer.parseInt(oldMin),
                ALARM_TONE,
                TITLE,
                METHOD,
                System.currentTimeMillis(),
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                isWuc);
        createAlarmViewModel.update(alarm);
    }

    private void saveOldData() {
        DataBaseHelper alarmDB = new DataBaseHelper(RingActivity.this);
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        alarmDB.updateData(alarmID, oldHour, oldMin, TITLE, METHOD, ALARM_TONE, SNOOZE, "1", VOLUME, VIBRATE, WUC, TYPE);
    }

    /*private void animateClock() {
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(clock, "rotation", 0f, 20f, 0f, -20f, 0f);
        rotateAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimation.setDuration(800);
        rotateAnimation.start();
    }*/

    private void readData() {
        dataBaseHelper = new DataBaseHelper(RingActivity.this);
        sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readData(alarmID, sqLiteDatabase);
        if (cursor.moveToFirst()) {
            TITLE = cursor.getString(3);
            METHOD = cursor.getString(4);
            ALARM_TONE = cursor.getString(5);
            SNOOZE = cursor.getString(6);
            snoozeOrder = cursor.getString(7);
            VOLUME = cursor.getString(8);
            VIBRATE = cursor.getString(9);
            WUC = cursor.getString(10);
            TYPE = cursor.getString(11);
            title.setText(TITLE);
            method.setText(METHOD);
            tone.setText(ALARM_TONE);
            methodText = METHOD;
            if (Integer.parseInt(WUC)>1){
                isWuc = true;
            }
        }

    }


/*    private void readDataTogetID() {
        Calendar rightNow = Calendar.getInstance();
        int hours = rightNow.get(Calendar.HOUR_OF_DAY);
        int minutes = rightNow.get(Calendar.MINUTE);
        String h = String.valueOf(hours);
        String m = String.valueOf(minutes);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readHourMin(h, m, db);
        if (cursor.moveToFirst()) {
            alarmID = cursor.getString(0);
        }
    }*/

    private void setTime(){
        Calendar today = Calendar.getInstance();
        int hours = today.get(Calendar.HOUR_OF_DAY);
        int minutes = today.get(Calendar.MINUTE);
        String annot;
        if (hours<12){
            annot = " AM";
        }else {
           if (hours>12){
               hours = hours - 12;
           }
            annot = " PM";
        }
        String displayTime;
        if (minutes<10){
             displayTime = hours+":"+"0"+minutes+annot;
        }else {

             displayTime = hours+":"+minutes+annot;
        }
//        startTimer();
    }

    private void readMathsMethod() {
        dataBaseHelper = new DataBaseHelper(RingActivity.this);
        sqLiteDatabase = dataBaseHelper.getReadableDatabase();
            Cursor cursor = dataBaseHelper.readMethodData(METHOD, sqLiteDatabase);
            if (cursor.moveToFirst()) {
                numOfQuest = cursor.getString(1);
                difficulty = cursor.getString(2);
            }

    }

    private void challengeData(){
        dataBaseHelper = new DataBaseHelper(RingActivity.this);
        sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        if (METHOD.equals("Challenge Mode")){
            ALARM_TONE = String.valueOf(getRandomInteger(20,6));
            Cursor res = dataBaseHelper.getAllMethodData();
            int methodDecision = res.getCount() -1;
            String[] methodArray = new String[methodDecision];
            int count = 0;
            while (res.moveToNext()){
                String val = res.getString(0);
                if (!val.equals("Challenge")){
                    methodArray[count] = val;
                    count++;
                }
            }
            int max = new Random().nextInt(methodDecision);
            METHOD = methodArray[max];
            Cursor cursor = dataBaseHelper.readMethodData(METHOD, sqLiteDatabase);
            if (cursor.moveToFirst()) {
                numOfQuest = cursor.getString(1);
                difficulty = cursor.getString(2);
            }

            switch (METHOD){
                case "Maths":
                    Intent mathsIntent = new Intent(RingActivity.this, MathsDismissActivity.class);
                    mathsIntent.putExtra("numOfQuest", numOfQuest);
                    mathsIntent.putExtra("difficulty", difficulty);
                    mathsIntent.putExtra("tone", ALARM_TONE);
                    mathsIntent.putExtra("type", "alarm");
                    mathsIntent.putExtra("id", alarmID);
                    mathsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mathsIntent);
                    finish();
                    break;
                case "Shake":
                    Intent shakeIntent = new Intent(RingActivity.this, ShakeDismissActivity.class);
                    shakeIntent.putExtra("numOfQuest", numOfQuest);
                    shakeIntent.putExtra("difficulty", difficulty);
                    shakeIntent.putExtra("tone", ALARM_TONE);
                    shakeIntent.putExtra("type", "alarm");
                    shakeIntent.putExtra("id", alarmID);
                    shakeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(shakeIntent);
                    finish();
                    break;

                case "Bar Code":
                    Intent barcodeIntent = new Intent(RingActivity.this, BarCodeDismissActivity.class);
                    barcodeIntent.putExtra("code", numOfQuest);
                    barcodeIntent.putExtra("tone", ALARM_TONE);
                    barcodeIntent.putExtra("type", "alarm");
                    barcodeIntent.putExtra("id", alarmID);
                    barcodeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(barcodeIntent);
                    finish();
                    break;

                case "Walk":
                    Intent walkIntent = new Intent(RingActivity.this, WalkDismissActivity.class);
                    walkIntent.putExtra("steps", numOfQuest);
                    walkIntent.putExtra("tone", ALARM_TONE);
                    walkIntent.putExtra("type", "alarm");
                    walkIntent.putExtra("id", alarmID);
                    walkIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(walkIntent);
                    finish();
                    break;

                case "Pattern":
                    Intent patternIntent = new Intent(RingActivity.this, PatternDismissActivity.class);
                    patternIntent.putExtra("quest", numOfQuest);
                    patternIntent.putExtra("format", difficulty);
                    patternIntent.putExtra("tone", ALARM_TONE);
                    patternIntent.putExtra("type", "alarm");
                    patternIntent.putExtra("id", alarmID);
                    patternIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(patternIntent);
                    finish();
                    break;

                case "Text":
                    Intent textIntent = new Intent(RingActivity.this, TextDismissActivity.class);
                    textIntent.putExtra("quest", numOfQuest);
                    textIntent.putExtra("format", difficulty);
                    textIntent.putExtra("tone", ALARM_TONE);
                    textIntent.putExtra("type", "alarm");
                    textIntent.putExtra("id", alarmID);
                    textIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(textIntent);
                    finish();
                    break;
            }
        }
    }

    private void checkSnooze() {
        if (SNOOZE.equals("0")) {
            isSnooze = false;
        } else {
            isSnooze = true;
            i = Integer.parseInt(snoozeOrder);
        }
    }

    private void saveData(int h, int m) {
        DataBaseHelper alarmDB = new DataBaseHelper(RingActivity.this);
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        int ids = Integer.parseInt(alarmID);
        String id = String.valueOf(ids);
        String hour = String.valueOf(h);
        String min = String.valueOf(m);
        boolean result;
        if (typeOfSleeper.equals("3")){
            result = alarmDB.updateData(id, hour, min, TITLE, METHOD, ALARM_TONE, SNOOZE, "2", VOLUME, VIBRATE, WUC, TYPE);
        }else{
            result = alarmDB.updateData(id, hour, min, TITLE, METHOD, ALARM_TONE, SNOOZE, "1", VOLUME, VIBRATE, WUC, TYPE);
        }
        if (result) {
            int wucVal = Integer.parseInt(WUC);
            boolean wuc;
            wuc = (wucVal > 1) && (wucVal <= 10);
            scheduleAlarm(h, m, wuc);
            RingActivity.this.finish();
        } else {
            Toast.makeText(RingActivity.this, "Alarm not scheduled", Toast.LENGTH_LONG).show();
        }
    }

    private void wakeUpCheckAlarmCreation(int h, int m) {
        DataBaseHelper alarmDB = new DataBaseHelper(RingActivity.this);
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        int ids = Integer.parseInt(alarmID);
        String id = String.valueOf(ids);
        String hour = String.valueOf(h);
        String min = String.valueOf(m);
        boolean result = alarmDB.updateData(id, hour, min, TITLE, METHOD, ALARM_TONE, SNOOZE, "1", VOLUME, VIBRATE, WUC, "wuc");
        if (result) {
            scheduleWuc(h, m);
            RingActivity.this.finish();
        } else {
            Toast.makeText(RingActivity.this, "Alarm not scheduled", Toast.LENGTH_LONG).show();
        }
    }

    private void scheduleAlarm(int hour, int min, boolean wuc) {
        Alarm alarmSchedule = new Alarm(
                Integer.parseInt(alarmID),
                hour,
                min,
                ALARM_TONE,
                TITLE,
                methodText,
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
                wuc
        );
        alarmSchedule.schedule(getApplicationContext());
    }

    private void scheduleWuc(int hour, int min) {
        Alarm alarmSchedule = new Alarm(
                Integer.parseInt(alarmID),
                hour,
                min,
                ALARM_TONE,
                "WAKE UP CHECK",
                methodText,
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

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!allowRecent){
            RingActivity.this.finish();
            Intent service = new Intent("android.intent.action.MAIN");
            service.setClass(this,RingActivity.class);
            service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(service);
        }
    }

    private void readHrMin() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readHrMin(alarmID, db);
        if (cursor.moveToFirst()) {
            oldHour = cursor.getString(1);
            oldMin = cursor.getString(2);
        }
    }

    private void readRecurringData() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readRecurringData(alarmID, db);
        if (cursor.moveToFirst()) {
            sun = cursor.getString(1);
            mon = cursor.getString(2);
            tue = cursor.getString(3);
            wed = cursor.getString(4);
            thu = cursor.getString(5);
            fri = cursor.getString(6);
            sat = cursor.getString(7);

            SharedPreferences sunday=getSharedPreferences("sun",MODE_PRIVATE);
            SharedPreferences.Editor sunEditor=sunday.edit();
            sunEditor.putString("sun",sun);
            sunEditor.apply();

            SharedPreferences monday=getSharedPreferences("mon",MODE_PRIVATE);
            SharedPreferences.Editor monEditor=monday.edit();
            monEditor.putString("mon",mon);
            monEditor.apply();

            SharedPreferences tuesday=getSharedPreferences("tue",MODE_PRIVATE);
            SharedPreferences.Editor tueEditor=tuesday.edit();
            tueEditor.putString("tue",tue);
            tueEditor.apply();

            SharedPreferences wednesday=getSharedPreferences("wed",MODE_PRIVATE);
            SharedPreferences.Editor wedEditor=wednesday.edit();
            wedEditor.putString("wed",wed);
            wedEditor.apply();

            SharedPreferences thursday=getSharedPreferences("thu",MODE_PRIVATE);
            SharedPreferences.Editor thuEditor=thursday.edit();
            thuEditor.putString("thu",thu);
            thuEditor.apply();

            SharedPreferences friday=getSharedPreferences("fri",MODE_PRIVATE);
            SharedPreferences.Editor friEditor=friday.edit();
            friEditor.putString("fri",fri);
            friEditor.apply();

            SharedPreferences saturday=getSharedPreferences("sat",MODE_PRIVATE);
            SharedPreferences.Editor satEditor=saturday.edit();
            satEditor.putString("sat",sat);
            satEditor.apply();


            isRecurring = sun.equals("yes") || mon.equals("yes") || tue.equals("yes") || wed.equals("yes") || thu.equals("yes") || fri.equals("yes") || sat.equals("yes");

            if (isRecurring){
                if (sun.equals("yes")){
                    isSun = true;
                }
                if (mon.equals("yes")){
                    isMon = true;
                }
                if (tue.equals("yes")){
                    isTue = true;
                }
                if (wed.equals("yes")){
                    isWed = true;
                }
                if (thu.equals("yes")){
                    isThu = true;
                }
                if (fri.equals("yes")){
                    isFri = true;
                }
                if (sat.equals("yes")){
                    isSat = true;
                }
                scheduleRepeatingAlarm();
            }
        }
    }

    private void scheduleRepeatingAlarm() {
        alarmScheduleRepeating  = new Alarm(
                Integer.parseInt(alarmID),
                Integer.parseInt(oldHour),
                Integer.parseInt(oldMin),
                ALARM_TONE,
                TITLE,
                methodText,
                System.currentTimeMillis(),
                true,
                isRecurring,
                isMon,
                isTue,
                isWed,
                isThu,
                isFri,
                isSat,
                isSun,
                isWuc
        );
        getDates();
//        alarmScheduleRepeating.scheduleRecurring(getApplicationContext(),6);
    }

    public void getDates() {
        String todaysDate = date();
        //String month = month();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        String monday="",tuesday="",wednesday="",thursday="",friday="",saturday="",sunday="";
        int dateVal = Integer.parseInt(todaysDate);
        switch (day) {
            case "Monday":
                monday = todaysDate;
                tuesday = String.valueOf(dateVal + 1);
                wednesday = String.valueOf(dateVal + 2);
                thursday = String.valueOf(dateVal + 3);
                friday = String.valueOf(dateVal + 4);
                saturday = String.valueOf(dateVal + 5);
                sunday = String.valueOf(dateVal + 6);
                break;
            case "Tuesday":
                tuesday = todaysDate;
                wednesday = String.valueOf(dateVal + 1);
                thursday = String.valueOf(dateVal + 2);
                friday = String.valueOf(dateVal + 3);
                saturday = String.valueOf(dateVal + 4);
                sunday = String.valueOf(dateVal + 5);
                monday = String.valueOf(dateVal + 6);
                break;
            case "Wednesday":
                tuesday = String.valueOf(dateVal + 6);
                wednesday = todaysDate;
                thursday = String.valueOf(dateVal + 1);
                friday = String.valueOf(dateVal + 2);
                saturday = String.valueOf(dateVal + 3);
                sunday = String.valueOf(dateVal + 4);
                monday = String.valueOf(dateVal + 5);
                break;
            case "Thursday":
                tuesday = String.valueOf(dateVal + 5);
                wednesday = String.valueOf(dateVal + 6);
                thursday = todaysDate;
                friday = String.valueOf(dateVal + 1);
                saturday = String.valueOf(dateVal + 2);
                sunday = String.valueOf(dateVal + 3);
                monday = String.valueOf(dateVal + 4);
                break;
            case "Friday":
                tuesday = String.valueOf(dateVal + 4);
                wednesday = String.valueOf(dateVal + 5);
                thursday = String.valueOf(dateVal + 6);
                friday = todaysDate;
                saturday = String.valueOf(dateVal + 1);
                sunday = String.valueOf(dateVal + 2);
                monday = String.valueOf(dateVal + 3);
                break;
            case "Saturday":
                tuesday = String.valueOf(dateVal + 3);
                wednesday = String.valueOf(dateVal + 4);
                thursday = String.valueOf(dateVal + 5);
                friday = String.valueOf(dateVal + 6);
                saturday = todaysDate;
                sunday = String.valueOf(dateVal + 1);
                monday = String.valueOf(dateVal + 2);
                break;
            case "Sunday":
                tuesday = String.valueOf(dateVal + 2);
                wednesday = String.valueOf(dateVal + 3);
                thursday = String.valueOf(dateVal + 4);
                friday = String.valueOf(dateVal + 5);
                saturday = String.valueOf(dateVal + 6);
                sunday = todaysDate;
                monday = String.valueOf(dateVal + 1);
                break;
        }

        switch (day) {
            case "Monday":
                if (isTue) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(tuesday));
                } else if (isWed) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(wednesday));
                } else if (isThu) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(thursday));
                } else if (isFri) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(friday));
                } else if (isSat) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(saturday));
                } else if (isSun) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(sunday));
                } else if (isMon) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(sunday) + 1);
                }
                break;
            case "Tuesday":
                if (isWed) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(wednesday));
                } else if (isThu) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(thursday));
                } else if (isFri) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(friday));
                } else if (isSat) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(saturday));
                } else if (isSun) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(sunday));
                } else if (isMon) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(monday));
                } else if (isTue) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(monday) + 1);
                }
                break;
            case "Wednesday":
                if (isThu) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(thursday));
                } else if (isFri) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(friday));
                } else if (isSat) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(saturday));
                } else if (isSun) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(sunday));
                } else if (isMon) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(monday));
                } else if (isTue) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(tuesday));
                } else if (isWed) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(tuesday) + 1);
                }
                break;
            case "Thursday":
                if (isFri) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(friday));
                } else if (isSat) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(saturday));
                } else if (isSun) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(sunday));
                } else if (isMon) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(monday));
                } else if (isTue) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(tuesday));
                } else if (isWed) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(wednesday));
                } else if (isThu) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(wednesday) + 1);
                }
                break;
            case "Friday":
                if (isSat) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(saturday));
                } else if (isSun) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(sunday));
                } else if (isMon) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(monday));
                } else if (isTue) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(tuesday));
                } else if (isWed) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(wednesday));
                } else if (isThu) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(thursday));
                } else if (isFri) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(thursday) + 1);
                }
                break;
            case "Saturday":
                if (isSun) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(sunday));
                } else if (isMon) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(monday));
                } else if (isTue) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(tuesday));
                } else if (isWed) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(wednesday));
                } else if (isThu) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(thursday));
                } else if (isFri) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(friday));
                } else if (isSat) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(friday) + 1);
                }
                break;
            case "Sunday":
                if (isMon) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(monday));
                } else if (isTue) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(tuesday));
                } else if (isWed) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(wednesday));
                } else if (isThu) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(thursday));
                } else if (isFri) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(friday));
                } else if (isSat) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(saturday));
                } else if (isSun) {
                    alarmScheduleRepeating.scheduleRecurring(getApplicationContext(), Integer.parseInt(saturday) + 1);
                }
                break;
        }
    }

    private void updateTrackerTime(){
        DataBaseHelper alarmDB = new DataBaseHelper(this);
        sqLiteDatabase = alarmDB.getReadableDatabase();
        Cursor res = alarmDB.getAllTrackerData();
        id = ""+res.getCount();
        String status;
        Cursor cursor = alarmDB.readTrackerData(id, sqLiteDatabase);
        if (cursor.moveToFirst()) {
            startTime = cursor.getString(1);
            status = cursor.getString(4);
            Calendar rightNow = Calendar.getInstance();
            int hours = rightNow.get(Calendar.HOUR_OF_DAY);
            int minutes = rightNow.get(Calendar.MINUTE);
            String h = ""+hours;
            String m = ""+minutes;
            String endTime = h+":"+m;
            String h1 = "",m1 = "";
            for (String val: startTime.split(":")){
                if (h1.equals("")){
                    h1=val;
                }else if (m1.equals("")){
                    m1=val;
                }
            }
            int hr=0,min=0;

            if (Integer.parseInt(h1)>Integer.parseInt(h)){
            hr = 23-Integer.parseInt(h1);
            min = 60-Integer.parseInt(m1);
            hr = hr+Integer.parseInt(h);
            min = min+Integer.parseInt(m);
            if (min>59){
                hr = hr+1;
                min = min-60;
            }

            }
            else if (Integer.parseInt(h)>=Integer.parseInt(h1)){
                hr = Integer.parseInt(h)-Integer.parseInt(h1);
                min = Integer.parseInt(m)-Integer.parseInt(m1);
                if (hr<0){
                    hr = hr*(-1);
                }
                if (min<0){
                    min = min*(-1);
                }
            }

            if ((res.getCount()>0)&&(status.equals("On"))){
                alarmDB.updateTrackerData(id,startTime,endTime,hr+":"+min,"Off");
            }
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
            typeOfSleeper = cursor.getString(4);
        }
        else {
            isSettingsAvailable = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        readUserSettings();
    }
    public static int getRandomInteger(int maximum, int minimum){
        return ((int) (Math.random()*(maximum - minimum))) + minimum;
    }

/*    public static String month(){
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
        return simpleDateFormat.format(c);
    }*/

    public static String date(){
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
        return simpleDateFormat.format(c);
    }

  /*  private void startTimer() {
        countDownTimer = new CountDownTimer(TIME_LEFT_IN_MILLIS,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TIME_LEFT_IN_MILLIS = millisUntilFinished;
                updateTime();
            }

            @Override
            public void onFinish() {
                resetTimer();
            }
        }.start();

    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void updateVolume() {
        audioManager  = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Integer.parseInt(VOLUME)+5, 0);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

  /*  private void resetTimer() {
        countDownTimer.cancel();
        TIME_LEFT_IN_MILLIS = START_TIME_IN_MILLIS;
    }*/

}