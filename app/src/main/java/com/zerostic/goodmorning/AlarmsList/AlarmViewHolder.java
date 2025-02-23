package com.zerostic.goodmorning.AlarmsList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Data.Alarm;

/**
 Coded by iamjayantjha
 **/

public class AlarmViewHolder extends RecyclerView.ViewHolder {
    private TextView alarmTime,method;
    private ImageView alarmRecurring,delete,alarmNotRecurring;
    private TextView alarmRecurringDays;
    private TextView alarmTitle,wuc;
    SwitchMaterial alarmStarted;
    MaterialCardView alarmCard;
    int alarmID;
    private OnToggleAlarmListener listener;
    private onDeleteAlarmListener newListener;
    private alarmUpdateListener updateListener;
    private Boolean deleteAlarm;

    public AlarmViewHolder(@NonNull View itemView, OnToggleAlarmListener listener, onDeleteAlarmListener newListener, alarmUpdateListener updateListener) {
        super(itemView);

        alarmTime = itemView.findViewById(R.id.item_alarm_time);
        wuc = itemView.findViewById(R.id.wuc);
        alarmStarted = itemView.findViewById(R.id.item_alarm_started);
        alarmRecurring = itemView.findViewById(R.id.item_alarm_recurring);
        alarmNotRecurring = itemView.findViewById(R.id.item_alarm_not_recurring);
        alarmRecurringDays = itemView.findViewById(R.id.item_alarm_recurringDays);
        alarmTitle = itemView.findViewById(R.id.item_alarm_title);
        delete = itemView.findViewById(R.id.deleteBtn);
        alarmCard = itemView.findViewById(R.id.alarm_card);
        method = itemView.findViewById(R.id.method);
        this.listener = listener;
        this.newListener = newListener;
        this.updateListener = updateListener;
    }

    public void bind(Alarm alarm) {

        int hour,min;
        String a,b;
        hour = alarm.getHour();
        min = alarm.getMinute();
        alarmID = alarm.getAlarmId();
        if (alarm.isWUC()){
            wuc.setText("Wake Up Check");
        }else {
            wuc.setText("");
        }
        int length = (int)(Math.log10(min)+1);
        if (length<2){
            if (hour>=12){
                hour = hour-12;
                if (hour == 0){
                    hour = 12;
                }
                a = " PM";
                b=hour+":"+"0"+min+a;
                alarmTime.setText(b);
                alarmStarted.setChecked(alarm.isStarted());
            }
            else {
                if (hour == 0){
                    hour = 12;
                }

                a = " AM";
                b= hour+":"+"0"+min+a;
                alarmTime.setText(b);
                alarmStarted.setChecked(alarm.isStarted());
            }
        }else {
            if (hour>=12){
                hour = hour-12;
                if (hour == 0){
                    hour = 12;
                }
                a = " PM";
                b= hour+":"+min+a;
                alarmTime.setText(b);
            }else {
                if (hour == 0){
                    hour = 12;
                }

                a = " AM";
                b= hour+":"+min+a;
                alarmTime.setText(b);
            }
        }

        if (alarm.isRecurring()) {
            alarmRecurring.setVisibility(View.VISIBLE);
            alarmNotRecurring.setVisibility(View.GONE);
            alarmRecurringDays.setText(alarm.getRecurringDaysText());
        } else {
            alarmRecurring.setVisibility(View.GONE);
            alarmNotRecurring.setVisibility(View.VISIBLE);
            alarmRecurringDays.setText("Repeat -> OFF");
        }

        if (alarm.getTitle().length() != 0) {
            alarmTitle.setText(alarm.getTitle());
        } else {
            alarmTitle.setText("Alarm");
        }
        method.setText(alarm.getMethod());

        if (alarm.isStarted()) {
            alarmStarted.setChecked(true);
            deleteAlarm = false;
        }else {
            deleteAlarm = true;
        }


        alarmStarted.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onToggle(alarm));



        delete.setOnClickListener(view -> {
            if (deleteAlarm){
                newListener.onDelete(alarm);
            }   else {
                listener.onToggle(alarm);
                newListener.onDelete(alarm);
            }
//            newListener.onDelete(alarm);
//            listener.onToggle(alarm);
//            if (alarmStarted.isChecked()){
//                alarmStarted.setChecked(false);
//            }
        });

        alarmCard.setOnClickListener(v -> {
            updateListener.onUpdate(alarm);
        });

    }

}
