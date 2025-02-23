package com.zerostic.goodmorning.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;

public class ChallengeActivity extends AppCompatActivity {
    private TextView method;
    private String fName, tone, wuc, act, id, payStat;
    private int count = 0;
    Vibrator vibrator;
    final long[] pattern = {10, 20};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        readUserData();
        TextView heading = findViewById(R.id.heading);
        TextView challengeInfo = findViewById(R.id.challengeInfo);
        MaterialButton yesBtn = findViewById(R.id.saveBtn);
        TextView userName = findViewById(R.id.userName);
        String headingText = getIntent().getStringExtra("heading");
        tone = getIntent().getStringExtra("tone");
        wuc = getIntent().getStringExtra("wuc");
        act = getIntent().getStringExtra("act");
        id = getIntent().getStringExtra("id");
        payStat = getIntent().getStringExtra("payStat");
        heading.setText(headingText);
        method = findViewById(R.id.method);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        data();
        readMethod();
        boolean isMethodsAvailable = readMethods();
        if (isMethodsAvailable){
            userName.setText(fName+" ready for the challenge?");
            challengeInfo.setText("you'll get any alarm tone and any of the following methods to turn off your alarm."+"\n");
            yesBtn.setEnabled(true);
        }else {
            switch (count){
                case 1:
                    userName.setText("Sorry "+fName+" please provide two more methods");
                    method.setVisibility(View.GONE);
                    break;

                case 2:
                    userName.setText("Sorry "+fName+" please provide one more method");
                    method.setVisibility(View.GONE);
                    break;

                default:
                    userName.setText("Sorry "+fName+" please provide at least three methods");
                    method.setVisibility(View.GONE);
                    break;
            }
            yesBtn.setVisibility(View.GONE);
        }
        yesBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            challengeData();
            Intent methodActivity = new Intent(ChallengeActivity.this, MethodActivity.class);
            methodActivity.putExtra("method","Challenge Mode");
            methodActivity.putExtra("tone",tone);
            methodActivity.putExtra("wuc",wuc);
            methodActivity.putExtra("act",act);
            methodActivity.putExtra("id",id);
            methodActivity.putExtra("payStat",payStat);
            methodActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(methodActivity);
            finish();
        });

    }

    private void readUserData(){
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = db.readUserData("1",sqLiteDatabase);
        if (cursor.moveToFirst()){
            fName = cursor.getString(1);
        }
    }

    private boolean readMethods() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(ChallengeActivity.this);
        dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.getAllMethodData();
        if (cursor.getCount()>=3){
            return true;
        }else {
            count = cursor.getCount();
            return false;
        }
    }

    private void data(){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(ChallengeActivity.this);
        Cursor res = dataBaseHelper.getAllMethodData();
        StringBuffer stringBuffer = new StringBuffer();
        if (res != null && res.getCount()>0){
            while (res.moveToNext()){
                String val = res.getString(0);
                if (!val.equals("Challenge")){
                    stringBuffer.append(val+"\n\n");
                }
            }
            method.setText(stringBuffer.toString());
        }

    }

    private void challengeData(){
        DataBaseHelper methodDB = new DataBaseHelper(this);
        Cursor res = methodDB.getMethodData();
        if (res != null && res.getCount()>0){
            boolean result;
            result = methodDB.insertMethodData("Challenge","","");

            if (result){
                Toast.makeText(getApplicationContext(),"Alright get ready....",Toast.LENGTH_SHORT).show();
            }else {
                result = methodDB.updateMethodData("Challenge","","");
            }
            if (result){
                Toast.makeText(getApplicationContext(),"Alright get ready....",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Sorry something went wrong",Toast.LENGTH_SHORT).show();
            }
        }else {
            boolean result = methodDB.insertMethodData("Challenge","","");
            if (result){
                Toast.makeText(getApplicationContext(),"Alright get ready....",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Sorry something went wrong",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void readMethod(){
        DataBaseHelper methodDB = new DataBaseHelper(this);
        methodDB.getReadableDatabase();
        Cursor cursor = methodDB.getAllMethodData();
        if (cursor.moveToFirst()){
            String param1 = cursor.getString(0);
        }
    }
}