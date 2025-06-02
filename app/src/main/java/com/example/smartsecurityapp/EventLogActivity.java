package com.example.smartsecurityapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventLogActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlertAdapter adapter;
    private List<Alert> alertList;
    private DatabaseReference databaseReference;
    private TextView textViewLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_log);
        databaseReference= FirebaseDatabase.getInstance().getReference("event_logs");

        recyclerView = findViewById(R.id.recyclerView);
        textViewLog = findViewById(R.id.textViewLog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        alertList = new ArrayList<>();
        adapter = new AlertAdapter(alertList);
        recyclerView.setAdapter(adapter);

        // Reference to Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("alerts");

        // Retrieve alerts from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alertList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Alert alert = dataSnapshot.getValue(Alert.class);
                    alertList.add(alert);
                }
                Collections.reverse(alertList); // ça inverse la liste pour avoir la plus récente en haut
                adapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}