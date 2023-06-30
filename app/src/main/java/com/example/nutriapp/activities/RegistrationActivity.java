package com.example.nutriapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutriapp.DatabaseHelper;
import com.example.nutriapp.R;
import com.example.nutriapp.models.common.User;

import org.mindrot.jbcrypt.BCrypt;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        // Initialize views
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        // Initialize Database Helper
        databaseHelper = new DatabaseHelper(this);

        // Set click listener for register button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        // Get the entered username and password
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String hashPassword = hashPassword(password);

        // Check if any fields are left blank
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(RegistrationActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return; // Stop further execution
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return; // Stop further execution
        }

        // Check if the username is already taken
        if (databaseHelper.isUsernameTaken(username)) {
            Toast.makeText(RegistrationActivity.this, "This username is taken", Toast.LENGTH_SHORT).show();
            return; // Stop further execution
        }

        User user = new User();
        user.setUsername(username);
        user.setHashPassword(hashPassword);


        // Insert user details into the database
        long rowId = databaseHelper.insertUser(user);


        if (rowId != -1) {
            // Registration successful
            Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            finish();
        } else {
            // Registration failed
            Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private String hashPassword(String password) {
        // Generate a salt for bcrypt
        String salt = BCrypt.gensalt();

        // Hash the password using bcrypt
        return BCrypt.hashpw(password, salt);
    }
}
