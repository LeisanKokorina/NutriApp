package com.example.nutriapp.models.listeners;

import com.example.nutriapp.models.getInfoByID.IngredientInfoAPIResponse;
import com.example.nutriapp.models.ingredientSearch.IngredientSearchAPIResponse;

public interface IngredientSearchResponseListener {
    void fetch(IngredientSearchAPIResponse response, String message);
    void error(String message);
}
