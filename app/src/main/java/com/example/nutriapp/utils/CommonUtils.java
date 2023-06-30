package com.example.nutriapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.nutriapp.DatabaseHelper;

public class CommonUtils {

    public static int getCurrentUserId(SharedPreferences sharedPreferences, DatabaseHelper databaseHelper) {

        String sessionToken = sharedPreferences.getString("sessionToken", null);

        if(!sessionToken.isEmpty()){
            return databaseHelper.getUserIdBySessionToken(sessionToken);
        } else {
            return -1;
        }
    }
}
