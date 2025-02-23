package com.zerostic.goodmorning.Data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

/**
 Coded by iamjayantjha
 **/

public class AlarmRepository {
    private AlarmDao alarmDao;
    private LiveData<List<Alarm>> alarmsLiveData;

    public AlarmRepository(Application application) {
        AlarmDatabase db = AlarmDatabase.getDatabase(application);
        alarmDao = db.alarmDao();
        alarmsLiveData = alarmDao.getAlarms();
    }

    public void insert(Alarm alarm) {
        AlarmDatabase.databaseWriteExecutor.execute(() -> {
            alarmDao.insert(alarm);
        });
    }

    public void update(Alarm alarm) {
        AlarmDatabase.databaseWriteExecutor.execute(() -> {
            alarmDao.update(alarm);
        });
    }

    public void delete(Alarm alarm){
        AlarmDatabase.databaseWriteExecutor.execute(() -> {
            alarmDao.delete(alarm);
        });
    }

    public AlarmDao getAlarmDao(){
        return alarmDao;
    }

    public LiveData<List<Alarm>> getAlarmsLiveData() {
        return alarmsLiveData;
    }
}
