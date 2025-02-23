package com.zerostic.goodmorning.DismissActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.card.MaterialCardView;
import com.zerostic.goodmorning.Activities.ScanBarCodeActivity;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Activities.AlarmPreviewActivity;
import com.zerostic.goodmorning.Activities.AlarmRestartActivity;
import com.zerostic.goodmorning.Activities.WeatherActivity;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Data.Alarm;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.Service.AlarmService;
import java.io.IOException;
import java.util.Calendar;
import jp.shts.android.storiesprogressview.StoriesProgressView;

/**
 Coded by iamjayantjha
 **/

public class BarCodeDismissActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener{
    String tone,code;
    MaterialCardView dismissBtn,stopPreview;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private StoriesProgressView storiesProgressView;
    int hours;
    int minutes,count =0;
    String alarmID;
    String TITLE,METHOD ,ALARM_TONE,SNOOZE,snoozeOrder,VOLUME,VIBRATE,WUC,TYPE,mType;
    CodeScanner codeScanner;
    CodeScannerView scannerView;
    boolean allowRecents = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code_dismiss);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Utils.blackIconStatusBar(BarCodeDismissActivity.this, R.color.background);
        code = getIntent().getStringExtra("code");
        tone = getIntent().getStringExtra("tone");
        mType = getIntent().getStringExtra("type");
        alarmID = getIntent().getStringExtra("id");
        scannerView = findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this, scannerView);
        stopPreview = findViewById(R.id.stopPreview);
        storiesProgressView = findViewById(R.id.timer);
        storiesProgressView.setStoriesCount(1);
        storiesProgressView.setStoryDuration(20000);
        storiesProgressView.setStoriesListener(this);
        // storiesProgressView.setRotation(180);
        storiesProgressView.startStories();
        dismissBtn = findViewById(R.id.dismissBtn);
        if (mType.equals("alarm")){
            readData();
        }else {
            allowRecents = true;
        }
        if (mType.equals("preview")){
            stopPreview.setVisibility(View.VISIBLE);
        }
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        stopPreview.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            BarCodeDismissActivity.this.finish();
        });
        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);
        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setFlashEnabled(false);
        codeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            vibrator.vibrate(pattern,-1);
            if (result.getText().equals(code)) {
                if (mType.equals("alarm")) {
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
                        }
                    }

                    Intent weatherIntent = new Intent(BarCodeDismissActivity.this, WeatherActivity.class);
                    weatherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(weatherIntent);
                    allowRecents = true;
                    finish();
                } else {
                    BarCodeDismissActivity.this.finish();
                }
            } else {
                codeScanner.startPreview();
            }
        }));
        codeScanner.setErrorCallback(error -> runOnUiThread(() -> {
            Toast.makeText(BarCodeDismissActivity.this, "Camera initialization error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }));
        /*
        dismissBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            if (Code.getText().toString().equals("")||Code.getText().toString().equals(null)||Code.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(),"Scan code to dismiss your alarm",Toast.LENGTH_LONG).show();
            }else if (Code.getText().toString().equals(code)){
                if (mType.equals("alarm")){
                    if (TYPE.equals("scheduled")){
                        int wucVal = Integer.parseInt(WUC);
                        if ((wucVal>1)&&(wucVal<=10)){
                            int hour, min;
                            int s = Integer.parseInt(WUC);
                            vibrator.vibrate(pattern,-1);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.add(Calendar.MINUTE, s);
                            hour = calendar.get(Calendar.HOUR_OF_DAY);
                            min = calendar.get(Calendar.MINUTE);
                            wakeUpCheckAlarmCreation(hour,min);
                        }
                    }

                    Intent weatherIntent = new Intent(BarCodeDismissActivity.this, WeatherActivity.class);
                    weatherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(weatherIntent);
                    allowRecents = true;
                    finish();
                }else {
                    BarCodeDismissActivity.this.finish();
                }

            }
        });*/
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
        allowRecents = true;
        if (mType.equals("alarm")){
            startAlarmService(this);
            Intent alarmRestart = new Intent(BarCodeDismissActivity.this, AlarmRestartActivity.class);
            startActivity(alarmRestart);
            finish();
        }else {
            Intent alarmRestart = new Intent(BarCodeDismissActivity.this, AlarmPreviewActivity.class);
            alarmRestart.putExtra("tone",tone);
            alarmRestart.putExtra("numOfQuest",code);
            alarmRestart.putExtra("difficulty","");
            alarmRestart.putExtra("method","Bar Code");
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

    @Override
    protected void onStop() {
        super.onStop();
        if (!allowRecents){
            BarCodeDismissActivity.this.finish();
            startAlarmService(this);
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
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        int ids = Integer.parseInt(alarmID);
        String id = String.valueOf(ids);
        String hour = String.valueOf(h);
        String min = String.valueOf(m);
        boolean result = dataBaseHelper.updateData(id,hour,min,TITLE,METHOD,ALARM_TONE,SNOOZE,"1",VOLUME,VIBRATE,WUC,"wuc");
        if (result){
            scheduleWuc(h,m);
            BarCodeDismissActivity.this.finish();
        }else {
            Toast.makeText(BarCodeDismissActivity.this,"There won't be any wake up check",Toast.LENGTH_LONG).show();
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

    private void startAlarmService(Context context) {
        Intent intentService = new Intent(context, AlarmService.class);
        intentService.putExtra("id", alarmID);
        intentService.putExtra("act","AlarmRestartActivity");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

}