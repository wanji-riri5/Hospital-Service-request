package com.example.hospitalserviceapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "HospitalApp.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_SERVICES = "services";
    private static final String TABLE_REQUESTS = "requests";

    // Common columns
    private static final String COLUMN_ID = "id";

    // Users Table Columns
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role"; // "admin" or "user"

    // Services Table Columns
    private static final String COLUMN_SERVICE_CODE = "service_code";
    private static final String COLUMN_SERVICE_NAME = "service_name";

    // Requests Table Columns
    private static final String COLUMN_REQ_USER = "user_name";
    private static final String COLUMN_REQ_SERVICE = "service_name";
    private static final String COLUMN_REQ_NOTES = "notes";
    private static final String COLUMN_REQ_WARD = "ward";
    private static final String COLUMN_REQ_BED = "bed";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_ROLE + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create Services Table
        String CREATE_SERVICES_TABLE = "CREATE TABLE " + TABLE_SERVICES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SERVICE_CODE + " TEXT,"
                + COLUMN_SERVICE_NAME + " TEXT" + ")";
        db.execSQL(CREATE_SERVICES_TABLE);

        // Create Requests Table
        String CREATE_REQUESTS_TABLE = "CREATE TABLE " + TABLE_REQUESTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_REQ_USER + " TEXT,"
                + COLUMN_REQ_SERVICE + " TEXT,"
                + COLUMN_REQ_NOTES + " TEXT,"
                + COLUMN_REQ_WARD + " TEXT,"
                + COLUMN_REQ_BED + " TEXT" + ")";
        db.execSQL(CREATE_REQUESTS_TABLE);

        // Insert Default Services
        insertDefaultServices(db);
        
        // Insert a default admin for testing
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, "admin");
        values.put(COLUMN_EMAIL, "admin@hospital.com");
        values.put(COLUMN_PASSWORD, "admin123");
        values.put(COLUMN_ROLE, "admin");
        db.insert(TABLE_USERS, null, values);
    }

    private void insertDefaultServices(SQLiteDatabase db) {
        String[][] services = {
                {"CL001", "Cleaning"},
                {"EP002", "Equipment assistance"},
                {"LC001", "Linen change"},
                {"PS001", "Porter services"}
        };
        for (String[] s : services) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SERVICE_CODE, s[0]);
            values.put(COLUMN_SERVICE_NAME, s[1]);
            db.insert(TABLE_SERVICES, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTS);
        onCreate(db);
    }

    // User Operations
    public boolean registerUser(String username, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public Cursor checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{username, password});
    }

    // Service Operations
    public Cursor getAllServices() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SERVICES, null);
    }

    public boolean addService(String code, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SERVICE_CODE, code);
        values.put(COLUMN_SERVICE_NAME, name);
        return db.insert(TABLE_SERVICES, null, values) != -1;
    }

    public boolean deleteService(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SERVICES, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    // Request Operations
    public boolean addRequest(String user, String service, String notes, String ward, String bed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_REQ_USER, user);
        values.put(COLUMN_REQ_SERVICE, service);
        values.put(COLUMN_REQ_NOTES, notes);
        values.put(COLUMN_REQ_WARD, ward);
        values.put(COLUMN_REQ_BED, bed);
        return db.insert(TABLE_REQUESTS, null, values) != -1;
    }

    public Cursor getAllRequests() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_REQUESTS, null);
    }

    public boolean deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USERS, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }
    
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
    }
}
