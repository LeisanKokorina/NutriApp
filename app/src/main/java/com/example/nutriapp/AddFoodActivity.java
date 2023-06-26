package com.example.nutriapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutriapp.models.getInfoByID.IngredientInfoAPIResponse;
import com.example.nutriapp.models.getInfoByID.Nutrient;
import com.example.nutriapp.models.listeners.IngredientInfoResponseListener;

import java.util.ArrayList;
import java.util.List;

public class AddFoodActivity extends AppCompatActivity {
    private EditText editTextIngredientId;
    private EditText editTextAmount;

    private Button buttonSubmit;
    private TextView textViewIngredientIdLabel;

    private TextView textViewAmountLabel;
    private TextView textViewUnitsLabel;

    private Spinner spinnerUnits;

    private SpoonacularRequestManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        editTextIngredientId = findViewById(R.id.editTextIngredientId);
        editTextAmount = findViewById(R.id.editTextAmount);
        spinnerUnits = findViewById(R.id.spinnerUnits);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        textViewIngredientIdLabel = findViewById(R.id.textViewIngredientIdLabel);
        textViewIngredientIdLabel.setText("Ingredient ID");

        textViewAmountLabel = findViewById(R.id.textViewAmountLabel);
        textViewAmountLabel.setText("Amount");

        textViewUnitsLabel = findViewById(R.id.textViewUnitsLabel);
        textViewUnitsLabel.setText("Units");

        manager = new SpoonacularRequestManager(this);

        editTextIngredientId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Retrieve the entered ingredient ID
                String ingredientIdText = s.toString();

                if (!ingredientIdText.isEmpty()) {
                    int ingredientId = Integer.parseInt(ingredientIdText);
                    // Call the method to retrieve ingredient information
                    getIngredientInfo(ingredientId);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed for this implementation
            }
        });


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredientIdText = editTextIngredientId.getText().toString();
                String amountText = editTextAmount.getText().toString();
                String selectedUnit = spinnerUnits.getSelectedItem().toString();

                if (!ingredientIdText.isEmpty() && !amountText.isEmpty() ) {
                    int ingredientId = Integer.parseInt(ingredientIdText);
                    int amount = Integer.parseInt(amountText);
                    // Call the method to retrieve ingredient information
                    getIngredientInfo(ingredientId, amount, selectedUnit);
                } else {
                    Toast.makeText(AddFoodActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getIngredientInfo(int ingredientId) {
        // Make the API call to retrieve ingredient information
        // Replace the following code with your actual implementation
        // Retrofit call and response handling
        // Example code:
        manager.getIngredientInfo(new IngredientInfoResponseListener() {
            @Override
            public void fetch(IngredientInfoAPIResponse response, String message) {
                // Retrieve ingredient/products units
                ArrayList<String> possibleUnits = response.getPossibleUnits();
                populateUnitsSpinner(possibleUnits);
            }

            @Override
            public void error(String errorMessage) {
                Toast.makeText(AddFoodActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, ingredientId);
    }
    private void getIngredientInfo(int ingredientId, double amount, String units) {
        // Make the API call to retrieve ingredient information
        // Replace the following code with your actual implementation
        // Retrofit call and response handling
        // Example code:
        manager.getIngredientInfo(new IngredientInfoResponseListener() {
            @Override
            public void fetch(IngredientInfoAPIResponse response, String message) {
                // Process the retrieved ingredient information
                // Update the HomeActivity with the nutrition data
                // Example code:
                updateHomeActivity(response);
            }

            @Override
            public void error(String errorMessage) {
                Toast.makeText(AddFoodActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, ingredientId, amount, units);
    }

    private void updateHomeActivity(IngredientInfoAPIResponse response) {
        Intent intent = getIntent(); // Retrieve the current intent
        ArrayList<Nutrient> nutrients = response.getNutrition().getNutrients();
        intent.putExtra("protein", nutrients.get(0).getPercentOfDailyNeeds());
        intent.putExtra("fat", nutrients.get(19).getPercentOfDailyNeeds());
        intent.putExtra("carbs", nutrients.get(7).getPercentOfDailyNeeds());
        setResult(RESULT_OK, intent); // Set the updated intent as the result
        finish();
    }


    private void populateUnitsSpinner(ArrayList<String> unitsList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unitsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnits.setAdapter(adapter);
    }
}
