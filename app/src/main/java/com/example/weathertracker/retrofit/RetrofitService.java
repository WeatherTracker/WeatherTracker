package com.example.weathertracker.retrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {
    @FormUrlEncoded
    @POST("signIn")
    Call<Ack> signIn(@Field("email") String email,
                     @Field("password") String password);

    @FormUrlEncoded
    @POST("signUp")
    Call<Ack> signUp(@Field("email") String email,
                     @Field("password") String password,
                     @Field("FCMToken") String FCMToken);

    @FormUrlEncoded
    @POST("sendResetMail")
    Call<Ack> sendResetMail(@Field("email") String email);

    @PUT("editProfile")
    Call<Ack> editProfile(@Body User user);

    @GET("getCalendarMonth")
    Call<List<String>> getCalendarMonth(@Query("userId") String userId,
                                        @Query("month") String month);

    @GET("getCalendarDay")
    Call<List<Event>> getCalendarDay(@Query("userId") String userId,
                                     @Query("day") String day);

    @GET("getChart")
    Call<chartList> getChart(@Query("latitude") float latitude,
                             @Query("longitude") float longitude,
                             @Query("day") String day);

    @GET("getWeatherIcon")
    Call<List<String>> getWeatherIcon(@Query("latitude") float latitude,
                                      @Query("longitude") float longitude);

    @POST("newEvent")
    Call<Ack> newEvent(@Body Event e);


    @DELETE("deleteEvent/{eventId}")
    Call<Ack> deleteEvent(@Path("eventId") String eventId);


    @PUT("editEvent")
    Call<Ack> editEvent(@Body Event event);

    @FormUrlEncoded
    @PUT("inOrOutEvent")
    Call<Ack> inOrOutEvent(@Field("eventId") String eventId,
                           @Field("userId") String userId,
                           @Field("action") Boolean action);

    @GET("getRecommendEvents")
    Call<List<Event>> getRecommendEvents(@Query("userId") String userId);

    @GET("getRecommendSights")
    Call<List<Sight>> getRecommendSights(@Query("longitude") double longitude,
                                         @Query("latitude") double latitude);

    @GET("searchEvent")
    Call<List<Event>> searchEvent(@Query("input") String input);

    @GET("getRecommendTime")
    Call<List<String>> getRecommendTime(@Query("userId") String userId,
                                        @Query("eventId") String eventId,
                                        @Query("whiteList") List<String> whiteList,
                                        @Query("blackList") List<String> blackList);

    @GET("getProfile")
    Call<User> getProfile(@Query("userId") String userId);

    @FormUrlEncoded
    @PUT("changeTime")
    Call<Ack> changeTime(@Field("eventId") String eventId,
                         @Field("pickTime") String pickTime);
}