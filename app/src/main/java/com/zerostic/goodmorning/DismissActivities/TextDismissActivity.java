package com.zerostic.goodmorning.DismissActivities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Activities.AlarmPreviewActivity;
import com.zerostic.goodmorning.Activities.AlarmRestartActivity;
import com.zerostic.goodmorning.Activities.WeatherActivity;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Data.Alarm;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.Service.AlarmService;
import java.util.Calendar;
import java.util.Locale;
import jp.shts.android.storiesprogressview.StoriesProgressView;

/**
 Coded by iamjayantjha
 **/

public class TextDismissActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener{
    String type, quest, tone;
    TextView typeTextView,charVal;
    EditText text;
    MaterialCardView editTextCard;
    int totalQuest=0;
    int i =1;
    MaterialButton doneBtn;
    private StoriesProgressView storiesProgressView;
    int hours;
    int minutes;
    String alarmID;
    String ol1,ol2,ol3,ol4,ol5,ol6,ol7,phrase1,phrase2,phrase3;
    String TITLE,METHOD ,ALARM_TONE,SNOOZE,snoozeOrder,VOLUME,VIBRATE,WUC,TYPE,mType;

    boolean allowRecent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_dismiss);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Utils.blackIconStatusBar(TextDismissActivity.this, R.color.background);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        storiesProgressView = findViewById(R.id.timer);
        storiesProgressView.setStoriesCount(1); // <- set stories
        storiesProgressView.setStoryDuration(20000); // <- set a story duration
        storiesProgressView.setStoriesListener(this); // <- set listener
        storiesProgressView.startStories();
        type = getIntent().getStringExtra("format");
        quest = getIntent().getStringExtra("quest");
        tone = getIntent().getStringExtra("tone");
        mType = getIntent().getStringExtra("type");
        alarmID = getIntent().getStringExtra("id");
