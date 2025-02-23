package com.zerostic.goodmorning;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.zerostic.goodmorning.Activities.SettingsActivity;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.Calendar;
import java.util.Locale;


public class TrackerFragment extends Fragment {
    private static final long START_TIME_IN_MILLIS = 420999;
    private Vibrator vibrator;
    final long[] pattern = {40, 80};
    private MaterialCardView startBtn;
    private Dialog dialog;
    private TextView title;
    private TextView message;
    private TextView btnText;
    private Animation animation;
    private CountDownTimer countDownTimer;
    private boolean isDataAvailable;
    private long TIME_LEFT_IN_MILLIS = START_TIME_IN_MILLIS;
    DataBaseHelper alarmDB;
    String id,fName,sleepingTime = "",sleepHour = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracker, container, false);
        alarmDB = new DataBaseHelper(getContext());
        ImageView settings = view.findViewById(R.id.settings);
        startBtn = view.findViewById(R.id.startBtn);
        btnText = view.findViewById(R.id.btnText);
        PieChart pieChart = view.findViewById(R.id.piechart);
        ImageView deleteBtn = view.findViewById(R.id.deleteBtn);
        TextView sleepTime = view.findViewById(R.id.sleepTime);
        TextView totalSleepHour = view.findViewById(R.id.sleepHour);
        RelativeLayout rl1 = view.findViewById(R.id.rl1);
        RelativeLayout rl2 = view.findViewById(R.id.rl2);
        RelativeLayout rl3 = view.findViewById(R.id.rl3);
        RelativeLayout rl4 = view.findViewById(R.id.rl4);
        RelativeLayout rl5 = view.findViewById(R.id.rl5);
        readUserData();
        boolean isTrackerDataAvailable = readTrackerDetails();
        TextView sleepDataHeading = view.findViewById(R.id.sleepDataHeading);
        String headingText;
        if (isTrackerDataAvailable){
            readTrackerData();
            headingText = fName + " your sleep data is shown below";
            int num1 = getRandomInteger(45,30);
            int num2 = getRandomInteger(45,30);
            int num3 = getRandomInteger(30,10);
            int total = num1+num2+num3;
            if (total>100){
                num1 = num1+(100-total);
            }else if (total<100){
              num3 = num1 + (total-100);
            }
            if (!sleepHour.equals("")){
                pieChart.setVisibility(View.VISIBLE);
                rl1.setVisibility(View.VISIBLE);
                rl2.setVisibility(View.VISIBLE);
                rl3.setVisibility(View.VISIBLE);
                rl4.setVisibility(View.VISIBLE);
                rl5.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.VISIBLE);
                pieChart.addPieSlice(
                        new PieModel(
                                "Moderate",num1,
                                Color.parseColor("#53B4FF")));
                pieChart.addPieSlice(
                        new PieModel(
                                "Deep",
                                num3,
                                Color.parseColor("#0077D3")));
                pieChart.addPieSlice(
                        new PieModel(
                                "Light",
                                num2,
                                Color.parseColor("#B1DDFF")));
                sleepTime.setText(sleepingTime);
                totalSleepHour.setText(sleepHour);
                pieChart.startAnimation();
            }else {
                headingText = fName + " your sleep data will be shown below once it is captured.";
            }

        }else {
            rl1.setVisibility(View.GONE);
            rl2.setVisibility(View.GONE);
            rl3.setVisibility(View.GONE);
            rl4.setVisibility(View.GONE);
            rl5.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
            if (isDataAvailable){
                if (fName.equals("null")){
                    headingText = "Hey, we don't have your sufficient sleep data";
                }else {
                    headingText = fName + " we don't have your sufficient sleep data";
                }
            }else {
                headingText = "Hey, we don't have your sufficient sleep data";
            }
        }
        sleepDataHeading.setText(headingText);
        vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
        settings.setOnClickListener(view1 -> {
            vibrator.vibrate(pattern, -1);
            Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(settingsIntent);
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.permission_dialog);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        Button okay = dialog.findViewById(R.id.okayBtn);
        title = dialog.findViewById(R.id.headerText);
        message = dialog.findViewById(R.id.confirmText);
        startBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            title.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
            title.setText(R.string.information);
            if (btnText.getText().toString().equals("START")){
                message.setText(R.string.trackerStart);
            }else {
                message.setText(R.string.trackerStop);
            }
            dialog.show();
        });
        okay.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            dialog.dismiss();
            animation = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_fadeout);
            startBtn.startAnimation(animation);
            new Handler().postDelayed(() -> {
                animation = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_fadein);
                startBtn.startAnimation(animation);
                if (btnText.getText().toString().equals("START")){
                    startTimer();
                    insertTrackerData();
                }else {
                    resetTimer();
                    deleteTrackerData();
                }
            }, 2000);
        });
        deleteBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            title.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
            title.setText(R.string.confirmation);
            message.setText(R.string.deleteConfirmation);
            dialog.show();
            okay.setOnClickListener(v1 -> {
               vibrator.vibrate(pattern,-1);
               deleteTrackerData();
               dialog.dismiss();
            });
        });
        return view;
    }

    private void insertTrackerData() {
        DataBaseHelper alarmDB = new DataBaseHelper(getContext());
        Cursor res = alarmDB.getAllTrackerData();
        int idVal = res.getCount()+1;
        id = ""+idVal;
        Calendar rightNow = Calendar.getInstance();
        int hours = rightNow.get(Calendar.HOUR_OF_DAY);
        int minutes = rightNow.get(Calendar.MINUTE);
        minutes = minutes+7;
        if (minutes>60){
            hours = hours+1;
            minutes = minutes - 60;
        }
        String h = ""+hours;
        String m = ""+minutes;
        String startTime = h+":"+m;
        alarmDB.insertTrackerData(id,startTime,"","","On");
        if (idVal>7){
            /*String deleteingID = ""+(idVal-7);
            DataBaseHelper db = new DataBaseHelper(getContext());
            SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
            db.deleteTrackerData(deleteingID,sqLiteDatabase);*/
            DataBaseHelper db = new DataBaseHelper(getContext());
            db.getReadableDatabase();
            db.deleteEntireTrackerData();
            id = "1";
            alarmDB.insertTrackerData(id,startTime,"","","On");
        }
    }

    private void deleteTrackerData(){
        DataBaseHelper db = new DataBaseHelper(getContext());
        Cursor res = alarmDB.getAllTrackerData();
        SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
        String id = ""+res.getCount();
        db.deleteTrackerData(id,sqLiteDatabase);
    }

    private void resetTimer() {
        countDownTimer.cancel();
        TIME_LEFT_IN_MILLIS = START_TIME_IN_MILLIS;
        btnText.setText(R.string.start);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(TIME_LEFT_IN_MILLIS,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TIME_LEFT_IN_MILLIS = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                btnText.setTextSize(28f);
                btnText.setText(R.string.trackerStarted);
            }
        }.start();

    }

    private void updateCountDownText() {
        int minutes = (int) (TIME_LEFT_IN_MILLIS / 1000 ) / 60;
        int seconds = (int) (TIME_LEFT_IN_MILLIS / 1000 ) % 60;
        String timeFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        btnText.setText(timeFormatted);
    }


    private void readUserData(){
        UserDatabaseHelper db = new UserDatabaseHelper(getContext());
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = db.readUserData("1",sqLiteDatabase);
        if (cursor.moveToFirst()){
            fName = cursor.getString(1);
            isDataAvailable = true;
        }else {
            isDataAvailable = false;
        }
    }

    private boolean readTrackerDetails(){
        DataBaseHelper alarmDB = new DataBaseHelper(getContext());
        alarmDB.getReadableDatabase();
        Cursor res = alarmDB.getAllTrackerData();
        return res.getCount() > 0;
    }

    private void readTrackerData(){
        DataBaseHelper alarmDB = new DataBaseHelper(getContext());
        SQLiteDatabase sqLiteDatabase = alarmDB.getReadableDatabase();
        Cursor res = alarmDB.getAllTrackerData();
        String id = ""+res.getCount();
        Cursor cursor = alarmDB.readTrackerData(id, sqLiteDatabase);
        if (cursor.moveToFirst()){
            sleepingTime = cursor.getString(1);
            sleepHour = cursor.getString(3);
            String h1="",m1="",annot;
            if (!sleepingTime.equals("")){
                for (String val: sleepingTime.split(":")){
                    if (h1.equals("")){
                        h1=val;
                    }else if (m1.equals("")){
                        m1=val;
                    }
                }
                int h2 = Integer.parseInt(h1);
                int m2 = Integer.parseInt(m1);
                if (h2>12){
                    h2 = h2-12;
                    annot = "PM";
                }else if (h2 == 0){
                    h2 = 12;
                    annot = "AM";
                }else {
                    annot = "AM";
                }
                if (m2<10){
                    sleepingTime = ""+h2+":"+"0"+m2+" "+annot;
                }else {
                    sleepingTime = ""+h2+":"+m2+" "+annot;
                }
            }
        }
    }

    public static int getRandomInteger(int maximum, int minimum){
        return ((int) (Math.random()*(maximum - minimum))) + minimum;
    }
}
