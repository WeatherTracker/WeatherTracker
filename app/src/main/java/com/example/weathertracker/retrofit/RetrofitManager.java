package com.example.weathertracker.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    // 以Singleton模式建立
    private static final RetrofitManager retrofitManager = new RetrofitManager();

    private final RetrofitService retrofitService;

    private RetrofitManager() {

        // 設置baseUrl即要連的網站，addConverterFactory用Gson作為資料處理Converter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://059b3af71897.ngrok.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitService = retrofit.create(RetrofitService.class);
    }

    public static RetrofitManager getInstance() {
        return retrofitManager;
    }

    public RetrofitService getService() {
        return retrofitService;
    }
}
