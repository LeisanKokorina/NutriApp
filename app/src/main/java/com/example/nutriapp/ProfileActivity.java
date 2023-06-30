package com.example.nutriapp;

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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private EditText editTextWeight;
    private EditText editTextHeight;
    private Spinner spinnerDay;
    private Spinner spinnerMonth;
    private Spinner spinnerYear;
    private RadioGroup radioGroupGender;
    private RadioGroup radioGroupActivity;
    private Button buttonSave;

    private ActivityResultLauncher<Intent> homePageLauncher;

    private DatabaseHelper databaseHelper;
    private CurrentUser currentUser;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
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
        User user = databaseHelper.getUserById(getCurrentUserId());
        if (user.getWeight() != 0 && user.getHeight() != 0 && !user.getDateOfBirth().isEmpty() && !user.getGender().isEmpty() && !user.getActivityLevel().isEmpty())
            prefillProfilePage(user);


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
        String dateOfBirth = getDateOfBirth();
        String gender = getSelectedGender();
        String levelActivity = getSelectedActivity();

        // Validate date of birth
        if (!isValidDate(dateOfBirth)) {
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


        if (rowId != -1) {

            // profile saved successfully
            Toast.makeText(ProfileActivity.this, "Your profile information successfully updated!", Toast.LENGTH_SHORT).show();
            CurrentUser.getInstance().setUser(databaseHelper.getUserById(rowId));
            databaseHelper.printUsers();
            databaseHelper.printSessions();
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

    private String getDateOfBirth() {
        // Values from the spinners
        String selectedDay = spinnerDay.getSelectedItem().toString();
        String selectedMonth = spinnerMonth.getSelectedItem().toString();
        String selectedYear = spinnerYear.getSelectedItem().toString();


        return selectedDay + "-" + getMonthNumber(selectedMonth) + "-" + selectedYear;
    }

    private String getMonthNumber(String monthName) {
        switch (monthName.toLowerCase()) {
            case "january":
                return "01";
            case "february":
                return "02";
            case "march":
                return "03";
            case "april":
                return "04";
            case "may":
                return "05";
            case "june":
                return "06";
            case "july":
                return "07";
            case "august":
                return "08";
            case "september":
                return "09";
            case "october":
                return "10";
            case "november":
                return "11";
            case "december":
                return "12";
            default:
                return "-1"; // Invalid month name
        }
    }

    private boolean isValidDate(String date) {
        try {
            // Split the date into day, month, and year
            String[] parts = date.split("-");
            int day = Integer.parseInt(parts[0]);  //31
            int month = Integer.parseInt(parts[1]); //02
            int year = Integer.parseInt(parts[2]); //1994

            // Check if the day is valid for the given month
            boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
            int[] daysInMonth = {31, isLeapYear ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            int maxDays = daysInMonth[month - 1];
            if (day > maxDays) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private int getSpinnerIndex(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                return i;
            }
        }
        return 0; // Default index if value not found
    }

    private String getMonthName(String monthNumber) {
        switch (monthNumber) {
            case "01":
                return "January";
            case "02":
                return "February";
            case "03":
                return "March";
            case "04":
                return "April";
            case "05":
                return "May";
            case "06":
                return "June";
            case "07":
                return "July";
            case "08":
                return "August";
            case "09":
                return "September";
            case "10":
                return "October";
            case "11":
                return "November";
            case "12":
                return "December";
            default:
                return ""; // Empty string if month number is invalid
        }
    }

    private int getCurrentUserId() {
        // Retrieve the session token from SharedPreferences
        String sessionToken = sharedPreferences.getString("sessionToken", null);

        // Get the user ID based on the session token from the DatabaseHelper
        return databaseHelper.getUserIdBySessionToken(sessionToken);
    }

    private void setSpinnerSelectionByValue(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(value);
            spinner.setSelection(position);
        }
    }


    private void prefillProfilePage(User user) {
        // Set the user information into the corresponding views
        editTextWeight.setText(String.valueOf((int) user.getWeight()));
        editTextHeight.setText(String.valueOf((int) user.getHeight()));

        String[] dateOfBirthParts = user.getDateOfBirth().split("-");
        String day = dateOfBirthParts[0];
        String month = getMonthName(dateOfBirthParts[1]);
        String year = dateOfBirthParts[2];

        int dayIndex = getSpinnerIndex(spinnerDay, day);
        int monthIndex = getSpinnerIndex(spinnerMonth, month);
        int yearIndex = getSpinnerIndex(spinnerYear, year);

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


