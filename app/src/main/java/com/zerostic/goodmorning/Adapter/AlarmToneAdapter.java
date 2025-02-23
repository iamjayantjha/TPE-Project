package com.zerostic.goodmorning.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.zerostic.goodmorning.Activities.AlarmEditActivity;
import com.zerostic.goodmorning.Activities.AlarmToneActivity;
import com.zerostic.goodmorning.Activities.CreateAlarmActivity;
import com.zerostic.goodmorning.Application.AlarmTone;
import com.zerostic.goodmorning.R;

import java.util.List;

public class AlarmToneAdapter extends RecyclerView.Adapter<AlarmToneAdapter.AlarmToneViewHolder>{
    List<AlarmTone> alarmTones;
    AlarmToneActivity alarmToneActivity;
    boolean toPlay;
    public static MediaPlayer mediaPlayer;
    String act,id, method,wuc;
    Context context;
    Vibrator vibrator;
    final long[] pattern = {40, 80};

    public AlarmToneAdapter(List<AlarmTone> alarmTones, AlarmToneActivity alarmToneActivity, MediaPlayer mediaPlayer, boolean toPlay, Context context, String act, String id, String method, String wuc) {
        this.alarmTones = alarmTones;
        this.alarmToneActivity = alarmToneActivity;
        AlarmToneAdapter.mediaPlayer = mediaPlayer;
        this.toPlay = toPlay;
        this.context = context;
        this.act = act;
        this.id = id;
        this.method = method;
        this.wuc = wuc;
    }

    @NonNull
    @Override
    public AlarmToneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        View view = View.inflate(parent.getContext(), R.layout.alarm_tone_item, null);
        return new AlarmToneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmToneViewHolder holder, int position) {
        AlarmTone alarmTone = alarmTones.get(position);
        holder.toneName.setText(alarmTone.getToneName());
        if (alarmTone.isSelected()) {
            holder.toneCard.setStrokeColor(ContextCompat.getColor(context, R.color.selected));
        } else {
            holder.toneCard.setStrokeColor(ContextCompat.getColor(context, R.color.cardStrokeColor));
        }
        holder.toneCard.setOnClickListener(v -> {
            for (AlarmTone tone : alarmTones) {
                tone.setSelected(false);
            }
            alarmTone.setSelected(true);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            if (toPlay){
                mediaPlayer = MediaPlayer.create(context, alarmTone.getToneResId());
                mediaPlayer.setLooping(false);
                mediaPlayer.start();
            }
            vibrator.vibrate(pattern, -1);
            Intent intent;
            if (act.equals("create")){
                intent = new Intent(context, CreateAlarmActivity.class);
                intent.putExtra("tone", String.valueOf(position));
                intent.putExtra("method",method);
                intent.putExtra("wuc",wuc);
            }else {
                intent = new Intent(context, AlarmEditActivity.class);
                intent.putExtra("tone", String.valueOf(position));
                intent.putExtra("method",method);
                intent.putExtra("wuc",wuc);
                intent.putExtra("id",id);
            }
            intent.putExtra("act","tone");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notifyDataSetChanged();
            if (toPlay){
                mediaPlayer.setOnCompletionListener(mp -> {
                    context.startActivity(intent);
                    alarmToneActivity.finish();
                });
            }else {
                context.startActivity(intent);
                alarmToneActivity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmTones.size();
    }

    static class AlarmToneViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView toneCard;
        TextView toneName, toneSelected;
        public AlarmToneViewHolder(@NonNull View itemView) {
            super(itemView);
            toneCard = itemView.findViewById(R.id.toneCard);
            toneName = itemView.findViewById(R.id.toneName);
            toneSelected = itemView.findViewById(R.id.toneSelected);
        }
    }
}
