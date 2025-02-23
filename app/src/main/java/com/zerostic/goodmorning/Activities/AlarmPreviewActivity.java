package com.zerostic.goodmorning.Activities;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
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

import java.util.Calendar;

/**
 Coded by iamjayantjha
 **/

public class AlarmPreviewActivity extends AppCompatActivity {
    String tone,numOfQuest,difficulty,method,wuc,mType,alarmID,from,TITLE,SNOOZE,snoozeOrder,VOLUME,VIBRATE;
    int integerTone;
    private MediaPlayer mediaPlayer;
    SlideToActView dismissBtn;
    MaterialCardView stopPreview;
    TextView title, time;
    ImageView clock;
    int hours;
    boolean isWuc = false, alarmRestarted = true, toStop = false;
    int minutes;
    AudioManager audioManager;
    private CountDownTimer countDownTimer;
    private static final long START_TIME_IN_MILLIS = 3600000;
    private long TIME_LEFT_IN_MILLIS = START_TIME_IN_MILLIS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_preview);
        Utils.blackIconStatusBar(AlarmPreviewActivity.this, R.color.background);
        clock = findViewById(R.id.clockImg);
        stopPreview = findViewById(R.id.stopPreview);
        title = findViewById(R.id.title);
        time = findViewById(R.id.time);
        tone = getIntent().getStringExtra("tone");
        numOfQuest = getIntent().getStringExtra("numOfQuest");
        difficulty = getIntent().getStringExtra("difficulty");
        method = getIntent().getStringExtra("method");
        mType = getIntent().getStringExtra("type");
        alarmID = getIntent().getStringExtra("id");
        from = getIntent().getStringExtra("from");
        try {
            if (mType.equals("preview")){
                from = "preview";
                stopPreview.setVisibility(View.VISIBLE);
                alarmRestarted = false;
            }
        }catch (Exception e){
            mType = "alarm";
            from =  "wuc";
        }
        readData();
        if (from.equals("wuc")){
            wuc = getIntent().getStringExtra("wuc");
            isWuc = (getIntent().getStringExtra("isWuc").equals("wuc")) && (!wuc.equals("0")) && (!wuc.equals("1"));
        }
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        if (tone.equals("")){
            integerTone = 0;
        }else {
            integerTone = Integer.parseInt(tone);
        }
        dismissBtn = findViewById(R.id.dismissBtn);
        int resID;
        if (integerTone == 0){
            resID = R.raw.default_tone;
        }else if (integerTone == 1){
            resID = R.raw.alarm_clock;
        }else if (integerTone == 2){
            resID = R.raw.alarm_clock_old;
        }else if (integerTone == 3){
            resID = R.raw.beeps;
        }else if (integerTone == 4){
            resID = R.raw.siren;
        }else if (integerTone == 5){
            resID = R.raw.tuning;
        }else if (integerTone == 6){
            resID = R.raw.gradually_increasing1;
        }else if (integerTone == 7){
            resID = R.raw.gradually_increasing2;
        }else if (integerTone == 8){
            resID = R.raw.gradually_increasing3;
        }else if (integerTone == 9){
            resID = R.raw.gradually_increasing4;
        }else if (integerTone == 10){
            resID = R.raw.gradually_increasing5;
        }else if (integerTone == 11){
            resID = R.raw.gradually_increasing6;
        }else if (integerTone == 12){
            resID = R.raw.loud_alarm1;
        }else if (integerTone == 13){
            resID = R.raw.loud_alarm2;
        }else if (integerTone == 14){
            resID = R.raw.loud_alarm3;
        }else if (integerTone == 15){
            resID = R.raw.loud_alarm4;
        }else if (integerTone == 16){
            resID = R.raw.loud_alarm5;
        }else if (integerTone == 17){
            resID = R.raw.loud_alarm6;
        }else if (integerTone == 18){
            resID = R.raw.loud_alarm7;
        }else {
            resID = R.raw.default_tone;
        }
        if (alarmRestarted){
            title.setText(TITLE);
            setTime();
        }
        SharedPreferences preferences = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        int newVolume = preferences.getInt("volume", 15);
        mediaPlayer= MediaPlayer.create(this, resID);
        audioManager  = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer.setLooping(true);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
        mediaPlayer.start();
        stopPreview.setOnClickListener(v -> {
          vibrator.vibrate(pattern,-1);
          AlarmPreviewActivity.this.finish();
        });
        dismissBtn.setOnSlideCompleteListener(slideToActView -> {
            vibrator.vibrate(pattern, -1);
            mediaPlayer.stop();
            switch (method) {
                case "Maths": {
                    Intent dismissAct = new Intent(AlarmPreviewActivity.this, MathsDismissActivity.class);
                    dismissAct.putExtra("tone", tone);
                    dismissAct.putExtra("numOfQuest", numOfQuest);
                    dismissAct.putExtra("difficulty", difficulty);
                    dismissAct.putExtra("type", mType);
                    dismissAct.putExtra("id", alarmID);
                    startActivity(dismissAct);
                    finish();
                    break;
                }
                case "Shake": {
                    Intent dismissAct = new Intent(AlarmPreviewActivity.this, ShakeDismissActivity.class);
                    dismissAct.putExtra("tone", tone);
                    dismissAct.putExtra("numOfQuest", numOfQuest);
                    dismissAct.putExtra("difficulty", difficulty);
                    dismissAct.putExtra("type", mType);
                    dismissAct.putExtra("id", alarmID);
                    startActivity(dismissAct);
                    finish();
                    break;
                }
                case "Bar Code": {
                    Intent dismissAct = new Intent(AlarmPreviewActivity.this, BarCodeDismissActivity.class);
                    dismissAct.putExtra("tone", tone);
                    dismissAct.putExtra("code", numOfQuest);
                    dismissAct.putExtra("type", mType);
                    dismissAct.putExtra("id", alarmID);
                    startActivity(dismissAct);
                    finish();
                    break;
                }
                case "Walk": {
                    Intent dismissAct = new Intent(AlarmPreviewActivity.this, WalkDismissActivity.class);
                    dismissAct.putExtra("tone", tone);
                    dismissAct.putExtra("steps", numOfQuest);
                    dismissAct.putExtra("type", mType);
                    dismissAct.putExtra("id", alarmID);
                    startActivity(dismissAct);
                    finish();
                    break;
                }
                case "Pattern": {
                    Intent dismissAct = new Intent(AlarmPreviewActivity.this, PatternDismissActivity.class);
                    dismissAct.putExtra("tone", tone);
                    dismissAct.putExtra("quest", numOfQuest);
                    dismissAct.putExtra("format", difficulty);
                    dismissAct.putExtra("type", mType);
                    dismissAct.putExtra("id", alarmID);
                    startActivity(dismissAct);
                    finish();
                    break;
                }
                case "Text": {
                    Intent dismissAct = new Intent(AlarmPreviewActivity.this, TextDismissActivity.class);
                    dismissAct.putExtra("tone", tone);
                    dismissAct.putExtra("quest", numOfQuest);
                    dismissAct.putExtra("format", difficulty);
                    dismissAct.putExtra("type", mType);
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
        //animateClock();
        Animation animation =  AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_jump);
        clock.startAnimation(animation);
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

/*    private void animateClock() {
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(clock, "rotation", 0f, 20f, 0f, -20f, 0f);
        rotateAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimation.setDuration(800);
        rotateAnimation.start();
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
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
        DataBaseHelper alarmDB = new DataBaseHelper(AlarmPreviewActivity.this);
        alarmDB.getWritableDatabase();
        String hour = String.valueOf(h);
        String min = String.valueOf(m);
        boolean result = alarmDB.updateData(alarmID,hour,min,TITLE,method,tone,"0","2",VOLUME,VIBRATE,wuc,"wuc");
        if (result){
            scheduleWuc(h,m);
            AlarmPreviewActivity.this.finish();
        }else {
            Toast.makeText(AlarmPreviewActivity.this,"Alarm not scheduled",Toast.LENGTH_LONG).show();
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
        DataBaseHelper dataBaseHelper = new DataBaseHelper(AlarmPreviewActivity.this);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readData(alarmID,sqLiteDatabase);
        if (cursor.moveToFirst()){
            TITLE = cursor.getString(3);
            SNOOZE = cursor.getString(6);
            snoozeOrder = cursor.getString(7);
            VOLUME = cursor.getString(8);
            VIBRATE = cursor.getString(9);
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
}