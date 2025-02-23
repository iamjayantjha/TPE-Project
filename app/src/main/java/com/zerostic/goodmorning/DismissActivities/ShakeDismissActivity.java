package com.zerostic.goodmorning.DismissActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
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
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

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

public class ShakeDismissActivity extends AppCompatActivity implements SensorEventListener, StoriesProgressView.StoriesListener {
    private SensorManager sensorManager;
    private Sensor shakeSensor;
    private boolean isSensorAvailable, notFirstTime = false;
    TextView xDirection, yDirection, zDirection, totalShakes;
    private float cX, cY, cZ, lX, lY, lZ;
    float xDiff, yDiff, zDiff, shakeThreshold;
    private Vibrator vibrator;
    String numberOfShakes, threshold, tone;
    int totalShakesNumber;
    private StoriesProgressView storiesProgressView;
    int hours;
    int minutes, count = 1;
    boolean allowRecent = false;
    String alarmID;
    String TITLE,METHOD ,ALARM_TONE,numOfQuest,difficulty,SNOOZE,snoozeOrder,VOLUME,VIBRATE,WUC,TYPE,mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        Utils.blackIconStatusBar(ShakeDismissActivity.this, R.color.solid_dark_color);
        KeyguardManager.KeyguardLock keyguardLock = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE)).newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();
        storiesProgressView = findViewById(R.id.timer);
        storiesProgressView.setStoriesCount(1); // <- set stories
        storiesProgressView.setStoryDuration(20000); // <- set a story duration
        storiesProgressView.setStoriesListener(this);
        storiesProgressView.startStories();// <- set listener
        xDirection = findViewById(R.id.xDirection);
        yDirection = findViewById(R.id.yDirection);
        zDirection = findViewById(R.id.zDirection);
        totalShakes = findViewById(R.id.totalShakes);
        numberOfShakes = getIntent().getStringExtra("difficulty");
        threshold = getIntent().getStringExtra("numOfQuest");
        tone = getIntent().getStringExtra("tone");
        mType = getIntent().getStringExtra("type");
        alarmID = getIntent().getStringExtra("id");

        if (mType.equals("alarm")){
            readData();
        }else {
            allowRecent = true;
        }
        totalShakesNumber = Integer.parseInt(numberOfShakes);
        totalShakes.setText(""+totalShakesNumber);
        shakeThreshold = Float.parseFloat(threshold+"f");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){
            shakeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isSensorAvailable = true;
        }else {
            Toast.makeText(getApplicationContext(),"Shake Sensor is not available in your device",Toast.LENGTH_LONG).show();
            isSensorAvailable = false;
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
    public void onSensorChanged(SensorEvent event) {
        if (totalShakesNumber!=0){
            xDirection.setText(event.values[0]+"m/s2");
            yDirection.setText(event.values[1]+"m/s2");
            zDirection.setText(event.values[2]+"m/s2");
            cX = event.values[0];
            cY = event.values[1];
            cZ = event.values[2];

            if (notFirstTime){
                xDiff = Math.abs(lX - cX);
                yDiff = Math.abs(lY - cY);
                zDiff = Math.abs(lZ - cZ);

                if ((xDiff > shakeThreshold && yDiff > shakeThreshold) ||
                        (xDiff > shakeThreshold && zDiff > shakeThreshold) ||
                        (yDiff>shakeThreshold && zDiff>shakeThreshold)){
                    storiesProgressView.reverse();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                        totalShakesNumber = totalShakesNumber - 1;
                        totalShakes.setText(""+totalShakesNumber);
                    }else {
                        vibrator.vibrate(500);
                        totalShakesNumber = totalShakesNumber - 1;
                        totalShakes.setText(""+totalShakesNumber);
                    }
                }
            }
            lX = cX;
            lY = cY;
            lZ = cZ;
            notFirstTime = true;
        } else {
            storiesProgressView.reverse();
            totalShakes.setText("Completed");
            new Handler().postDelayed(() -> {
                if (mType.equals("alarm")){
                    if (TYPE.equals("scheduled")){
                        if (count == 1){
                            int wucVal = Integer.parseInt(WUC);
                            if ((wucVal>1)&&(wucVal<=10)){
                                int hour, min;
                                int s = Integer.parseInt(WUC);
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
                        weatherIntent = new Intent(ShakeDismissActivity.this, WeatherActivity.class);
                        weatherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(weatherIntent);
                    }else {
                        ShakeDismissActivity.this.finish();
                    }
                }
                finish();
            }, 1000);


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorAvailable){
            sensorManager.registerListener(this, shakeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorAvailable){
            sensorManager.unregisterListener(this);
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
            Intent alarmRestart = new Intent(ShakeDismissActivity.this, AlarmRestartActivity.class);
            startActivity(alarmRestart);
            ShakeDismissActivity.this.finish();
        }else {
            Intent alarmRestart = new Intent(ShakeDismissActivity.this, AlarmPreviewActivity.class);
            alarmRestart.putExtra("tone",tone);
            alarmRestart.putExtra("difficulty",numberOfShakes);
            alarmRestart.putExtra("numOfQuest",threshold);
            alarmRestart.putExtra("method","Shake");
            alarmRestart.putExtra("type",mType);
            alarmRestart.putExtra("id",alarmID);
            alarmRestart.putExtra("from","dismiss");
            startActivity(alarmRestart);
            finish();
        }
    }

    private void startAlarmService() {
        Intent intentService = new Intent(ShakeDismissActivity.this, AlarmService.class);
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
            ShakeDismissActivity.this.finish();
            startAlarmService();
            Intent service = new Intent("android.intent.action.MAIN");
            service.setClass(this, AlarmRestartActivity.class);
            service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(service);
        }
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
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
            ShakeDismissActivity.this.finish();
        }else {
            Toast.makeText(ShakeDismissActivity.this,"There won't be any wake up check",Toast.LENGTH_LONG).show();
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