package com.example.weathertracker.Retrofit;

import java.util.ArrayList;

public class User {
    private String user_id, FCM_token, user_name;
    private ArrayList<String> current_events, past_events, hobbies;
    private ArrayList<Double> AHP_Preference;
    private ArrayList<Integer> free_time;

    public User(String FCM_token, String user_name, ArrayList<String> hobbies, ArrayList<Double> AHP_Preference, ArrayList<Integer> free_time) {
        this.FCM_token = FCM_token;
        this.user_name = user_name;
        this.hobbies = hobbies;
        this.AHP_Preference = AHP_Preference;
        this.free_time = free_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getFCM_token() {
        return FCM_token;
    }

    public String getUser_name() {
        return user_name;
    }

    public ArrayList<String> getCurrent_events() {
        return current_events;
    }

    public ArrayList<String> getPast_events() {
        return past_events;
    }

    public ArrayList<String> getHobbies() {
        return hobbies;
    }

    public ArrayList<Double> getAHP_Preference() {
        return AHP_Preference;
    }

    public ArrayList<Integer> getFree_time() {
        return free_time;
    }
}
