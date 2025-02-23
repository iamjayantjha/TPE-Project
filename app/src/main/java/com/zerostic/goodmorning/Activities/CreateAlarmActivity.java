package com.zerostic.goodmorning.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.SnoozeBottomSheet;
import com.zerostic.goodmorning.Application.TitleBottomSheet;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Application.WUCBottomSheet;
import com.zerostic.goodmorning.CreateAlarm.CreateAlarmViewModel;
import com.zerostic.goodmorning.CreateAlarm.TimePickerUtil;
import com.zerostic.goodmorning.Data.Alarm;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/**
 Coded by iamjayantjha
 **/

public class CreateAlarmActivity extends AppCompatActivity implements TitleBottomSheet.bottomSheetListener, SnoozeBottomSheet.snoozeBottomSheetListener,WUCBottomSheet.wucBottomSheetListener {
    private CreateAlarmViewModel createAlarmViewModel;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 2323;
    TimePicker timePicker;
    CheckBox recurring;
    Chip mon,tue,wed,thu,fri,sat,sun;
    TextView ringsIn,method,alarmToneText,snoozeTitleText,wakeUpCheckTitleText;
    ImageView editTitle;
    MaterialCardView methodCard,alarmToneCard,snoozeCard,wakeUpCheckCard, scheduleAlarm;
//    RelativeLayout recurringOptions;
    String methodText ,alarmTone,name,currentSub, act = "other";
    int alarmId = new Random().nextInt(Integer.MAX_VALUE);
    int hour,min;
    DataBaseHelper alarmDB;
    Dialog dialog;
    Button okay;
    Vibrator vibrator;
    final long[] pattern = {10, 20};
    String alarmDay, paid;
    String ringingIn,typeOfSleeper;
    String time = "0",titleVal,wuc="0";
    ImageView nextImg,wucRecommended;
    SeekBar volume;
    int vol = 10;
    String mVol;
    String isChecked = "yes";
    boolean isWUC=false,toSpeak, isSettingsAvailable,isPaid;
    String isSun,isMon,isTue,isWed,isThu,isFri,isSat, voiceSettings;
    private TextToSpeech tts;
    Alarm alarm;
    LottieAnimationView animationView;
    TextView alarmTitle;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);
        preferences = getSharedPreferences("PREFS",MODE_PRIVATE);
        ImageView sound = findViewById(R.id.sound);
        dialog = new Dialog(CreateAlarmActivity.this);
        dialog.setContentView(R.layout.permission_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        alarmTitle = findViewById(R.id.alarmTitle);
        okay = dialog.findViewById(R.id.okayBtn);
        editTitle = findViewById(R.id.editTitle);
        TextView title = dialog.findViewById(R.id.headerText);
        TextView message = dialog.findViewById(R.id.confirmText);
        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        title.setText(R.string.permission);
        message.setText(R.string.display_over_other_apps);
        createAlarmViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(CreateAlarmViewModel.class);
        timePicker = findViewById(R.id.fragment_createalarm_timePicker);
        /*NumberPicker minutePicker = (NumberPicker) timePicker.findViewById(Resources.getSystem().getIdentifier("minute", "id", "android"));
        minutePicker.setOnValueChangedListener(null);*/
        scheduleAlarm = findViewById(R.id.fragment_createalarm_scheduleAlarm);
        alarmDB = new DataBaseHelper(this);
        Utils.blackIconStatusBar(CreateAlarmActivity.this, R.color.background);
        nextImg = findViewById(R.id.nextImg);
        wucRecommended = findViewById(R.id.wucRecommended);
        animationView = findViewById(R.id.animationView);
        FirebaseAnalytics.getInstance(this);
        readPayDetails();
        readUserData();
        readUserSettings();
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS){
                int result = tts.setLanguage(Locale.ENGLISH);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                    Toast.makeText(getApplicationContext(),"Language is not supported",Toast.LENGTH_SHORT).show();
                    toSpeak = false;
                }else {
                    toSpeak = true;

                }
            }
        });
        if (typeOfSleeper.equals("3")){
            wucRecommended.setVisibility(View.VISIBLE);
        }
        recurring = findViewById(R.id.fragment_createalarm_recurring);
        mon = findViewById(R.id.mon);
        alarmToneCard = findViewById(R.id.alarmToneCard);
        snoozeCard = findViewById(R.id.snoozeCard);
        alarmToneText = findViewById(R.id.alarmToneText);
        snoozeTitleText = findViewById(R.id.snoozeTitleText);
        wakeUpCheckCard = findViewById(R.id.wakeUpCheckCard);
        wakeUpCheckTitleText = findViewById(R.id.wakeUpCheckTitleText);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        volume = findViewById(R.id.volume);
        tue = findViewById(R.id.tue);
        wed = findViewById(R.id.wed);
        thu = findViewById(R.id.thu);
        fri = findViewById(R.id.fri);
        sat = findViewById(R.id.sat);
        sun = findViewById(R.id.sun);
        ringsIn = findViewById(R.id.ringsIn);
        method = findViewById(R.id.method);
        methodCard = findViewById(R.id.methodCard);
        if (volume.getProgress()>1){
            sound.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.cardColor), android.graphics.PorterDuff.Mode.SRC_IN);
        }else {
            sound.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        hour = preferences.getInt("hour",Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        min = preferences.getInt("min",Calendar.getInstance().get(Calendar.MINUTE));
        time = preferences.getString("snooze","0");
        titleVal = preferences.getString("title","Alarm");
        ringingIn = preferences.getString("ringsIn","Rings in 24 hours");
        ringsIn.setText(ringingIn);
        //recurringOptions = findViewById(R.id.fragment_createalarm_recurring_options);
        try
        {
            methodText = getIntent().getStringExtra("method");
            alarmTone = getIntent().getStringExtra("tone");
            wuc = getIntent().getStringExtra("wuc");
            act = getIntent().getStringExtra("act");
            method.setText(methodText);
        }
        catch(Exception ex)
        {
            methodText = "";
            alarmTone = "0";
        }
        if (wuc.equals("0") || wuc.equals("Off")){
            wakeUpCheckTitleText.setText("Off");
            isWUC = false;
        }else {
            String mVal = wuc+" Minutes";
            wakeUpCheckTitleText.setText(mVal);
            wucRecommended.setVisibility(View.GONE);
            isWUC = true;
        }
        if (!act.equals("main")){
            timePicker.setHour(hour);
            timePicker.setMinute(min);
        }
        if (time.equals("0")){
            snoozeTitleText.setText("Off");
        }else {
            snoozeTitleText.setText(time+" Minutes");
        }

        if (isPaid){
            animationView.setVisibility(View.GONE);
            nextImg.setVisibility(View.VISIBLE);
            wakeUpCheckCard.setEnabled(true);
        }else{
            animationView.setVisibility(View.VISIBLE);
            nextImg.setVisibility(View.GONE);
            wakeUpCheckCard.setEnabled(false);
        }
        try
        {
            switch (alarmTone) {
                case "0":
                    alarmToneText.setText(R.string.defaultVal);
                    break;
                case "1":
                    alarmToneText.setText(R.string.alarm_clock);
                    break;
                case "2":
                    alarmToneText.setText(R.string.alarm_clock_old);
                    break;
                case "3":
                    alarmToneText.setText(R.string.beeps);
                    break;
                case "4":
                    alarmToneText.setText(R.string.siren);
                    break;
                case "5":
                    alarmToneText.setText(R.string.tuning);
                    break;
                case "6":
                    alarmToneText.setText(R.string.gi1);
                    break;
                case "7":
                    alarmToneText.setText(R.string.gi2);
                    break;
                case "8":
                    alarmToneText.setText(R.string.gi3);
                    break;
                case "9":
                    alarmToneText.setText(R.string.gi4);
                    break;
                case "10":
                    alarmToneText.setText(R.string.gi5);
                    break;
                case "11":
                    alarmToneText.setText(R.string.gi6);
                    break;
                case "12":
                    alarmToneText.setText(R.string.la1);
                    break;
                case "13":
                    alarmToneText.setText(R.string.la2);
                    break;
                case "14":
                    alarmToneText.setText(R.string.la3);
                    break;
                case "15":
                    alarmToneText.setText(R.string.la4);
                    break;
                case "16":
                    alarmToneText.setText(R.string.la5);
                    break;
                case "17":
                    alarmToneText.setText(R.string.la6);
                    break;
                case "18":
                    alarmToneText.setText(R.string.la7);
                    break;
                case "19":
                    alarmToneText.setText(R.string.la8);
                    break;
                case "20":
                    alarmToneText.setText(R.string.la9);
                    break;
                default:
                    alarmToneText.setText(R.string.nil);
                    break;
            }
        }
        catch(Exception ex)
        {
            methodText = "Default";
            alarmTone = "Default";
            alarmToneText.setText(alarmTone);
        }
        volume.setProgress(vol);

        mVol = String.valueOf(vol);
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vibrator.vibrate(pattern, -1);
                vol = progress;
                mVol = String.valueOf(progress);
                if (progress>1){
                    sound.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.cardColor), android.graphics.PorterDuff.Mode.SRC_IN);
                }else {
                    sound.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        isChecked = "yes";
        sun.setOnCheckedChangeListener((compoundButton, b) -> {
            vibrator.vibrate(pattern, -1);
            if (sun.isChecked()||sat.isChecked()){
                recurring.setText(R.string.everyday);
            }else {
                recurring.setText(R.string.weekdays);
            }
            recurring.setChecked(sun.isChecked() || mon.isChecked() || tue.isChecked() || wed.isChecked() || thu.isChecked() || fri.isChecked() || sat.isChecked());
            if (sun.isChecked()){
                alarmDay = "1";
            }



        });

        mon.setOnCheckedChangeListener((compoundButton, b) -> {
            vibrator.vibrate(pattern, -1);
            recurring.setChecked(true);
            recurring.setChecked(sun.isChecked() || mon.isChecked() || tue.isChecked() || wed.isChecked() || thu.isChecked() || fri.isChecked() || sat.isChecked());
            if (mon.isChecked()){
                alarmDay = "2";
            }

        });

        tue.setOnCheckedChangeListener((compoundButton, b) -> {
            vibrator.vibrate(pattern, -1);
                    recurring.setChecked(true);
            recurring.setChecked(sun.isChecked() || mon.isChecked() || tue.isChecked() || wed.isChecked() || thu.isChecked() || fri.isChecked() || sat.isChecked());
            if (tue.isChecked()){
                alarmDay = "3";
            }

                }
        );

        wed.setOnCheckedChangeListener((compoundButton, b) -> {
            vibrator.vibrate(pattern, -1);
            recurring.setChecked(true);
            recurring.setChecked(sun.isChecked() || mon.isChecked() || tue.isChecked() || wed.isChecked() || thu.isChecked() || fri.isChecked() || sat.isChecked());
            if (wed.isChecked()){
                alarmDay = "4";
            }
        });

        thu.setOnCheckedChangeListener((compoundButton, b) -> {
            vibrator.vibrate(pattern, -1);
            recurring.setChecked(true);
            recurring.setChecked(sun.isChecked() || mon.isChecked() || tue.isChecked() || wed.isChecked() || thu.isChecked() || fri.isChecked() || sat.isChecked());
            if (thu.isChecked()){
                alarmDay = "5";
            }
        });

        fri.setOnCheckedChangeListener((compoundButton, b) -> {
            vibrator.vibrate(pattern, -1);
            recurring.setChecked(true);
            recurring.setChecked(sun.isChecked() || mon.isChecked() || tue.isChecked() || wed.isChecked() || thu.isChecked() || fri.isChecked() || sat.isChecked());
            if (fri.isChecked()){
                alarmDay = "6";
            }
        });

        sat.setOnCheckedChangeListener((compoundButton, b) -> {
            vibrator.vibrate(pattern, -1);
            if (sun.isChecked()||sat.isChecked()){
                recurring.setText(R.string.everyday);
            }else {
                recurring.setText(R.string.weekdays);
            }
            recurring.setChecked(sun.isChecked() || mon.isChecked() || tue.isChecked() || wed.isChecked() || thu.isChecked() || fri.isChecked() || sat.isChecked());
            if (sat.isChecked()){
                alarmDay = "7";
            }
        });

        Calendar calendar = Calendar.getInstance();
        timePicker.setOnTimeChangedListener((timePicker, hr, mins) -> {
            vibrator.vibrate(pattern, -1);
            hour = hr;
            min = mins;
            SharedPreferences.Editor editor=preferences.edit();
            editor.putInt("hour",hour);
            editor.putInt("min",min);
            calendar.setTimeInMillis(System.currentTimeMillis());
            int today = calendar.get(Calendar.DAY_OF_WEEK);
            if (!sun.isChecked()&&!mon.isChecked()&&!tue.isChecked()&&!wed.isChecked()&&!thu.isChecked()&&!fri.isChecked()&&!sat.isChecked()){
                alarmDay = String.valueOf(today);
            }
            if (today == 1){
                if (sun.isChecked()){
                    alarmDay = "1";
                }else if (mon.isChecked()){
                    alarmDay = "2";
                }else if (tue.isChecked()){
                    alarmDay = "3";
                }else if (wed.isChecked()){
                    alarmDay ="4";
                }else if (thu.isChecked()){
                    alarmDay = "5";
                }else if (fri.isChecked()){
                    alarmDay = "6";
                }else if (sat.isChecked()){
                    alarmDay ="7";
                }
            }else if (today == 2){
                if (mon.isChecked()){
                    alarmDay = "2";
                }else if (tue.isChecked()){
                    alarmDay = "3";
                }else if (wed.isChecked()){
                    alarmDay ="4";
                }else if (thu.isChecked()){
                    alarmDay = "5";
                }else if (fri.isChecked()){
                    alarmDay = "6";
                }else if (sat.isChecked()){
                    alarmDay ="7";
                }else if (sun.isChecked()){
                    alarmDay = "1";
                }
            }else if (today == 3){
                if (tue.isChecked()){
                    alarmDay = "3";
                }else if (wed.isChecked()){
                    alarmDay ="4";
                }else if (thu.isChecked()){
                    alarmDay = "5";
                }else if (fri.isChecked()){
                    alarmDay = "6";
                }else if (sat.isChecked()){
                    alarmDay ="7";
                }else if (sun.isChecked()){
                    alarmDay = "1";
                }else if (mon.isChecked()){
                    alarmDay = "2";
                }
            }else if (today == 4){
                if (wed.isChecked()){
                    alarmDay ="4";
                }else if (thu.isChecked()){
                    alarmDay = "5";
                }else if (fri.isChecked()){
                    alarmDay = "6";
                }else if (sat.isChecked()){
                    alarmDay ="7";
                }else if (sun.isChecked()){
                    alarmDay ="1";
                }else if (mon.isChecked()){
                    alarmDay = "2";
                }else if (tue.isChecked()){
                    alarmDay = "3";
                }
            }else if (today == 5){
                if (thu.isChecked()){
                    alarmDay = "5";
                }else if (fri.isChecked()){
                    alarmDay = "6";
                }else if (sat.isChecked()){
                    alarmDay ="7";
                }else if (sun.isChecked()){
                    alarmDay = "1";
                }else if (mon.isChecked()){
                    alarmDay = "2";
                }else if (tue.isChecked()){
                    alarmDay = "3";
                }else if (wed.isChecked()){
                    alarmDay ="4";
                }
            }else if (today == 6){
                if (fri.isChecked()){
                    alarmDay = "6";
                }else if (sat.isChecked()){
                    alarmDay ="7";
                }else if (sun.isChecked()){
                    alarmDay = "1";
                }else if (mon.isChecked()){
                    alarmDay = "2";
                }else if (tue.isChecked()){
                    alarmDay = "3";
                }else if (wed.isChecked()){
                    alarmDay ="4";
                }else if (thu.isChecked()){
                    alarmDay = "5";
                }
            }else if (today == 7){
                if (sat.isChecked()){
                    alarmDay ="7";
                }else if (sun.isChecked()){
                    alarmDay ="1";
                }else if (mon.isChecked()){
                    alarmDay = "2";
                }else if (tue.isChecked()){
                    alarmDay = "3";
                }else if (wed.isChecked()){
                    alarmDay ="4";
                }else if (thu.isChecked()){
                    alarmDay = "5";
                }else if (fri.isChecked()){
                    alarmDay = "6";
                }
            }
            calendar.set(Calendar.HOUR_OF_DAY, hr);
            calendar.set(Calendar.MINUTE, mins);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            }
            long time = calendar.getTimeInMillis() - System.currentTimeMillis() ;
            long mills = Math.abs(time);
            int hours = (int) (mills / (1000 * 60 * 60));
            int minutes = (int) ((mills / (1000 * 60)) % 60);
            if (alarmDay.equals(String.valueOf(today))){
                if (hours == 0){
                    if (minutes != 0){
                        if (minutes > 1){
                            ringingIn = "Rings in "+minutes+" minutes";
                            ringsIn.setText(ringingIn);
                        }else if (minutes == 1){
                            ringingIn = "Rings in "+minutes+" minutes";
                            ringsIn.setText(ringingIn);
                        }
                    }else {
                        ringingIn = "Rings in less than a minute.";
                        ringsIn.setText(ringingIn);
                    }
                }else if (hours == 1){
                    if (minutes == 1){
                        ringingIn = "Rings in "+hours+" hour"+" and "+minutes+" minute";
                        ringsIn.setText(ringingIn);
                    }else if (minutes > 1){
                        ringingIn = "Rings in "+hours+" hour"+" and "+minutes+" minutes";
                        ringsIn.setText(ringingIn);
                    }else {
                        ringingIn = "Rings in "+hours+" hour";
                        ringsIn.setText(ringingIn);
                    }
                }

                else {
                    if (minutes == 1){
                        ringingIn = "Rings in "+hours+" hours"+" and "+minutes+" minute";
                        ringsIn.setText(ringingIn);
                    }else if (minutes > 1){
                        ringingIn = "Rings in "+hours+" hours"+" and "+minutes+" minutes";
                        ringsIn.setText(ringingIn);
                    }else {
                        ringingIn = "Rings in "+hours+" hours";
                        ringsIn.setText(ringingIn);
                    }
                }
            }else {
                int a;
                int scheduledDay = Integer.parseInt(alarmDay);
                int remainingDays = today - scheduledDay;
                if (remainingDays<1){
                    a = (remainingDays*remainingDays)/-remainingDays;
                    if (a == 1){
                        ringingIn = "Rings in 1 day";
                    }else {
                        ringingIn = "Rings in "+a+" days";
                    }
                }else {

                    a= 7-remainingDays;
                    ringingIn = "Rings in "+a+" days";
                }
                ringsIn.setText(ringingIn);
            }
            editor.putString("ringsIn",ringingIn);
            editor.apply();

        });

        methodCard.setOnClickListener(view1 -> {
            vibrator.vibrate(pattern, -1);
            Intent methodIntent = new Intent(CreateAlarmActivity.this, MethodActivity.class);
            methodIntent.putExtra("tone",alarmTone);
            methodIntent.putExtra("method",method.getText().toString());
            methodIntent.putExtra("wuc",wuc);
            methodIntent.putExtra("act","create");
            methodIntent.putExtra("payStat",paid);
            startActivity(methodIntent);
        });

        alarmToneCard.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            Intent alarmToneIntent = new Intent(CreateAlarmActivity.this,AlarmToneActivity.class);
            alarmToneIntent.putExtra("method",methodText);
            alarmToneIntent.putExtra("tone",alarmTone);
            alarmToneIntent.putExtra("wuc",wuc);
            alarmToneIntent.putExtra("act","create");
            alarmToneIntent.putExtra("payStat",paid);
            startActivity(alarmToneIntent);
        });

        snoozeCard.setOnClickListener(v -> {
            if (time.isEmpty()){
                time = "";
            }
            vibrator.vibrate(pattern, -1);
            SharedPreferences preferences = getSharedPreferences("snooze",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("snooze",time);
            editor.apply();
            SnoozeBottomSheet snoozeBottomSheet = new SnoozeBottomSheet();
            snoozeBottomSheet.show(getSupportFragmentManager(),"snoozeBottomSheet");
        });

        wakeUpCheckCard.setOnClickListener(v -> {
            if (wuc.isEmpty() || wuc.isEmpty()){
                wuc = "";
            }
            vibrator.vibrate(pattern, -1);
            SharedPreferences preferences=getSharedPreferences("wucTime",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("wucTime",wuc);
            editor.apply();
            WUCBottomSheet wucBottomSheet = new WUCBottomSheet();
            wucBottomSheet.show(getSupportFragmentManager(),"wucBottomSheet");
        });

        alarmTitle.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            SharedPreferences preferences=getSharedPreferences("title",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("title",alarmTitle.getText().toString());
            editor.apply();
            TitleBottomSheet titleBottomSheet = new TitleBottomSheet();
            titleBottomSheet.show(getSupportFragmentManager(),"titleBottomSheet");
        });
        editTitle.setOnClickListener(v->{
            vibrator.vibrate(pattern, -1);
            SharedPreferences preferences=getSharedPreferences("title",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("title",alarmTitle.getText().toString());
            editor.apply();
            TitleBottomSheet titleBottomSheet = new TitleBottomSheet();
            titleBottomSheet.show(getSupportFragmentManager(),"titleBottomSheet");
        });

        scheduleAlarm.setOnClickListener(v -> {
            String speakText;
            vibrator.vibrate(pattern, -1);
            try{
                Toast.makeText(getApplicationContext(),ringingIn,Toast.LENGTH_SHORT).show();
                if (ringsIn.getText().toString().equals("Rings in 24 hours")){
                    speakText = name+" your alarm Rings in 24 hours";
                    if (voiceSettings.equals("1")|| voiceSettings.isEmpty()){
                        say(speakText);
                    }
                }else {
                    speakText = name+" your alarm "+ringingIn;
                    if (voiceSettings.equals("1")|| voiceSettings.isEmpty()){
                        say(speakText);
                    }
                }
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            saveRecurringData();
            saveData();
        });



    }

    private void say(String speakText) {
        tts.setPitch(1.005f);
        tts.setSpeechRate(1);
        tts.speak(speakText, TextToSpeech.QUEUE_ADD, null);
    }

    private void saveRecurringData() {
        String id = String.valueOf(alarmId);
        if (sun.isChecked()){
            isSun = "yes";
        }else {
            isSun = "no";
        }if (mon.isChecked()){
            isMon = "yes";
        }else {
            isMon = "no";
        }if (tue.isChecked()){
            isTue = "yes";
        }else {
            isTue = "no";
        }if (wed.isChecked()){
            isWed = "yes";
        }else {
            isWed = "no";
        }if (thu.isChecked()){
            isThu = "yes";
        }else {
            isThu = "no";
        }if (fri.isChecked()){
            isFri = "yes";
        }else {
            isFri = "no";
        }if (sat.isChecked()){
            isSat = "yes";
        }else {
            isSat = "no";
        }

        DataBaseHelper db = new DataBaseHelper(this);
        db.getWritableDatabase();
        db.insertRecurringData(id, isSun, isMon, isTue, isWed, isThu, isFri, isSat);
    }

    private void checkDeviceSpecificPermission(){
        String manufacturer = Build.MANUFACTURER;
        if (manufacturer.equalsIgnoreCase("xiaomi")) {
            // this is Xiaomi device
            // Toast.makeText(getApplicationContext(), "Xiaomi", Toast.LENGTH_SHORT).show();
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.permission_dialog);
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            TextView header = dialog.findViewById(R.id.headerText);
            TextView message = dialog.findViewById(R.id.confirmText);
            header.setText(R.string.permission);
            message.setText("Xiaomi devices require you to manually enable the permission for Good Morning to work properly. Please enable the permission in the app settings.");
            header.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
            Button okay = dialog.findViewById(R.id.okayBtn);
            dialog.show();
            okay.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                SharedPreferences preferences1=getSharedPreferences("PREFS",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences1.edit();
                editor.putBoolean("permissionCheck",true);
                editor.apply();
                dialog.dismiss();
                Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter",
                        "com.miui.permcenter.permissions.PermissionsEditorActivity");
                intent.putExtra("extra_pkgname", getPackageName());
                startActivity(intent);
            });

        }
    }


    private void saveData(){
        String snoozeTime;
        String snoozeOrder;
        if (snoozeTitleText.getText().toString().isEmpty() || snoozeTitleText.getText().toString().equals("") || snoozeTitleText.getText().equals("Off")){
            snoozeTime = "0";
            snoozeOrder = "2";
        }else {
            snoozeTime = time;
            snoozeOrder = "1";
        }
        String id = String.valueOf(alarmId);

        hour = TimePickerUtil.getTimePickerHour(timePicker);
        min = TimePickerUtil.getTimePickerMinute(timePicker);
        if (wuc.equals("Off")){
            wuc = "0";
        }

        boolean result = alarmDB.insertData(id,String.valueOf(hour),String.valueOf(min),alarmTitle.getText().toString(),method.getText().toString(),alarmTone,snoozeTime,snoozeOrder,mVol,isChecked,wuc,"scheduled");
        if (result){
            insertHrMin();
        }
    }

    private void insertHrMin(){
        String id = String.valueOf(alarmId);
        boolean result = alarmDB.insertHrMin(id,String.valueOf(hour),String.valueOf(min));
        if (result){
            scheduleAlarm();
            CreateAlarmActivity.this.finish();
        }else {
            Toast.makeText(CreateAlarmActivity.this,"Alarm not scheduled",Toast.LENGTH_LONG).show();
        }
    }

    private void scheduleAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, TimePickerUtil.getTimePickerHour(timePicker));
        calendar.set(Calendar.MINUTE, TimePickerUtil.getTimePickerMinute(timePicker));
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        alarm = new Alarm(
                alarmId,
                TimePickerUtil.getTimePickerHour(timePicker),
                TimePickerUtil.getTimePickerMinute(timePicker),
                alarmToneText.getText().toString(),
                alarmTitle.getText().toString(),
                method.getText().toString(),
                calendar.getTimeInMillis(),
                true,
                recurring.isChecked(),
                mon.isChecked(),
                tue.isChecked(),
                wed.isChecked(),
                thu.isChecked(),
                fri.isChecked(),
                sat.isChecked(),
                sun.isChecked(),
                isWUC
        );
        createAlarmViewModel.insert(alarm);
        if (recurring.isChecked()){
           getDates();
        }else {
            alarm.schedule(getApplicationContext());
        }
    }

    public void getDates() {
            String todaysDate = date();
        month();
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            String day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
           // Toast.makeText(getApplicationContext(),day,Toast.LENGTH_SHORT).show();
            if (day.equals("Monday") && mon.isChecked()){
                alarm.schedule(getApplicationContext());
            }else if (day.equals("Tuesday") && tue.isChecked()){
                alarm.schedule(getApplicationContext());
            }else if (day.equals("Wednesday") && wed.isChecked()){
                alarm.schedule(getApplicationContext());
            }else if (day.equals("Thursday") && thu.isChecked()){
                alarm.schedule(getApplicationContext());
            }else if (day.equals("Friday") && fri.isChecked()){
                alarm.schedule(getApplicationContext());
            }else if (day.equals("Saturday") && sat.isChecked()){
                alarm.schedule(getApplicationContext());
            }else if (day.equals("Sunday") && sun.isChecked()){
                alarm.schedule(getApplicationContext());
            }
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

            if (day.equals("Monday") && !mon.isChecked()){
                if (tue.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(tuesday));
                }else if (wed.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(wednesday));
                }else if (thu.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(thursday));
                }else if (fri.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(friday));
                }else if (sat.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(saturday));
                }else if (sun.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(sunday));
                }
            }else if (day.equals("Tuesday") && !tue.isChecked()){
                if (wed.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(wednesday));
                }else if (thu.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(thursday));
                }else if (fri.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(friday));
                }else if (sat.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(saturday));
                }else if (sun.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(sunday));
                }else if (mon.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(monday));
                }
            }else if (day.equals("Wednesday") && !wed.isChecked()){
                if (thu.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(thursday));
                }else if (fri.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(friday));
                }else if (sat.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(saturday));
                }else if (sun.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(sunday));
                }else if (mon.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(monday));
                }else if (tue.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(tuesday));
                }
            }else if (day.equals("Thursday") && !thu.isChecked()){
                if (fri.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(friday));
                }else if (sat.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(saturday));
                }else if (sun.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(sunday));
                }else if (mon.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(monday));
                }else if (tue.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(tuesday));
                }else if (wed.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(wednesday));
                }
            }else if (day.equals("Friday") && !fri.isChecked()){
                if (sat.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(saturday));
                }else if (sun.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(sunday));
                }else if (mon.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(monday));
                }else if (tue.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(tuesday));
                }else if (wed.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(wednesday));
                }else if (thu.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(thursday));
                }
            }else if (day.equals("Saturday") && !sat.isChecked()){
                if (sun.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(sunday));
                }else if (mon.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(monday));
                }else if (tue.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(tuesday));
                }else if (wed.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(wednesday));
                }else if (thu.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(thursday));
                }else if (fri.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(friday));
                }
            }else if (day.equals("Sunday") && !sun.isChecked()){
                if (mon.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(monday));
                }else if (tue.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(tuesday));
                }else if (wed.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(wednesday));
                }else if (thu.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(thursday));
                }else if (fri.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(friday));
                }else if (sat.isChecked()){
                    alarm.scheduleRecurring(getApplicationContext(),Integer.parseInt(saturday));
                }
            }
    }

    @Override
    public void onClick(String text) {
        alarmTitle.setText(text);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Calendar rightNow = Calendar.getInstance();
        hour = rightNow.get(Calendar.HOUR_OF_DAY);
        min = rightNow.get(Calendar.MINUTE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!Settings.canDrawOverlays(this)) {
                dialog.show();
                okay.setOnClickListener(v -> {
                    vibration();
                    Intent permission = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    ActivityCompat.startActivityForResult(this,permission,ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE,Bundle.EMPTY);
                    dialog.dismiss();
                });
            }
        }
        SharedPreferences.Editor editor= preferences.edit();
        editor.putInt("hour",timePicker.getHour());
        editor.putInt("min",timePicker.getMinute());
        editor.apply();
        boolean permissionCheck = preferences.getBoolean("permissionCheck", false);
        if (!permissionCheck){
            checkDeviceSpecificPermission();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM}, 100);
            }
        }
    }

    private void vibration() {
        vibrator.vibrate(pattern, -1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        CreateAlarmActivity.this.finish();
    }

    private void readUserData(){
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = db.readUserData("1",sqLiteDatabase);
        if (cursor.moveToFirst()){
            name = cursor.getString(1);
            paid = "y";
            /*if (paid.equals("n")){
                if (currentSub.equals("free_trial")){
                    paid = "y";
                    isPaid = true;
                }else{
                    isPaid = false;
                }
            }else {
                isPaid = true;
            }*/
            isPaid = true;
        }
    }

    private void readPayDetails() {
        UserDatabaseHelper dataBaseHelper = new UserDatabaseHelper(this);
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readPaymentData("1", db);
        if (cursor.moveToFirst()) {
            currentSub = cursor.getString(1);
        }
    }

    private void readUserSettings(){
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = db.readUserSettings("1",sqLiteDatabase);
        if (cursor.moveToFirst()){
            voiceSettings = cursor.getString(2);
            typeOfSleeper = cursor.getString(4);
            isSettingsAvailable = true;
        }
        else {
            isSettingsAvailable = false;
        }
    }
    public static String month(){
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
        return simpleDateFormat.format(c);
    }

    public static String date(){
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
        return simpleDateFormat.format(c);
    }

    @Override
    public void onSaveClick(String text) {
        time = text;
        String val;
        if (time.equals("0")){
            val = "Off";
        }else {
            val = text+" Minutes";
        }
        snoozeTitleText.setText(val);
    }

    @Override
    public void onWUCSaveClick(String text) {
        wuc = text;
        String val;
        if (wuc.equals("0")){
            val = "Off";
            isWUC = false;
        }else {
            val = text+" Minutes";
            isWUC = true;
        }
        wakeUpCheckTitleText.setText(val);
    }
}