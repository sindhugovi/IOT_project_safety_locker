package com.example.safe_lock;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class LockerActivity extends AppCompatActivity {

    private AlertDatabase alertDatabase;
    private RecordAdapter unlockedAdapter;
    private RecordAdapter emergencyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker);

        // Initialize RecyclerViews
        RecyclerView unlockedRecyclerView = findViewById(R.id.unlockedRecyclerView);
        RecyclerView emergencyRecyclerView = findViewById(R.id.emergencyRecyclerView);

        unlockedAdapter = new RecordAdapter();
        emergencyAdapter = new RecordAdapter();

        unlockedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        unlockedRecyclerView.setAdapter(unlockedAdapter);

        emergencyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        emergencyRecyclerView.setAdapter(emergencyAdapter);

        // Initialize Room Database
        alertDatabase = AlertDatabase.getDatabase(this);

        // Load existing records
        loadUnlockedRecords();
        loadEmergencyRecords();

        // Firebase listeners
        setupFirebaseListeners();
    }

    private void setupFirebaseListeners() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Listen for password_correct changes
        databaseReference.child("password_correct").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer value = snapshot.getValue(Integer.class);
                if (value != null && value == 1) {
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    AlertRecord newRecord = new AlertRecord("Safe Unlocked", timestamp);
                    saveUnlockedRecord(newRecord);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LockerActivity.this, "Failed to read data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Listen for emergency_alert changes
        databaseReference.child("emergency_alert").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer value = snapshot.getValue(Integer.class);
                if (value != null && value == 1) {
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    AlertRecord newRecord = new AlertRecord("Emergency Alert", timestamp);
                    saveEmergencyRecord(newRecord);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LockerActivity.this, "Failed to read data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUnlockedRecords() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<AlertRecord> records = alertDatabase.alertRecordDao().getLast5Unlocked();
            runOnUiThread(() -> unlockedAdapter.setRecords(records));
        });
    }

    private void loadEmergencyRecords() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<AlertRecord> records = alertDatabase.alertRecordDao().getLast5Emergency();
            runOnUiThread(() -> emergencyAdapter.setRecords(records));
        });
    }

    private void saveUnlockedRecord(AlertRecord record) {
        Executors.newSingleThreadExecutor().execute(() -> {
            alertDatabase.alertRecordDao().insert(record);
            loadUnlockedRecords();
        });
    }

    private void saveEmergencyRecord(AlertRecord record) {
        Executors.newSingleThreadExecutor().execute(() -> {
            alertDatabase.alertRecordDao().insert(record);
            loadEmergencyRecords();
        });
    }
}
