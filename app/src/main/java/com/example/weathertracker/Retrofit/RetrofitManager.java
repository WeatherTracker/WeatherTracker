package com.example.weathertracker.Retrofit;

import android.content.Context;
import android.content.res.Resources;

import com.bumptech.glide.load.engine.Resource;
import com.example.weathertracker.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    // 以Singleton模式建立
    private static final RetrofitManager retrofitManager = new RetrofitManager();

    private final RetrofitService retrofitService;

    private RetrofitManager() {

        // 設置baseUrl即要連的網站，addConverterFactory用Gson作為資料處理Converter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://b39bd9c7d1d9.ngrok.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitService = retrofit.create(RetrofitService.class);
    }

    public static RetrofitManager getInstance() {
        return retrofitManager;
    }

    public RetrofitService getAPI() {
        return retrofitService;
    }
}
