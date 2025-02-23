package com.zerostic.goodmorning.AlarmsList;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.zerostic.goodmorning.Data.Alarm;
import com.zerostic.goodmorning.Data.AlarmDao;
import com.zerostic.goodmorning.Data.AlarmRepository;

import java.util.List;

/**
 Coded by iamjayantjha
 **/

public class AlarmsListViewModel extends AndroidViewModel {
    private AlarmRepository alarmRepository;
    private LiveData<List<Alarm>> alarmsLiveData;
    private final AlarmDao alarmDao;

    public AlarmsListViewModel(@NonNull Application application) {
        super(application);

        alarmRepository = new AlarmRepository(application);
        alarmsLiveData = alarmRepository.getAlarmsLiveData();
        alarmDao = alarmRepository.getAlarmDao();

    }

    public void update(Alarm alarm) {
        alarmRepository.update(alarm);
    }
    public void remove(Alarm alarm) {
        alarmRepository.delete(alarm);
    }

    public void delete(Alarm alarm){
        alarmRepository.delete(alarm);
    }

    public LiveData<List<Alarm>> getAlarmsLiveData() {
        return alarmsLiveData;
    }

    public Alarm getNextAlarm() {
        return alarmDao.getNextAlarm(System.currentTimeMillis());
    }

    public void insert(Alarm alarm) {
        alarmRepository.insert(alarm);
    }
}
