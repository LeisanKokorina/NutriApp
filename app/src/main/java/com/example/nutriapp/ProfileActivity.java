package com.example.nutriapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private EditText editTextWeight;
    private EditText editTextHeight;
    private LinearLayout datePicker;
    private RadioGroup radioGroupGender;
    private RadioGroup radioGroupActivity;
    private Button buttonSave;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
        }

        editTextWeight = findViewById(R.id.editTextWeight);
        editTextHeight = findViewById(R.id.editTextHeight);
        datePicker = findViewById(R.id.linearLayoutDateOfBirth);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioGroupActivity = findViewById(R.id.radioGroupActivity);
        buttonSave = findViewById(R.id.buttonSave);

        // Initialize Database Helper
        databaseHelper = new DatabaseHelper(this);

        Spinner spinnerYear = findViewById(R.id.spinnerYear);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerYear.setAdapter(yearAdapter);

        int startYear = 1923;
        int endYear = 2023;

        for (int year = startYear; year <= endYear; year++) {
            yearAdapter.add(String.valueOf(year));
        }

        yearAdapter.notifyDataSetChanged();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void saveProfile() {
        // Get the entered username and password
        float height = Float.parseFloat(editTextHeight.getText().toString().trim());
        float weight = Float.parseFloat(editTextWeight.getText().toString().trim());
        String dateOfBirth = datePicker.toString().trim();
        String gender = getSelectedGender();
        String levelActivity = getSelectedActivity();

        User user = new User(null,null, height, weight, dateOfBirth, gender, levelActivity);


        // Insert user details into the database
        long rowId = databaseHelper.updateUser(user);


        if (rowId != -1) {
            user.setUserId(rowId);
            // Registration successful
            Toast.makeText(ProfileActivity.this, "Your profile information successfully updated!", Toast.LENGTH_SHORT).show();

            // Optionally, you can navigate the user to the login page or any other desired activity
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finish();
        } else {
            // Registration failed
            Toast.makeText(ProfileActivity.this, "Failed to fill out the profile information", Toast.LENGTH_SHORT).show();
        }
    }


    private String getSelectedGender() {
        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedId == R.id.radioButtonMale) {
            return "Male";
        } else if (selectedId == R.id.radioButtonFemale) {
            return "Female";
        }
        return "";
    }

    private String getSelectedActivity() {
        int selectedId = radioGroupActivity.getCheckedRadioButtonId();
        if (selectedId == R.id.radioButtonInactive) {
            return "Inactive";
        } else if (selectedId == R.id.radioButtonSomeWhatActive) {
            return "Somewhat Active";
        }else if (selectedId == R.id.radioButtonActive) {
            return "Active";
        }else if (selectedId == R.id.radioButtonVeryActive) {
            return "Very Active";
        }

        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Handle the back button click
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


