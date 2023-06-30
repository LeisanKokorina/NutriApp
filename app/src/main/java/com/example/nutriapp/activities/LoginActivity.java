package com.example.nutriapp.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutriapp.DatabaseHelper;
import com.example.nutriapp.R;
import com.example.nutriapp.models.common.User;

import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private TextView textViewSignUp;
    // Create an instance of the DatabaseHelper
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;


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

        sharedPreferences = getSharedPreferences("SessionPrefs", MODE_PRIVATE);


        // Check if user is already logged in
        if (isLoggedIn()) {
            // User is already logged in, proceed to next activity
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
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
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
    }

    boolean isLoggedIn() {
        return databaseHelper.getUserIdBySessionToken(sharedPreferences.getString("sessionToken", "")) != -1;
    }

    private void login() {
        // Get the entered username and password
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        User user = new User(username, password);

        // Check if the entered username and password match the values in the database
        int validUserID = databaseHelper.getUserIdUsersTable(user);

        if (validUserID != -1) {
            User userSession = new User();
            // Generate a session token
            String sessionToken = generateSessionToken();
            userSession.setToken(sessionToken);
            userSession.setUserId(validUserID);
            databaseHelper.createSession(userSession);

            // Save the session token to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("sessionToken", sessionToken);
            editor.apply();

            User currentUser = databaseHelper.getUserById(validUserID);
            if(currentUser.getWeight() == 0.0 && currentUser.getHeight() == 0.0 && currentUser.getDateOfBirth() == null && currentUser.getGender() == null && currentUser.getActivityLevel() == null){
                startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                finish();
            } else {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            }
        } else {
            // Invalid username or password, show an error message
            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateSessionToken() {
        // Generate a session token using UUID or any other method
        return UUID.randomUUID().toString();
    }
}

