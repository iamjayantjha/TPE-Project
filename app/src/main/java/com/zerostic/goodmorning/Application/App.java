package com.zerostic.goodmorning.Application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/**
 Coded by iamjayantjha
 **/

public class App extends Application {
    public static final String CHANNEL_ID = "GOOD MORNING ALARM SERVICE";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannnel();
    }

    private void createNotificationChannnel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "GOOD MORNING ALARM SERVICE",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
