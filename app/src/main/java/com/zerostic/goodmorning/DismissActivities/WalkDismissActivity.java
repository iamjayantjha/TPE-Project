package com.zerostic.goodmorning.DismissActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Activities.AlarmPreviewActivity;
import com.zerostic.goodmorning.Activities.AlarmRestartActivity;
import com.zerostic.goodmorning.Activities.WeatherActivity;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Data.Alarm;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.Service.AlarmService;

import java.util.Calendar;
import jp.shts.android.storiesprogressview.StoriesProgressView;

/**
 Coded by iamjayantjha
 **/

public class WalkDismissActivity extends AppCompatActivity implements SensorEventListener,StoriesProgressView.StoriesListener{
    String steps,tone;
    SensorManager sensorManager;
    Sensor stepsCounter,stepDetector;
    boolean isSensorAvailable,isDetectorAvailable,notFirstTime = false;
    int totalSteps = 0,stepsDetetcted = 0;
    StoriesProgressView storiesProgressView;
    private Vibrator vibrator;
    TextView mSteps,cheating;
    private double MagnitudePrevious = 0;
    private long userSteps = 0;
    float lastx,lasty,lastz;
    int hours;
    int minutes, count =1;
    String alarmID;
    LottieAnimationView animationView;
    boolean allowRecent = false;
    String TITLE,METHOD ,ALARM_TONE,SNOOZE,snoozeOrder,VOLUME,VIBRATE,WUC,TYPE,mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_dismiss);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Utils.blackIconStatusBar(WalkDismissActivity.this, R.color.solid_dark_color);
        steps = getIntent().getStringExtra("steps");
        tone = getIntent().getStringExtra("tone");
        mType = getIntent().getStringExtra("type");
        alarmID = getIntent().getStringExtra("id");
        if (mType.equals("alarm")){
            readData();
        }else {
            allowRecent = true;
        }
        animationView = findViewById(R.id.animationView);
        totalSteps = Integer.parseInt(steps);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        storiesProgressView = findViewById(R.id.timer);
        storiesProgressView.setStoriesCount(1); // <- set stories
        storiesProgressView.setStoryDuration(20000); // <- set a story duration
        storiesProgressView.setStoriesListener(this);
        mSteps = findViewById(R.id.steps);
        cheating = findViewById(R.id.cheating);
        new Handler().postDelayed(() -> {
            mSteps.setText("Get ready");
        }, 100);
        new Handler().postDelayed(() -> {
            mSteps.setText("Stand Up");
        }, 1000);
        new Handler().postDelayed(() -> {
            mSteps.setText("Walk");
            animationView.playAnimation();
            storiesProgressView.startStories();
        }, 3000);
        mSteps.setText(steps);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
            stepsCounter = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isSensorAvailable = true;
        }else {
            isSensorAvailable = false;
            Toast.makeText(getApplicationContext(),"Sensor is not available on your device",Toast.LENGTH_LONG).show();
            WalkDismissActivity.this.finish();
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)!=null){
            stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            isDetectorAvailable = true;
        }else {
            isDetectorAvailable = false;
            Toast.makeText(getApplicationContext(),"Steps Detector is not available on your device",Toast.LENGTH_LONG).show();
            WalkDismissActivity.this.finish();
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
            Intent alarmRestart = new Intent(WalkDismissActivity.this, AlarmRestartActivity.class);
            startActivity(alarmRestart);
            WalkDismissActivity.this.finish();
        }else {
            Intent alarmRestart = new Intent(WalkDismissActivity.this, AlarmPreviewActivity.class);
            alarmRestart.putExtra("tone",tone);
            alarmRestart.putExtra("difficulty","0");
            alarmRestart.putExtra("numOfQuest",steps);
            alarmRestart.putExtra("method","Walk");
            alarmRestart.putExtra("type",mType);
            alarmRestart.putExtra("id",alarmID);
            alarmRestart.putExtra("from","dismiss");
            startActivity(alarmRestart);
            finish();
        }
    }

    private void startAlarmService() {
        Intent intentService = new Intent(WalkDismissActivity.this, AlarmService.class);
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
            WalkDismissActivity.this.finish();
            startAlarmService();
            Intent service = new Intent("android.intent.action.MAIN");
            service.setClass(this, AlarmRestartActivity.class);
            service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(service);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (totalSteps!=0){
            float x_acceleration = event.values[0];
            float y_acceleration = event.values[1];
            float z_acceleration = event.values[2];
            float threshorld = 25f;
            final long[] pattern = {50, 100};

            if (notFirstTime){
                float xDiff = Math.abs(lastz-x_acceleration);
                float yDiff = Math.abs(lasty-y_acceleration);
                float zDiff = Math.abs(lastz-z_acceleration);

                if (((xDiff < threshorld) && (xDiff>8f) && (yDiff < threshorld) && (yDiff>8f)) ||
                        (((xDiff < threshorld)&&(xDiff>8f) && (zDiff < threshorld)&&(zDiff>8f))) ||
                        (((yDiff < threshorld) && (yDiff>8f) && (zDiff < threshorld)&&(zDiff>8f)))){
                    storiesProgressView.reverse();
                    if (cheating.getVisibility() == View.VISIBLE){
                        storiesProgressView.reverse();
                        new Handler().postDelayed(() -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(pattern,-1);
                                totalSteps = Integer.parseInt(steps);
                                totalSteps = totalSteps - 1;
                                mSteps.setTextColor(getResources().getColor(R.color.selected));
                                mSteps.setVisibility(View.VISIBLE);
                                cheating.setVisibility(View.GONE);
                                mSteps.setText(""+totalSteps);
                                if (animationView.getVisibility() == View.GONE){
                                    animationView.setVisibility(View.VISIBLE);
                                    animationView.playAnimation();
                                }
                            }else {
                                vibrator.vibrate(pattern,-1);
                                totalSteps = Integer.parseInt(steps);
                                totalSteps = totalSteps - 1;
                                mSteps.setTextColor(getResources().getColor(R.color.selected));
                                mSteps.setVisibility(View.VISIBLE);
                                cheating.setVisibility(View.GONE);
                                mSteps.setText(""+totalSteps);
                                if (animationView.getVisibility() == View.GONE){
                                    animationView.setVisibility(View.VISIBLE);
                                    animationView.playAnimation();
                                }
                            }
                        }, 1000);
                    }else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(pattern,-1);
                            totalSteps = totalSteps - 1;
                            mSteps.setTextColor(getResources().getColor(R.color.selected));
                            mSteps.setVisibility(View.VISIBLE);
                            cheating.setVisibility(View.GONE);
                            mSteps.setText(""+totalSteps);
                            if (animationView.getVisibility() == View.GONE){
                                animationView.setVisibility(View.VISIBLE);
                                animationView.playAnimation();
                            }
                        }else {
                            vibrator.vibrate(pattern,-1);
                            totalSteps = totalSteps - 1;
                            mSteps.setTextColor(getResources().getColor(R.color.selected));
                            mSteps.setVisibility(View.VISIBLE);
                            cheating.setVisibility(View.GONE);
                            mSteps.setText(""+totalSteps);
                            if (animationView.getVisibility() == View.GONE){
                                animationView.setVisibility(View.VISIBLE);
                                animationView.playAnimation();
                            }
                        }
                    }

                }else if ((xDiff > threshorld && yDiff > threshorld) ||
                        (xDiff > threshorld && zDiff > threshorld) ||
                        (yDiff>threshorld && zDiff>threshorld)){
                    vibrator.vibrate(pattern,-1);
                    cheating.setVisibility(View.VISIBLE);
                    mSteps.setVisibility(View.GONE);
                    animationView.setVisibility(View.GONE);
                    animationView.pauseAnimation();
                    /*new Handler().postDelayed(() -> {
                        mSteps.setText("No Cheating");
                    }, 3000);*/
                    totalSteps = Integer.parseInt(steps);
                    new Handler().postDelayed(() -> {
                        cheating.setVisibility(View.GONE);
                        mSteps.setVisibility(View.VISIBLE);
                        animationView.setVisibility(View.VISIBLE);
                        animationView.playAnimation();
                        totalSteps = Integer.parseInt(steps);
                        mSteps.setText(""+totalSteps);
                    }, 1000);
                }

            }

            lastx = x_acceleration;
            lasty = y_acceleration;
            lastz = z_acceleration;
            notFirstTime = true;
        }else {
            if (mType.equals("alarm")){
                if (TYPE.equals("scheduled")){
                    if (count == 1){
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
                            count++;
                        }
                    }
                }
                allowRecent = true;
                Intent weatherIntent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    weatherIntent = new Intent(WalkDismissActivity.this, WeatherActivity.class);
                    weatherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(weatherIntent);
                }else {
                    WalkDismissActivity.this.finish();
                }

            }
            finish();

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorAvailable){
            sensorManager.registerListener(this,stepsCounter,SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (isDetectorAvailable){
            sensorManager.registerListener(this,stepDetector,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorAvailable){
            sensorManager.unregisterListener(this,stepsCounter);
        }

        if (isDetectorAvailable){
            sensorManager.unregisterListener(this,stepDetector);
        }
    }

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
    }

    private void readDataTogetID() {
        Calendar rightNow = Calendar.getInstance();
        int hours = rightNow.get(Calendar.HOUR_OF_DAY);
        int minutes = rightNow.get(Calendar.MINUTE);
        String h = String.valueOf(hours);
        String m = String.valueOf(minutes);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readHourMin(h,m,db);
        if (cursor.moveToFirst()){
            alarmID  = cursor.getString(0);
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
            WalkDismissActivity.this.finish();
        }else {
            Toast.makeText(WalkDismissActivity.this,"There won't be any wake up check",Toast.LENGTH_LONG).show();
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