//        type = "oneliner";
//        quest = "3";
//        tone = "0";
        if (mType.equals("alarm")){
            readData();
        }else {
            allowRecent = true;
        }
        totalQuest = Integer.parseInt(quest);
        typeTextView = findViewById(R.id.type);
        charVal = findViewById(R.id.charVal);
        text = findViewById(R.id.typeEditText);
        editTextCard = findViewById(R.id.typeText);
        charVal.setMovementMethod(new ScrollingMovementMethod());
        if (type.equals("phrase")){
            new Handler().postDelayed(() -> {
                doneBtn.setText("CHANGE TEXT");
            }, 300000);
        }

        /*if (type.equals("oneliner")){
            RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,350);
            params.addRule(RelativeLayout.BELOW, R.id.type);
            editTextCard.setLayoutParams(params);
        }else if (type.equals("phrase")){
            RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,800);
            params.addRule(RelativeLayout.BELOW, R.id.type);
            editTextCard.setLayoutParams(params);
        }*/

        ol1 = "Inspiration does exist, but it must find you working.";
        ol2 = "Courage is like a muscle. We strengthen it by use.";
        ol3 = "Worry is a misuse of imagination.";
        ol4 ="Don’t worry about failure you only have to be right once.";
        ol5 = "Never let success get to your head and never let failure get to your heart.";
        ol6 = "The most difficult thing is the decision to act, the rest is merely tenacity.";
        ol7 = "Don’t Let Yesterday Take Up Too Much Of Today.";
        phrase1 = "Nothing in the world can take the place of Persistence. Nothing is more common than unsuccessful men with talent. Unrewarded genius is almost a proverb. The world is full of educated derelicts. The slogan 'Press On' has solved and always will solve the problems of the human race.";
        phrase2 = "Someone will declare, “I am the leader!” and expect everyone to get in line and follow him or her to the gates of heaven or hell. My experience is that it doesn’t happen that way. Others follow you based on the quality of your actions rather than the magnitude of your declarations.";
        phrase3 = "If You Are Working On Something That You Really Care About, You Don’t Have To Be Pushed. The Vision Pulls You.";

        doneBtn = findViewById(R.id.doneBtn);
        changeText(i);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                charVal.setText(s);
                charVal.setTextColor(getResources().getColor(R.color.selected));
                storiesProgressView.reverse();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        doneBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            storiesProgressView.reverse();
            if (doneBtn.getText().toString().trim().equals("DONE")){
                if (typeTextView.getText().toString().trim().toUpperCase(Locale.ROOT).equals(text.getText().toString().trim().toUpperCase(Locale.ROOT))){
                i++;
                changeText(i);
                text.setText("");
                if (i>totalQuest){
                   // TextDismissActivity.this.finish();
                    if (mType.equals("Alarm")){
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
                        allowRecent = true;
                        Intent weatherIntent = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            weatherIntent = new Intent(TextDismissActivity.this, WeatherActivity.class);
                            weatherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(weatherIntent);
                        }else {
                            TextDismissActivity.this.finish();
                        }
                    }
                    finish();
                }
            }else if (typeTextView.getText().toString().trim().toLowerCase(Locale.ROOT).equals(text.getText().toString().trim().toLowerCase(Locale.ROOT))){
                i++;
                changeText(i);
                text.setText("");
                if (i>totalQuest){
                    // TextDismissActivity.this.finish();
                    if (mType.equals("Alarm")){
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
                        Intent weatherIntent = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            weatherIntent = new Intent(TextDismissActivity.this, WeatherActivity.class);
                            weatherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(weatherIntent);
                        }else {
                            TextDismissActivity.this.finish();
                        }
                    }
                    finish();
                }
            }else{
                String error = "You have not written the text correctly. Do check the commas and full stops.";
                charVal.setText(error);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    charVal.setTextColor(getResources().getColor(R.color.error));
                }
            }
            }else if ((doneBtn.getText().toString().trim().equals("CHANGE TEXT")) && (type.equals("phrase"))){
                doneBtn.setText("DONE");
                if ((typeTextView.getText().toString().trim().toUpperCase(Locale.ROOT).equals(phrase1.toUpperCase(Locale.ROOT))) || (typeTextView.getText().toString().trim().toLowerCase(Locale.ROOT).equals(phrase1.toLowerCase(Locale.ROOT)))){
                    typeTextView.setText(phrase2);
                }else if ((typeTextView.getText().toString().trim().toUpperCase(Locale.ROOT).equals(phrase2.toUpperCase(Locale.ROOT))) || (typeTextView.getText().toString().trim().toLowerCase(Locale.ROOT).equals(phrase2.toLowerCase(Locale.ROOT)))){
                    typeTextView.setText(phrase3);
                } else if ((typeTextView.getText().toString().trim().toUpperCase(Locale.ROOT).equals(phrase3.toUpperCase(Locale.ROOT))) || (typeTextView.getText().toString().trim().toLowerCase(Locale.ROOT).equals(phrase3.toLowerCase(Locale.ROOT)))){
                    typeTextView.setText(phrase1);
                }
                charVal.setText("Text Changed");
                charVal.setTextColor(getResources().getColor(R.color.selected));
            }
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
    private void changeText(int i){
        if (type.equals("oneliner")){
            if (i<=totalQuest){
                if (i == 1){
                    typeTextView.setText(ol1);
                }else if (i == 2){
                    typeTextView.setText(ol2);
                }else if (i == 3){
                    typeTextView.setText(ol3);
                }else if (i == 4){
                    typeTextView.setText(ol4);
                }else if (i == 5){
                    typeTextView.setText(ol5);
                }else if (i == 6){
                    typeTextView.setText(ol6);
                }else if (i == 7){
                    typeTextView.setText(ol7);
                }

            }
        }else if (type.equals("phrase")){
            if (i<=totalQuest){
                if (i == 1){
                    typeTextView.setText(phrase1);
                }else if (i == 2){
                    typeTextView.setText(phrase2);
                }else if (i==3){
                    typeTextView.setText(phrase3);
                }
            }
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
            Intent alarmRestart = new Intent(TextDismissActivity.this, AlarmRestartActivity.class);
            startActivity(alarmRestart);
            TextDismissActivity.this.finish();
        }else{
            Intent alarmRestart = new Intent(TextDismissActivity.this, AlarmPreviewActivity.class);
            alarmRestart.putExtra("tone",tone);
            alarmRestart.putExtra("numOfQuest",quest);
            alarmRestart.putExtra("difficulty",type);
            alarmRestart.putExtra("method","Text");
            alarmRestart.putExtra("type",mType);
            alarmRestart.putExtra("id",alarmID);
            alarmRestart.putExtra("from","dismiss");
            startActivity(alarmRestart);
            finish();
        }
    }

    private void startAlarmService() {
        Intent intentService = new Intent(TextDismissActivity.this, AlarmService.class);
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
            TextDismissActivity.this.finish();
            startAlarmService();
            Intent service = new Intent("android.intent.action.MAIN");
            service.setClass(this, AlarmRestartActivity.class);
            service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(service);
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
            TextDismissActivity.this.finish();
        }else {
            Toast.makeText(TextDismissActivity.this,"There won't be any wake up check",Toast.LENGTH_LONG).show();
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