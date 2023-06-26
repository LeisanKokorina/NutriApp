package com.example.nutriapp.models.getInfoByID;

import java.util.ArrayList;

public class IngredientInfoAPIResponse {
    public int id;
    public String original;
    public String originalName;
    public String name;
    public double amount;
    public String unit;
    public String unitShort;
    public String unitLong;
    public ArrayList<String> possibleUnits;
    public EstimatedCost estimatedCost;
    public String consistency;
    public ArrayList<String> shoppingListUnits;
    public String aisle;
    public String image;
    public ArrayList<Object> meta;
    public Nutrition nutrition;
    public ArrayList<String> categoryPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnitShort() {
        return unitShort;
    }

    public void setUnitShort(String unitShort) {
        this.unitShort = unitShort;
    }

    public String getUnitLong() {
        return unitLong;
    }

    public void setUnitLong(String unitLong) {
        this.unitLong = unitLong;
    }

    public ArrayList<String> getPossibleUnits() {
        return possibleUnits;
    }

    public void setPossibleUnits(ArrayList<String> possibleUnits) {
        this.possibleUnits = possibleUnits;
    }

    public EstimatedCost getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(EstimatedCost estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getConsistency() {
        return consistency;
    }

    public void setConsistency(String consistency) {
        this.consistency = consistency;
    }

    public ArrayList<String> getShoppingListUnits() {
        return shoppingListUnits;
    }

    public void setShoppingListUnits(ArrayList<String> shoppingListUnits) {
        this.shoppingListUnits = shoppingListUnits;
    }

    public String getAisle() {
        return aisle;
    }

    public void setAisle(String aisle) {
        this.aisle = aisle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<Object> getMeta() {
        return meta;
    }

    public void setMeta(ArrayList<Object> meta) {
        this.meta = meta;
    }

    public Nutrition getNutrition() {
        return nutrition;
    }

    public void setNutrition(Nutrition nutrition) {
        this.nutrition = nutrition;
    }

    public ArrayList<String> getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(ArrayList<String> categoryPath) {
        this.categoryPath = categoryPath;
    }
}
