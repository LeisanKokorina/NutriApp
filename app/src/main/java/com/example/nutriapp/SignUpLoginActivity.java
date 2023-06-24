package com.example.nutriapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpLoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonSignUp, buttonLogin;
    private TextView textViewSignUp;
    // Create an instance of the DatabaseHelper
    DatabaseHelper databaseHelper;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_login);

        // Initialize views
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        // Create an instance of the DatabaseHelper
        databaseHelper = new DatabaseHelper(this);


        // Check if user is already logged in
        if (isLoggedIn()) {
            // User is already logged in, proceed to next activity
            startActivity(new Intent(SignUpLoginActivity.this, HomeActivity.class));
            finish();
        }


        // Set click listener for login button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // Set click listener for sign up text
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the registration page
                startActivity(new Intent(SignUpLoginActivity.this, RegistrationActivity.class));
            }
        });
    }

    boolean isLoggedIn() {
        return databaseHelper.getLastValidSessionUsername() != null;
    }

    private void login() {
        // Get the entered username and password
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        // Check if the entered username and password match the values in the database
        boolean isValidUser = databaseHelper.checkUser(username, password);

        if (isValidUser) {
            // Username and password match, proceed to next activity
            databaseHelper.createSession(username);
            startActivity(new Intent(SignUpLoginActivity.this, HomeActivity.class));
            finish();
        } else {
            // Invalid username or password, show an error message
            Toast.makeText(SignUpLoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }
}

