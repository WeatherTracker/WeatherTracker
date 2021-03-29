package com.example.weathertracker.retrofit;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId, FCMToken, userName;
    private List<String> currentEvents, pastEvents, hobbies;
    private List<Double> AHPPreference;
    private List<Integer> freeTime;

    public User(String FCMToken, String userName, List<String> hobbies, List<Double> AHPPreference, List<Integer> freeTime) {
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

    public List<String> getCurrentEvents() {
        return currentEvents;
    }

    public List<String> getPastEvents() {
        return pastEvents;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public List<Double> getAHPPreference() {
        return AHPPreference;
    }

    public List<Integer> getFreeTime() {
        return freeTime;
    }
}
