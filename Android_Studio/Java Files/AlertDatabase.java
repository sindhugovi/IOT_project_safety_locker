package com.example.safe_lock;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.safe_lock.AlertRecord;

@Database(entities = {AlertRecord.class}, version = 1)
public abstract class AlertDatabase extends RoomDatabase {

    public abstract com.example.safe_lock.AlertRecordDao alertRecordDao();

    private static volatile AlertDatabase INSTANCE;

    public static AlertDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AlertDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AlertDatabase.class, "alert_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
