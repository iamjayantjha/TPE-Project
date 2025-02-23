package com.zerostic.goodmorning.DismissActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Application.WUCDismissBottomSheet;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;
import java.util.Locale;

/**
 Coded by iamjayantjha
 **/

public class WakeUpCheckDismissActivity extends AppCompatActivity implements WUCDismissBottomSheet.wucBottomSheetListener {
 //   NeumorphButton dismissBtn;
    PowerManager.WakeLock wakeLock;
    PowerManager powerManager;
    String alarmID,TITLE,METHOD,ALARM_TONE,VOLUME,VIBRATE,WUC,SNOOZE,SNOOZE_ORDER,TYPE;
    String h,m;
    String oldHour, oldMin, name;
    String voiceSettings;
    boolean isSettingsAvailable, toSpeak, allowRecent = false;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up_check_dismiss);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        Utils.blackIconStatusBar(WakeUpCheckDismissActivity.this, R.color.background);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        readUserData();
        readUserSettings();
        powerManager  = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire(10*60*1000L /*10 minutes*/);
    //    dismissBtn = findViewById(R.id.dismissBtn);
        SharedPreferences preferences = this.getSharedPreferences("id", Context.MODE_PRIVATE);
        alarmID = preferences.getString("id","");
        readData();
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
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        String speech = "Hey. are you up?";
        if (voiceSettings.equals("1")||voiceSettings.equals("2")||voiceSettings.equals("")){
            new Handler().postDelayed(() -> say(speech), 2500);
            new Handler().postDelayed(() -> say(speech), 7500);
            new Handler().postDelayed(() -> say(speech), 12500);
            new Handler().postDelayed(() -> say(speech), 17500);
        }else {
            new Handler().postDelayed(() -> say(speech), 2500);
        }

      /*  dismissBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern,-1);
            allowRecent = true;
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            readHrMin();
            wakeUpCheckAlarmCreation();
            Intent weatherIntent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                weatherIntent = new Intent(WakeUpCheckDismissActivity.this, WeatherActivity.class);
            }else {
                WakeUpCheckDismissActivity.this.finish();
            }
            weatherIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(weatherIntent);
            finish();
        });*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!allowRecent){
            WakeUpCheckDismissActivity.this.finish();
            Intent service = new Intent("android.intent.action.MAIN");
            service.setClass(this,WakeUpCheckDismissActivity.class);
            service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(service);
        }/*else {
            try{
                if (tts != null){
                    tts.stop();
                    tts.shutdown();
                }
            }catch (Exception ignored){

            }
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences1=getSharedPreferences("method",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences1.edit();
        editor.putString("method",METHOD);
        editor.apply();
        WUCDismissBottomSheet wucDismissBottomSheet = new WUCDismissBottomSheet();
        wucDismissBottomSheet.show(getSupportFragmentManager(),"wucBottomSheet");
        wucDismissBottomSheet.setCancelable(false);
    }

    private void say(String speakText) {
        AudioManager audioManager  = (AudioManager) getSystemService(Context.AUDIO_SERVICE);;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
        tts.setPitch(1.005f);
        tts.setSpeechRate(1);
        tts.speak(speakText, TextToSpeech.QUEUE_ADD, null);
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                try{
                    if (tts != null) {
                        tts.stop();
                        tts.shutdown();
                    }
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }

    /*@Override
    public void onComplete() {
        allowRecent = true;

        Calendar rightNow = Calendar.getInstance();
        int hrs = rightNow.get(Calendar.HOUR_OF_DAY);
        int mins = rightNow.get(Calendar.MINUTE);
        String hr = String.valueOf(hrs);
        String min = String.valueOf(mins);
        DataBaseHelper alarmDB = new DataBaseHelper(WakeUpCheckDismissActivity.this);
        alarmDB.getWritableDatabase();
        boolean result = alarmDB.updateData(alarmID,hr,min,TITLE,METHOD,ALARM_TONE,SNOOZE,SNOOZE_ORDER,VOLUME,VIBRATE,WUC,"scheduled");
        if (result){
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            SharedPreferences preferences=this.getSharedPreferences("id",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("id",alarmID);
            editor.apply();
            *//*switch (METHOD) {
                case "Maths": {
                    Intent alarmRestart = new Intent(WakeUpCheckDismissActivity.this, AlarmRestartActivity.class);
                    startActivity(alarmRestart);
                    finish();
                    break;
                }
                case "Shake": {
                    Intent alarmRestart = new Intent(WakeUpCheckDismissActivity.this, AlarmRestartActivity.class);
                    startActivity(alarmRestart);
                    finish();
                    break;
                }
                case "Bar Code": {
                    Intent alarmRestart = new Intent(WakeUpCheckDismissActivity.this, AlarmRestartActivity.class);
                    startActivity(alarmRestart);
                    finish();
                    break;
                }
                case "Walk": {
                    Intent alarmRestart = new Intent(WakeUpCheckDismissActivity.this, AlarmRestartActivity.class);
                    startActivity(alarmRestart);
                    finish();
                    break;
                }
                case "Pattern": {
                    Intent alarmRestart = new Intent(WakeUpCheckDismissActivity.this, AlarmRestartActivity.class);
                    startActivity(alarmRestart);
                    finish();
                    break;
                }
                case "Text": {
                    Intent alarmRestart = new Intent(WakeUpCheckDismissActivity.this, AlarmRestartActivity.class);
                    startActivity(alarmRestart);
                    finish();
                    break;
                }

                case "Default": {
                    Intent alarmRestart = new Intent(WakeUpCheckDismissActivity.this, AlarmRestartActivity.class);
                    startActivity(alarmRestart);
                    finish();
                    break;
                }*//*
            //}
            Intent alarmRestart = new Intent(WakeUpCheckDismissActivity.this, AlarmRestartActivity.class);
            startActivity(alarmRestart);
            finish();
            startAlarmService(this);
        }else {
            WakeUpCheckDismissActivity.this.finish();
        }

    }
*/
    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
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
            SNOOZE_ORDER = cursor.getString(7);
            VOLUME = cursor.getString(8);
            VIBRATE = cursor.getString(9);
            WUC = cursor.getString(10);
            TYPE = cursor.getString(11);
        }

    }
    private void readHrMin(){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readHrMin(alarmID,db);
        if (cursor.moveToFirst()){
            oldHour = cursor.getString(1);
            oldMin = cursor.getString(2);
        }
    }

    private void wakeUpCheckAlarmCreation(){
        DataBaseHelper alarmDB = new DataBaseHelper(WakeUpCheckDismissActivity.this);
        alarmDB.getWritableDatabase();
        boolean result = alarmDB.updateData(alarmID,oldHour,oldMin,TITLE,METHOD,ALARM_TONE,SNOOZE,SNOOZE_ORDER,VOLUME,VIBRATE,WUC,"scheduled");
        if (result){
            WakeUpCheckDismissActivity.this.finish();
        }
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

    private void readUserData(){
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = db.readUserData("1",sqLiteDatabase);
        if (cursor.moveToFirst()){
            name = cursor.getString(1);
        }
    }

   /* @Override
    protected void onDestroy() {
        try{
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        } catch (Exception ignored) {

        }
        super.onDestroy();
    }*/

/*    private void startAlarmService(Context context) {
        Intent intentService = new Intent(context, AlarmService.class);
        intentService.putExtra("id", alarmID);
        intentService.putExtra("act","AlarmRestartActivity");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }*/


    @Override
    public void onClick(String text) {
        allowRecent = true;
        if (!text.equals("dismiss")){
            String speech;
            if (!text.equals("Evening")){
                speech = "Good " + text + " " + name + ". All the best for your day.";
            }else {
                speech = "Good " + text + " " + name + ". Have a good night.";
            }
            say(speech);
        }
    }
}