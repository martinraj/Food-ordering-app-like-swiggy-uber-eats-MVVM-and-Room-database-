package com.marty.yummy.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//API client class setting up retrofit client.
public class APIClient {

    //Address of the network api service.
    private static final String BASE_URL = "https://android-full-time-task.firebaseio.com";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
