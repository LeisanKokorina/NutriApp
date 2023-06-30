package com.example.nutriapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class User {
    private long userId;
    private String username;
    private String hashPassword;
    private double height;
    private double weight;
    private String gender;
    private String dateOfBirth;

    private String activityLevel;

    private String token;
    private int fruitsVegs;
    private int protein;
    private int fats;
    private int carbs;
    private int sodium;



    public User(String username, String hashPassword, double height, double weight, String gender, String dateOfBirth, String activityLevel) {
        this.username = username;
        this.hashPassword = hashPassword;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.activityLevel = activityLevel;
    }

    public User(String username, String hashPassword) {
        this.username = username;
        this.hashPassword = hashPassword;
    }

    public User() {
    }

    public User(long userId, String username, String hashPassword, double height, double weight, String gender, String dateOfBirth, String activityLevel, int fruitsVegs, int protein, int fats, int carbs, int sodium) {
        this.userId = userId;
        this.username = username;
        this.hashPassword = hashPassword;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.activityLevel = activityLevel;
        this.fruitsVegs = fruitsVegs;
        this.protein = protein;
        this.fats = fats;
        this.carbs = carbs;
        this.sodium = sodium;
    }



    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getFruitsVegs() {
        return fruitsVegs;
    }

    public void setFruitsVegs(int fruitsVegs) {
        this.fruitsVegs = fruitsVegs;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public int getFats() {
        return fats;
    }

    public void setFats(int fats) {
        this.fats = fats;
    }

    public int getCarbs() {
        return carbs;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public int getSodium() {
        return sodium;
    }

    public void setSodium(int sodium) {
        this.sodium = sodium;
    }

    public int calculateAge() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Calendar currentDate = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();

        try {
            Date date = dateFormat.parse(dateOfBirth);
            birthDate.setTime(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int age = currentDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        if (currentDate.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

}
