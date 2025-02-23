package com.zerostic.goodmorning.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.SnoozeBottomSheet;
import com.zerostic.goodmorning.Application.TitleBottomSheet;
import com.zerostic.goodmorning.Application.WUCBottomSheet;
import com.zerostic.goodmorning.BroadCastReceiver.AlarmBroadcastReceiver;
import com.zerostic.goodmorning.CreateAlarm.CreateAlarmViewModel;
import com.zerostic.goodmorning.CreateAlarm.TimePickerUtil;
import com.zerostic.goodmorning.Data.Alarm;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;

import java.util.Calendar;
import java.util.Locale;

public class AlarmEditActivity extends AppCompatActivity implements TitleBottomSheet.bottomSheetListener, SnoozeBottomSheet.snoozeBottomSheetListener, WUCBottomSheet.wucBottomSheetListener {
    String alarmID, currentSub, name, voiceSettings,paid,HOUR,MINUTE,TITLE,METHOD,ALARM_TONE,SNOOZE,snoozeOrder,VOLUME,VIBRATE,WUC,TYPE,act,tone,mVol,alarmDay,ringingIn,oldHour,oldMin;
    TextView titleText,method,alarmToneText,wakeUpCheckTitleText,snoozeTitleText,ringsIn,type;
    MaterialCardView methodCard,wakeUpCheckCard;
    TimePicker timePicker;
    private CreateAlarmViewModel createAlarmViewModel;
    SeekBar volume;
    Vibrator vibrator;
    CheckBox recurring;
    ImageView nextImg;
    final long[] pattern = {40,80};
    Chip mon,tue,wed,thu,fri,sat,sun;
    int hour,min;
    String isSun,isMon,isTue,isWed,isThu,isFri,isSat;
    boolean isWUC=false, toSpeak, isSettingsAvailable,isRecurring = false,isPaid = true;
    private TextToSpeech tts;
    LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_edit);
        ImageView sound = findViewById(R.id.sound);
        createAlarmViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(CreateAlarmViewModel.class);
        alarmID = getIntent().getStringExtra("id");
        act = getIntent().getStringExtra("act");
        readPayDetails();
        readUserData();
        readUserSettings();
        titleText = findViewById(R.id.alarmTitle);
        method = findViewById(R.id.method);
        mon = findViewById(R.id.mon);
        tue = findViewById(R.id.tue);
        wed = findViewById(R.id.wed);
        thu = findViewById(R.id.thu);
        fri = findViewById(R.id.fri);
        sat = findViewById(R.id.sat);
        sun = findViewById(R.id.sun);
        type = findViewById(R.id.type);
        wakeUpCheckCard = findViewById(R.id.wakeUpCheckCard);
        recurring = findViewById(R.id.fragment_createalarm_recurring);
        alarmToneText = findViewById(R.id.alarmToneText);
        ringsIn = findViewById(R.id.ringsIn);
        wakeUpCheckTitleText = findViewById(R.id.wakeUpCheckTitleText);
        methodCard = findViewById(R.id.methodCard);
        timePicker = findViewById(R.id.fragment_createalarm_timePicker);
        snoozeTitleText = findViewById(R.id.snoozeTitleText);
        volume = findViewById(R.id.volume);
        if (volume.getProgress()>1){
            sound.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.cardColor), android.graphics.PorterDuff.Mode.SRC_IN);
        }else {
            sound.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        readData();
        readRecurringData();
        readHrMin();
        String value;
        nextImg = findViewById(R.id.nextImg);
        animationView = findViewById(R.id.animationView);
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
        if (SNOOZE.equals("0")){
            value = "Off";
        }else {
            value = SNOOZE+" Minutes";
        }
        snoozeTitleText.setText(value);
        mVol = VOLUME;
        tone = ALARM_TONE;
        titleText.setText(TITLE);
        if (act.equals("main")){
            setValue(ALARM_TONE);
        }else {
            tone = getIntent().getStringExtra("tone");
            METHOD = getIntent().getStringExtra("method");
            WUC = getIntent().getStringExtra("wuc");
            setValue(tone);
        }
        method.setText(METHOD);
        if (isPaid){
            animationView.setVisibility(View.GONE);
            nextImg.setVisibility(View.VISIBLE);
            wakeUpCheckCard.setEnabled(true);
        }else{
            animationView.setVisibility(View.VISIBLE);
            nextImg.setVisibility(View.GONE);
            wakeUpCheckCard.setEnabled(false);
        }
        if (WUC.equals("0")){
            wakeUpCheckTitleText.setText(R.string.off);
        }else {
            String wucVal = WUC+" Minutes";
            wakeUpCheckTitleText.setText(wucVal);
        }
        if (isSun.equals("yes")){
            sun.setChecked(true);
            isRecurring = true;
        }
        if (isMon.equals("yes")){
            mon.setChecked(true);
            isRecurring = true;
        }
        if (isTue.equals("yes")){
            tue.setChecked(true);
            isRecurring = true;
        }
        if (isWed.equals("yes")){
            wed.setChecked(true);
            isRecurring = true;
        }
        if (isThu.equals("yes")){
            thu.setChecked(true);
            isRecurring = true;
        }
        if (isFri.equals("yes")){
            fri.setChecked(true);
            isRecurring = true;
        }
        if (isSat.equals("yes")){
            sat.setChecked(true);
            isRecurring = true;
        }

        isWUC = !WUC.equals("Off") && !WUC.equals("0");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (act.equals("main")){
               timePicker.setHour(Integer.parseInt(oldHour));
               timePicker.setMinute(Integer.parseInt(oldMin));
            }else{
                SharedPreferences preferences2 = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                hour = preferences2.getInt("hour",Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                min = preferences2.getInt("min",Calendar.getInstance().get(Calendar.MINUTE));
                timePicker.setHour(hour);
                timePicker.setMinute(min);
            }

        }

        volume.setProgress(Integer.parseInt(VOLUME));
        VIBRATE = "yes";
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
            SharedPreferences preferences1=getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences1.edit();
            editor.putInt("hour",hour);
            editor.putInt("min",min);
            editor.apply();
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



        });

        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vibrator.vibrate(pattern, -1);
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences1=getSharedPreferences("PREFS",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences1.edit();
        editor.putInt("hour",TimePickerUtil.getTimePickerHour(timePicker));
        editor.putInt("min",TimePickerUtil.getTimePickerMinute(timePicker));
        editor.apply();
    }

    private void readHrMin() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readHrMin(alarmID, db);
        if (cursor.moveToFirst()) {
            oldHour = cursor.getString(1);
            oldMin = cursor.getString(2);
        }
        if ((Integer.parseInt(HOUR)>Integer.parseInt(oldHour)) || (Integer.parseInt(MINUTE)>Integer.parseInt(oldMin))){
            type.setVisibility(View.VISIBLE);
            String heading = "Alarm will ring ";
            if (TYPE.equals("wuc")){
                heading = "Wake Up Check";
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(HOUR));
            calendar.set(Calendar.MINUTE, Integer.parseInt(MINUTE));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (calendar.getTimeInMillis() >= System.currentTimeMillis()) {
                calendar.setTimeInMillis(System.currentTimeMillis());
                int minute = calendar.get(Calendar.MINUTE);
                String msg;
            /*if ((Integer.parseInt(HOUR)>Integer.parseInt(oldHour))){

            }else{
                msg = heading+" in "+(Integer.parseInt(MINUTE)-Integer.parseInt(oldMin))+" minutes";
            }*/
                int val  =(Integer.parseInt(MINUTE)-minute);
                if (val>1){
                    msg = heading+" in "+val+" minutes";
                }else {
                    msg = heading+" in "+val+" minute";
                }
                type.setText(msg);
            }else {
                type.setVisibility(View.GONE);
            }
        }
    }

    private void setValue(String tone) {
        switch (tone) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        AlarmEditActivity.this.finish();
    }

    public void onMethodClick(View v){
        vibrator.vibrate(pattern, -1);
        Intent methodIntent = new Intent(AlarmEditActivity.this, MethodActivity.class);
        methodIntent.putExtra("tone",tone);
        methodIntent.putExtra("method",METHOD);
        methodIntent.putExtra("wuc",WUC);
        methodIntent.putExtra("act","edit");
        methodIntent.putExtra("id",alarmID);
        methodIntent.putExtra("payStat",paid);
        startActivity(methodIntent);
    }

    private void readData() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(AlarmEditActivity.this);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readData(alarmID,sqLiteDatabase);
        if (cursor.moveToFirst()){
            HOUR = cursor.getString(1);
            MINUTE = cursor.getString(2);
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

    public void onAlarmToneClick(View view){
        vibrator.vibrate(pattern, -1);
        Intent alarmToneIntent = new Intent(AlarmEditActivity.this,AlarmToneActivity.class);
        alarmToneIntent.putExtra("method",METHOD);
        alarmToneIntent.putExtra("tone",tone);
        alarmToneIntent.putExtra("wuc",WUC);
        alarmToneIntent.putExtra("act","edit");
        alarmToneIntent.putExtra("id",alarmID);
        alarmToneIntent.putExtra("payStat",paid);
        startActivity(alarmToneIntent);
    }

    public void scheduleUpdatedAlarm(View view){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, TimePickerUtil.getTimePickerHour(timePicker));
        calendar.set(Calendar.MINUTE, TimePickerUtil.getTimePickerMinute(timePicker));
        vibrator.vibrate(pattern,-1);
        isRecurring = sun.isChecked() || mon.isChecked() || tue.isChecked() || wed.isChecked() || thu.isChecked() || fri.isChecked() || sat.isChecked();
        Alarm alarm = new Alarm(
                Integer.parseInt(alarmID),
                TimePickerUtil.getTimePickerHour(timePicker),
                TimePickerUtil.getTimePickerMinute(timePicker),
                tone,
                TITLE,
                METHOD,
                calendar.getTimeInMillis(),
                true,
                isRecurring,
                mon.isChecked(),
                tue.isChecked(),
                wed.isChecked(),
                thu.isChecked(),
                fri.isChecked(),
                sat.isChecked(),
                sun.isChecked(),
                isWUC);
        createAlarmViewModel.update(alarm);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmBroadcastReceiver.class);
        PendingIntent alarmPendingIntent;// = PendingIntent.getBroadcast(this, Integer.parseInt(alarmID), intent, 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmPendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(alarmID), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }else{
            alarmPendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(alarmID), intent, PendingIntent.FLAG_IMMUTABLE);
        }
        alarmManager.cancel(alarmPendingIntent);
        alarm.schedule(getApplicationContext());
        updateAlarmData();
        saveRecurringData();
        String speakText;

        try{
            if (ringsIn.getText().toString().equals("")){
                speakText = name+" your alarm is scheduled";
                Toast.makeText(getApplicationContext(),speakText,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),ringingIn,Toast.LENGTH_SHORT).show();
                speakText = name+" your alarm "+ringingIn;
            }
            if (voiceSettings.equals("1")|| voiceSettings.equals("")){
                say(speakText);
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Alarm Scheduled",Toast.LENGTH_SHORT).show();
        }
        AlarmEditActivity.this.finish();
    }

    private void updateAlarmData(){
        DataBaseHelper alarmDB = new DataBaseHelper(AlarmEditActivity.this);
        alarmDB.getWritableDatabase();
        String hour = String.valueOf(TimePickerUtil.getTimePickerHour(timePicker));
        String min = String.valueOf(TimePickerUtil.getTimePickerMinute(timePicker));
        insertHrMin(hour,min);
        if (SNOOZE.equals("0")){
            snoozeOrder = "2";
        }else {
            snoozeOrder = "1";
        }
        alarmDB.updateData(alarmID, hour, min, TITLE, METHOD, tone, SNOOZE, snoozeOrder, mVol, VIBRATE, WUC, "scheduled");
    }

    @Override
    public void onSaveClick(String text) {
        SNOOZE = text;
        String val;
        if (text.equals("0")){
            val = "Off";
        }else {
            val = text+" Minutes";
        }
        snoozeTitleText.setText(val);
    }

    @Override
    public void onClick(String text) {
        TITLE = text;
        titleText.setText(text);
    }

    private void readPayDetails() {
        UserDatabaseHelper dataBaseHelper = new UserDatabaseHelper(this);
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readPaymentData("1", db);
        if (cursor.moveToFirst()) {
            currentSub = cursor.getString(1);
        }
    }

    public void titleChange(View view){
        vibrator.vibrate(pattern, -1);
        SharedPreferences preferences=getSharedPreferences("title",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("title",titleText.getText().toString());
        editor.apply();
        TitleBottomSheet titleBottomSheet = new TitleBottomSheet();
        titleBottomSheet.show(getSupportFragmentManager(),"titleBottomSheet");
    }

    public void snoozeVal(View view){
        vibrator.vibrate(pattern, -1);
        SharedPreferences preferences=getSharedPreferences("snooze",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("snooze",SNOOZE);
        editor.apply();
        SnoozeBottomSheet snoozeBottomSheet = new SnoozeBottomSheet();
        snoozeBottomSheet.show(getSupportFragmentManager(),"titleBottomSheet");
    }

    private void insertHrMin(String h, String m){
        DataBaseHelper alarmDB = new DataBaseHelper(this);
        alarmDB.getWritableDatabase();
        alarmDB.updateHrMin(alarmID, h, m);
    }

    private void saveRecurringData() {
        String id = String.valueOf(alarmID);
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
        db.updateRecurringData(id, isSun, isMon, isTue, isWed, isThu, isFri, isSat);
    }

    private void readRecurringData() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(AlarmEditActivity.this);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readRecurringData(alarmID,sqLiteDatabase);
        if (cursor.moveToFirst()){
            isSun = cursor.getString(1);
            isMon = cursor.getString(2);
            isTue = cursor.getString(3);
            isWed = cursor.getString(4);
            isThu = cursor.getString(5);
            isFri = cursor.getString(6);
            isSat = cursor.getString(7);
        }
    }

    public void wakeUpCheck(View view){
       /* vibrator.vibrate(pattern, -1);
        Intent wucIntent = new Intent(AlarmEditActivity.this, WakeUpCheckScheduleActivity.class);
        wucIntent.putExtra("tone",ALARM_TONE);
        wucIntent.putExtra("snooze",SNOOZE);
        wucIntent.putExtra("title",TITLE);
        wucIntent.putExtra("method",METHOD);
        wucIntent.putExtra("wuc",WUC);
        wucIntent.putExtra("type","edit");
        wucIntent.putExtra("id",alarmID);
        wucIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(wucIntent);*/
        if (WUC.equals("") || WUC.isEmpty()){
            WUC = "";
        }
        vibrator.vibrate(pattern, -1);
        SharedPreferences preferences=getSharedPreferences("wucTime",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("wucTime",WUC);
        editor.apply();
        WUCBottomSheet wucBottomSheet = new WUCBottomSheet();
        wucBottomSheet.show(getSupportFragmentManager(),"wucBottomSheet");
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
                }else {
                    isPaid = false;
                }
            }else {
                isPaid = true;
            }*/
        }
    }
    private void say(String speakText) {
        tts.setPitch(1.005f);
        tts.setSpeechRate(1);
        tts.speak(speakText, TextToSpeech.QUEUE_ADD, null);
    }

    private void readUserSettings(){
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = db.readUserSettings("1",sqLiteDatabase);
        if (cursor.moveToFirst()){
            voiceSettings = cursor.getString(2);
            isSettingsAvailable = true;
        }
        else {
            isSettingsAvailable = false;
        }
    }

    @Override
    public void onWUCSaveClick(String text) {
        WUC = text;
        String val;
        if (WUC.equals("0")){
            val = "Off";
            isWUC = false;
        }else {
            val = text+" Minutes";
            isWUC = true;
        }
        wakeUpCheckTitleText.setText(val);
    }
}