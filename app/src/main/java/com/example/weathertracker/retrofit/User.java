package com.example.weathertracker.retrofit;

import java.util.ArrayList;

public class User {
    private String userId, FCMToken, userName;
    private ArrayList<String> currentEvents, pastEvents, hobbies;
    private ArrayList<Double> AHPPreference;
    private ArrayList<Integer> freeTime;

    public User(String FCMToken, String userName, ArrayList<String> hobbies, ArrayList<Double> AHPPreference, ArrayList<Integer> freeTime) {
        this.FCMToken = FCMToken;
        this.userName = userName;
        this.hobbies = hobbies;
        this.AHPPreference = AHPPreference;
        this.freeTime = freeTime;
    }

    public String getUserId() {
        return userId;
    }

    public String getFCMToken() {
        return FCMToken;
    }

    public String getUserName() {
        return userName;
    }

    public ArrayList<String> getCurrentEvents() {
        return currentEvents;
    }

    public ArrayList<String> getPastEvents() {
        return pastEvents;
    }

    public ArrayList<String> getHobbies() {
        return hobbies;
    }

    public ArrayList<Double> getAHPPreference() {
        return AHPPreference;
    }

    public ArrayList<Integer> getFreeTime() {
        return freeTime;
    }
}
