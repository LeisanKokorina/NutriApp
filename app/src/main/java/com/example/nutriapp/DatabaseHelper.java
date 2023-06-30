package com.example.nutriapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.nutriapp.models.common.User;

import org.mindrot.jbcrypt.BCrypt;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 6;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_HEIGHT = "height";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_DATE_OF_BIRTH = "date_of_birth";

    private static final String COLUMN_ACTIVITY_LEVEL = "activity_level";

    private static final String COLUMN_PROGRESS_BAR_FRUITS = "fruit_veg";
    private static final String COLUMN_PROGRESS_BAR_PROTEIN = "protein";
    private static final String COLUMN_PROGRESS_BAR_FATS = "fats";
    private static final String COLUMN_PROGRESS_BAR_CARBS = "carbs";
    private static final String COLUMN_PROGRESS_BAR_SODIUM = "sodium";


    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_USERS + " ("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USERNAME + " TEXT, "
            + COLUMN_PASSWORD + " TEXT, "
            + COLUMN_HEIGHT + " REAL, "
            + COLUMN_WEIGHT + " REAL, "
            + COLUMN_GENDER + " TEXT, "
            + COLUMN_DATE_OF_BIRTH + " TEXT, "
            + COLUMN_ACTIVITY_LEVEL + " TEXT, "
            + COLUMN_PROGRESS_BAR_FRUITS + " REAL DEFAULT 0, "
            + COLUMN_PROGRESS_BAR_PROTEIN + " REAL DEFAULT 0, "
            + COLUMN_PROGRESS_BAR_FATS + " REAL DEFAULT 0, "
            + COLUMN_PROGRESS_BAR_CARBS + " REAL DEFAULT 0, "
            + COLUMN_PROGRESS_BAR_SODIUM + " REAL DEFAULT 0 "
            + ")";


    private static final String TABLE_SESSIONS = "sessions";
    private static final String COLUMN_SESSION_ID = "session_id";

    private static final String COLUMN_SESSION_TIMESTAMP = "timestamp";

    private static final String COLUMN_SESSION_USER_ID = "user_id";

    private static final String COLUMN_SESSION_TOKEN = "token";

    private static final String CREATE_TABLE_SESSIONS_QUERY =
            "CREATE TABLE " + TABLE_SESSIONS + " ("
                    +  COLUMN_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_SESSION_TIMESTAMP + " INTEGER,"
                    + COLUMN_SESSION_USER_ID + " INTEGER,"
                    + COLUMN_SESSION_TOKEN + " TEXT"
                    + ")";

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
        if (oldVersion < DATABASE_VERSION) {
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
            if (user.getActivityLevel() != null) {
                values.put(COLUMN_ACTIVITY_LEVEL, user.getActivityLevel());
            }
            if (user.getFruitsVegs() != 0) {
                values.put(COLUMN_PROGRESS_BAR_FRUITS, user.getFruitsVegs());
            }
            if (user.getProtein() != 0) {
                values.put(COLUMN_PROGRESS_BAR_PROTEIN, user.getProtein());
            }
            if (user.getFats() != 0) {
                values.put(COLUMN_PROGRESS_BAR_FATS, user.getFats());
            }
            if (user.getCarbs() != 0) {
                values.put(COLUMN_PROGRESS_BAR_CARBS, user.getCarbs());
            }
            if (user.getSodium() != 0) {
                values.put(COLUMN_PROGRESS_BAR_SODIUM, user.getSodium());
            }


            String whereClause = COLUMN_USER_ID + " = ?";
            String[] whereArgs = {String.valueOf(user.getUserId())};

            return db.update(TABLE_USERS, values, whereClause, whereArgs);
        } finally {
            db.close();
        }
    }


    public int getUserIdUsersTable(User user) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            String[] columns = {COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_PASSWORD , COLUMN_HEIGHT, COLUMN_WEIGHT, COLUMN_GENDER, COLUMN_DATE_OF_BIRTH, COLUMN_ACTIVITY_LEVEL, COLUMN_PROGRESS_BAR_FRUITS, COLUMN_PROGRESS_BAR_PROTEIN,COLUMN_PROGRESS_BAR_FATS, COLUMN_PROGRESS_BAR_CARBS, COLUMN_PROGRESS_BAR_SODIUM};
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
            values.put(COLUMN_SESSION_TIMESTAMP, System.currentTimeMillis());
            values.put(COLUMN_SESSION_USER_ID, user.getUserId());
            values.put(COLUMN_SESSION_TOKEN, user.getToken());
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
            String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_SESSION_TIMESTAMP + " >= ?";
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
            String olderThanSixHours = COLUMN_SESSION_TIMESTAMP + " < ?";
            String[] olderThanSixHoursArgs = {String.valueOf(sixHoursAgo)};
            db.delete(TABLE_SESSIONS, olderThanSixHours, olderThanSixHoursArgs);
        } finally {
            db.close();
        }
    }

    public void clearSession(int userId) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // Clear sessions for the specified user ID
            String whereClause = COLUMN_SESSION_USER_ID + " = ?";
            String[] whereArgs = {String.valueOf(userId)};
            db.delete(TABLE_SESSIONS, whereClause, whereArgs);
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
            String selection = COLUMN_SESSION_TIMESTAMP + " >= ?";
            String[] selectionArgs = {String.valueOf(getSixHoursAgoTimestamp())};
            String orderBy = COLUMN_SESSION_TIMESTAMP + " DESC";
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

    //For debugging
    public void printUsers() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            String[] columns = {COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_PASSWORD , COLUMN_HEIGHT, COLUMN_WEIGHT, COLUMN_GENDER, COLUMN_DATE_OF_BIRTH, COLUMN_ACTIVITY_LEVEL, COLUMN_PROGRESS_BAR_FRUITS, COLUMN_PROGRESS_BAR_PROTEIN,COLUMN_PROGRESS_BAR_FATS, COLUMN_PROGRESS_BAR_CARBS, COLUMN_PROGRESS_BAR_SODIUM};
            Cursor cursor = db.query(TABLE_USERS, columns, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    int idColumnIndex = cursor.getColumnIndex(COLUMN_USER_ID);
                    int userColumnIndex = cursor.getColumnIndex(COLUMN_USERNAME);
                    int passwordColumnIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
                    int heightColumnIndex = cursor.getColumnIndex(COLUMN_HEIGHT);
                    int weightColumnIndex = cursor.getColumnIndex(COLUMN_WEIGHT);
                    int genderColumnIndex = cursor.getColumnIndex(COLUMN_GENDER);
                    int dateColumnIndex = cursor.getColumnIndex(COLUMN_DATE_OF_BIRTH);
                    int activityColumnIndex = cursor.getColumnIndex(COLUMN_ACTIVITY_LEVEL);
                    int fruitsColumnIndex = cursor.getColumnIndex(COLUMN_PROGRESS_BAR_FRUITS);
                    int proteinColumnIndex = cursor.getColumnIndex(COLUMN_PROGRESS_BAR_PROTEIN);
                    int fatsColumnIndex = cursor.getColumnIndex(COLUMN_PROGRESS_BAR_FATS);
                    int carbsColumnIndex = cursor.getColumnIndex(COLUMN_PROGRESS_BAR_CARBS);
                    int sodiumColumnIndex = cursor.getColumnIndex(COLUMN_PROGRESS_BAR_SODIUM);
                    if (idColumnIndex != -1 && userColumnIndex != -1 && passwordColumnIndex != -1 && heightColumnIndex != -1 && weightColumnIndex != -1 && genderColumnIndex != -1 && dateColumnIndex != -1 && activityColumnIndex != -1) {
                        String id = cursor.getString(idColumnIndex);
                        String username = cursor.getString(userColumnIndex);
                        String password = cursor.getString(passwordColumnIndex);
                        String height = cursor.getString(heightColumnIndex);
                        String weight = cursor.getString(weightColumnIndex);
                        String gender = cursor.getString(genderColumnIndex);
                        String date = cursor.getString(dateColumnIndex);
                        String activity = cursor.getString(activityColumnIndex);
                        String fruits = cursor.getString(fruitsColumnIndex);
                        String protein = cursor.getString(proteinColumnIndex);
                        String fats = cursor.getString(fatsColumnIndex);
                        String carbs = cursor.getString(carbsColumnIndex);
                        String sodium = cursor.getString(sodiumColumnIndex);
                        Log.d("User", "Username id: " + id + ", username: " + username + ", Password: " + password + ", height: " + height + ", weight: " + weight + ", gender: " + gender + ", date of birth: " + date + ", activity level: " + activity+ ", fruits_veg: " + fruits+ ", protein: " + protein+ ", fats: " + fats+ ", carbs: " + carbs+ ", sodium: " + sodium);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } finally {
            db.close();
        }
    }

    //For debugging
    public void printSessions() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            String[] columns = {COLUMN_SESSION_ID, COLUMN_SESSION_TIMESTAMP, COLUMN_SESSION_USER_ID, COLUMN_SESSION_TOKEN};
            Cursor cursor = db.query(TABLE_SESSIONS, columns, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    int idColumnIndex = cursor.getColumnIndex(COLUMN_SESSION_ID);
                    int timeColumnIndex = cursor.getColumnIndex(COLUMN_SESSION_TIMESTAMP);
                    int userIDColumnIndex = cursor.getColumnIndex(COLUMN_SESSION_USER_ID);
                    int tokenColumnIndex = cursor.getColumnIndex(COLUMN_SESSION_TOKEN);
                    if (idColumnIndex != -1 && userIDColumnIndex != -1 && timeColumnIndex != -1 && tokenColumnIndex != -1) {
                        String id = cursor.getString(idColumnIndex);
                        String timestamp = cursor.getString(timeColumnIndex);
                        String userId = cursor.getString(userIDColumnIndex);
                        String token = cursor.getString(tokenColumnIndex);
                        Log.d("Session", "Session id: " + id + ", UserId: " + userId + ", Timestamp: " + timestamp+ ", Token: " + token);
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

    // Method to get the user ID based on the session token
    public int getUserIdBySessionToken(String sessionToken) {
        SQLiteDatabase db = getReadableDatabase();
        int userId = -1;

        if (sessionToken != null) {
            String query = "SELECT " + COLUMN_SESSION_USER_ID +
                    " FROM " + TABLE_SESSIONS +
                    " WHERE " + COLUMN_SESSION_TOKEN + " = ?";
            String[] selectionArgs = {sessionToken};

            Cursor cursor = db.rawQuery(query, selectionArgs);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_SESSION_USER_ID);
                if (columnIndex != -1) {
                    userId = cursor.getInt(columnIndex);
                }
            }
        }

        return userId;
    }


    public User getUserById(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USERNAME,
                COLUMN_PASSWORD,
                COLUMN_HEIGHT,
                COLUMN_WEIGHT,
                COLUMN_GENDER,
                COLUMN_DATE_OF_BIRTH,
                COLUMN_ACTIVITY_LEVEL,
                COLUMN_PROGRESS_BAR_FRUITS,
                COLUMN_PROGRESS_BAR_PROTEIN,
                COLUMN_PROGRESS_BAR_FATS,
                COLUMN_PROGRESS_BAR_CARBS,
                COLUMN_PROGRESS_BAR_SODIUM
        };

        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        Cursor cursor = db.query(
                TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_ID);
            int usernameIndex = cursor.getColumnIndexOrThrow(COLUMN_USERNAME);
            int passwordIndex = cursor.getColumnIndexOrThrow(COLUMN_PASSWORD);
            int heightIndex = cursor.getColumnIndexOrThrow(COLUMN_HEIGHT);
            int weightIndex = cursor.getColumnIndexOrThrow(COLUMN_WEIGHT);
            int genderIndex = cursor.getColumnIndexOrThrow(COLUMN_GENDER);
            int dateOfBirthIndex = cursor.getColumnIndexOrThrow(COLUMN_DATE_OF_BIRTH);
            int activityLevelIndex = cursor.getColumnIndexOrThrow(COLUMN_ACTIVITY_LEVEL);
            int fruitsColumnIndex = cursor.getColumnIndex(COLUMN_PROGRESS_BAR_FRUITS);
            int proteinColumnIndex = cursor.getColumnIndex(COLUMN_PROGRESS_BAR_PROTEIN);
            int fatsColumnIndex = cursor.getColumnIndex(COLUMN_PROGRESS_BAR_FATS);
            int carbsColumnIndex = cursor.getColumnIndex(COLUMN_PROGRESS_BAR_CARBS);
            int sodiumColumnIndex = cursor.getColumnIndex(COLUMN_PROGRESS_BAR_SODIUM);

            long id = cursor.getLong(idIndex);
            String username = cursor.getString(usernameIndex);
            String password = cursor.getString(passwordIndex);
            double height = cursor.getDouble(heightIndex);
            double weight = cursor.getDouble(weightIndex);
            String gender = cursor.getString(genderIndex);
            String dateOfBirth = cursor.getString(dateOfBirthIndex);
            String activityLevel = cursor.getString(activityLevelIndex);
            int fruits = cursor.getInt(fruitsColumnIndex);
            int protein = cursor.getInt(proteinColumnIndex);
            int fats = cursor.getInt(fatsColumnIndex);
            int carbs = cursor.getInt(carbsColumnIndex);
            int sodium = cursor.getInt(sodiumColumnIndex);

            // Create the User object using the retrieved values
            user = new User(id, username, password, height, weight, gender, dateOfBirth, activityLevel, fruits, protein, fats, carbs, sodium);
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return user;
    }

}
