package com.zerostic.goodmorning.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Activities.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FirebaseCloudMessagingService extends FirebaseMessagingService {
    public static int NOTIFICATION_ID = 1;
    String name;

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        SharedPreferences preferences = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        name = preferences.getString("name","User");
        generateNotification(Objects.requireNonNull(remoteMessage.getNotification()).getBody(),remoteMessage.getNotification().getTitle());
    }

   /* @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("newToken", s);
        getSharedPreferences("PREFS", MODE_PRIVATE).edit().putString("token", s).apply();
    }*/

    private void generateNotification(String body, String title) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this,0,mainIntent,PendingIntent.FLAG_ONE_SHOT| PendingIntent.FLAG_IMMUTABLE);
        }else{
           pendingIntent = PendingIntent.getActivity(this,0,mainIntent,PendingIntent.FLAG_ONE_SHOT);
        }

        Uri notificationSound  = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_goodmorning_logo_svg)
                .setContentTitle(title)
                .setContentText("Hey "+name+", "+body)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);


       /* Intent notificationIntent;
        PendingIntent pendingIntent;
        Notification notification;

        notificationIntent = new Intent(this, SettingsActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("No Title")
                .setContentText("Just to check whether you are awake or not.")
                .setSmallIcon(R.drawable.ic_goodmorning_logo_svg)
                .setAutoCancel(true)
                .setSilent(true)
                .setContentIntent(pendingIntent)
                .build();*/

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (NOTIFICATION_ID > 107374182){
            NOTIFICATION_ID = 0;
        }

      //  startForeground(1, notification);

        notificationManager.notify(NOTIFICATION_ID,notification.build());
    }
}