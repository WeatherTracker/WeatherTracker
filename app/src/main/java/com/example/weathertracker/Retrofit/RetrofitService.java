package com.example.weathertracker.Retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitService {
    @FormUrlEncoded
    @POST("send_mail")
    Call<Ack> send_registeredMail(@Field("email") String email,
                                  @Field("password") String password,
                                  @Field("FCM_token") String FCM_token);

    @FormUrlEncoded
    @POST("login")
    Call<Ack> login(@Field("email") String email,
                    @Field("password") String password);
    @GET("dateTimeTest")
    Call<timeTest> timeTest();

    @FormUrlEncoded
    @POST("dateTimeTest")
    Call<Ack> sendDate(@Field("dateTime") String datetime);
}
