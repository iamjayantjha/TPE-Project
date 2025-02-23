package com.zerostic.goodmorning.CreateAlarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Activities.MethodActivity;
import com.zerostic.goodmorning.AlarmsList.AlarmsListFragment;
import com.zerostic.goodmorning.Data.Alarm;

import java.util.Calendar;
import java.util.Random;

/**
 Coded by iamjayantjha
 **/

public class CreateAlarmFragment extends Fragment {
    TimePicker timePicker;
    EditText title;
    Button scheduleAlarm;
    CheckBox recurring;
    CheckBox mon, tue, wed, thu, fri, sat, sun;
    TextView ringsIn, method;
    CardView methodCard;
    RelativeLayout recurringOptions;
    String methodText ;
    int alarmTone  = R.raw.default_tone;
    private CreateAlarmViewModel createAlarmViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createAlarmViewModel = ViewModelProviders.of(this).get(CreateAlarmViewModel.class);
        //methodText = getActivity().getIntent().getExtras().getString("method");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createalarm, container, false);
        timePicker = view.findViewById(R.id.timePicker);
        title = view.findViewById(R.id.title);
        scheduleAlarm = view.findViewById(R.id.scheduleAlarmBtn);
        recurring = view.findViewById(R.id.recurring);
        mon = view.findViewById(R.id.mon);
        tue = view.findViewById(R.id.tue);
        wed = view.findViewById(R.id.wed);
        thu = view.findViewById(R.id.thu);
        fri = view.findViewById(R.id.fri);
        sat = view.findViewById(R.id.sat);
        sun = view.findViewById(R.id.sun);
        recurringOptions = view.findViewById(R.id.recurring_options);
        methodCard = view.findViewById(R.id.methodCard);
        ringsIn = view.findViewById(R.id.ringsIn);
        method = view.findViewById(R.id.method);
        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        methodText = preferences.getString("method", "Default");

        /*if (!alarmDetails.getMethod().isEmpty() && method.getText().toString() != alarmDetails.getMethod()){
            method.setText(alarmDetails.getMethod());
        }*/
        method.setText(methodText);
        Calendar calendar = Calendar.getInstance();
        timePicker.setOnTimeChangedListener((timePicker, hr, mins) -> {
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hr);
            calendar.set(Calendar.MINUTE, mins);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            }
            long time = calendar.getTimeInMillis() - System.currentTimeMillis() ;
            long mills = Math.abs(time);
            int hours = (int) (mills / (1000 * 60 * 60));
            int minutes = (int) ((mills / (1000 * 60)) % 60);
            if (hours == 0){
                if (minutes != 0){
                    if (minutes > 1){
                        ringsIn.setText("Rings in "+minutes+" minutes");
                    }else if (minutes == 1){
                        ringsIn.setText("Rings in "+minutes+" minute");
                    }
                }else {
                    ringsIn.setText("Rings in less than a minute.");
                }
            }else if (hours == 1){
                    if (minutes == 1){
                        ringsIn.setText("Rings in "+hours+" hour"+" and "+minutes+" minute");
                    }else if (minutes > 1){
                        ringsIn.setText("Rings in "+hours+" hour"+" and "+minutes+" minutes");
                    }else {
                        ringsIn.setText("Rings in "+hours+" hour");
                    }
                }

             else {
                    if (minutes == 1){
                        ringsIn.setText("Rings in "+hours+" hours"+" and "+minutes+" minute");
                    }else if (minutes > 1){
                        ringsIn.setText("Rings in "+hours+" hours"+" and "+minutes+" minutes");
                    }else {
                        ringsIn.setText("Rings in "+hours+" hours");
                    }
                }


        });

        methodCard.setOnClickListener(view1 -> {
            Intent methodIntent = new Intent(getContext(), MethodActivity.class);
            startActivity(methodIntent);
        });

        recurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recurringOptions.setVisibility(View.VISIBLE);
                    if (sun.isChecked()||sat.isChecked()){
                        recurring.setText("Everyday");
                    }else {
                        recurring.setText("Weekdays");
                    }
                } else {
                    recurringOptions.setVisibility(View.GONE);
                    recurring.setText("Repeat Alarm");
                }
            }
        });

        sun.setOnCheckedChangeListener((compoundButton, b) -> {
            if (sun.isChecked()||sat.isChecked()){
                recurring.setText("Everyday");
            }else {
                recurring.setText("Weekdays");
            }

        });



        sat.setOnCheckedChangeListener((compoundButton, b) -> {
            if (sun.isChecked()||sat.isChecked()){
                recurring.setText("Everyday");
            }else {
                recurring.setText("Weekdays");
            }
        });


        scheduleAlarm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                scheduleAlarm();
                AlarmsListFragment alarmsListFragment = new AlarmsListFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_frame, alarmsListFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
//                Navigation.findNavController(v).navigate(R.id.action_createAlarmFragment_to_alarmsListFragment);
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void scheduleAlarm() {
        int alarmId = new Random().nextInt(Integer.MAX_VALUE);
        Alarm alarm = new Alarm(
                alarmId,
                TimePickerUtil.getTimePickerHour(timePicker),
                TimePickerUtil.getTimePickerMinute(timePicker),
                "alarmTone",
                title.getText().toString(),
                method.getText().toString(),
                System.currentTimeMillis(),
                true,
                recurring.isChecked(),
                mon.isChecked(),
                tue.isChecked(),
                wed.isChecked(),
                thu.isChecked(),
                fri.isChecked(),
                sat.isChecked(),
                sun.isChecked(),
                false
        );

        createAlarmViewModel.insert(alarm);

        alarm.schedule(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        recurring.setChecked(true);
        mon.setChecked(true);
        tue.setChecked(true);
        wed.setChecked(true);
        thu.setChecked(true);
        fri.setChecked(true);
   /*     AlarmDetails alarmDetails = new AlarmDetails();
        method.setText(alarmDetails.getMethod());*/
       // Toast.makeText(getContext(),alarmDetails.getMethod(),Toast.LENGTH_SHORT).show();
    }
}
