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
import retrofit2.http.Query;

public interface RetrofitService {
    @FormUrlEncoded
    @POST("login")
    Call<Ack> login(@Field("email") String email,
                    @Field("password") String password);

    @FormUrlEncoded
    @POST("signUp")
    Call<Ack> signUp(@Field("email") String email,
                     @Field("password") String password,
                     @Field("FCMToken") String FCMToken);

    @FormUrlEncoded
    @POST("sendResetMail")
    Call<Ack> sendResetMail(@Field("email") String email);

    @FormUrlEncoded
    @PUT("resetPassword")
    Call<Ack> resetPassword(@Field("userId") String userId,
                            @Field("password") String password);

    @FormUrlEncoded
    @PUT("editProfile")
    Call<Ack> editProfile(@Field("userId") String userId,
                          @Field("userName") String userName,
                          @Field("AHPPreference") List<Double> AHPPreference,
                          @Field("freeTime") List<Integer> freeTime,
                          @Field("hobbies") List<String> hobbies);

    @GET("getCalendarMonth")
    Call<List<String>> getCalendarMonth(@Query("userId") String userId,
                                        @Query("month") String month);

    @GET("getCalendarDay")
    Call<List<Event>> getCalendarDay(@Query("userId") String userId,
                                     @Query("day") String day);

    @GET("getChart")
    Call<List<XYPlot>> getChart(@Query("gps") List<Double> gps,
                                @Query("day") String day);

    //todo:
    @GET("getWeatherIcon")
    Call<Ack> getWeatherIcon(@Query("gps") List<Double> gps);

    @FormUrlEncoded
    @POST("newEvent")
    Call<Ack> newEvent(@Field("userId") String userId,
                       @Body Event e);

    @FormUrlEncoded
    @DELETE("deleteEvent")
    Call<Ack> deleteEvent(@Field("eventId") String eventId);


    @PUT("editEvent")
    Call<Ack> editEvent(@Body Event event);

    @FormUrlEncoded
    @PUT("inOrOutEvent")
    Call<Ack> inOrOutEvent(@Field("eventId") String eventId,
                           @Field("userId") String userId,
                           @Field("action") Boolean action);

    @GET("getRecommendEvent")
    Call<List<Event>> getRecommendEvent(@Query("userId") String userId);

    @GET("searchEvent")
    Call<List<Event>> searchEvent(@Query("input") String input);

    @GET("getRecommendTime")
    Call<List<String>> getRecommendTime(@Query("userId") String userId,
                                        @Query("eventId") String eventId);

    @FormUrlEncoded
    @PUT("changeTime")
    Call<Ack> changeTime(@Field("eventId") String eventId,
                         @Field("pickTime") String pickTime);
}
