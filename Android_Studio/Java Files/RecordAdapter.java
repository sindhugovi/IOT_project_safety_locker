package com.example.safe_lock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private final List<AlertRecord> recordList = new ArrayList<>();

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_recordadapter, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        AlertRecord record = recordList.get(position);
        holder.messageTextView.setText(record.message);
        holder.timestampTextView.setText(record.timestamp);
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public void setRecords(List<AlertRecord> records) {
        recordList.clear();
        recordList.addAll(records);
        notifyDataSetChanged();
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, timestampTextView;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.recordTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }
    }
}
