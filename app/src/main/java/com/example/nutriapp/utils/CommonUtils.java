package com.example.nutriapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.nutriapp.DatabaseHelper;

public class CommonUtils {

    public static int getCurrentUserId(SharedPreferences sharedPreferences, DatabaseHelper databaseHelper) {

        String sessionToken = sharedPreferences.getString("sessionToken", null);

        // Get the user ID based on the session token from the DatabaseHelper
        return databaseHelper.getUserIdBySessionToken(sessionToken);
    }
}
