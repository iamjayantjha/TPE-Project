package com.zerostic.goodmorning.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ncorti.slidetoact.SlideToActView;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Data.Alarm;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.DismissActivities.BarCodeDismissActivity;
import com.zerostic.goodmorning.DismissActivities.MathsDismissActivity;
import com.zerostic.goodmorning.DismissActivities.PatternDismissActivity;
import com.zerostic.goodmorning.DismissActivities.ShakeDismissActivity;
import com.zerostic.goodmorning.DismissActivities.TextDismissActivity;
import com.zerostic.goodmorning.DismissActivities.WalkDismissActivity;
import com.zerostic.goodmorning.Service.AlarmService;

import java.util.Calendar;

public class AlarmRestartActivity extends AppCompatActivity {
    String tone,numOfQuest,difficulty,method,wuc,mType,alarmID,from,TITLE,SNOOZE,snoozeOrder,VOLUME,VIBRATE;
    int integerTone;
    private MediaPlayer mediaPlayer;
    SlideToActView dismissBtn;
    TextView title, time;
    ImageView clock;
    int hours;
    boolean isWuc = true, alarmRestarted = true, toStop = false, allowRecent = false;
    int minutes;
    AudioManager audioManager;
    private CountDownTimer countDownTimer;
    private static final long START_TIME_IN_MILLIS = 3600000;
    private long TIME_LEFT_IN_MILLIS = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_restart);
        Utils.blackIconStatusBar(AlarmRestartActivity.this, R.color.background);
      //  Toast.makeText(this, "AlarmRestartActivity", Toast.LENGTH_SHORT).show();
        SharedPreferences preferences = this.getSharedPreferences("id", Context.MODE_PRIVATE);
        alarmID = preferences.getString("id","");
        clock = findViewById(R.id.activity_ring_clock);
        title = findViewById(R.id.title);
        time = findViewById(R.id.time);
        dismissBtn = findViewById(R.id.activity_ring_dismiss);
        readData();
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
    @Override
    protected void onStart() {
        super.onStart();
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        if (alarmRestarted){
            title.setText(TITLE);
            setTime();
        }
        dismissBtn.setOnSlideCompleteListener(slideToActView -> {
            vibrator.vibrate(pattern, -1);
          //  mediaPlayer.stop();
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            allowRecent = true;
            switch (method) {
                case "Maths": {
                    Intent dismissAct = new Intent(AlarmRestartActivity.this, MathsDismissActivity.class);
                    dismissAct.putExtra("tone", tone);
                    dismissAct.putExtra("numOfQuest", numOfQuest);
                    dismissAct.putExtra("difficulty", difficulty);
                    dismissAct.putExtra("type", "alarm");
                    dismissAct.putExtra("id", alarmID);
                    startActivity(dismissAct);
                    finish();
                    break;
                }
                case "Shake": {
                    Intent dismissAct = new Intent(AlarmRestartActivity.this, ShakeDismissActivity.class);
                    dismissAct.putExtra("tone", tone);
                    dismissAct.putExtra("numOfQuest", numOfQuest);
                    dismissAct.putExtra("difficulty", difficulty);
                    dismissAct.putExtra("type", "alarm");
                    dismissAct.putExtra("id", alarmID);
                    startActivity(dismissAct);
                    finish();
                    break;
                }
                case "Bar Code": {
                    Intent dismissAct = new Intent(AlarmRestartActivity.this, BarCodeDismissActivity.class);
                    dismissAct.putExtra("tone", tone);
                    dismissAct.putExtra("code", numOfQuest);
                    dismissAct.putExtra("type", "alarm");
                    dismissAct.putExtra("id", alarmID);
                    startActivity(dismissAct);
                    finish();
                    break;
                }
                case "Walk": {
                    Intent dismissAct = new Intent(AlarmRestartActivity.this, WalkDismissActivity.class);
                    dismissAct.putExtra("tone", tone);
                    dismissAct.putExtra("steps", numOfQuest);
                    dismissAct.putExtra("type", "alarm");
                    dismissAct.putExtra("id", alarmID);
                    startActivity(dismissAct);
                    finish();
                    break;
                }
                case "Pattern": {
                    Intent dismissAct = new Intent(AlarmRestartActivity.this, PatternDismissActivity.class);
                    dismissAct.putExtra("tone", tone);
                    dismissAct.putExtra("quest", numOfQuest);
                    dismissAct.putExtra("format", difficulty);
                    dismissAct.putExtra("type", "alarm");
                    dismissAct.putExtra("id", alarmID);
                    startActivity(dismissAct);
                    finish();
                    break;
                }
                case "Text": {
                    Intent dismissAct = new Intent(AlarmRestartActivity.this, TextDismissActivity.class);
                    dismissAct.putExtra("tone", tone);
                    dismissAct.putExtra("quest", numOfQuest);
                    dismissAct.putExtra("format", difficulty);
                    dismissAct.putExtra("type", "alarm");
                    dismissAct.putExtra("id", alarmID);
                    startActivity(dismissAct);
                    finish();
                    break;
                }
                case "Default":{
                    if (isWuc){
                        int hour, min;
                        int s = Integer.parseInt(wuc);
                        vibrator.vibrate(pattern,-1);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.add(Calendar.MINUTE, s);
                        hour = calendar.get(Calendar.HOUR_OF_DAY);
                        min = calendar.get(Calendar.MINUTE);
                        wakeUpCheckAlarmCreation(hour,min);
                    }
                    break;
                }
            }

            if (toStop){
                resetTimer();
            }

        });
        animateClock();
    }

    private void animateClock() {
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(clock, "rotation", 0f, 20f, 0f, -20f, 0f);
        rotateAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimation.setDuration(800);
        rotateAnimation.start();
    }
