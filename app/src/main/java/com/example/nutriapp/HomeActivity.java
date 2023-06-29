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
    private ProgressBar progressFruitVeg;
    private ProgressBar progressBarProtein;
    private ProgressBar progressBarFat;
    private ProgressBar progressBarCarbs;
    private ProgressBar progressBarSalt;

    private Button buttonAddFood;

    private ActivityResultLauncher<Intent> addFoodLauncher;

    private double totalFruitVeg = 0;
    private double totalProtein = 0;
    private double totalFat = 0;
    private double totalCarbs = 0;
    private double totalSalt = 0;

    DatabaseHelper databaseHelper;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        databaseHelper = new DatabaseHelper(this);
        currentUser = CurrentUser.getInstance().getUser();

        progressFruitVeg = findViewById(R.id.progressBarFruitsVeg);
        progressBarProtein = findViewById(R.id.progressBarProtein);
        progressBarFat = findViewById(R.id.progressBarFat);
        progressBarCarbs = findViewById(R.id.progressBarCarbs);
        progressBarSalt = findViewById(R.id.progressBarSalt);
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
                                double fruitVeg = data.getDoubleExtra("fruits_veg", 0);
                                double protein = data.getDoubleExtra("protein", 0);
                                double fat = data.getDoubleExtra("fat", 0);
                                double carbs = data.getDoubleExtra("carbs", 0);
                                double salt = data.getDoubleExtra("salt", 0);
                                updateProgressBars(fruitVeg, protein, fat, carbs, salt);
                            }
                        }
                    }
                });
    }

    private void openAddFoodActivity() {
        Intent intent = new Intent(HomeActivity.this, AddFoodActivity.class);
        addFoodLauncher.launch(intent);
    }

    private void updateProgressBars(double fruitVeg, double protein, double fat, double carbs, double salt) {
        totalFruitVeg += fruitVeg;
        totalProtein += protein;
        totalFat += fat;
        totalCarbs += carbs;
        totalSalt += salt;

        int fruitVegProgress = (int) totalFruitVeg;
        int proteinProgress = (int) totalProtein;
        int fatProgress = (int) totalFat;
        int carbsProgress = (int) totalCarbs;
        int saltProgress = (int) totalSalt;

        progressFruitVeg.setProgress(fruitVegProgress);
        progressBarProtein.setProgress(proteinProgress);
        progressBarFat.setProgress(fatProgress);
        progressBarCarbs.setProgress(carbsProgress);
        progressBarSalt.setProgress(saltProgress);
    }

    private double expectedProtein() {
        // Retrieve the user's weight from the database
        User currentUser = CurrentUser.getInstance().getUser();
        double weight = databaseHelper.getUserWeight(currentUser);

        // Calculate the maximum protein value based on the weight and protein allowance
        double proteinAllowance = 0.8; // Protein allowance in grams per kg body weight per day

        return weight * proteinAllowance;
    }

    private double expectedFat() {
        return calculateCalories() * 0.3;
    }

    private double expectedCarbs() {
        return calculateCalories() * 0.55;
    }

    // Men: BMR = 88.362 + (13.397 x weight in kg) + (4.799 x height in cm) – (5.677 x age in years)
    // Women: BMR = 447.593 + (9.247 x weight in kg) + (3.098 x height in cm) – (4.330 x age in years)
    private double calculateBMR() {
      if(currentUser.getGender().equalsIgnoreCase("Female")){
          return 447.593 + (9.247 * currentUser.getWeight()) + (3.098 * currentUser.getHeight()) - (4.330 * currentUser.calculateAge());
      }else{
          return 88.362 + (13.397 * currentUser.getWeight()) + (4.799 * currentUser.getHeight()) - (5.677 * currentUser.calculateAge());
      }
    }

    private double calculateCalories() {
        if (currentUser.getActivityLevel().equalsIgnoreCase("inactive")) {
            return 1.2F * calculateBMR();
        } else  if (currentUser.getActivityLevel().equalsIgnoreCase("somewhat active")) {
            return 1.375F * calculateBMR();
        }else  if (currentUser.getActivityLevel().equalsIgnoreCase("active")) {
            return 1.55F * calculateBMR();
        }else  if (currentUser.getActivityLevel().equalsIgnoreCase("very active")) {
            return 1.725F * calculateBMR();
        }
        return 0;
    }
}


