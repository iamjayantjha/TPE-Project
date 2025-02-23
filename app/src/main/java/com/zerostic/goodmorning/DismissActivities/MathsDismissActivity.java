package com.zerostic.goodmorning.DismissActivities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.zerostic.goodmorning.Activities.AlarmPreviewActivity;
import com.zerostic.goodmorning.Activities.AlarmRestartActivity;
import com.zerostic.goodmorning.Activities.WeatherActivity;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Data.Alarm;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Service.AlarmService;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import java.util.Calendar;
import jp.shts.android.storiesprogressview.StoriesProgressView;

/**
 Coded by iamjayantjha
 **/

public class MathsDismissActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {
    String numOfQuest,difficulty,process1,process2,tone;
    TextView numInput,calculatingValue,numOfQues;
    MaterialButton btnOne,btnTwo,btnThree,btnFour,btnFive,btnSix,btnSeven,btnEight,btnNine,btnZero,btnDone,btnClear;
    int num1,num2,num3;
    String sum="+",sub;
    String finalResult1;
    final int min = 20;
    final int max = 100;
    int i = 0,a;
    boolean allowRecent = false;
    DataBaseHelper dataBaseHelper;
    SQLiteDatabase sqLiteDatabase;
    int diff;
    int newVal;
    private StoriesProgressView storiesProgressView;
    int hours;
    int minutes;
    String alarmID;
    String TITLE,METHOD ,ALARM_TONE,SNOOZE,snoozeOrder,VOLUME,VIBRATE,WUC,TYPE,mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maths);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Utils.blackIconStatusBar(MathsDismissActivity.this, R.color.background);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {40, 40};
        final long[] wrong = {40,200};
            numOfQuest = getIntent().getStringExtra("numOfQuest");
            difficulty = getIntent().getStringExtra("difficulty");
            tone = getIntent().getStringExtra("tone");
            mType = getIntent().getStringExtra("type");
            alarmID = getIntent().getStringExtra("id");
          //  Toast.makeText(getApplicationContext(),tone,Toast.LENGTH_SHORT).show();
     //       numOfQuest = "1";
        //    difficulty = "3";
        if (mType.equals("alarm")){
            readData();
        }else {
           allowRecent = true;
        }

        calculatingValue = findViewById(R.id.calculatingValue);
        numOfQues = findViewById(R.id.numOfQuest);
        int nOQ = Integer.parseInt(numOfQuest);
        diff = Integer.parseInt(difficulty);
        setValue(diff);
      //  Toast.makeText(getApplicationContext(),tone,Toast.LENGTH_SHORT).show();
        storiesProgressView = findViewById(R.id.timer);
        storiesProgressView.setStoriesCount(1); // <- set stories
        storiesProgressView.setStoryDuration(20000); // <- set a story duration
        storiesProgressView.setStoriesListener(this); // <- set listener
       // storiesProgressView.setRotation(180);
        storiesProgressView.startStories();
        a=i+1;
        numOfQues.setText(""+a+"/"+nOQ);
        btnOne = findViewById(R.id.btnOne);
        btnTwo = findViewById(R.id.btnTwo);
        btnThree = findViewById(R.id.btnThree);
        btnFour = findViewById(R.id.btnFour);
        btnFive = findViewById(R.id.btnFive);
        btnSix = findViewById(R.id.btnSix);
        btnSeven = findViewById(R.id.btnSeven);
        btnEight = findViewById(R.id.btnEight);
        btnNine = findViewById(R.id.btnNine);
        btnZero = findViewById(R.id.btnZero);
        btnDone = findViewById(R.id.btnDone);
        btnClear = findViewById(R.id.btnClear);
        numInput = findViewById(R.id.numInput);
        btnOne.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            storiesProgressView.reverse();
            process1 = numInput.getText().toString();
            numInput.setText(process1 + "1");
        });
        btnTwo.setOnClickListener(view ->{
            vibrator.vibrate(pattern, -1);
            storiesProgressView.reverse();
            process1 = numInput.getText().toString();
            numInput.setText(process1+"2");
        });
        btnThree.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            storiesProgressView.reverse();
            process1 = numInput.getText().toString();
            numInput.setText(process1+"3");
        });
        btnFour.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            storiesProgressView.reverse();
            process1 = numInput.getText().toString();
            numInput.setText(process1+"4");
        });
        btnFive.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            storiesProgressView.reverse();
            process1 = numInput.getText().toString();
            numInput.setText(process1+"5");
        });
        btnSix.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            storiesProgressView.reverse();
            process1 = numInput.getText().toString();
            numInput.setText(process1+"6");
        });
        btnSeven.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            storiesProgressView.reverse();
            process1 = numInput.getText().toString();
            numInput.setText(process1+"7");
        });
        btnEight.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            storiesProgressView.reverse();
            process1 = numInput.getText().toString();
            numInput.setText(process1+"8");
        });
        btnNine.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            storiesProgressView.reverse();
            process1 = numInput.getText().toString();
            numInput.setText(process1+"9");
        });
        btnZero.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            storiesProgressView.reverse();
            process1 = numInput.getText().toString();
            numInput.setText(process1+"0");
        });

       valueCalculate();


        btnDone.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            if (i<=nOQ){
            process1 = numInput.getText().toString();
            if (process1.isEmpty()||process1.equals(null)){
                process1 = "0";
            }
            int processValue = Integer.parseInt(process1);
            if (newVal == processValue){
                storiesProgressView.reverse();
                Toast.makeText(getApplicationContext(),"Great",Toast.LENGTH_SHORT).show();
                setValue(diff);
                valueCalculate();
                numInput.setText("");
                i++;
                a=i+1;
                numOfQues.setText(""+a+"/"+nOQ);
            }else {
               // storiesProgressView.pause();
                vibrator.vibrate(wrong, -1);
                setValue(diff);
                valueCalculate();
                Toast.makeText(getApplicationContext(),"Try Again...",Toast.LENGTH_SHORT).show();
            }
            }
            if (i==nOQ){
                if (mType.equals("alarm")){
                    if (TYPE.equals("scheduled")){
                        int wucVal = Integer.parseInt(WUC);
                        if ((wucVal>1)&&(wucVal<=10)){
                            int hour, min;
                            vibrator.vibrate(pattern,-1);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.add(Calendar.MINUTE, wucVal);
                            hour = calendar.get(Calendar.HOUR_OF_DAY);
                            min = calendar.get(Calendar.MINUTE);
                            wakeUpCheckAlarmCreation(hour,min);
                        }
                    }
                    allowRecent = true;
                    Intent weatherIntent = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        weatherIntent = new Intent(MathsDismissActivity.this, WeatherActivity.class);
                        weatherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(weatherIntent);
                    }else {
                        MathsDismissActivity.this.finish();
                    }

                }
                finish();
            }
        });

        btnClear.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            storiesProgressView.reverse();
            process1 = numInput.getText().toString();
                   if (process1.length()>1){
                       process1 = process1.substring(0 , process1.length() - 1);
                       numInput.setText(process1);
                   }else {
                       numInput.setText("");
                   }

        });

        btnClear.setOnLongClickListener(view -> {
            storiesProgressView.reverse();
              numInput.setText("");
           // numInput.setText(""+newVal);
            return true;
        });

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
    private void valueCalculate(){
        process2 = calculatingValue.getText().toString();
        process2 = process2.replaceAll("×","*");
        process2 = process2.replaceAll("%","/100");
        process2 = process2.replaceAll("÷","/");
        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1);

        String finalResult = "";

        try {
            Scriptable scriptable = rhino.initStandardObjects();
            finalResult = rhino.evaluateString(scriptable,process2,"javascript",1,null).toString();
        }catch (Exception e){
            finalResult="0";
        }
        double val = Double.parseDouble(finalResult);
        newVal = (int) val;
        finalResult1 = finalResult;
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
            Intent alarmRestart = new Intent(MathsDismissActivity.this, AlarmRestartActivity.class);
            startActivity(alarmRestart);
            MathsDismissActivity.this.finish();
        }else{
            Intent alarmRestart = new Intent(MathsDismissActivity.this, AlarmPreviewActivity.class);
            alarmRestart.putExtra("tone",tone);
            alarmRestart.putExtra("numOfQuest",numOfQuest);
            alarmRestart.putExtra("difficulty",difficulty);
            alarmRestart.putExtra("method","Maths");
            alarmRestart.putExtra("type",mType);
            alarmRestart.putExtra("id",alarmID);
            alarmRestart.putExtra("from","dismiss");
            startActivity(alarmRestart);
            finish();

        }
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    private void setValue(int diff){
        if (diff == 1){
            num1 = getRandomInteger(9,7);// [0,60] + 20 => [20,80]
            num2 = getRandomInteger(9,1);
            calculatingValue.setText(""+num1+"+"+num2);

        }else if (diff == 2){
            num1 = getRandomInteger(99,76); // [0,60] + 20 => [20,80]
            num2 = getRandomInteger(99,10);
            calculatingValue.setText(""+num1+"+"+num2);

        }else if (diff == 3){
            num1 = getRandomInteger(150,100); // [0,60] + 20 => [20,80]
            num2 = getRandomInteger(99,57);
            num3 = getRandomInteger(99,10);
            calculatingValue.setText(""+num1+"+"+num2+"+"+num3);

        }else if (diff == 4){
            num1 = getRandomInteger(150,100); // [0,60] + 20 => [20,80]
            num2 = getRandomInteger(99,10);
            num3 = getRandomInteger(60,10);
            if (num1+num3<num2){
                calculatingValue.setText(""+num1+"+"+num2+"+"+num3);
            }else if (num1+num2<num3){
                calculatingValue.setText(""+num1+"+"+num2+"+"+num3);
            }else {
                calculatingValue.setText(""+num1+"+"+num2+"-"+num3);
            }
        }else if (diff == 5){
            num1 = getRandomInteger(150,100); // [0,60] + 20 => [20,80]
            num2 = getRandomInteger(50,20);
            num3 = getRandomInteger(50,20);
            calculatingValue.setText(""+num1+"+"+num2+"*"+num3);

        }else if (diff == 6){
            num1 = getRandomInteger(500,100); // [0,60] + 20 => [20,80]
            num2 = getRandomInteger(150,100);
            num3 = getRandomInteger(100,70);
            calculatingValue.setText(""+"("+num1+"*"+num2+")"+"/"+num3);

        }
    }
/*    private void deleteMethod(){
        dataBaseHelper = new DataBaseHelper(MathsDismissActivity.this);
        sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        dataBaseHelper.deleteData("Maths",sqLiteDatabase);
    }*/

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
            MathsDismissActivity.this.finish();
        }else {
            Toast.makeText(MathsDismissActivity.this,"There won't be any wake up check",Toast.LENGTH_LONG).show();
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

    public static int getRandomInteger(int maximum, int minimum){
        return ((int) (Math.random()*(maximum - minimum))) + minimum;
    }

    private void startAlarmService() {
        Intent intentService = new Intent(MathsDismissActivity.this, AlarmService.class);
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
            MathsDismissActivity.this.finish();
            startAlarmService();
            Intent service = new Intent("android.intent.action.MAIN");
            service.setClass(this, AlarmRestartActivity.class);
            service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(service);
        }
    }
}