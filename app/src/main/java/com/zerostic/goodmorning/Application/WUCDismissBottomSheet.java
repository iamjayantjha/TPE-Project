package com.zerostic.goodmorning.Application;


import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ncorti.slidetoact.SlideToActView;
import com.view.circulartimerview.CircularTimerListener;
import com.view.circulartimerview.CircularTimerView;
import com.view.circulartimerview.TimeFormatEnum;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Activities.AlarmRestartActivity;
import com.zerostic.goodmorning.Activities.WeatherActivity;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.Service.AlarmService;

import java.util.Calendar;

public class WUCDismissBottomSheet extends BottomSheetDialogFragment {
    private wucBottomSheetListener mListener;
    CircularTimerView timer;
    SlideToActView dismissBtn;
    String method = "Default",alarmID,TITLE,METHOD,ALARM_TONE,SNOOZE,SNOOZE_ORDER,VOLUME,VIBRATE,WUC,oldHour,oldMin;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wuc_dismiss_bottom_sheet,container,true);
        timer = view.findViewById(R.id.timer);
        dismissBtn = view.findViewById(R.id.dismissBtn);
        Vibrator vibrator = (Vibrator) requireActivity().getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        SharedPreferences preferences= requireActivity().getSharedPreferences("method", MODE_PRIVATE);
        method  = preferences.getString("method", "Default");
        preferences = requireActivity().getSharedPreferences("id",MODE_PRIVATE);
        alarmID = preferences.getString("id",null);
        timer.setProgress(0f);
        timer.setMaxValue(30f);
        timer.setProgressColor(getResources().getColor(R.color.cardColor));
        readData();
        timer.setCircularTimerListener(new CircularTimerListener() {
            @Override
            public String updateDataOnTick(long remainingTimeInMs) {
                String time = String.valueOf((int)Math.ceil((remainingTimeInMs / 1000.f)));
                if (Integer.parseInt(time)<10){
                   timer.setProgressBackgroundColor(getResources().getColor(R.color.error));
                   timer.setTextColor(getResources().getColor(R.color.error));
                }
                return time;
            }

            @Override
            public void onTimerFinished() {
                mListener.onClick("dismiss");
                timer.setPrefix("");
                timer.setSuffix("");
                Calendar rightNow = Calendar.getInstance();
                int hrs = rightNow.get(Calendar.HOUR_OF_DAY);
                int mins = rightNow.get(Calendar.MINUTE);
                String hr = String.valueOf(hrs);
                String min = String.valueOf(mins);
                DataBaseHelper alarmDB = new DataBaseHelper(getActivity());
                alarmDB.getWritableDatabase();
                boolean result = alarmDB.updateData(alarmID,hr,min,TITLE,METHOD,ALARM_TONE,SNOOZE,SNOOZE_ORDER,VOLUME,VIBRATE,WUC,"scheduled");
                if (result){
                    Intent intentService = new Intent(getActivity(), AlarmService.class);
                    getActivity().stopService(intentService);
                    SharedPreferences preferences=requireActivity().getSharedPreferences("id",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("id",alarmID);
                    editor.apply();
/*                    switch (method) {
                        case "Maths": {
                        *//*Intent alarmRestart = new Intent(getActivity(), AlarmPreviewActivity.class);
                        alarmRestart.putExtra("tone", ALARM_TONE);
                        alarmRestart.putExtra("numOfQuest", numOfQuest);
                        alarmRestart.putExtra("difficulty", difficulty);
                        alarmRestart.putExtra("method", "Maths");
                        alarmRestart.putExtra("type", "alarm");
                        alarmRestart.putExtra("id", alarmID);
                        alarmRestart.putExtra("wuc",WUC);
                        alarmRestart.putExtra("isWuc",TYPE);
                        alarmRestart.putExtra("from","wuc");
                        startActivity(alarmRestart);
                        finish();*//*
                            break;
                        }
                        case "Shake": {
                        *//*Intent alarmRestart = new Intent(getActivity(), AlarmPreviewActivity.class);
                        alarmRestart.putExtra("tone", ALARM_TONE);
                        alarmRestart.putExtra("difficulty", difficulty);
                        alarmRestart.putExtra("numOfQuest", numOfQuest);
                        alarmRestart.putExtra("method", "Shake");
                        alarmRestart.putExtra("type", "alarm");
                        alarmRestart.putExtra("id", alarmID);
                        alarmRestart.putExtra("wuc",WUC);
                        alarmRestart.putExtra("isWuc",TYPE);
                        alarmRestart.putExtra("from","wuc");
                        startActivity(alarmRestart);
                        finish();*//*
                            break;
                        }
                        case "Bar Code": {
                       *//* Intent alarmRestart = new Intent(getActivity(), AlarmPreviewActivity.class);
                        alarmRestart.putExtra("tone", ALARM_TONE);
                        alarmRestart.putExtra("numOfQuest", numOfQuest);
                        alarmRestart.putExtra("difficulty", "");
                        alarmRestart.putExtra("method", "Bar Code");
                        alarmRestart.putExtra("type", "alarm");
                        alarmRestart.putExtra("id", alarmID);
                        alarmRestart.putExtra("wuc",WUC);
                        alarmRestart.putExtra("isWuc",TYPE);
                        alarmRestart.putExtra("from","wuc");
                        startActivity(alarmRestart);
                        finish();*//*
                            break;
                        }
                        case "Walk": {
                       *//* Intent alarmRestart = new Intent(getActivity(), AlarmPreviewActivity.class);
                        alarmRestart.putExtra("tone", ALARM_TONE);
                        alarmRestart.putExtra("difficulty", "0");
                        alarmRestart.putExtra("numOfQuest", numOfQuest);
                        alarmRestart.putExtra("method", "Walk");
                        alarmRestart.putExtra("type", "alarm");
                        alarmRestart.putExtra("id", alarmID);
                        alarmRestart.putExtra("wuc",WUC);
                        alarmRestart.putExtra("isWuc",TYPE);
                        alarmRestart.putExtra("from","wuc");
                        startActivity(alarmRestart);
                        finish();*//*
                            break;
                        }
                        case "Pattern": {
                        *//*Intent alarmRestart = new Intent(getActivity(), AlarmPreviewActivity.class);
                        alarmRestart.putExtra("tone", ALARM_TONE);
                        alarmRestart.putExtra("numOfQuest", numOfQuest);
                        alarmRestart.putExtra("difficulty", difficulty);
                        alarmRestart.putExtra("method", "Pattern");
                        alarmRestart.putExtra("type", "alarm");
                        alarmRestart.putExtra("id", alarmID);
                        alarmRestart.putExtra("wuc",WUC);
                        alarmRestart.putExtra("isWuc",TYPE);
                        alarmRestart.putExtra("from","wuc");
                        startActivity(alarmRestart);
                        finish();*//*
                            break;
                        }
                        case "Text": {
                        *//*Intent alarmRestart = new Intent(getActivity(), AlarmPreviewActivity.class);
                        alarmRestart.putExtra("tone", ALARM_TONE);
                        alarmRestart.putExtra("numOfQuest", numOfQuest);
                        alarmRestart.putExtra("difficulty", difficulty);
                        alarmRestart.putExtra("method", "Text");
                        alarmRestart.putExtra("type", "alarm");
                        alarmRestart.putExtra("id", alarmID);
                        alarmRestart.putExtra("wuc",WUC);
                        alarmRestart.putExtra("isWuc",TYPE);
                        alarmRestart.putExtra("from","wuc");
                        startActivity(alarmRestart);
                        finish();*//*
                            break;
                        }

                        case "Default": {
                            //     Intent alarmRestart = new Intent(getActivity(), SplashActivity.class);
                        *//*alarmRestart.putExtra("tone", "0");
                        alarmRestart.putExtra("numOfQuest", "1");
                        alarmRestart.putExtra("difficulty", "1");
                        alarmRestart.putExtra("method", "Default");
                        alarmRestart.putExtra("type", "alarm");
                        alarmRestart.putExtra("id", alarmID);
                        alarmRestart.putExtra("wuc",WUC);
                        alarmRestart.putExtra("isWuc",TYPE);
                        alarmRestart.putExtra("from","wuc");*//*
//                        startActivity(alarmRestart);
//                        requireActivity().finish();
//                        break;
                        }
                    }*/
                    Intent alarmRestart = new Intent(getActivity(), AlarmRestartActivity.class);
                    startActivity(alarmRestart);
                    requireActivity().finish();
                    startAlarmService();
                }else {
                    requireActivity().finish();
                }
//                Intent intentService = new Intent(getActivity(), AlarmService.class);
//                requireActivity().stopService(intentService);
            }
        }, 30, TimeFormatEnum.SECONDS, 10);
        timer.startTimer();
        dismissBtn.setOnSlideCompleteListener(slideToActView -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if (hour<12){
                mListener.onClick("Morning");
            }else {
                if (hour<18) {
                    mListener.onClick("Afternoon");
                }else {
                    mListener.onClick("Evening");
                }
            }
            vibrator.vibrate(pattern,-1);
            Intent intentService = new Intent(getActivity(), AlarmService.class);
            getActivity().stopService(intentService);
            readHrMin();
            wakeUpCheckAlarmCreation();
            Intent weatherIntent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                weatherIntent = new Intent(getActivity(), WeatherActivity.class);
            }else {
                requireActivity().finish();
            }
            weatherIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(weatherIntent);
            requireActivity().finish();
        });
        return view;
    }

    private void wakeUpCheckAlarmCreation(){
        DataBaseHelper alarmDB = new DataBaseHelper(getActivity());
        alarmDB.getWritableDatabase();
        boolean result = alarmDB.updateData(alarmID,oldHour,oldMin,TITLE,METHOD,ALARM_TONE,SNOOZE,SNOOZE_ORDER,VOLUME,VIBRATE,WUC,"scheduled");
        if (result){
            requireActivity().finish();
        }
    }

    private void startAlarmService() {
        Intent intentService = new Intent(getActivity(), AlarmService.class);
        intentService.putExtra("id", alarmID);
        intentService.putExtra("act","AlarmRestartActivity");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(intentService);
        } else {
            requireActivity().startService(intentService);
        }
    }

    private void readHrMin(){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readHrMin(alarmID,db);
        if (cursor.moveToFirst()){
            oldHour = cursor.getString(1);
            oldMin = cursor.getString(2);
        }
    }


    public interface wucBottomSheetListener{
        void onClick(String text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (WUCDismissBottomSheet.wucBottomSheetListener) context;
        }
        catch (ClassCastException classCastException){
            throw new ClassCastException(context.toString()+" must implement Bottom Sheet Listener");
        }

    }

    private void readData() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readData(alarmID,db);
        if (cursor.moveToFirst()){
            TITLE = cursor.getString(3);
            METHOD = cursor.getString(4);
            ALARM_TONE = cursor.getString(5);
            SNOOZE = cursor.getString(6);
            SNOOZE_ORDER = cursor.getString(7);
            VOLUME = cursor.getString(8);
            VIBRATE = cursor.getString(9);
            WUC = cursor.getString(10);
            //TYPE = cursor.getString(11);
        }

    }
}

