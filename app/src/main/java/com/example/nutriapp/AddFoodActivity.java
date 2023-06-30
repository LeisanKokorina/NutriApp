package com.example.nutriapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutriapp.models.getInfoByID.IngredientInfoAPIResponse;
import com.example.nutriapp.models.getInfoByID.Nutrient;
import com.example.nutriapp.models.ingredientSearch.IngredientSearchAPIResponse;
import com.example.nutriapp.models.ingredientSearch.Result;
import com.example.nutriapp.models.listeners.IngredientInfoResponseListener;
import com.example.nutriapp.models.listeners.IngredientSearchResponseListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AddFoodActivity extends AppCompatActivity {
    private AutoCompleteTextView autoCompleteIngredientName;

    private EditText editTextIngredientId;
    private EditText editTextAmount;

    private Button buttonSubmit;


    private Spinner spinnerUnits;
    private int selectedIngredientId;

    private SpoonacularRequestManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        autoCompleteIngredientName = findViewById(R.id.autoCompleteTextViewIngredientName);
        editTextAmount = findViewById(R.id.editTextAmount);
        spinnerUnits = findViewById(R.id.spinnerUnits);
        buttonSubmit = findViewById(R.id.buttonSubmit);


        manager = new SpoonacularRequestManager(this);

        autoCompleteIngredientName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Retrieve the entered ingredient name
                String ingredientName = s.toString();

                if (!ingredientName.isEmpty()) {
                    // Call the method to fetch the ingredient ID based on the name
                    fetchIngredientId(ingredientName);
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
                String amountText = editTextAmount.getText().toString();
                String selectedUnit = spinnerUnits.getSelectedItem().toString();

                if (selectedIngredientId != 0 && !amountText.isEmpty() && !selectedUnit.isEmpty() ) {
                    int amount = Integer.parseInt(amountText);
                    // Call the method to retrieve ingredient information
                    getIngredientInfo(selectedIngredientId, amount, selectedUnit);
                } else {
                    Toast.makeText(AddFoodActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getIngredientInfo(int ingredientId) {
        // The API call to retrieve ingredient information
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
        // The API call to retrieve ingredient information
        manager.getIngredientInfo(new IngredientInfoResponseListener() {
            @Override
            public void fetch(IngredientInfoAPIResponse response, String message) {
                updateHomeActivity(response);
            }

            @Override
            public void error(String errorMessage) {
                Toast.makeText(AddFoodActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, ingredientId, amount, units);
    }

    private void fetchIngredientId(String selectedIngredientName) {
        manager.getIngredientList(new IngredientSearchResponseListener() {
            @Override
            public void fetch(IngredientSearchAPIResponse response, String message) {
                List<Result> results = response.getResults();
                ArrayList<String> ingredientNames = new ArrayList<>();

                for (Result result : results) {
                    ingredientNames.add(result.getName());
                }

                // Create the adapter and set it on the AutoCompleteTextView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddFoodActivity.this, android.R.layout.simple_dropdown_item_1line, ingredientNames);
                autoCompleteIngredientName.setAdapter(adapter);
                autoCompleteIngredientName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectedName = (String) parent.getItemAtPosition(position);
                        selectedIngredientId = getIngredientIdByName(results,selectedName);
                        getIngredientInfo(selectedIngredientId);
                    }
                });
            }

            @Override
            public void error(String errorMessage) {
                Toast.makeText(AddFoodActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, selectedIngredientName);
    }

    private int getIngredientIdByName(List<Result> results, String ingredientName) {
        // The API call to retrieve ingredient information
        int ingredientId = 0;
        // Iterate over the list of ingredients and find the matching ID
        for (Result result : results) {
            if (result.getName().equalsIgnoreCase(ingredientName)) {
                ingredientId = result.getId();
                break;
            }
        }
        return ingredientId;
    }

    private void updateHomeActivity(IngredientInfoAPIResponse response) {
        Intent intent = getIntent(); // Retrieve the current intent
        ArrayList<Nutrient> nutrients = response.getNutrition().getNutrients();
        ArrayList<String> category = response.getCategoryPath();
        if(response.getAisle().contains("produce") || category.contains("vegetable") || category.contains("fruit")){
            intent.putExtra("fruits_veg", response.getNutrition().getWeightPerServing().getAmount());
        }
        for(int i = 0; i< nutrients.size(); i++){
            if (nutrients.get(i).getName().equalsIgnoreCase("Carbohydrates")) {
                intent.putExtra("carbs", nutrients.get(i).getAmount());
            }else if(nutrients.get(i).getName().equalsIgnoreCase("Fat")){
                intent.putExtra("fat", nutrients.get(i).getAmount());
            }else if (nutrients.get(i).getName().equalsIgnoreCase("Protein")){
                intent.putExtra("protein", nutrients.get(i).getAmount());
            }else if (nutrients.get(i).getName().equalsIgnoreCase("Sodium")){
                intent.putExtra("sodium", nutrients.get(i).getAmount());
            }
        }
        setResult(RESULT_OK, intent); // Set the updated intent as the result
        finish();
    }


    private void populateUnitsSpinner(ArrayList<String> unitsList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unitsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnits.setAdapter(adapter);
    }
}
