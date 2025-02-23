package com.zerostic.goodmorning.Data;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.zerostic.goodmorning.Application.AlarmWidgetProvider;
import com.zerostic.goodmorning.BroadCastReceiver.AlarmBroadcastReceiver;
import com.zerostic.goodmorning.BroadCastReceiver.WakeUpReceiver;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import static com.zerostic.goodmorning.BroadCastReceiver.AlarmBroadcastReceiver.FRIDAY;
import static com.zerostic.goodmorning.BroadCastReceiver.AlarmBroadcastReceiver.MONDAY;
import static com.zerostic.goodmorning.BroadCastReceiver.AlarmBroadcastReceiver.RECURRING;
import static com.zerostic.goodmorning.BroadCastReceiver.AlarmBroadcastReceiver.SATURDAY;
import static com.zerostic.goodmorning.BroadCastReceiver.AlarmBroadcastReceiver.SUNDAY;
import static com.zerostic.goodmorning.BroadCastReceiver.AlarmBroadcastReceiver.THURSDAY;
import static com.zerostic.goodmorning.BroadCastReceiver.AlarmBroadcastReceiver.TITLE;
import static com.zerostic.goodmorning.BroadCastReceiver.AlarmBroadcastReceiver.TUESDAY;
import static com.zerostic.goodmorning.BroadCastReceiver.AlarmBroadcastReceiver.WEDNESDAY;
import static com.zerostic.goodmorning.BroadCastReceiver.AlarmBroadcastReceiver.WUC;

/**
 Coded by iamjayantjha
 **/

@Entity(tableName = "alarm_table")
public class Alarm {
    @PrimaryKey
    @NonNull
    private int alarmId;
    private int hour, minute;
    String tone;
    private boolean started, recurring;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
    public boolean isWuc() {
        return wuc;
    }
    public void setWuc(boolean wuc) {
        this.wuc = wuc;
    }
    private boolean wuc;
    String title,method;
    private long created;
    String id;// Time of the alarm in milliseconds


    public Alarm(int alarmId, int hour, int minute, String tone, String title, String method, long created, boolean started, boolean recurring, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday,boolean wuc) {
        this.alarmId = alarmId;
        this.hour = hour;
        this.minute = minute;
        this.started = started;
        this.recurring = recurring;
        this.tone = tone;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        this.wuc = wuc;
        this.method = method;
        this.title = title;
        this.created = created;
    }


    public int getHour() {
        return hour;
    }
    public String getTone() {
        return tone;
    }

    public int getMinute() {
        return minute;
    }

