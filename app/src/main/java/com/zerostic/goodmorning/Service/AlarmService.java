package com.zerostic.goodmorning.Service;

import static com.zerostic.goodmorning.Application.App.CHANNEL_ID;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Activities.AlarmRestartActivity;
import com.zerostic.goodmorning.Activities.RingActivity;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.DismissActivities.WakeUpCheckDismissActivity;

import java.util.Objects;

/**
 Coded by iamjayantjha
 **/

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    DataBaseHelper dataBaseHelper;
    SQLiteDatabase sqLiteDatabase;
    int integerTone;
    String alarmID,TITLE="",METHOD="",ALARM_TONE="",mVibrate="",WUC="",TYPE="",VOLUME="";
    AudioManager audioManager;
    String tone,act;
    Intent service;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //if (isIDAvailable){
        try{
            alarmID = intent.getStringExtra("id");
            act = intent.getStringExtra("act");
        }catch (Exception e){
            /*SharedPreferences preferences = this.getSharedPreferences("id", Context.MODE_PRIVATE);
            alarmID = preferences.getString("id","");
            act = preferences.getString("act","");*/
            // log the exception to crashlytics
         //   Crashlytics.logException(new Exception("My custom error message"));
            FirebaseCrashlytics.getInstance().log(Objects.requireNonNull(e.getMessage())+"User ID "+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        }
        /*Toast.makeText(this, "Alarm Service Started", Toast.LENGTH_SHORT).show();
        SharedPreferences preferences = this.getSharedPreferences("id", Context.MODE_PRIVATE);
        alarmID = preferences.getString("id","");*/
        //   readDataTogetID();
        try{
            readData();
        }catch (Exception ignored){
        }

        /*       if (isIDAvailable){
         *//*SharedPreferences preferences=getSharedPreferences("id",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("id",alarmID);
            editor.apply();*//*

        }*/
        int resID;
        if (METHOD.equals("Challenge Mode")){
            integerTone = getRandomInteger(20,6);
        }

        if (integerTone == 0){
            resID = R.raw.default_tone;
            tone = "default_tone.mp3";
        }else if (integerTone == 1){
            resID = R.raw.alarm_clock;
            tone = "alarm_clock.mp3";
        }else if (integerTone == 2){
            resID = R.raw.alarm_clock_old;
            tone = "alarm_clock_old.mp3";
        }else if (integerTone == 3){
            resID = R.raw.beeps;
            tone = "beeps.mp3";
        }else if (integerTone == 4){
            resID = R.raw.siren;
            tone = "siren.mp3";
        }else if (integerTone == 5){
            resID = R.raw.tuning;
            tone = "tuning.mp3";
        }else if (integerTone == 6){
            resID = R.raw.gradually_increasing1;
            tone = "gradually_increasing1.mp3";
        }else if (integerTone == 7){
            resID = R.raw.gradually_increasing2;
            tone = "gradually_increasing2.mp3";
        }else if (integerTone == 8){
            resID = R.raw.gradually_increasing3;
            tone = "gradually_increasing3.mp3";
        }else if (integerTone == 9){
            resID = R.raw.gradually_increasing4;
            tone = "gradually_increasing4.mp3";
        }else if (integerTone == 10){
            resID = R.raw.gradually_increasing5;
            tone = "gradually_increasing5.mp3";
        }else if (integerTone == 11){
            resID = R.raw.gradually_increasing6;
            tone = "gradually_increasing6.mp3";
        }else if (integerTone == 12){
            resID = R.raw.loud_alarm1;
            tone = "loud_alarm1.mp3";
        }else if (integerTone == 13){
            resID = R.raw.loud_alarm2;
            tone = "loud_alarm2.mp3";
        }else if (integerTone == 14){
            resID = R.raw.loud_alarm3;
            tone = "loud_alarm3.mp3";
        }else if (integerTone == 15){
            resID = R.raw.loud_alarm4;
            tone = "loud_alarm4.mp3";
        }else if (integerTone == 16){
            resID = R.raw.loud_alarm5;
            tone = "loud_alarm5.mp3";
        }else if (integerTone == 17){
            resID = R.raw.loud_alarm6;
            tone = "loud_alarm6.mp3";
        }else if (integerTone == 18){
            resID = R.raw.loud_alarm7;
            tone = "loud_alarm7.mp3";
        }
        else {
            resID = R.raw.default_tone;
            tone = "default_tone.mp3";
        }

        mediaPlayer=MediaPlayer.create(this, resID);
        audioManager  = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer.setLooping(true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            Intent notificationIntent;
            PendingIntent pendingIntent;
            Notification notification=null;
            if (TYPE.equals("scheduled")){
                if (act.equals("RingActivity")){
                    service = new Intent("android.intent.action.MAIN");
                    service.setClass(this,RingActivity.class);
                    service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.startActivity(service);
                    notificationIntent = new Intent(this, RingActivity.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    } else {
                        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
                    }
                    boolean userTitle = !TITLE.equals("Alarm");
                    String alarmTitle = "Good Morning";
                    if (userTitle){
                        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setContentTitle(alarmTitle)
                                .setContentText(TITLE)
                                .setSmallIcon(R.drawable.ic_goodmorning_logo_svg)
                                .setContentIntent(pendingIntent)
                                .build();
                    }else {
                        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setContentTitle(alarmTitle)
                                .setContentText("Time to wake up")
                                .setSmallIcon(R.drawable.ic_goodmorning_logo_svg)
                                .setContentIntent(pendingIntent)
                                .build();
                    }
                    int newVolume = Integer.parseInt(VOLUME)+5;
                    if (METHOD.equals("Challenge Mode")){
                        newVolume = 100;
                    }
                    SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putInt("volume",newVolume);
                    editor.apply();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
                    mediaPlayer.start();
                    if (mVibrate.equals("yes")){
                        long[] pattern = { 0, 100, 1000 };
                        vibrator.vibrate(pattern, 0);
                    }
                }else if (act.equals("AlarmRestartActivity")){
                    notificationIntent = new Intent(this, AlarmRestartActivity.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    } else {
                        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
                    }
                    boolean userTitle = !TITLE.equals("Alarm");
                    String alarmTitle = "Good Morning";
                    if (userTitle){
                        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setContentTitle(alarmTitle)
                                .setContentText(TITLE)
                                .setSmallIcon(R.drawable.ic_goodmorning_logo_svg)
                                .setContentIntent(pendingIntent)
                                .build();
                    }else {
                        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setContentTitle(alarmTitle)
                                .setContentText("Time to wake up")
                                .setSmallIcon(R.drawable.ic_goodmorning_logo_svg)
                                .setContentIntent(pendingIntent)
                                .build();
                    }
                    int newVolume = Integer.parseInt(VOLUME)+5;
                    if (METHOD.equals("Challenge Mode")){
                        newVolume = 100;
                    }
                    SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putInt("volume",newVolume);
                    editor.apply();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
                    mediaPlayer.start();
                    if (mVibrate.equals("yes")){
                        long[] pattern = { 0, 100, 1000 };
                        vibrator.vibrate(pattern, 0);
                    }
                }
            }else if (TYPE.equals("wuc")){
                service = new Intent("android.intent.action.MAIN");
                service.setClass(this, WakeUpCheckDismissActivity.class);
                service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(service);
                notificationIntent = new Intent(this, WakeUpCheckDismissActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                } else {
                    pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
                }
                String alarmTitle = "Good Morning";
                notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(alarmTitle)
                        .setContentText("Just to check whether you are awake or not.")
                        .setSmallIcon(R.drawable.ic_goodmorning_logo_svg)
                        .setAutoCancel(true)
                        .setSilent(true)
                        .setContentIntent(pendingIntent)
                        .build();
            }
            try {
                startForeground(1, notification);
            }catch (Exception ignored){

            }

        /*}else {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notify = new Intent(this, MainActivity.class);
            notify.putExtra("yes",true);
            notify.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent notifyIntent = PendingIntent.getBroadcast(this, 0, notify, PendingIntent.FLAG_ONE_SHOT);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.app_name));
            notificationBuilder.setContentTitle("Upcoming Alarm");
            notificationBuilder.setContentText("Hey, your alarm rings in 10 minutes");
            notificationBuilder.setSmallIcon(R.drawable.ic_goodmorning_logo_svg);
            notificationBuilder.setSound(uri);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            notificationBuilder.addAction(R.drawable.ic_goodmorning_logo_svg,"Dismiss",notifyIntent);
            manager.notify(1, notificationBuilder.build());



        }*/
       return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try
        {
            mediaPlayer.stop();
            vibrator.cancel();

        }
        catch(Exception ignored)
        {

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

/*    private void readDataTogetID() {
        Calendar rightNow = Calendar.getInstance();
        int hours = rightNow.get(Calendar.HOUR_OF_DAY);
        int minutes = rightNow.get(Calendar.MINUTE);
        String h = String.valueOf(hours);
        String m = String.valueOf(minutes);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readHourMin(h,m,db);
        if (cursor.moveToFirst()){
            isIDAvailable = true;
            alarmID  = cursor.getString(0);
        }else {
            //isIDAvailable = false;
            Cursor res = dataBaseHelper.getAllData();
            if (res.getCount() == 1) {
                isIDAvailable = true;
                alarmID = res.getString(0);
            }else if (res.getCount() == 0) {
                isIDAvailable = false;
            }*//*else{
                isIDAvailable = true;
//                alarmID = res.getString(0);
                // read all with same hour and then search with different minutes and get the alarm id
            }*//*
        }
    }*/

    private void readData() {
        dataBaseHelper = new DataBaseHelper(this);
        sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readData(alarmID,sqLiteDatabase);
        if (cursor.moveToFirst()){
           TITLE = cursor.getString(3);
            METHOD = cursor.getString(4);
            ALARM_TONE = cursor.getString(5);
            VOLUME = cursor.getString(8);
            mVibrate = cursor.getString(9);
            WUC = cursor.getString(10);
            TYPE = cursor.getString(11);
            if (ALARM_TONE.equals("Default")){
                integerTone = 0;
            }else {
                integerTone = Integer.parseInt(ALARM_TONE);
            }
        }
    }

    public static int getRandomInteger(int maximum, int minimum){
        return ((int) (Math.random()*(maximum - minimum))) + minimum;
    }
}
