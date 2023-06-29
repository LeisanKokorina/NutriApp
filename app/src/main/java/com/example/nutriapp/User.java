package com.example.nutriapp;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class User {
    private long userId;
    private String username;
    private String hashPassword;
    private float height;
    private float weight;
    private String gender;
    private String dateOfBirth;

    public User(String username, String hashPassword, float height, float weight, String gender, String dateOfBirth) {
        this.username = username;
        this.hashPassword = hashPassword;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    public User(String username, String hashPassword) {
        this.username = username;
        this.hashPassword = hashPassword;
    }

    public User(long userId) {
        this.userId = userId;
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

    public String getHashPassword() {
        return hashPassword;
    }

    public float getHeight() {
        return height;
    }

    public float getWeight() {
        return weight;
    }

    public String getGender() {
        return gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
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
