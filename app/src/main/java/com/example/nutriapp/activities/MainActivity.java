package com.example.nutriapp.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutriapp.DatabaseHelper;
import com.example.nutriapp.models.common.User;
import com.example.nutriapp.utils.CommonUtils;

public class MainActivity extends AppCompatActivity {
    private final DatabaseHelper helper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("sessionToken", null);


        // Check if the user is already logged in
        if (helper.getUserIdBySessionToken(sessionToken) != -1) {
            // User is logged in, navigate to the home activity
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        } else {
            // User is not logged in, navigate to the sign up/login activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        // Finish the main activity so that the user cannot navigate back to it
        finish();
    }

}