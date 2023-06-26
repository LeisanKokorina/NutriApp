package com.example.nutriapp;

import android.content.Context;

import com.example.nutriapp.models.getInfoByID.IngredientInfoAPIResponse;
import com.example.nutriapp.models.ingredientSearch.IngredientSearchAPIResponse;
import com.example.nutriapp.models.listeners.IngredientInfoResponseListener;
import com.example.nutriapp.models.listeners.IngredientSearchResponseListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class SpoonacularRequestManager {
    Context context;
    Retrofit retrofit =  new Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public SpoonacularRequestManager(Context context) {
        this.context = context;
    }

    public void getIngredientInfo(IngredientInfoResponseListener listener, int id){
        CallIngredientInfo callIngredientInfo = retrofit.create(CallIngredientInfo.class);
        Call<IngredientInfoAPIResponse> call = callIngredientInfo.callNutritionInfo(id, context.getString(R.string.api_key));
        call.enqueue(new Callback<IngredientInfoAPIResponse>() {
            @Override
            public void onResponse(Call<IngredientInfoAPIResponse> call, Response<IngredientInfoAPIResponse> response) {
                if (!response.isSuccessful()){
                    listener.error(response.message());
                    return;
                }
                listener.fetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<IngredientInfoAPIResponse> call, Throwable t) {
                listener.error(t.getMessage());
            }
        });
    }
    public void getIngredientInfo(IngredientInfoResponseListener listener, int id, double amount, String unit){
        CallIngredientInfo callIngredientInfo = retrofit.create(CallIngredientInfo.class);
        Call<IngredientInfoAPIResponse> call = callIngredientInfo.callNutritionInfo(id, context.getString(R.string.api_key), amount, unit);
        call.enqueue(new Callback<IngredientInfoAPIResponse>() {
            @Override
            public void onResponse(Call<IngredientInfoAPIResponse> call, Response<IngredientInfoAPIResponse> response) {
                if (!response.isSuccessful()){
                    listener.error(response.message());
                    return;
                }
                listener.fetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<IngredientInfoAPIResponse> call, Throwable t) {
                listener.error(t.getMessage());
            }
        });
    }

    public void getIngredientList(IngredientSearchResponseListener listener, String name){
        CallIngredientInfo callIngredientInfo = retrofit.create(CallIngredientInfo.class);
        Call<IngredientSearchAPIResponse> call = callIngredientInfo.callIngredientList( context.getString(R.string.api_key), name);
        call.enqueue(new Callback<IngredientSearchAPIResponse>() {
            @Override
            public void onResponse(Call<IngredientSearchAPIResponse> call, Response<IngredientSearchAPIResponse> response) {
                if (!response.isSuccessful()){
                    listener.error(response.message());
                    return;
                }
                listener.fetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<IngredientSearchAPIResponse> call, Throwable t) {
                listener.error(t.getMessage());
            }
        });
    }
    private interface CallIngredientInfo {
        @GET("food/ingredients/{id}/information")
        Call<IngredientInfoAPIResponse> callNutritionInfo(@Path("id") int id, @Query("apiKey") String apiKey, @Query("amount") double amount, @Query("unit") String unit );

        @GET("food/ingredients/{id}/information")
        Call<IngredientInfoAPIResponse> callNutritionInfo(@Path("id") int id, @Query("apiKey") String apiKey );

        @GET("food/ingredients/search")
        Call<IngredientSearchAPIResponse> callIngredientList(@Query("apiKey") String apiKey, @Query("query") String name );
    }

}

