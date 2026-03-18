package com.example.hospitalserviceapp;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity {

    private ListView lvRequests, lvServices, lvUsers;
    private EditText etNewServiceCode, etNewServiceName;
    private Button btnAddService;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        dbHelper = new DatabaseHelper(this);
        lvRequests = findViewById(R.id.lv_requests);
        lvServices = findViewById(R.id.lv_services);
        lvUsers = findViewById(R.id.lv_users);
        etNewServiceCode = findViewById(R.id.et_new_service_code);
        etNewServiceName = findViewById(R.id.et_new_service_name);
        btnAddService = findViewById(R.id.btn_add_service);

        refreshData();

        btnAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etNewServiceCode.getText().toString().trim();
                String name = etNewServiceName.getText().toString().trim();

                if (code.isEmpty() || name.isEmpty()) {
                    Toast.makeText(AdminDashboardActivity.this, "Please fill both fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (dbHelper.addService(code, name)) {
                        Toast.makeText(AdminDashboardActivity.this, "Service Added", Toast.LENGTH_SHORT).show();
                        etNewServiceCode.setText("");
                        etNewServiceName.setText("");
                        refreshData();
                    } else {
                        Toast.makeText(AdminDashboardActivity.this, "Error Adding Service", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Delete Service on Click
        lvServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Simplified deletion logic using position/index for this prototype
                Cursor cursor = dbHelper.getAllServices();
                if (cursor.moveToPosition(position)) {
                    int serviceId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    if (dbHelper.deleteService(serviceId)) {
                        Toast.makeText(AdminDashboardActivity.this, "Service Removed", Toast.LENGTH_SHORT).show();
                        refreshData();
                    }
                }
                cursor.close();
            }
        });

        // Delete User on Click
        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = dbHelper.getAllUsers();
                if (cursor.moveToPosition(position)) {
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
                    if (role.equals("admin")) {
                        Toast.makeText(AdminDashboardActivity.this, "Cannot delete admin", Toast.LENGTH_SHORT).show();
                    } else if (dbHelper.deleteUser(userId)) {
                        Toast.makeText(AdminDashboardActivity.this, "User Deleted", Toast.LENGTH_SHORT).show();
                        refreshData();
                    }
                }
                cursor.close();
            }
        });
    }

    private void refreshData() {
        loadRequests();
        loadServices();
        loadUsers();
    }

    private void loadRequests() {
        Cursor cursor = dbHelper.getAllRequests();
        ArrayList<String> requestList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String user = cursor.getString(cursor.getColumnIndexOrThrow("user_name"));
                String service = cursor.getString(cursor.getColumnIndexOrThrow("service_name"));
                String ward = cursor.getString(cursor.getColumnIndexOrThrow("ward"));
                String bed = cursor.getString(cursor.getColumnIndexOrThrow("bed"));
                requestList.add("User: " + user + "\nService: " + service + " (Ward " + ward + ", Bed " + bed + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();
        lvRequests.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, requestList));
    }

    private void loadServices() {
        Cursor cursor = dbHelper.getAllServices();
        ArrayList<String> serviceList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                serviceList.add(cursor.getString(cursor.getColumnIndexOrThrow("service_code")) + " - " + cursor.getString(cursor.getColumnIndexOrThrow("service_name")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        lvServices.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, serviceList));
    }

    private void loadUsers() {
        Cursor cursor = dbHelper.getAllUsers();
        ArrayList<String> userList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                userList.add(cursor.getString(cursor.getColumnIndexOrThrow("username")) + " (" + cursor.getString(cursor.getColumnIndexOrThrow("role")) + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();
        lvUsers.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList));
    }
}
