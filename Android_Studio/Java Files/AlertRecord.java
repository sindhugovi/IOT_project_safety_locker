package com.example.safe_lock;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alert_records")
public class AlertRecord {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String message;
    public String timestamp;

    public AlertRecord(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
}
