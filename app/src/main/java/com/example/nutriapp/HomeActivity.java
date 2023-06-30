package com.example.nutriapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class HomeActivity extends AppCompatActivity {
    private ProgressBar progressFruitVeg;
    private ProgressBar progressBarProtein;
    private ProgressBar progressBarFat;
    private ProgressBar progressBarCarbs;
    private ProgressBar progressBarSodium;
    private TextView textViewCurrentValueFruitsVeg;
    private TextView textViewCurrentValueProtein;
    private TextView textViewCurrentValueFats;
    private TextView textViewCurrentValueCarbs;
    private TextView textViewCurrentValueSodium;
    private TextView textViewRecommendedValueFruitsVeg;
    private TextView textViewMinProtein;
    private TextView textViewMaxFats;
    private TextView textViewMaxCarbs;
    private TextView textViewMaxSalt;

    private Button buttonAddFood;

    private ActivityResultLauncher<Intent> addFoodLauncher;

    private DatabaseHelper databaseHelper;
    private User currentUser;
    private SharedPreferences sharedPreferences;
    private int totalFruitVeg;
    private int totalProtein;
    private int totalFat;
    private int totalCarbs;
    private int totalSodium;
    double minFruitVeg;
    double minProtein;
    double maxFats;
    double maxCarbs;
    double maxSodium;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        databaseHelper = new DatabaseHelper(this);

        sharedPreferences = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
        currentUser = databaseHelper.getUserById(getCurrentUserId());

        progressFruitVeg = findViewById(R.id.progressBarFruitsVeg);
        progressBarProtein = findViewById(R.id.progressBarProtein);
        progressBarFat = findViewById(R.id.progressBarFat);
        progressBarCarbs = findViewById(R.id.progressBarCarbs);
        progressBarSodium = findViewById(R.id.progressBarSalt);
        buttonAddFood = findViewById(R.id.buttonAddFood);

        textViewCurrentValueFruitsVeg = findViewById(R.id.textViewCurrentValueFruitsVeg);
        textViewCurrentValueProtein = findViewById(R.id.textViewCurrentValueProtein);
        textViewCurrentValueFats = findViewById(R.id.textViewCurrentValueFats);
        textViewCurrentValueCarbs = findViewById(R.id.textViewCurrentValueCarbs);
        textViewCurrentValueSodium = findViewById(R.id.textViewCurrentValueSalt);
        textViewRecommendedValueFruitsVeg = findViewById(R.id.textViewRecommendedValueFruitsVeg);
        textViewMinProtein = findViewById(R.id.textViewMinProtein);
        textViewMaxFats = findViewById(R.id.textViewMaxFats);
        textViewMaxCarbs = findViewById(R.id.textViewMaxCarbs);
        textViewMaxSalt = findViewById(R.id.textViewMaxSalt);

        // Calculate the minimum and maximum values for each progress bar
        minFruitVeg = 400; // Minimum value for fruit and vegetables (WHO recommendation)
        minProtein = expectedProteinPerDay();
        maxFats = expectedFatPerDay();
        maxCarbs = expectedCarbsPerDay();
        maxSodium = 2;

        totalFruitVeg=currentUser.getFruitsVegs();
        totalProtein=currentUser.getProtein();
        totalFat=currentUser.getFats();
        totalCarbs=currentUser.getCarbs();
        totalSodium=currentUser.getSodium();

        updateProgressBars(totalFruitVeg, totalProtein, totalFat, totalCarbs, totalSodium);
        setRecommendedTextViewValues();


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
                                int fruitVeg = (int) Math.round(data.getDoubleExtra("fruits_veg", 0));
                                int protein = (int) Math.round(data.getDoubleExtra("protein", 0));
                                int fat = (int) Math.round(data.getDoubleExtra("fat", 0));
                                int carbs = (int) Math.round(data.getDoubleExtra("carbs", 0));
                                int sodium = (int) Math.round(data.getDoubleExtra("sodium", 0));
                                updateProgressBars(fruitVeg, protein, fat, carbs, sodium);

                            }
                        }
                    }
                });
    }


    private void openAddFoodActivity() {
        Intent intent = new Intent(HomeActivity.this, AddFoodActivity.class);
        addFoodLauncher.launch(intent);
    }

    private void updateProgressBars(int fruitVeg, int protein, int fat, int carbs, int sodium) {
        totalFruitVeg += fruitVeg;
        totalProtein += protein;
        totalFat += fat;
        totalCarbs += carbs;
        totalSodium += sodium;

        // Calculate the progress percentage for each progress bar
        int fruitVegProgress = (int) ((totalFruitVeg / minFruitVeg) * 100);
        int proteinProgress = (int) ((totalProtein / minProtein) * 100);
        int fatProgress = (int) ((totalFat / maxFats) * 100);
        int carbsProgress = (int) ((totalCarbs / maxCarbs) * 100);
        int saltProgress = (int) ((totalSodium / maxSodium) * 100);

        // Set the progress values for each progress bar
        progressFruitVeg.setProgress(fruitVegProgress);
        progressBarProtein.setProgress(proteinProgress);
        progressBarFat.setProgress(fatProgress);
        progressBarCarbs.setProgress(carbsProgress);
        progressBarSodium.setProgress(saltProgress);

        // Set the progress bar colors based on the progress values
        setProgressBarColorRedToGreen(progressFruitVeg, fruitVegProgress);
        setProgressBarColorRedToGreen(progressBarProtein, proteinProgress);
        setProgressBarColorGreenToRed(progressBarFat, fatProgress);
        setProgressBarColorGreenToRed(progressBarCarbs, carbsProgress);
        setProgressBarColorGreenToRed(progressBarSodium, saltProgress);

        setRecommendedTextViewValues();
        currentUser.setFruitsVegs(totalFruitVeg);
        currentUser.setProtein(totalProtein);
        currentUser.setFats(totalFat);
        currentUser.setCarbs(totalCarbs);
        currentUser.setSodium(totalSodium);

        databaseHelper.updateUser(currentUser);
    }

    private void setRecommendedTextViewValues() {
        // Update the text values of "Current", "Max", and "Min" for each category
        textViewCurrentValueFruitsVeg.setText("Current: " + totalFruitVeg);
        textViewCurrentValueProtein.setText("Current: " + totalProtein);
        textViewCurrentValueFats.setText("Current: " + totalFat);
        textViewCurrentValueCarbs.setText("Current: " + totalCarbs);
        textViewCurrentValueSodium.setText("Current: " + totalSodium);

        textViewRecommendedValueFruitsVeg.setText("Min: " + Math.round(minFruitVeg)+ " g");
        textViewMinProtein.setText("Min: " + Math.round(minProtein)+ " g");
        textViewMaxFats.setText("Max: " + Math.round(maxFats)+ " g");
        textViewMaxCarbs.setText("Max: " + Math.round(maxCarbs)+ " g");
        textViewMaxSalt.setText("Max: " + Math.round(maxSodium)+ " g");
    }

    private void setProgressBarColorRedToGreen(ProgressBar progressBar, int progress) {
        // Calculate the color based on the progress value
        int darkGreenColor = ContextCompat.getColor(this, R.color.dark_green);
        int greenColor = ContextCompat.getColor(this, R.color.green);
        int lightGreen = ContextCompat.getColor(this, R.color.light_green);
        int progressBarColor;

        if (progress >= 100) {
            progressBarColor = darkGreenColor;
        } else if (progress >= 50) {
            progressBarColor = greenColor;
        } else {
            progressBarColor = lightGreen;
        }

        // Set the progress bar color
        progressBar.setProgressTintList(ColorStateList.valueOf(progressBarColor));
    }

    private void setProgressBarColorGreenToRed(ProgressBar progressBar, int progress) {
        // Calculate the color based on the progress value
        int greenColor = ContextCompat.getColor(this, R.color.green);
        int redColor = ContextCompat.getColor(this, R.color.red);
        int darkRedColor = ContextCompat.getColor(this, R.color.dark_red);
        int progressBarColor;

        if (progress >= 100) {
            progressBarColor = darkRedColor;
        } else if (progress >= 50) {
            progressBarColor = redColor;
        } else {
            progressBarColor = greenColor;
        }

        // Set the progress bar color
        progressBar.setProgressTintList(ColorStateList.valueOf(progressBarColor));
    }


    private double expectedProteinPerDay() {
        // Retrieve the user's weight from the database
        double weight = currentUser.getWeight();

        // Calculate the maximum protein value based on the weight and protein allowance
        double proteinAllowance = 0.8; // Protein allowance in grams per kg body weight per day

        return weight * proteinAllowance;
    }

    private double expectedFatPerDay() {
        return (calculateCalories() * 0.3)/9; //Fat provides 9 calories per gram
    }

    private double expectedCarbsPerDay() {
        return (calculateCalories() * 0.65)/4;//Carbohydrates provide 4 calories per gram
    }

    // Men: BMR = 88.362 + (13.397 x weight in kg) + (4.799 x height in cm) – (5.677 x age in years)
    // Women: BMR = 447.593 + (9.247 x weight in kg) + (3.098 x height in cm) – (4.330 x age in years)
    private double calculateBMR() {
        if (currentUser.getGender().equalsIgnoreCase("Female")) {
            return 447.593 + (9.247 * currentUser.getWeight()) + (3.098 * currentUser.getHeight()) - (4.330 * currentUser.calculateAge());
        } else {
            return 88.362 + (13.397 * currentUser.getWeight()) + (4.799 * currentUser.getHeight()) - (5.677 * currentUser.calculateAge());
        }
    }

    private double calculateCalories() {
        if (currentUser.getActivityLevel().equalsIgnoreCase("inactive")) {
            return 1.2F * calculateBMR();
        } else if (currentUser.getActivityLevel().equalsIgnoreCase("somewhat active")) {
            return 1.375F * calculateBMR();
        } else if (currentUser.getActivityLevel().equalsIgnoreCase("active")) {
            return 1.55F * calculateBMR();
        } else if (currentUser.getActivityLevel().equalsIgnoreCase("very active")) {
            return 1.725F * calculateBMR();
        }
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_logout) {
            // Handle logout action here
            performLogout();
            return true;
        }
        if (item.getItemId() == R.id.profile) {
            // Handle profile action
            openProfileActivityWithUserInfo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openProfileActivityWithUserInfo() {
        // Get the user information from the database
        int userId = databaseHelper.getUserIdBySessionToken(sharedPreferences.getString("sessionToken", ""));
        User user = databaseHelper.getUserById(userId);

        // Create an intent to open the ProfileActivity
        Intent intent = new Intent(this, ProfileActivity.class);

        // Pass the user information as extras to the intent
        intent.putExtra("userId", user.getUserId());
        intent.putExtra("height", user.getHeight());
        intent.putExtra("weight", user.getWeight());
        intent.putExtra("dateOfBirth", user.getDateOfBirth());
        intent.putExtra("gender", user.getGender());
        intent.putExtra("activityLevel", user.getActivityLevel());

        // Start the ProfileActivity
        startActivity(intent);
    }

    private int getCurrentUserId() {
        // Retrieve the session token from SharedPreferences
        String sessionToken = sharedPreferences.getString("sessionToken", null);

        // Get the user ID based on the session token from the DatabaseHelper
        return databaseHelper.getUserIdBySessionToken(sessionToken);
    }

    private void performLogout() {
        // Implement your logout logic here
        // For example, clear session data and navigate to the login screen
        databaseHelper.clearSession(getCurrentUserId());
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


}


