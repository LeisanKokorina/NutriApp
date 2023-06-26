package com.example.nutriapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private ProgressBar progressBarProtein;
    private ProgressBar progressBarFat;
    private ProgressBar progressBarCarbs;

    private Button buttonAddFood;

    private ActivityResultLauncher<Intent> addFoodLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressBarProtein = findViewById(R.id.progressBarProtein);
        progressBarFat = findViewById(R.id.progressBarFat);
        progressBarCarbs = findViewById(R.id.progressBarCarbs);

        buttonAddFood = findViewById(R.id.buttonAddFood);

        buttonAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddFoodActivity();
            }
        });

        // Initialize the ActivityResultLauncher
        addFoodLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                double protein = data.getDoubleExtra("protein", 0);
                                double fat = data.getDoubleExtra("fat", 0);
                                double carbs = data.getDoubleExtra("carbs", 0);
                                updateProgressBars(protein, fat, carbs);
                            }
                        }
                    }
                });
    }

    private void openAddFoodActivity() {
        Intent intent = new Intent(HomeActivity.this, AddFoodActivity.class);
        addFoodLauncher.launch(intent);
    }

    private void updateProgressBars(double protein, double fat, double carbs) {
        int proteinProgress = (int) protein;
        int fatProgress = (int) fat;
        int carbsProgress = (int) carbs;

        progressBarProtein.setProgress(proteinProgress);
        progressBarFat.setProgress(fatProgress);
        progressBarCarbs.setProgress(carbsProgress);
    }

    private float expectedProtein() {
        return 100; // Modify this method to return the actual expected protein value
    }

    private float expectedFat() {
        return 50; // Modify this method to return the actual expected fat value
    }

    private float expectedCarbs() {
        return 200; // Modify this method to return the actual expected carbs value
    }
}


