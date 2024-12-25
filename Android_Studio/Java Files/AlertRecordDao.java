package com.example.safe_lock;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AlertRecordDao {

    @Insert
    void insert(AlertRecord record);

    @Query("SELECT * FROM alert_records ORDER BY id DESC LIMIT 10")
    List<AlertRecord> getLast10Records();

    @Query("DELETE FROM alert_records")
    void deleteAll();
    @Query("SELECT * FROM alert_records WHERE message = 'Safe Unlocked' ORDER BY id DESC LIMIT 5")
    List<AlertRecord> getLast5Unlocked();

    @Query("SELECT * FROM alert_records WHERE message = 'Emergency Alert' ORDER BY id DESC LIMIT 5")
    List<AlertRecord> getLast5Emergency();

}

