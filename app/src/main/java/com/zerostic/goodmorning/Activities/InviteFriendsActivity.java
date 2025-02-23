package com.zerostic.goodmorning.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Utils;

public class InviteFriendsActivity extends AppCompatActivity {
    String instagram = "com.instagram.android";
    String whatsapp = "com.whatsapp";
    String messenger = "com.facebook.orca";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        Utils.blackIconStatusBar(InviteFriendsActivity.this, R.color.background);
        TextView message = findViewById(R.id.message);
        ImageView whatsappBtn = findViewById(R.id.whatsappBtn);
        ImageView instagramBtn = findViewById(R.id.instagramBtn);
        ImageView messengerBtn = findViewById(R.id.messengerBtn);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {40,80};
        String name = getIntent().getStringExtra("name");
        String messageText = name+" enjoying your early mornings, invite your friends to make their mornings beautiful";
        message.setText(messageText);
        whatsappBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage(whatsapp);
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "*Good Morning*\n\nI found this app on Play Store and it helps me wake up every morning. You must try this out, download now from the link below\uD83D\uDC47\uD83C\uDFFB\n\n https://zerostic.link/gm \n\nIf you want to know more about the app then read it on https://zerostic.com/");
            try {
                startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(),"Whatsapp is not installed.",Toast.LENGTH_SHORT).show();
            }
        });
        instagramBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage(instagram);
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Good Morning\n\nI found this app on Play Store and it helps me wake up every morning. You must try this out, download now from the link below\uD83D\uDC47\uD83C\uDFFB\n\n https://zerostic.link/gm \n\nIf you want to know more about the app then read it on https://zerostic.com/");
            try {
                startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(),"Instagram is not installed.",Toast.LENGTH_SHORT).show();
            }
        });
        messengerBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage(messenger);
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Good Morning\n\nI found this app on Play Store and it helps me wake up every morning. You must try this out, download now from the link below\uD83D\uDC47\uD83C\uDFFB\n\n https://zerostic.link/gm \n\nIf you want to know more about the app then read it on https://zerostic.com/");
            try {
                startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(),"Messenger is not installed.",Toast.LENGTH_SHORT).show();
            }
        });
    }
}