/*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }*/

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!allowRecent){
            AlarmRestartActivity.this.finish();
            Intent service = new Intent("android.intent.action.MAIN");
            service.setClass(this, AlarmRestartActivity.class);
            service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(service);
        }
    }

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
        String displayTime = hours+":"+minutes+annot;
        time.setText(displayTime);
        time.setVisibility(View.VISIBLE);
        toStop = true;
        startTimer();

    }

    private void wakeUpCheckAlarmCreation(int h, int m){
        DataBaseHelper alarmDB = new DataBaseHelper(AlarmRestartActivity.this);
        alarmDB.getWritableDatabase();
        String hour = String.valueOf(h);
        String min = String.valueOf(m);
        boolean result = alarmDB.updateData(alarmID,hour,min,TITLE,method,tone,SNOOZE,"1",VOLUME,VIBRATE,wuc,"wuc");
        if (result){
            scheduleWuc(h,m);
            AlarmRestartActivity.this.finish();
        }else {
            Toast.makeText(AlarmRestartActivity.this,"Alarm not scheduled",Toast.LENGTH_LONG).show();
        }
    }

    private void scheduleWuc(int hour, int min) {
        Alarm alarmSchedule = new Alarm(
                Integer.parseInt(alarmID),
                hour,
                min,
                tone,
                "WAKE UP CHECK",
                method,
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
    private void readData() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(AlarmRestartActivity.this);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readData(alarmID,sqLiteDatabase);
        if (cursor.moveToFirst()){
            TITLE = cursor.getString(3);
            method = cursor.getString(4);
            tone = cursor.getString(5);
            SNOOZE = cursor.getString(6);
            snoozeOrder = cursor.getString(7);
            VOLUME = cursor.getString(8);
            VIBRATE = cursor.getString(9);
            wuc = cursor.getString(10);
            mType = cursor.getString(11);
            readMethod();
        }

    }

    private void startTimer() {
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

    }
    private void updateVolume() {
        audioManager  = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Integer.parseInt(VOLUME)+5, 0);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void updateTime() {
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
        time.setText(displayTime);
        time.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        countDownTimer.cancel();
        TIME_LEFT_IN_MILLIS = START_TIME_IN_MILLIS;
    }

    private void readMethod(){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readMethodData(method,db);
        if (cursor.moveToFirst()){
            numOfQuest = cursor.getString(1);
            difficulty = cursor.getString(2);
        }
    }

}