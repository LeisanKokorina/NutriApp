package com.example.nutriapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nutriapp.DatabaseHelper;
import com.example.nutriapp.R;
import com.example.nutriapp.models.common.User;
import com.example.nutriapp.utils.CommonUtils;
import com.example.nutriapp.utils.DateUtils;

public class ProfileActivity extends AppCompatActivity {
    private EditText editTextWeight;
    private EditText editTextHeight;
    private Spinner spinnerDay;
    private Spinner spinnerMonth;
    private Spinner spinnerYear;
    private RadioGroup radioGroupGender;
    private RadioGroup radioGroupActivity;
    private Button buttonSave;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false); // disable the back button
        }
        sharedPreferences = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextHeight = findViewById(R.id.editTextHeight);
        spinnerDay = findViewById(R.id.spinnerDay);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);
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

        // Retrieve the user information from the Intent
        int userId = CommonUtils.getCurrentUserId(sharedPreferences, databaseHelper);
        User user = databaseHelper.getUserById(userId);
        if (user.getWeight() != 0 && user.getHeight() != 0 && !user.getDateOfBirth().isEmpty() && !user.getGender().isEmpty() && !user.getActivityLevel().isEmpty()){
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
            }
            prefillProfilePage(user);
        }


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void saveProfile() {
        String dateOfBirth = DateUtils.getDateOfBirth(spinnerDay, spinnerMonth, spinnerYear);
        String gender = getSelectedGender();
        String levelActivity = getSelectedActivity();

        // Validate all the fields
        if (editTextHeight.getText().toString().isEmpty() || editTextWeight.getText().toString().isEmpty() || dateOfBirth.isEmpty() || gender == null || levelActivity == null) {
            Toast.makeText(ProfileActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get the entered username and password
        float height = Float.parseFloat(editTextHeight.getText().toString().trim());
        float weight = Float.parseFloat(editTextWeight.getText().toString().trim());

        // Validate date of birth
        if (!DateUtils.isValidDate(dateOfBirth)) {
            Toast.makeText(ProfileActivity.this, "Please enter a valid date of birth", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = databaseHelper.getUserIdBySessionToken(sharedPreferences.getString("sessionToken", ""));

        User user = new User();
        user.setHeight(height);
        user.setWeight(weight);
        user.setDateOfBirth(dateOfBirth);
        user.setGender(gender);
        user.setActivityLevel(levelActivity);
        user.setUserId(userId);

        // Insert user details into the database
        long rowId = databaseHelper.updateUser(user);


        if (rowId != 0) {

            // profile saved successfully
            Toast.makeText(ProfileActivity.this, "Your profile information successfully updated!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finish();
        } else {
            // Nothing has changed
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
        return null;
    }

    private String getSelectedActivity() {
        int selectedId = radioGroupActivity.getCheckedRadioButtonId();
        if (selectedId == R.id.radioButtonInactive) {
            return "Inactive";
        } else if (selectedId == R.id.radioButtonSomeWhatActive) {
            return "Somewhat Active";
        } else if (selectedId == R.id.radioButtonActive) {
            return "Active";
        } else if (selectedId == R.id.radioButtonVeryActive) {
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

    private void prefillProfilePage(User user) {
        // Set the user information into the corresponding views
        editTextWeight.setText(String.valueOf((int) user.getWeight()));
        editTextHeight.setText(String.valueOf((int) user.getHeight()));

        String[] dateOfBirthParts = user.getDateOfBirth().split("-");
        String day = dateOfBirthParts[0];
        String month = DateUtils.getMonthName(dateOfBirthParts[1]);
        String year = dateOfBirthParts[2];

        int dayIndex = DateUtils.getSpinnerIndex(spinnerDay, day);
        int monthIndex = DateUtils.getSpinnerIndex(spinnerMonth, month);
        int yearIndex = DateUtils.getSpinnerIndex(spinnerYear, year);

        spinnerDay.setSelection(dayIndex);
        spinnerMonth.setSelection(monthIndex);
        spinnerYear.setSelection(yearIndex);

        if (user.getGender().equals("Male")) {
            radioGroupGender.check(R.id.radioButtonMale);
        } else if (user.getGender().equals("Female")) {
            radioGroupGender.check(R.id.radioButtonFemale);
        }

        switch (user.getActivityLevel()) {
            case "Inactive":
                radioGroupActivity.check(R.id.radioButtonInactive);
                break;
            case "Somewhat Active":
                radioGroupActivity.check(R.id.radioButtonSomeWhatActive);
                break;
            case "Active":
                radioGroupActivity.check(R.id.radioButtonActive);
                break;
            case "Very Active":
                radioGroupActivity.check(R.id.radioButtonVeryActive);
                break;
        }
    }


}