    public boolean isStarted() {
        return started;
    }
    public boolean isWUC(){
        return wuc;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public boolean isMonday() {
        return monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void schedule(Context context) {
        id = String.valueOf(alarmId);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(RECURRING, recurring);
        intent.putExtra(MONDAY, monday);
        intent.putExtra(TUESDAY, tuesday);
        intent.putExtra(WEDNESDAY, wednesday);
        intent.putExtra(THURSDAY, thursday);
        intent.putExtra(FRIDAY, friday);
        intent.putExtra("method",method);
        intent.putExtra(SATURDAY, saturday);
        intent.putExtra(SUNDAY, sunday);
        intent.putExtra(TITLE, title);
        intent.putExtra(WUC, wuc);
        intent.putExtra("id", id);
        PendingIntent alarmPendingIntent = null;
        alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // if alarm time has already passed, increment day by 1
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(),alarmPendingIntent), alarmPendingIntent);
        Intent alarmProvider = new Intent(context, AlarmWidgetProvider.class);
        alarmProvider.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, AlarmWidgetProvider.class));
        alarmProvider.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(alarmProvider);

        this.started = true;
    }

    public void scheduleRecurring(Context context, int date){
        String month = month();
        int mon = Integer.parseInt(month) - 1;
        if (date>31){
            switch (Integer.parseInt(month)){
                case 1: date = date - 31;
                mon = mon+1;
                break;
                case 2: date = date - 28;
                    mon = mon+1;
                break;
                case 3: date = date - 31;
                    mon = mon+1;
                break;
                case 4: date = date - 30;
                    mon = mon+1;
                break;
                case 5: date = date - 31;
                    mon = mon+1;
                break;
                case 6: date = date - 30;
                    mon = mon+1;
                break;
                case 7: date = date - 31;
                    mon = mon+1;
                break;
                case 8: date = date - 31;
                    mon = mon+1;
                break;
                case 9: date = date - 30;
                    mon = mon+1;
                break;
                case 10: date = date - 31;
                    mon = mon+1;
                break;
                case 11: date = date - 30;
                    mon = mon+1;
                break;
                case 12: date = date - 31;
                    mon = mon+1;
                break;
            }
        } else if (date>30){
            switch (Integer.parseInt(month)){
                case 2: date = date - 28;
                    mon = mon+1;
                break;
                case 4: date = date - 30;
                    mon = mon+1;
                break;
                case 6: date = date - 30;
                    mon = mon+1;
                break;
                case 9: date = date - 30;
                    mon = mon+1;
                break;
                case 11: date = date - 30;
                    mon = mon+1;
                break;
            }
        }else if (date > 28){
            if (Integer.parseInt(month) == 3) {
                date = date - 28;
                mon = mon+1;
            }
        }
        id = String.valueOf(alarmId);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(RECURRING, recurring);
        intent.putExtra(MONDAY, monday);
        intent.putExtra(TUESDAY, tuesday);
        intent.putExtra(WEDNESDAY, wednesday);
        intent.putExtra(THURSDAY, thursday);
        intent.putExtra(FRIDAY, friday);
        intent.putExtra("method",method);
        intent.putExtra(SATURDAY, saturday);
        intent.putExtra(SUNDAY, sunday);
        intent.putExtra(TITLE, title);
        intent.putExtra(WUC, wuc);
        intent.putExtra("id", id);
        PendingIntent alarmPendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }else {
            alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_IMMUTABLE);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MONTH, mon);
        calendar.set(Calendar.DAY_OF_MONTH, date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), alarmPendingIntent), alarmPendingIntent);

        this.started = true;
    }



    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent alarmPendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }else {
            alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_IMMUTABLE);
        }
        alarmManager.cancel(alarmPendingIntent);
        this.started = false;
    }

    public String getRecurringDaysText() {
        if (!recurring) {
            return null;
        }

        String days = "";
        if (sunday) {
            days += "Sun ";
        }if (monday) {
            days += "Mon ";
        }
        if (tuesday) {
            days += "Tue ";
        }
        if (wednesday) {
            days += "Wed ";
        }
        if (thursday) {
            days += "Thu ";
        }
        if (friday) {
            days += "Fri ";
        }
        if (saturday) {
            days += "Sat ";
        }

        if (sunday&&monday&&tuesday&&wednesday&&thursday&&friday&&saturday){
            days = "Everyday";
        }else if(sunday&&saturday){
            days = "Weekend";
        }else if (monday&&tuesday&&wednesday&&thursday&&friday){
            days = "Weekdays";
        }

        return days;
    }

    public String getTitle() {
        return title;
    }

    public String getMethod(){
        return method;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public void scheduleWakeup(Context context){

        id = String.valueOf(alarmId);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WakeUpReceiver.class);
        intent.putExtra(RECURRING, recurring);
        intent.putExtra(MONDAY, monday);
        intent.putExtra(TUESDAY, tuesday);
        intent.putExtra(WEDNESDAY, wednesday);
        intent.putExtra(THURSDAY, thursday);
        intent.putExtra(FRIDAY, friday);
        intent.putExtra("method",method);
        intent.putExtra(SATURDAY, saturday);
        intent.putExtra(SUNDAY, sunday);
        intent.putExtra(TITLE, title);
        intent.putExtra("id", id);
        PendingIntent alarmPendingIntent;
        alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // if alarm time has already passed, increment day by 1
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);

        this.started = true;

    }

    public static String month(){
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
        return simpleDateFormat.format(c);
    }

}