package com.zerostic.goodmorning.BroadCastReceiver;


import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;
import com.zerostic.goodmorning.Service.AlarmService;
import com.zerostic.goodmorning.Service.RescheduleAlarmsService;
import java.util.Calendar;

/**
 Coded by iamjayantjha
 **/

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public static final String MONDAY = "MONDAY";
    public static final String TUESDAY = "TUESDAY";
    public static final String WEDNESDAY = "WEDNESDAY";
    public static final String THURSDAY = "THURSDAY";
    public static final String FRIDAY = "FRIDAY";
    public static final String SATURDAY = "SATURDAY";
    public static final String SUNDAY = "SUNDAY";
    public static final String RECURRING = "RECURRING";
    public static final String TITLE = "TITLE";
    public static final String WUC = "WUC";


    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Toast.makeText(context, "Alarm Reboot", Toast.LENGTH_SHORT).show();
            startRescheduleAlarmsService(context);

        }
        else {
            String toastText = "Alarm Received";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            SharedPreferences preferences=context.getSharedPreferences("id",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("id",intent.getStringExtra("id"));
            editor.apply();
            if (!intent.getBooleanExtra(RECURRING, false)) {
                startAlarmService(context, intent);
            } {
                if (alarmIsToday(intent)) {
                    startAlarmService(context, intent);
                }
            }
        }

    }

    private boolean alarmIsToday(Intent intent) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        switch(today) {
            case Calendar.MONDAY:
                return intent.getBooleanExtra(MONDAY, false);
            case Calendar.TUESDAY:
                return intent.getBooleanExtra(TUESDAY, false);
            case Calendar.WEDNESDAY:
                return intent.getBooleanExtra(WEDNESDAY, false);
            case Calendar.THURSDAY:
                return intent.getBooleanExtra(THURSDAY, false);
            case Calendar.FRIDAY:
                return intent.getBooleanExtra(FRIDAY, false);
            case Calendar.SATURDAY:
                return intent.getBooleanExtra(SATURDAY, false);
            case Calendar.SUNDAY:
                return intent.getBooleanExtra(SUNDAY, false);
        }
        return false;
    }

    private void startAlarmService(Context context, Intent intent) {

        Intent intentService = new Intent(context, AlarmService.class);
        intentService.putExtra("id", intent.getStringExtra("id"));
        intentService.putExtra("act", "RingActivity");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmsService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

}
