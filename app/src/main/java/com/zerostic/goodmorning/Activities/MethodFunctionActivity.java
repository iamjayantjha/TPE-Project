package com.zerostic.goodmorning.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Data.DataBaseHelper;

/**
 Coded by iamjayantjha
 **/

public class MethodFunctionActivity extends AppCompatActivity {
    SQLiteDatabase sqLiteDatabase;
    String headingText, act, id, payStat, tone, wuc, param1="", code, type="",quest="";
    Chip alphabet,number,sixQuest,eightQuest,tenQuest;
    ChipGroup chipGrp1,chipGrp2;
    TextView heading,levelVal,numOfQuestVal,text1,text2,text3,text4,text5;
    SeekBar level;
    MaterialCardView difficultyCard,numberofQuestionCard,addCard,patternCard;
    int levelValue = 1, numOfQuest;
    MaterialButton saveBtn;
    NumberPicker numberOfQuest;
    DataBaseHelper methodDB;
    ImageView previewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method_function);
        Utils.blackIconStatusBar(MethodFunctionActivity.this, R.color.background);
        heading = findViewById(R.id.heading);
        headingText = getIntent().getStringExtra("heading");
        tone = getIntent().getStringExtra("tone");
        wuc = getIntent().getStringExtra("wuc");
        act = getIntent().getStringExtra("act");
        id = getIntent().getStringExtra("id");
        payStat = getIntent().getStringExtra("payStat");
        heading.setText(headingText);
     //   Toast.makeText(getApplicationContext(),tone,Toast.LENGTH_SHORT).show();
        methodDB = new DataBaseHelper(this);
        previewBtn = findViewById(R.id.previewBtn);
        difficultyCard = findViewById(R.id.difficultyCard);
        numberofQuestionCard = findViewById(R.id.numberofQuestionCard);
        addCard = findViewById(R.id.addCard);
        patternCard = findViewById(R.id.patternCard);
        level = findViewById(R.id.level);
        level.setProgress(2);
        levelVal = findViewById(R.id.levelValue);
        text1 = findViewById(R.id.text1);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
        text5 = findViewById(R.id.text5);
        chipGrp1 = findViewById(R.id.chipGrp1);
        chipGrp2 = findViewById(R.id.chipGrp2);
        alphabet = findViewById(R.id.alphabet);
        number = findViewById(R.id.number);
        sixQuest = findViewById(R.id.sixQuest);
        eightQuest = findViewById(R.id.eightQuest);
        tenQuest = findViewById(R.id.tenQuest);
        text2 = findViewById(R.id.text2);
        numOfQuestVal = findViewById(R.id.numOfQuest);
        saveBtn = findViewById(R.id.saveBtn);
        numberOfQuest = findViewById(R.id.numberOfQuest);
        readMethod();
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        if (heading.getText().toString().equals("Maths")){
            level.setMax(6);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                level.setMin(1);
            }
            numberOfQuest.setMinValue(1);
            numberOfQuest.setMaxValue(10);
            numOfQuest = 1;
            text1.setText(R.string.select_difficulty_level);
            text2.setVisibility(View.VISIBLE);
        }else if (heading.getText().toString().equals("Shake")){
            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){
                level.setMax(3);
                levelValue = 18;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    level.setMin(1);
                }
                numberOfQuest.setMinValue(25);
                numberOfQuest.setMaxValue(100);
                numOfQuest = 25;
                text1.setText(R.string.shake_intensity);
                text3.setText(R.string.shake_count);
            }else {
                Toast.makeText(getApplicationContext(),"Shake Sensors are not available in your device or they are not working properly.",Toast.LENGTH_LONG).show();
            }

        }else if (heading.getText().toString().equals("Bar Code")){
            text4.setText(R.string.add_code);
            addCard.setOnClickListener(v -> {
                if (checkCameraPermission()){
                 vibrator.vibrate(pattern,-1);
                 Intent scanCode = new Intent(MethodFunctionActivity.this, ScanBarCodeActivity.class);
                 scanCode.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 scanCode.putExtra("method","Bar Code");
                 scanCode.putExtra("tone",tone);
                 scanCode.putExtra("wuc",wuc);
                 scanCode.putExtra("act",act);
                 scanCode.putExtra("id",id);
                 scanCode.putExtra("payStat",payStat);
                 startActivity(scanCode);
                }else{
                    requestCameraPermission();
                }
            });

        }else if (heading.getText().toString().equals("Walk")){
            text3.setText(R.string.numOfSteps);
            numOfQuest = 25;
            numberOfQuest.setMaxValue(200);
            numberOfQuest.setMinValue(25);
        }else if (heading.getText().toString().equals("Pattern")){
            text5.setText(R.string.patternType);
            alphabet.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                chipGrp2.setVisibility(View.VISIBLE);
            });
            number.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                chipGrp2.setVisibility(View.VISIBLE);
            });
            if (alphabet.isChecked()){
                type = "alphabet";
            }else if (number.isChecked()){
                type = "number";
            }else {
                type = "";
            }
            eightQuest.setOnClickListener(v -> vibrator.vibrate(pattern, -1));
            sixQuest.setOnClickListener(v -> vibrator.vibrate(pattern, -1));
            tenQuest.setOnClickListener(v -> vibrator.vibrate(pattern, -1));
            if (sixQuest.isChecked()){
                quest = "6";
            }else if (eightQuest.isChecked()){
                quest = "8";
            }else if (tenQuest.isChecked()){
                quest = "10";
            }else {
                quest = "";
            }
        }else if (heading.getText().toString().equals("Text")){
            patternCard.setVisibility(View.VISIBLE);
            text5.setText("Select type of text you want");
            alphabet.setText("Phrase");
            number.setText("One-liners");
            alphabet.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                chipGrp2.setVisibility(View.VISIBLE);
                sixQuest.setText("One Phrase");
                eightQuest.setText("Two Phrases");
                tenQuest.setText("Three Phrases");
            });
            number.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                chipGrp2.setVisibility(View.VISIBLE);
                sixQuest.setText("Three");
                eightQuest.setText("Five");
                tenQuest.setText("Seven");
            });
            eightQuest.setOnClickListener(v -> vibrator.vibrate(pattern, -1));
            sixQuest.setOnClickListener(v -> vibrator.vibrate(pattern, -1));
            tenQuest.setOnClickListener(v -> vibrator.vibrate(pattern, -1));

        }
        else {
            numberOfQuest.setMinValue(1);
            numberOfQuest.setMaxValue(10);
            numOfQuest = 1;
        }
        switch (headingText) {
            case "Bar Code":
                difficultyCard.setVisibility(View.GONE);
                numberofQuestionCard.setVisibility(View.GONE);
                addCard.setVisibility(View.VISIBLE);
                code = getIntent().getStringExtra("code");
                headingText = getIntent().getStringExtra("heading");
                tone = getIntent().getStringExtra("tone");
                break;
            case "Walk":
                difficultyCard.setVisibility(View.GONE);
                break;
            case "Pattern":
                patternCard.setVisibility(View.VISIBLE);
                addCard.setVisibility(View.GONE);
                difficultyCard.setVisibility(View.GONE);
                numberofQuestionCard.setVisibility(View.GONE);
                break;
            case "Text":
                difficultyCard.setVisibility(View.GONE);
                numberofQuestionCard.setVisibility(View.GONE);
                break;
        }
        numberOfQuest.setOnValueChangedListener((numberPicker, i, i1) -> {
            vibrator.vibrate(pattern, -1);
            numOfQuest = numberOfQuest.getValue();
            if (heading.getText().toString().equals("Maths")){
                numOfQuestVal.setText("Total questions: " + numOfQuest);
            }else if (heading.getText().toString().equals("Shake")){
                numOfQuestVal.setText("You have to shake your device " + numOfQuest+" times");
            }else if (heading.getText().toString().equals("Bar Code")){

            }else if (heading.getText().toString().equals("Walk")){
                numOfQuestVal.setText("You have to walk " + numOfQuest+" steps");
            }
            else {
                numOfQuestVal.setText("Total questions: " + numOfQuest);
            }

        });

        level.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                vibrator.vibrate(pattern, -1);
                if (heading.getText().toString().equals("Maths")){
                    if (i == 0){
                        levelValue = 1;
                        text2.setText("Example: 5 + 9");
                        levelVal.setText("Sorry difficulty level cannot be 0 \nCurrent difficulty level: "+String.valueOf(levelValue));
                    }else if (i == 1){
                        text2.setText("Example: 5 + 9");
                        levelValue = i;
                        levelVal.setText("Difficulty level: "+ levelValue);
                    }else if (i == 2){
                        text2.setText("Example: 17 + 19");
                        levelValue = i;
                        levelVal.setText("Difficulty level: "+ levelValue);
                    }else if (i == 3){
                        text2.setText("Example: 57 + 74 + 98");
                        levelValue = i;
                        levelVal.setText("Difficulty level: "+ levelValue);
                    }else if (i == 4){
                        text2.setText("Example: 68 + 99 - 44");
                        levelValue = i;
                        levelVal.setText("Difficulty level: "+ levelValue);
                    }else if (i == 5){
                        text2.setText("Example: 587 + 44 x 12");
                        levelValue = i;
                        levelVal.setText("Difficulty level: "+ levelValue);
                    }else if (i == 6){
                        text2.setText("Example: (854 x 478) ÷ 97");
                        levelValue = i;
                        levelVal.setText("Difficulty level: "+ levelValue);
                    }
                }
                else if (heading.getText().toString().equals("Shake")){
                    if (i == 0){
                        levelValue = 18;
                       // levelVal.setText(""+levelValue);
                    }else if (i == 1){
                        levelValue = 18;
                      //  levelVal.setText(""+levelValue);
                    }else if (i == 2){
                        levelValue = 20;
                     //   levelVal.setText(""+levelValue);
                    }else if (i == 3){
                        levelValue = 30;
                     //   levelVal.setText(""+levelValue);
                    }else {
                        levelValue = 18;
                       // levelVal.setText(""+levelValue);
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
               // vibrator.vibrate(pattern, -1);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        if (heading.getText().toString().equals("Maths")){
            levelVal.setText("Difficulty level: "+ levelValue);
            numOfQuestVal.setText("Total questions: " + numOfQuest);
            text2.setText("Example: 5 + 9");
            saveBtn.setOnClickListener(view -> {
                vibrator.vibrate(pattern, -1);
                mathsData();
                Intent methodActivity = new Intent(MethodFunctionActivity.this, MethodActivity.class);
                methodActivity.putExtra("method","Maths");
                methodActivity.putExtra("tone",tone);
                methodActivity.putExtra("wuc",wuc);
                methodActivity.putExtra("act",act);
                methodActivity.putExtra("id",id);
                methodActivity.putExtra("payStat",payStat);
                methodActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(methodActivity);
                finish();
            });
            previewBtn.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                Intent previewIntent = new Intent(MethodFunctionActivity.this, AlarmPreviewActivity.class);
                previewIntent.putExtra("tone",tone);
                previewIntent.putExtra("method","Maths");
                previewIntent.putExtra("type","preview");
                previewIntent.putExtra("numOfQuest",String.valueOf(numOfQuest));
                previewIntent.putExtra("difficulty",String.valueOf(levelValue));
                previewIntent.putExtra("id","");
                previewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(previewIntent);
                finish();
            });
        }else if (heading.getText().toString().equals("Shake")){
            saveBtn.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                shakeData();
                Intent methodActivity = new Intent(MethodFunctionActivity.this, MethodActivity.class);
                methodActivity.putExtra("method","Shake");
                methodActivity.putExtra("tone",tone);
                methodActivity.putExtra("wuc",wuc);
                methodActivity.putExtra("act",act);
                methodActivity.putExtra("id",id);
                methodActivity.putExtra("payStat",payStat);
                methodActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(methodActivity);
                finish();
            });
            previewBtn.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                Intent previewIntent = new Intent(MethodFunctionActivity.this, AlarmPreviewActivity.class);
                previewIntent.putExtra("tone",tone);
                previewIntent.putExtra("method","Shake");
                previewIntent.putExtra("type","preview");
                previewIntent.putExtra("numOfQuest",String.valueOf(levelValue));
                previewIntent.putExtra("difficulty",String.valueOf(numOfQuest));
                previewIntent.putExtra("id","");
                previewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(previewIntent);
                finish();
            });
            numOfQuestVal.setText("You have to shake your device " + numOfQuest+" times");
        }else if (heading.getText().toString().equals("Bar Code")){
            saveBtn.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                if (code.equals("")||code.equals(null)||code.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Scan a code to save",Toast.LENGTH_LONG).show();
                }else {
                    barcodeData();
                    Intent methodActivity = new Intent(MethodFunctionActivity.this, MethodActivity.class);
                    methodActivity.putExtra("method","Bar Code");
                    methodActivity.putExtra("tone",tone);
                    methodActivity.putExtra("wuc",wuc);
                    methodActivity.putExtra("act",act);
                    methodActivity.putExtra("id",id);
                    methodActivity.putExtra("payStat",payStat);
                    methodActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(methodActivity);
                    finish();
                }
            });
            previewBtn.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                if (code.equals("")||code.equals(null)||code.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Scan a code to preview",Toast.LENGTH_LONG).show();
                }else {
                    Intent previewIntent = new Intent(MethodFunctionActivity.this, AlarmPreviewActivity.class);
                    previewIntent.putExtra("tone",tone);
                    previewIntent.putExtra("method","Bar Code");
                    previewIntent.putExtra("type","preview");
                    previewIntent.putExtra("numOfQuest",code);
                    previewIntent.putExtra("difficulty","0");
                    previewIntent.putExtra("id","");
                    previewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(previewIntent);
                    finish();
                }
            });
        }
        else if (heading.getText().toString().equals("Walk")){
            numOfQuestVal.setText("You have to walk " + numOfQuest+" steps");
            saveBtn.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                walkData();
                Intent methodActivity = new Intent(MethodFunctionActivity.this, MethodActivity.class);
                methodActivity.putExtra("method","Walk");
                methodActivity.putExtra("tone",tone);
                methodActivity.putExtra("wuc",wuc);
                methodActivity.putExtra("act",act);
                methodActivity.putExtra("id",id);
                methodActivity.putExtra("payStat",payStat);
                methodActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(methodActivity);
                finish();
            });
            previewBtn.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                Intent previewIntent = new Intent(MethodFunctionActivity.this, AlarmPreviewActivity.class);
                previewIntent.putExtra("tone",tone);
                previewIntent.putExtra("method","Walk");
                previewIntent.putExtra("type","preview");
                previewIntent.putExtra("numOfQuest",String.valueOf(numOfQuest));
                previewIntent.putExtra("difficulty","0");
                previewIntent.putExtra("id","");
                previewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(previewIntent);
                finish();
            });
        }else if (heading.getText().toString().equals("Pattern")){
         saveBtn.setOnClickListener(v -> {
         vibrator.vibrate(pattern, -1);
                if (alphabet.isChecked()){
                    type = "alphabet";
                }else if (number.isChecked()){
                    type = "number";
                }else {
                    type = "";
                }

                if (sixQuest.isChecked()){
                    quest = "6";
                }else if (eightQuest.isChecked()){
                    quest = "8";
                }else if (tenQuest.isChecked()){
                    quest = "10";
                }else {
                    quest = "";
                }
         if (type.equals("")){
             Toast.makeText(getApplicationContext(),"Select type of pattern",Toast.LENGTH_SHORT).show();
         }
         else if (quest.equals("")){
             Toast.makeText(getApplicationContext(), "Select total patterns",Toast.LENGTH_SHORT).show();
         }else {
             patternData();
             Intent methodActivity = new Intent(MethodFunctionActivity.this, MethodActivity.class);
             methodActivity.putExtra("method","Pattern");
             methodActivity.putExtra("tone",tone);
             methodActivity.putExtra("wuc",wuc);
             methodActivity.putExtra("act",act);
             methodActivity.putExtra("id",id);
             methodActivity.putExtra("payStat",payStat);
             methodActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
             startActivity(methodActivity);
             finish();
         }
            });
         previewBtn.setOnClickListener(v -> {
             vibrator.vibrate(pattern, -1);
             if (alphabet.isChecked()){
                 type = "alphabet";
             }else if (number.isChecked()){
                 type = "number";
             }else {
                 type = "";
             }

             if (sixQuest.isChecked()){
                 quest = "6";
             }else if (eightQuest.isChecked()){
                 quest = "8";
             }else if (tenQuest.isChecked()){
                 quest = "10";
             }else {
                 quest = "";
             }
             if (type.equals("")){
                 Toast.makeText(getApplicationContext(),"Select type of pattern",Toast.LENGTH_SHORT).show();
             }
             else if (quest.equals("")){
                 Toast.makeText(getApplicationContext(), "Select total patterns",Toast.LENGTH_SHORT).show();
             }else {
                 Intent previewIntent = new Intent(MethodFunctionActivity.this, AlarmPreviewActivity.class);
                 previewIntent.putExtra("tone",tone);
                 previewIntent.putExtra("method","Pattern");
                 previewIntent.putExtra("type","preview");
                 previewIntent.putExtra("numOfQuest",quest);
                 previewIntent.putExtra("difficulty",type);
                 previewIntent.putExtra("id","");
                 previewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 startActivity(previewIntent);
                 finish();
             }
         });
        }
        else if (heading.getText().toString().equals("Text")){
            saveBtn.setOnClickListener(v -> {
                vibrator.vibrate(pattern,-1);
                quest = "";
                if (alphabet.isChecked()&&sixQuest.isChecked()){
                    type = "phrase";
                    quest = "1";
                }else if (alphabet.isChecked()&&eightQuest.isChecked()){
                    type = "phrase";
                    quest = "2";
                }else if (alphabet.isChecked()&&tenQuest.isChecked()){
                    type = "phrase";
                    quest = "3";
                }else if (number.isChecked()&&sixQuest.isChecked()){
                    type = "oneliner";
                    quest = "3";
                }else if (number.isChecked()&&eightQuest.isChecked()){
                    type = "oneliner";
                    quest = "5";
                }else if (number.isChecked()&&tenQuest.isChecked()){
                    type = "oneliner";
                    quest = "7";
                }else {
                    type = "";
                    quest = "";
                }
                if (alphabet.isChecked()){
                    type = "phrase";
                }else if (number.isChecked()){
                    type = "oneliner";
                }
                if (type.equals("")){
                    Toast.makeText(getApplicationContext(),"Select type of text",Toast.LENGTH_SHORT).show();
                }
                else if (quest.equals("")){
                    Toast.makeText(getApplicationContext(), "Select total texts",Toast.LENGTH_SHORT).show();
                }else {
                    textData();
                    Intent methodActivity = new Intent(MethodFunctionActivity.this, MethodActivity.class);
                    methodActivity.putExtra("method","Text");
                    methodActivity.putExtra("tone",tone);
                    methodActivity.putExtra("wuc",wuc);
                    methodActivity.putExtra("act",act);
                    methodActivity.putExtra("id",id);
                    methodActivity.putExtra("payStat",payStat);
                    methodActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(methodActivity);
                    finish();
                }
            });
            previewBtn.setOnClickListener(v -> {
                vibrator.vibrate(pattern, -1);
                quest = "";
                if (alphabet.isChecked()&&sixQuest.isChecked()){
                    type = "phrase";
                    quest = "1";
                }else if (alphabet.isChecked()&&eightQuest.isChecked()){
                    type = "phrase";
                    quest = "2";
                }else if (alphabet.isChecked()&&tenQuest.isChecked()){
                    type = "phrase";
                    quest = "3";
                }else if (number.isChecked()&&sixQuest.isChecked()){
                    type = "oneliner";
                    quest = "3";
                }else if (number.isChecked()&&eightQuest.isChecked()){
                    type = "oneliner";
                    quest = "5";
                }else if (number.isChecked()&&tenQuest.isChecked()){
                    type = "oneliner";
                    quest = "7";
                }else {
                    type = "";
                    quest = "";
                }
                if (alphabet.isChecked()){
                    type = "phrase";
                }else if (number.isChecked()){
                    type = "oneliner";
                }
                if (type.equals("")){
                    Toast.makeText(getApplicationContext(),"Select type of text",Toast.LENGTH_SHORT).show();
                }
                else if (quest.equals("")){
                    Toast.makeText(getApplicationContext(), "Select total texts",Toast.LENGTH_SHORT).show();
                }else {
                    Intent previewIntent = new Intent(MethodFunctionActivity.this, AlarmPreviewActivity.class);
                    previewIntent.putExtra("tone",tone);
                    previewIntent.putExtra("method","Text");
                    previewIntent.putExtra("type","preview");
                    previewIntent.putExtra("numOfQuest",quest);
                    previewIntent.putExtra("difficulty",type);
                    previewIntent.putExtra("id","");
                    previewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(previewIntent);
                    finish();
                }
            });
        }
        else {
            numOfQuestVal.setText("Total questions: " + numOfQuest);
        }


    }

    private boolean checkCameraPermission() {
        getPackageManager();
        return checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 101);
    }

    private void mathsData(){
        Cursor res = methodDB.getMethodData();
        if (res != null && res.getCount()>0){
            readMethod();
            boolean result;
            if (param1 == null || param1.isEmpty()){
                result = methodDB.insertMethodData(heading.getText().toString(),String.valueOf(numOfQuest),String.valueOf(levelValue));
            }else {
                result = methodDB.updateMethodData(heading.getText().toString(),String.valueOf(numOfQuest),String.valueOf(levelValue));
            }
            if (result){
                Toast.makeText(getApplicationContext(),"Maths problem updated",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Sorry problems are not changed",Toast.LENGTH_SHORT).show();
            }
        }else {
            boolean result = methodDB.insertMethodData(heading.getText().toString(),String.valueOf(numOfQuest),String.valueOf(levelValue));
            if (result){
                Toast.makeText(getApplicationContext(),"Maths is your new method",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Method change error",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shakeData(){
        Cursor res = methodDB.getMethodData();
        if (res != null && res.getCount()>0){
            boolean result;
            if (param1 == null || param1.isEmpty()){
                result = methodDB.insertMethodData(heading.getText().toString(),String.valueOf(levelValue),String.valueOf(numOfQuest));
            }else {
                result = methodDB.updateMethodData(heading.getText().toString(),String.valueOf(levelValue),String.valueOf(numOfQuest));
            }
            if (result){
                Toast.makeText(getApplicationContext(),"You need to shake your device "+numOfQuest+" times",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Oh oh shake method is not changed",Toast.LENGTH_SHORT).show();
            }
        }else {
            boolean result = methodDB.insertMethodData(heading.getText().toString(),String.valueOf(levelValue),String.valueOf(numOfQuest));
            if (result){
                Toast.makeText(getApplicationContext(),"You need to shake your device "+numOfQuest+" times now",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Oh no Shake is yet not your dismiss method",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void walkData(){
        Cursor res = methodDB.getMethodData();
        if (res != null && res.getCount()>0){
            boolean result;
            if (param1 == null || param1.isEmpty()){
                result = methodDB.insertMethodData(heading.getText().toString(),String.valueOf(numOfQuest),"0");
            }else {
                result = methodDB.updateMethodData(heading.getText().toString(),String.valueOf(numOfQuest),"0");
            }
            if (result){
                Toast.makeText(getApplicationContext(),"You have to walk "+numOfQuest+" steps",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Oh something went wrong no more walking",Toast.LENGTH_SHORT).show();
            }
        }else {
            boolean result = methodDB.insertMethodData(heading.getText().toString(),String.valueOf(numOfQuest),"0");
            if (result){
                Toast.makeText(getApplicationContext(),"You have to walk "+numOfQuest+" steps now",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Oh something went wrong no more walking",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void barcodeData(){
        Cursor res = methodDB.getMethodData();
        if (res != null && res.getCount()>0){
            boolean result;
            if (param1 == null || param1.isEmpty()){
                result = methodDB.insertMethodData(heading.getText().toString(),code,"0");
            }else {
                result = methodDB.updateMethodData(heading.getText().toString(),code,"0");
            }
            if (result){
                Toast.makeText(getApplicationContext(),"Scan it\nto\nDismiss it",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Oh something went wrong can't scan",Toast.LENGTH_SHORT).show();
            }
        }else {
            boolean result = methodDB.insertMethodData(heading.getText().toString(),code,"0");
            if (result){
                Toast.makeText(getApplicationContext(),"Scanning is your new go to",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Oh something went wrong can't scan",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void patternData(){
        Cursor res = methodDB.getMethodData();
        if (res != null && res.getCount()>0){
            boolean result;
            if (param1 == null || param1.isEmpty()){
                result = methodDB.insertMethodData(heading.getText().toString(),quest,type);
            }else {
                result = methodDB.updateMethodData(heading.getText().toString(),quest,type);
            }
            if (result){
                Toast.makeText(getApplicationContext(),"Changed the pattern",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Oh no Pattern remains same",Toast.LENGTH_SHORT).show();
            }
        }else {
            boolean result = methodDB.insertMethodData(heading.getText().toString(),quest,type);
            if (result){
                Toast.makeText(getApplicationContext(),"You got a pattern",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Sorry Something went wrong no pattern",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void textData(){
        Cursor res = methodDB.getMethodData();
        if (res != null && res.getCount()>0){
            boolean result;
            if (param1 == null || param1.isEmpty()){
                result = methodDB.insertMethodData(heading.getText().toString(),quest,type);
            }else {
                result = methodDB.updateMethodData(heading.getText().toString(),quest,type);
            }
            if (result){
                Toast.makeText(getApplicationContext(),"Changed the text",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Oh oh text remains same",Toast.LENGTH_SHORT).show();
            }
        }else {
            boolean result = methodDB.insertMethodData(heading.getText().toString(),quest,type);
            if (result){
                Toast.makeText(getApplicationContext(),"Write some text now",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Oh shit something went wrong\nNo more texting",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void readMethod(){
        sqLiteDatabase = methodDB.getReadableDatabase();
        Cursor cursor = methodDB.readMethodData(headingText,sqLiteDatabase);
        if (cursor.moveToFirst()){
            param1 = cursor.getString(0);
        }
    }

}