package com.marty.yummy.services;

import com.marty.yummy.model.FoodDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface YummyAPIServices {

    @GET("/data.json")
    Call<List<FoodDetails>> getFoodData();
}
