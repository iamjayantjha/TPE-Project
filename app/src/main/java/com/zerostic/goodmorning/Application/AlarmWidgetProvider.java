package com.zerostic.goodmorning.Application;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.Handler;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.zerostic.goodmorning.Data.Alarm;
import com.zerostic.goodmorning.Data.AlarmDatabase;
import com.zerostic.goodmorning.Data.DataBaseHelper;
import com.zerostic.goodmorning.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlarmWidgetProvider extends AppWidgetProvider {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // Schedule periodic updates
        final Handler handler = new Handler();
        final Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                for (int appWidgetId : appWidgetIds) {
                    updateWidgetWithAlarm(context, appWidgetManager, appWidgetId);
                }
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(updateRunnable);
    }



    private void updateWidgetWithAlarm(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        executorService.execute(() -> {
            // Access the Room database
            AlarmDatabase db = AlarmDatabase.getDatabase(context);
            DataBaseHelper alarmDatabase = new DataBaseHelper(context);
            Alarm alarm = db.alarmDao().getNextAlarm(System.currentTimeMillis());

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.alarm_widget_layout);

            // Update the widget
            if (alarm != null) {
                long alarmTime = alarm.getCreated();
                long remainingTime = alarmTime - System.currentTimeMillis();

                String formattedTime = formatAlarmTime(alarmTime);
                String countdownText = formatCountdown(remainingTime);
                formattedTime = formattedTime+" "+getAlarmDay(alarm.getAlarmId());

                views.setTextViewText(R.id.upcomingAlarmText, formattedTime);
                views.setTextViewText(R.id.countdownTimer, countdownText);

                // Notify the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            } else {
                views.setTextViewText(R.id.upcomingAlarmText, "No Alarm");
                views.setTextViewText(R.id.countdownTimer, "");
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        });
    }

    private String getAlarmDay(int alarmId) {

        return " ";
    }

    private String formatCountdown(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        StringBuilder countdown = new StringBuilder();
        if (days > 0) countdown.append(days).append("d ");
        if (hours % 24 > 0) countdown.append(hours % 24).append(" hours ");
        if (minutes % 60 > 0) countdown.append(minutes % 60).append(" minutes ");
        if (seconds % 60 > 0) countdown.append(seconds % 60).append(" seconds ");

        return countdown.toString().trim();
    }

    private String formatAlarmTime(long timeInMillis) {
        // Convert milliseconds to a readable time format
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timeInMillis));
    }
}