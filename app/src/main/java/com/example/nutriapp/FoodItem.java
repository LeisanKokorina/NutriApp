package com.example.nutriapp;

public class FoodItem {
    private String name;
    private int protein;
    private int fat;
    private int carbs;

    public FoodItem(String name, int protein, int fat, int carbs) {
        this.name = name;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
    }

    public String getName() {
        return name;
    }

    public int getProtein() {
        return protein;
    }

    public int getFat() {
        return fat;
    }

    public int getCarbs() {
        return carbs;
    }
}
