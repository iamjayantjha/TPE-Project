package com.zerostic.goodmorning.Service;

import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import com.zerostic.goodmorning.Data.Alarm;
import com.zerostic.goodmorning.Data.AlarmRepository;

/**
 Coded by iamjayantjha
 **/

public class RescheduleAlarmsService extends LifecycleService {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        AlarmRepository alarmRepository = new AlarmRepository(getApplication());

        alarmRepository.getAlarmsLiveData().observe(this, alarms -> {
            for (Alarm a : alarms) {
                if (a.isStarted()) {
                    a.schedule(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Alarms Rescheduled", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "No Alarms To Rescheduled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }
}
