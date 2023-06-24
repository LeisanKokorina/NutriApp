package com.example.nutriapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.mindrot.jbcrypt.BCrypt;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String CREATE_TABLE_QUERY =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_USERNAME + " TEXT PRIMARY KEY," +
                    COLUMN_PASSWORD + " TEXT)";


    private static final String TABLE_SESSIONS = "sessions";
    private static final String COLUMN_SESSION_ID = "session_id";

    private static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String CREATE_TABLE_SESSIONS_QUERY =
            "CREATE TABLE " + TABLE_SESSIONS + " (" +
                    COLUMN_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_USERNAME + " TEXT," +
                    COLUMN_TIMESTAMP + " INTEGER)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the users table
        db.execSQL(CREATE_TABLE_QUERY);
        // Create the sessions table
        db.execSQL(CREATE_TABLE_SESSIONS_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing table if needed and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_PASSWORD, hashPassword(password));
            return db.insert(TABLE_NAME, null, values);
        } finally {
            db.close();
        }
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            String[] columns = {COLUMN_USERNAME, COLUMN_PASSWORD};
            String selection = COLUMN_USERNAME + " = ?";
            String[] selectionArgs = {username};
            Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
            if (cursor.moveToFirst()) {
                int passwordColumnIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
                if (passwordColumnIndex != -1) {
                    String storedPassword = cursor.getString(passwordColumnIndex);
                    return BCrypt.checkpw(password, storedPassword);
                }
            }
            cursor.close();
            return false;
        } finally {
            db.close();
        }
    }

    private String hashPassword(String password) {
        // Generate a salt for bcrypt
        String salt = BCrypt.gensalt();

        // Hash the password using bcrypt
        return BCrypt.hashpw(password, salt);
    }


    public long createSession(String username) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
            return db.insert(TABLE_SESSIONS, null, values);
        } finally {
            db.close();
        }
    }

    public boolean isUserLoggedIn(String username) {
        SQLiteDatabase db = getReadableDatabase();
        boolean isLoggedIn = false;
        Cursor cursor = null;

        try {
            String[] columns = {COLUMN_USERNAME};
            String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_TIMESTAMP + " >= ?";
            String[] selectionArgs = {username, String.valueOf(getSixHoursAgoTimestamp())};
            cursor = db.query(TABLE_SESSIONS, columns, selection, selectionArgs, null, null, null);

            isLoggedIn = cursor.moveToFirst(); // Check if the cursor has any rows

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return isLoggedIn;
    }

    private long getSixHoursAgoTimestamp() {
        long currentTime = System.currentTimeMillis();
        long sixHoursInMillis = 6 * 60 * 60 * 1000; // 6 hours in milliseconds
        return currentTime - sixHoursInMillis;
    }


    public void clearSessionForUser(String username) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // Clear the session entry for the specified username
            String whereClause = COLUMN_USERNAME + " = ?";
            String[] whereArgs = {username};
            db.delete(TABLE_SESSIONS, whereClause, whereArgs);

            // Optionally, clear sessions older than 6 hours
            long currentTime = System.currentTimeMillis();
            long sixHoursAgo = currentTime - (6 * 60 * 60 * 1000); // 6 hours in milliseconds
            String olderThanSixHours = COLUMN_TIMESTAMP + " < ?";
            String[] olderThanSixHoursArgs = {String.valueOf(sixHoursAgo)};
            db.delete(TABLE_SESSIONS, olderThanSixHours, olderThanSixHoursArgs);
        } finally {
            db.close();
        }
    }

    public void clearSession() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // clear sessions older than 6 hours
            long currentTime = System.currentTimeMillis();
            long sixHoursAgo = currentTime - (6 * 60 * 60 * 1000); // 6 hours in milliseconds
            String olderThanSixHours = COLUMN_TIMESTAMP + " < ?";
            String[] olderThanSixHoursArgs = {String.valueOf(sixHoursAgo)};
            db.delete(TABLE_SESSIONS, olderThanSixHours, olderThanSixHoursArgs);
        } finally {
            db.close();
        }
    }

    public String getLastValidSessionUsername() {
        SQLiteDatabase db = getReadableDatabase();
        String username = null;
        Cursor cursor = null;

        try {
            String[] columns = {COLUMN_USERNAME};
            String selection = COLUMN_TIMESTAMP + " >= ?";
            String[] selectionArgs = {String.valueOf(getSixHoursAgoTimestamp())};
            String orderBy = COLUMN_TIMESTAMP + " DESC";
            String limit = "1";
            cursor = db.query(TABLE_SESSIONS, columns, selection, selectionArgs, null, null, orderBy, limit);

            if (cursor.moveToFirst()) {
                int userColumnIndex = cursor.getColumnIndex(COLUMN_USERNAME);
                if (userColumnIndex != -1) {
                    username = cursor.getString(userColumnIndex);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return username;
    }

    public void logout() {

        // Delete the session for the current user
        String username = getLastValidSessionUsername();
        clearSessionForUser(username);
    }


    public void printUsers() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            String[] columns = {COLUMN_USERNAME, COLUMN_PASSWORD};
            Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    int userColumnIndex = cursor.getColumnIndex(COLUMN_USERNAME);
                    int passwordColumnIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
                    if (passwordColumnIndex != -1 && userColumnIndex != -1) {
                        String username = cursor.getString(userColumnIndex);
                        String password = cursor.getString(passwordColumnIndex);
                        Log.d("User", "Username: " + username + ", Password: " + password);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } finally {
            db.close();
        }
    }

    public void printSessions() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            String[] columns = {COLUMN_USERNAME, COLUMN_TIMESTAMP};
            Cursor cursor = db.query(TABLE_SESSIONS, columns, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    int userColumnIndex = cursor.getColumnIndex(COLUMN_USERNAME);
                    int timeColumnIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);
                    if (timeColumnIndex != -1 && userColumnIndex != -1) {
                        String username = cursor.getString(userColumnIndex);
                        String timestamp = cursor.getString(timeColumnIndex);
                        Log.d("User", "Username: " + username + ", Timestamp: " + timestamp);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } finally {
            db.close();
        }
    }

}
