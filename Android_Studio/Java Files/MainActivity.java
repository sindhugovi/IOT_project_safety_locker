package com.example.safe_lock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class MainActivity extends AppCompatActivity {

    private AlertDatabase alertDatabase;
    private RecordAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recordAdapter = new RecordAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recordAdapter);

        // Initialize Room Database
        alertDatabase = AlertDatabase.getDatabase(this);

        // Load existing records
        loadRecords();

        // Set the OnClickListener for the Locker button
        Button lockerButton = findViewById(R.id.lockerButton);
        lockerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LockerActivity.class);
            startActivity(intent);
        });

        // Firebase listener to monitor "object_too_close" field
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("object_too_close");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer objectTooCloseValue = snapshot.getValue(Integer.class);
                if (objectTooCloseValue != null && objectTooCloseValue == 1) {
                    // Create a new alert record for object too close
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    AlertRecord newRecord = new AlertRecord("Alert: Someone reached near the safe!", timestamp);

                    // Save the record to the database
                    saveRecord(newRecord);

                    // Notify user
                    Toast.makeText(MainActivity.this, "Alert! Someone reached near the safe!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to read data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load the last 10 records from the database
    private void loadRecords() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<AlertRecord> records = alertDatabase.alertRecordDao().getLast10Records();
            runOnUiThread(() -> recordAdapter.setRecords(records));
        });
    }

    // Save a new record to the database
    private void saveRecord(AlertRecord record) {
        Executors.newSingleThreadExecutor().execute(() -> {
            alertDatabase.alertRecordDao().insert(record);
            loadRecords(); // Refresh the UI with updated records
        });
    }
}
