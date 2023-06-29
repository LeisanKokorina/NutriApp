package com.example.nutriapp;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    LoginActivity loginActivity = new LoginActivity();
    private DatabaseHelper helper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already logged in
        if (helper.getLastValidSessionUsername()!=null) {
            // User is logged in, navigate to the home activity
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        } else {
            // User is not logged in, navigate to the sign up/login activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        // Finish the main activity so that the user cannot navigate back to it
        finish();
    }


    private void logout(User user) {
        //  clear the session
        helper.logout(user);

        // Redirect the user to the login page
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish(); // Close the current activity
    }

}