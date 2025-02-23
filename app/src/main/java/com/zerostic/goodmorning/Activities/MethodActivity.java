package com.zerostic.goodmorning.Activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;

/**
 Coded by iamjayantjha
 **/

public class MethodActivity extends AppCompatActivity {
    MaterialCardView defaultCard, mathsCard, shakeCard, barcodeCard, walkCard, patternCard, textCard,challengeCard;
    MaterialButton saveBtn;
    String method,wuc,id="",typeOfSleeper;
    DataBaseHelper methodDB;
    private ImageView danger;
    private boolean isSettingsAvailable;
    LottieAnimationView animationView1,animationView2,animationView3,animationView4;
    String tone,act,payStat;
    ImageView defaultRecommended, mathsRecommended, shakeRecommended, walkRecommended, barcodeRecommended, patternRecommended, textRecommended;
    TextView maths,challenge ,shake, barcode, walk, patternText, text,mathsData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method);
        Utils.blackIconStatusBar(MethodActivity.this, R.color.background);
        tone = getIntent().getStringExtra("tone");
        method = getIntent().getStringExtra("method");
        wuc = getIntent().getStringExtra("wuc");
        act = getIntent().getStringExtra("act");
        payStat = getIntent().getStringExtra("payStat");
        if (act.equals("edit")){
            id = getIntent().getStringExtra("id");
        }
        readUserSettings();
        methodDB = new DataBaseHelper(this);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        defaultCard = findViewById(R.id.defaultCard);
        mathsCard = findViewById(R.id.mathsCard);
        mathsData = findViewById(R.id.mathsData);
        shakeCard = findViewById(R.id.shakeCard);
        barcodeCard = findViewById(R.id.barcodeCard);
        walkCard = findViewById(R.id.walkCard);
        danger = findViewById(R.id.danger);
        challenge = findViewById(R.id.challenge);
        animationView1 = findViewById(R.id.animationView1);
        animationView2 = findViewById(R.id.animationView2);
        animationView3 = findViewById(R.id.animationView3);
        animationView4 = findViewById(R.id.animationView4);
        defaultRecommended = findViewById(R.id.defaultRecommended);
        mathsRecommended = findViewById(R.id.mathsRecommended);
        shakeRecommended = findViewById(R.id.shakeRecommended);
        walkRecommended = findViewById(R.id.walkRecommended);
        barcodeRecommended = findViewById(R.id.barcodeRecommended);
        patternRecommended = findViewById(R.id.patternRecommended);
        textRecommended = findViewById(R.id.textRecommended);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction();
        patternCard = findViewById(R.id.patternCard);
        textCard = findViewById(R.id.textCard);
        challengeCard = findViewById(R.id.challengeCard);
        saveBtn = findViewById(R.id.saveBtn);
        maths = findViewById(R.id.maths);
        shake = findViewById(R.id.shake);
        barcode = findViewById(R.id.barcode);
        walk = findViewById(R.id.walk);
        patternText = findViewById(R.id.pattern);
        text = findViewById(R.id.text);
        if (isSettingsAvailable){
            switch (typeOfSleeper){
                case "3":
                    mathsRecommended.setVisibility(View.VISIBLE);
                    textRecommended.setVisibility(View.VISIBLE);
                    barcodeRecommended.setVisibility(View.VISIBLE);
                    break;

                case "2":
                    patternRecommended.setVisibility(View.VISIBLE);
                    walkRecommended.setVisibility(View.VISIBLE);
                    break;

                case "1":
                    shakeRecommended.setVisibility(View.VISIBLE);
                    defaultRecommended.setVisibility(View.VISIBLE);
                    break;
            }
        }
        switch (method) {
            case "Maths":
             //   mathsSelected.setVisibility(View.VISIBLE);
                mathsCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
                mathsData();
                break;
            case "Shake":
                shakeCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
                break;
            case "Bar Code":
                barcodeCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
                break;
            case "Walk":
                walkCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
                break;
            case "Pattern":
                patternCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
                break;
            case "Text":
                textCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
                break;
            case "Challenge Mode":
                challengeCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
                break;
            default:
                defaultCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
                break;
        }

        defaultCard.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            defaultCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
            switch (method){
                case "Maths":mathsCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.background)));
                break;

                case "Shake":shakeCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.background)));
                break;

                case "Walk":walkCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.background)));
                break;

                case "Bar Code":barcodeCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.background)));
                break;

                case "Pattern":patternCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.background)));
                break;

                case "Text":textCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.background)));
                break;

                case "Challenge Mode":challengeCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.background)));
                break;
            }
            method = "Default";
        });
        mathsCard.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            Intent methodValue = new Intent(MethodActivity.this, MethodFunctionActivity.class);
            methodValue.putExtra("heading","Maths");
            methodValue.putExtra("tone",tone);
            methodValue.putExtra("wuc",wuc);
            methodValue.putExtra("act",act);
            methodValue.putExtra("id",id);
            methodValue.putExtra("payStat",payStat);
            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(maths, "titleText");
            pairs[1] = new Pair<View, String>(saveBtn, "button");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MethodActivity.this, pairs);
            startActivity(methodValue,options.toBundle());
        });
        shakeCard.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            Intent methodValue = new Intent(MethodActivity.this, MethodFunctionActivity.class);
            methodValue.putExtra("heading","Shake");
            methodValue.putExtra("tone",tone);
            methodValue.putExtra("wuc",wuc);
            methodValue.putExtra("act",act);
            methodValue.putExtra("id",id);
            methodValue.putExtra("payStat",payStat);
            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(shake, "titleText");
            pairs[1] = new Pair<View, String>(saveBtn, "button");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MethodActivity.this, pairs);
            startActivity(methodValue,options.toBundle());
        });



        walkCard.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            Intent methodValue = new Intent(MethodActivity.this, MethodFunctionActivity.class);
            methodValue.putExtra("heading","Walk");
            methodValue.putExtra("tone",tone);
            methodValue.putExtra("wuc",wuc);
            methodValue.putExtra("act",act);
            methodValue.putExtra("id",id);
            methodValue.putExtra("payStat",payStat);
            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(walk, "titleText");
            pairs[1] = new Pair<View, String>(saveBtn, "button");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MethodActivity.this, pairs);
            startActivity(methodValue,options.toBundle());
        });

        if (payStat.equals("y")){
            animationView1.setVisibility(View.GONE);
            animationView2.setVisibility(View.GONE);
            animationView3.setVisibility(View.GONE);
            animationView4.setVisibility(View.GONE);
            barcodeCard.setOnClickListener(view -> {
                vibrator.vibrate(pattern, -1);
                Intent methodValue = new Intent(MethodActivity.this, MethodFunctionActivity.class);
                methodValue.putExtra("heading","Bar Code");
                methodValue.putExtra("tone",tone);
                methodValue.putExtra("wuc",wuc);
                methodValue.putExtra("code","");
                methodValue.putExtra("act",act);
                methodValue.putExtra("id",id);
                methodValue.putExtra("payStat",payStat);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(barcode, "titleText");
                pairs[1] = new Pair<View, String>(saveBtn, "button");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MethodActivity.this, pairs);
                startActivity(methodValue,options.toBundle());
            });

            patternCard.setOnClickListener(view -> {
                vibrator.vibrate(pattern, -1);
                Intent methodValue = new Intent(MethodActivity.this, MethodFunctionActivity.class);
                methodValue.putExtra("heading","Pattern");
                methodValue.putExtra("tone",tone);
                methodValue.putExtra("wuc",wuc);
                methodValue.putExtra("act",act);
                methodValue.putExtra("id",id);
                methodValue.putExtra("payStat",payStat);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(patternText, "titleText");
                pairs[1] = new Pair<View, String>(saveBtn, "button");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MethodActivity.this, pairs);
                startActivity(methodValue,options.toBundle());
            });

            textCard.setOnClickListener(view -> {
                vibrator.vibrate(pattern, -1);
                //  method = "Text";
                Intent methodValue = new Intent(MethodActivity.this, MethodFunctionActivity.class);
                methodValue.putExtra("heading","Text");
                methodValue.putExtra("tone",tone);
                methodValue.putExtra("wuc",wuc);
                methodValue.putExtra("act",act);
                methodValue.putExtra("id",id);
                methodValue.putExtra("payStat",payStat);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(text, "titleText");
                pairs[1] = new Pair<View, String>(saveBtn, "button");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MethodActivity.this, pairs);
                startActivity(methodValue,options.toBundle());
            });

            challengeCard.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                Intent methodValue = new Intent(MethodActivity.this, ChallengeActivity.class);
                methodValue.putExtra("heading","Challenge Mode");
                methodValue.putExtra("tone",tone);
                methodValue.putExtra("wuc",wuc);
                methodValue.putExtra("act",act);
                methodValue.putExtra("id",id);
                methodValue.putExtra("payStat",payStat);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(challenge, "titleText");
                pairs[1] = new Pair<View, String>(saveBtn, "button");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MethodActivity.this, pairs);
                startActivity(methodValue,options.toBundle());
            });
        }else {
            barcodeCard.setEnabled(false);
            patternCard.setEnabled(false);
            textCard.setEnabled(false);
            challengeCard.setEnabled(false);
            danger.setVisibility(View.GONE);
        }

        saveBtn.setOnClickListener(view -> {
            if (act.equals("create")){
                vibrator.vibrate(pattern, -1);
                Intent createAlarm = new Intent(MethodActivity.this, CreateAlarmActivity.class);
                createAlarm.putExtra("method",method);
                createAlarm.putExtra("tone",tone);
                createAlarm.putExtra("wuc",wuc);
                createAlarm.putExtra("act","method");
                createAlarm.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(createAlarm);
                finish();
            }else if(act.equals("edit")){
                vibrator.vibrate(pattern, -1);
                Intent createAlarm = new Intent(MethodActivity.this, AlarmEditActivity.class);
                createAlarm.putExtra("method",method);
                createAlarm.putExtra("tone",tone);
                createAlarm.putExtra("wuc",wuc);
                createAlarm.putExtra("id",id);
                createAlarm.putExtra("act","method");
                createAlarm.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(createAlarm);
                finish();
            }


        });
    }

    private void mathsData(){
        SQLiteDatabase db = methodDB.getReadableDatabase();
        Cursor res = methodDB.readMethodData("Maths",db);
        if (res.moveToFirst()){
            mathsData.setVisibility(View.VISIBLE);
            String numOfQuest;
            numOfQuest = res.getString(1);
            String val = "Number of questions are "+numOfQuest+"\n";
            mathsData.setText(val);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        switch (method) {
            case "Maths":
                mathsCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
                mathsData();
                break;
            case "Default":
                defaultCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
                break;
            case "Shake":
                shakeCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
                break;
            case "Bar Code":
                barcodeCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
                break;
            case "Walk":
                walkCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
                break;
            case "Pattern":
                patternCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
                break;
            case "Text":
                textCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
                break;
            case "Challenge Mode":
                challengeCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.selected)));
                break;
        }
    }

    private void readUserSettings(){
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = db.readUserSettings("1",sqLiteDatabase);
        if (cursor.moveToFirst()){
            typeOfSleeper = cursor.getString(4);
            isSettingsAvailable = true;
        }
        else {
            isSettingsAvailable = false;
        }
    }
}