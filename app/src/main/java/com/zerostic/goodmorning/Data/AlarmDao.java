package com.zerostic.goodmorning.Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 Coded by iamjayantjha
 **/

@Dao
public interface AlarmDao {
    @Insert
    void insert(Alarm alarm);

    @Query("DELETE FROM alarm_table")

    void deleteAll();

    @Query("SELECT * FROM alarm_table ORDER BY created ASC")
    LiveData<List<Alarm>> getAlarms();

    @Update
    void update(Alarm alarm);

    @Delete
    void delete(Alarm alarm);

    @Query("SELECT * FROM alarm_table WHERE started = 1 AND created > :currentTime ORDER BY created ASC LIMIT 1")
    Alarm getNextAlarm(long currentTime);
}
