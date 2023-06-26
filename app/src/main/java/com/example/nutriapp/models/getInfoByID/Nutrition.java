package com.example.nutriapp.models.getInfoByID;

import java.util.ArrayList;

public class Nutrition{
    public ArrayList<Nutrient> nutrients;
    public ArrayList<Property> properties;
    public ArrayList<Flavonoid> flavonoids;
    public CaloricBreakdown caloricBreakdown;
    public WeightPerServing weightPerServing;

    public ArrayList<Nutrient> getNutrients() {
        return nutrients;
    }

    public void setNutrients(ArrayList<Nutrient> nutrients) {
        this.nutrients = nutrients;
    }

    public ArrayList<Property> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<Property> properties) {
        this.properties = properties;
    }

    public ArrayList<Flavonoid> getFlavonoids() {
        return flavonoids;
    }

    public void setFlavonoids(ArrayList<Flavonoid> flavonoids) {
        this.flavonoids = flavonoids;
    }

    public CaloricBreakdown getCaloricBreakdown() {
        return caloricBreakdown;
    }

    public void setCaloricBreakdown(CaloricBreakdown caloricBreakdown) {
        this.caloricBreakdown = caloricBreakdown;
    }

    public WeightPerServing getWeightPerServing() {
        return weightPerServing;
    }

    public void setWeightPerServing(WeightPerServing weightPerServing) {
        this.weightPerServing = weightPerServing;
    }
}