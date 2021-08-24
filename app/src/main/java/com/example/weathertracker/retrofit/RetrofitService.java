package com.example.weathertracker.retrofit;

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
                     @Field("password") String password,
                     @Field("FCMToken") String FCMToken);

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
    @POST("inOrOutEvent")
    Call<Ack> inOrOutEvent(@Field("eventId") String eventId,
                           @Field("userId") String userId,
                           @Field("action") Boolean action);

    @FormUrlEncoded
    @POST("recommendEvent")
    Call<List<Event>> getRecommendEvents(@Field("userId") String userId,
                                         @Field("longitude") float longitude,
                                         @Field("latitude") float latitude);

    @GET("recommendScene")
    Call<List<Sight>> getRecommendSights(@Query("longitude") double longitude,
                                         @Query("latitude") double latitude);

    @GET("searchEvent")
    Call<List<Event>> searchEvent(@Query("input") String input);

    @FormUrlEncoded
    @POST("getRecommendTime")
    Call<List<String>> getRecommendTime(@Field("userId") String userId,
                                        @Field("eventId") String eventId,
                                        @Field("whiteList") List<String> whiteList,
                                        @Field("blackList") List<String> blackList);

    @GET("getProfile")
    Call<User> getProfile(@Query("userId") String userId);

    @FormUrlEncoded
    @PUT("changeTime")
    Call<Ack> changeTime(@Field("eventId") String eventId,
                         @Field("pickTime") String pickTime);

    @FormUrlEncoded
    @POST("googleSignIn")
    Call<Ack> googleSignIn(@Field("email") String email,
                           @Field("FCMToken") String FCMToken);

    @FormUrlEncoded
    @POST("googleSignUp")
    Call<Ack> googleSignUp(@Field("email") String email,
                           @Field("FCMToken") String FCMToken);

    @GET("FPRecommendEvent")
    Call<List<Event>> FPRecommendEvent(@Query("longitude") double longitude,
                                       @Query("latitude") double latitude);

    @FormUrlEncoded
    @POST("logout")
    Call<Ack> logout(@Field("userId") String userId);
}