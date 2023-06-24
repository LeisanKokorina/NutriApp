package com.example.nutriapp;

// HomeActivity.java

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView textViewCaloriesConsumed, textViewExpectedCalories;
    private Button buttonAddMeal;

    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        textViewCaloriesConsumed = findViewById(R.id.textViewCaloriesConsumed);
        textViewExpectedCalories = findViewById(R.id.textViewExpectedCalories);
        buttonAddMeal = findViewById(R.id.buttonAddMeal);
        helper = new DatabaseHelper(this);

        // Set the initial values for calories consumed and expected calories
        int caloriesConsumed = 0; // Replace with your actual data
        int expectedCalories = 2000; // Replace with your actual data
        textViewCaloriesConsumed.setText("Calories Consumed: " + caloriesConsumed);
        textViewExpectedCalories.setText("Expected Calories: " + expectedCalories);

        // Set click listener for the add meal button
        buttonAddMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the add meal button click
                // Example: startActivity(new Intent(HomeActivity.this, AddMealActivity.class));
            }
        });
    }
}
