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
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_HEIGHT = "height";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_DATE_OF_BIRTH = "date_of_birth";

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_USERS + " ("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USERNAME + " TEXT, "
            + COLUMN_PASSWORD + " TEXT, "
            + COLUMN_HEIGHT + " REAL, "
            + COLUMN_WEIGHT + " REAL, "
            + COLUMN_GENDER + " TEXT, "
            + COLUMN_DATE_OF_BIRTH + " TEXT"
            + ")";


    private static final String TABLE_SESSIONS = "sessions";
    private static final String COLUMN_SESSION_ID = "session_id";

    private static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String CREATE_TABLE_SESSIONS_QUERY =
            "CREATE TABLE " + TABLE_SESSIONS + " (" +
                    COLUMN_SESSION_ID + " INTEGER," +
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
        if (oldVersion < 2) {
            // Drop the old version of the table if it exists
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        }
        onCreate(db);
    }

    public long insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, user.getUsername());
            values.put(COLUMN_PASSWORD, user.getHashPassword());
            return db.insert(TABLE_USERS, null, values);
        } finally {
            db.close();
        }
    }

    public long updateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            if (user.getUsername() != null) {
                values.put(COLUMN_USERNAME, user.getUsername());
            }
            if (user.getHashPassword() != null) {
                values.put(COLUMN_PASSWORD, user.getHashPassword());
            }
            if (user.getHeight() != 0) {
                values.put(COLUMN_HEIGHT, user.getHeight());
            }
            if (user.getWeight() != 0) {
                values.put(COLUMN_WEIGHT, user.getWeight());
            }
            if (user.getGender() != null) {
                values.put(COLUMN_GENDER, user.getGender());
            }
            if (user.getDateOfBirth() != null) {
                values.put(COLUMN_DATE_OF_BIRTH, user.getDateOfBirth());
            }

            String whereClause = COLUMN_USER_ID + " = ?";
            String[] whereArgs = {String.valueOf(user.getUserId())};

            return db.update(TABLE_USERS, values, whereClause, whereArgs);
        } finally {
            db.close();
        }
    }


    public int getCurrentSessionUserId(User user) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            String[] columns = {COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_PASSWORD , COLUMN_HEIGHT, COLUMN_WEIGHT, COLUMN_GENDER, COLUMN_DATE_OF_BIRTH};
            String selection = COLUMN_USERNAME + " = ?";
            String[] selectionArgs = {user.getUsername()};
            Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                int userIdColumnIndex = cursor.getColumnIndex(COLUMN_USER_ID);
                int passwordColumnIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
                String storedPassword = cursor.getString(passwordColumnIndex);

                // Check if the entered password matches the stored password using BCrypt
                if (BCrypt.checkpw(user.getHashPassword(), storedPassword)) {
                    return cursor.getInt(userIdColumnIndex); // Passwords match, return the user ID
                }
            }
            cursor.close();

        } finally {
            db.close();
        }
        return -1; // User not found or password doesn't match
    }


    public long createSession(User user) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SESSION_ID, user.getUserId());
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


    public void clearSessionForUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // Clear the session entry for the specified username
            String whereClause = COLUMN_USERNAME + " = ?";
            String[] whereArgs = {user.getUsername()};
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
            String[] columns = {COLUMN_SESSION_ID};
            String selection = COLUMN_TIMESTAMP + " >= ?";
            String[] selectionArgs = {String.valueOf(getSixHoursAgoTimestamp())};
            String orderBy = COLUMN_TIMESTAMP + " DESC";
            String limit = "1";
            cursor = db.query(TABLE_SESSIONS, columns, selection, selectionArgs, null, null, orderBy, limit);

            if (cursor.moveToFirst()) {
                int userColumnIndex = cursor.getColumnIndex(COLUMN_SESSION_ID);
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

    public void logout(User user) {

        // Delete the session for the current user
        String username = getLastValidSessionUsername();
        clearSessionForUser(user);
    }


    public void printUsers() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            String[] columns = {COLUMN_USERNAME, COLUMN_PASSWORD};
            Cursor cursor = db.query(TABLE_USERS, columns, null, null, null, null, null);
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

    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
            cursor = db.rawQuery(query, new String[]{username});

            // Check if the cursor has any rows
            if (cursor != null && cursor.getCount() > 0) {
                return true; // Username is taken
            }

            return false; // Username is available
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public float getUserWeight(User user) {
        SQLiteDatabase db = getReadableDatabase();
        float weight = 0.0f;

        try {
            String[] columns = {COLUMN_WEIGHT};
            String selection = COLUMN_USER_ID + " = ?";
            String[] selectionArgs = {String.valueOf(getCurrentSessionUserId(user))};
            Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                int weightColumnIndex = cursor.getColumnIndex(COLUMN_WEIGHT);
                if (weightColumnIndex != -1) {
                    weight = cursor.getFloat(weightColumnIndex);
                }
            }

            cursor.close();
        } finally {
            db.close();
        }

        return weight;
    }

}
