package com.zerostic.goodmorning.CreateAlarm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerostic.goodmorning.Data.Alarm;
import com.zerostic.goodmorning.Data.AlarmRepository;

public class CreateAlarmViewModel extends AndroidViewModel {
    private AlarmRepository alarmRepository;

    public CreateAlarmViewModel(@NonNull Application application) {
        super(application);

        alarmRepository = new AlarmRepository(application);
    }

    public void insert(Alarm alarm) {
        alarmRepository.insert(alarm);
    }

    public void update(Alarm alarm) {
        alarmRepository.update(alarm);
    }
}
