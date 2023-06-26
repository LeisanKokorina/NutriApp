package com.example.nutriapp.models.listeners;

import com.example.nutriapp.models.getInfoByID.IngredientInfoAPIResponse;

public interface IngredientInfoResponseListener {
    void fetch(IngredientInfoAPIResponse response, String message);
    void error(String message);
}
