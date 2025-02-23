package com.zerostic.goodmorning.AlarmsList;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.zerostic.goodmorning.Data.Alarm;
import com.zerostic.goodmorning.R;

import java.util.ArrayList;
import java.util.List;
/**
 Coded by iamjayantjha
 **/

public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmViewHolder> {
    private List<Alarm> alarms;
    private OnToggleAlarmListener listener;
    private  onDeleteAlarmListener newListener;
    private alarmUpdateListener updateListener;
    MaterialCardView alarmCard;

    public AlarmRecyclerViewAdapter(OnToggleAlarmListener listener, onDeleteAlarmListener newListener, alarmUpdateListener updateListener) {
        this.alarms = new ArrayList<Alarm>();
        this.listener = listener;
        this.newListener = newListener;
        this.updateListener = updateListener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        ImageView deleteBtn = itemView.findViewById(R.id.deleteBtn);
        alarmCard = itemView.findViewById(R.id.alarm_card);
        return new AlarmViewHolder(itemView, listener, newListener, updateListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = alarms.get(position);
        holder.bind(alarm);
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(@NonNull AlarmViewHolder holder) {
        super.onViewRecycled(holder);
        holder.alarmStarted.setOnCheckedChangeListener(null);
    }

    public Alarm getAlarmAt(int adapterPosition) {
        return alarms.get(adapterPosition);
    }
}

