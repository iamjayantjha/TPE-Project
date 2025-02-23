package com.zerostic.goodmorning.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.zerostic.goodmorning.Adapter.AlarmToneAdapter;
import com.zerostic.goodmorning.Application.AlarmTone;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 Coded by iamjayantjha
 **/

public class AlarmToneActivity extends AppCompatActivity {
    RecyclerView toneListRV;
    ImageView audioBtn;
    String value,act,id, method,wuc,payStat;
    boolean toPlay;
    List<AlarmTone> alarmTonesList;
    MediaPlayer mediaPlayer;
    /*MaterialCardView defaultCard, acCard, acOldCard, beepsCard, sirenCard, tuningCard, gi1,gi2,gi3,gi4,gi5,gi6,la1,la2,la3,la4,la5,la6,la7;
    MaterialButton saveBtn;
    ImageView audioOnBtn,audioOFFBtn;*/
   // LottieAnimationView animationView1,animationView2,animationView3,animationView4,animationView5,animationView6,animationView7,animationView8;
    String[] alarmTones = {
            "Default",
            "Alarm Clock",
            "Alarm Clock Old",
            "Beeps",
            "Siren",
            "Tuning",
            "Gradually Increasing 1",
            "Gradually Increasing 2",
            "Gradually Increasing 3",
            "Gradually Increasing 4",
            "Gradually Increasing 5",
            "Gradually Increasing 6",
            "Loud Alarm 1",
            "Loud Alarm 2",
            "Loud Alarm 3",
            "Loud Alarm 4",
            "Loud Alarm 5",
            "Loud Alarm 6",
            "Loud Alarm 7"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_tone);
        Utils.blackIconStatusBar(AlarmToneActivity.this, R.color.background);
        method = getIntent().getStringExtra("method");
        value = getIntent().getStringExtra("tone");
        wuc = getIntent().getStringExtra("wuc");
        act = getIntent().getStringExtra("act");
        payStat = getIntent().getStringExtra("payStat");
        if (act.equals("edit")){
            id = getIntent().getStringExtra("id");
        }
        mediaPlayer = new MediaPlayer();
        toneListRV = findViewById(R.id.toneList);
        audioBtn = findViewById(R.id.audioBtn);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        SharedPreferences preferences = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        toPlay = preferences.getBoolean("toPlay",true);
        if (toPlay){
            audioBtn.setImageResource(R.drawable.ic_audiodark);
            audioBtn.setTag(R.string.on);
        }else {
            audioBtn.setImageResource(R.drawable.ic_audio_off_dark);
            audioBtn.setTag(R.string.off);
        }
        alarmTonesList = new ArrayList<>();
        getAlarmTones();
        audioBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            if (audioBtn.getTag().equals(R.string.on)){
                toPlay = false;
                audioBtn.setImageResource(R.drawable.ic_audio_off_dark);
                audioBtn.setTag(R.string.off);
                if (AlarmToneAdapter.mediaPlayer.isPlaying()){
                    AlarmToneAdapter.mediaPlayer.stop();
                }
            }else {
                toPlay = true;
                audioBtn.setImageResource(R.drawable.ic_audiodark);
                audioBtn.setTag(R.string.on);
            }
            SharedPreferences preferences1=getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences1.edit();
            editor.putBoolean("toPlay",toPlay);
            editor.apply();
            getAlarmTones();
        });
        toneListRV.setOnClickListener(view -> {
           // AlarmToneActivity.this.finish();
            Toast.makeText(AlarmToneActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            vibrator.vibrate(pattern, -1);
            value = String.valueOf(toneListRV.getChildAdapterPosition(view));
            if (act.equals("create")){
                Intent createAlarm = new Intent(AlarmToneActivity.this, CreateAlarmActivity.class);
                createAlarm.putExtra("tone", value);
                createAlarm.putExtra("method",method);
                createAlarm.putExtra("wuc",wuc);
                createAlarm.putExtra("act","tone");
                createAlarm.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(createAlarm);
                finish();
            }else if (act.equals("edit")){
                Intent createAlarm = new Intent(AlarmToneActivity.this, AlarmEditActivity.class);
                createAlarm.putExtra("tone", value);
                createAlarm.putExtra("method",method);
                createAlarm.putExtra("wuc",wuc);
                createAlarm.putExtra("id",id);
                createAlarm.putExtra("act","tone");
                createAlarm.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(createAlarm);
                finish();
            }

        });
    }

    private void getAlarmTones() {
        alarmTonesList.clear();
        int selectedTone = Integer.parseInt(value);
        for (int i = 0; i < alarmTones.length; i++) {
            alarmTonesList.add(new AlarmTone(alarmTones[i], getAlarmResource(alarmTones[i]), isSelected(i, selectedTone)));
        }
        AlarmToneAdapter alarmToneAdapter = new AlarmToneAdapter(alarmTonesList, AlarmToneActivity.this, mediaPlayer, toPlay, this, act, id, method, wuc);
        toneListRV.setAdapter(alarmToneAdapter);
    }

    private boolean isSelected(int i, int selectedTone) {
        return i == selectedTone;
    }

    private int getAlarmResource(String alarmTone) {
        switch (alarmTone) {
            case "Default":
                return R.raw.default_tone;
            case "Alarm Clock":
                return R.raw.alarm_clock;
            case "Alarm Clock Old":
                return R.raw.alarm_clock_old;
            case "Beeps":
                return R.raw.beeps;
            case "Siren":
                return R.raw.siren;
            case "Tuning":
                return R.raw.tuning;
            case "Gradually Increasing 1":
                return R.raw.gradually_increasing1;
            case "Gradually Increasing 2":
                return R.raw.gradually_increasing2;
            case "Gradually Increasing 3":
                return R.raw.gradually_increasing3;
            case "Gradually Increasing 4":
                return R.raw.gradually_increasing4;
            case "Gradually Increasing 5":
                return R.raw.gradually_increasing5;
            case "Gradually Increasing 6":
                return R.raw.gradually_increasing6;
            case "Loud Alarm 1":
                return R.raw.loud_alarm1;
            case "Loud Alarm 2":
                return R.raw.loud_alarm2;
            case "Loud Alarm 3":
                return R.raw.loud_alarm3;
            case "Loud Alarm 4":
                return R.raw.loud_alarm4;
            case "Loud Alarm 5":
                return R.raw.loud_alarm5;
            case "Loud Alarm 6":
                return R.raw.loud_alarm6;
            case "Loud Alarm 7":
                return R.raw.loud_alarm7;
            default:
                return R.raw.default_tone;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AlarmToneAdapter.mediaPlayer.isPlaying()){
            AlarmToneAdapter.mediaPlayer.stop();
        }
    }
}