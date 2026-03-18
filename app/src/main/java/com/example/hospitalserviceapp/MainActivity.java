package com.example.hospitalserviceapp;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerServices;
    private EditText etWard, etBed;
    private Button btnSubmit, btnMonetize;
    private DatabaseHelper dbHelper;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("USERNAME");

        spinnerServices = findViewById(R.id.spinner_services);
        etWard = findViewById(R.id.et_ward);
        etBed = findViewById(R.id.et_bed);
        btnSubmit = findViewById(R.id.btn_submit_request);
        btnMonetize = findViewById(R.id.btn_monetize);

        loadServices();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String service = spinnerServices.getSelectedItem() != null ? spinnerServices.getSelectedItem().toString() : "";
                String ward = etWard.getText().toString().trim();
                String bed = etBed.getText().toString().trim();

                if (ward.isEmpty() || bed.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Ward and Bed number are required", Toast.LENGTH_SHORT).show();
                } else if (service.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please select a service", Toast.LENGTH_SHORT).show();
                } else {
                    boolean success = dbHelper.addRequest(username, service, "Requested via mobile", ward, bed);
                    if (success) {
                        Toast.makeText(MainActivity.this, "Request submitted successfully", Toast.LENGTH_SHORT).show();
                        etWard.setText("");
                        etBed.setText("");
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to submit request", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Task 4: Monetization Demo
        btnMonetize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Task 4: Redirecting to Google Play / Payment Gateway...", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadServices() {
        Cursor cursor = dbHelper.getAllServices();
        ArrayList<String> services = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                services.add(cursor.getString(cursor.getColumnIndexOrThrow("service_name")));
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, services);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServices.setAdapter(adapter);
    }
}
