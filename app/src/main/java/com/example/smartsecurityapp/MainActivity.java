package com.example.smartsecurityapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tvStatus, tvDistance;
    private ImageView imgStatus;
    private Button btnToggle, btnViewLogs;
    private RecyclerView recyclerView;
    private AlertAdapter alertAdapter;
    private ArrayList<Alert> alertList;
    private boolean isArmed = false;

    private DatabaseReference systemStatusRef;
    private DatabaseReference sensorDataRef;
    private DatabaseReference alertsRef;
    private DatabaseReference secretKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Références Firebase
        systemStatusRef = FirebaseDatabase.getInstance().getReference("systemStatus");
        sensorDataRef = FirebaseDatabase.getInstance().getReference();
        alertsRef = FirebaseDatabase.getInstance().getReference("alerts");
        secretKeyRef = FirebaseDatabase.getInstance().getReference("security/secretKey");

        // Initialisation des vues
        tvStatus = findViewById(R.id.tvStatus);
        tvDistance = findViewById(R.id.tvDistance);
        imgStatus = findViewById(R.id.imgStatus);
        btnToggle = findViewById(R.id.btnToggle);
        btnViewLogs = findViewById(R.id.btnViewLogs);
        recyclerView = findViewById(R.id.recyclerView);

        // Setup RecyclerView
        alertList = new ArrayList<>();
        alertAdapter = new AlertAdapter(alertList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(alertAdapter);

        // Charger les alertes
        alertsRef.limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alertList.clear();
                for (DataSnapshot alertSnap : snapshot.getChildren()) {
                    Alert alert = alertSnap.getValue(Alert.class);
                    if (alert != null) {
                        alertList.add(0, alert);
                    }
                }
                alertAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load alerts: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Bouton ARM/DISARM
        btnToggle.setOnClickListener(v -> {
            if (!isArmed) {
                showKeyInputDialog(); // Demander la clé avant d'armer
            } else {
                disarmSystem(); // Désarmer directement
            }
        });

        // Bouton Logs
        btnViewLogs.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EventLogActivity.class);
            startActivity(intent);
        });

        // Données capteur
        sensorDataRef.child("alerte").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot alertSnap : snapshot.getChildren()) {
                    Double distance = alertSnap.child("distance").getValue(Double.class);
                    if (distance != null) {
                        tvDistance.setText(getString(R.string.distance_text, distance));
                        tvDistance.setTextColor(ContextCompat.getColor(MainActivity.this,
                                distance < 15 ? android.R.color.holo_red_dark : android.R.color.black));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load alert data: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    // Afficher boîte de dialogue pour la clé
    private void showKeyInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Security Key");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String userInput = input.getText().toString();
            validateKey(userInput);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Vérifier la clé avec Firebase
    private void validateKey(String userInput) {
        secretKeyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String correctKey = snapshot.getValue(String.class);
                if (correctKey != null && correctKey.equals(userInput)) {
                    armSystem();
                } else {
                    Toast.makeText(MainActivity.this, "Incorrect key!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to read key: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Armer le système
    private void armSystem() {
        isArmed = true;
        systemStatusRef.child("isArmed").setValue(true);
        tvStatus.setText(getString(R.string.system_armed));
        imgStatus.setImageResource(R.drawable.img);
        btnToggle.setText(getString(R.string.system_disarmed));
    }

    // Désarmer le système
    private void disarmSystem() {
        isArmed = false;
        systemStatusRef.child("isArmed").setValue(false);
        tvStatus.setText(getString(R.string.system_disarmed));
        imgStatus.setImageResource(R.drawable.img_1);
        btnToggle.setText(getString(R.string.system_armed));
    }
}


