package com.example.weathertracker.retrofit;

import java.util.List;

public class User {
    private String userId,FCMToken, userName;
    private List<String> currentEvents, pastEvents, hobbies;
    private List<Double> AHPPreference;
    private List<Float> barValue;
    private List<List<Boolean>> freeTime;

    public User(String FCMToken, String userName, List<String> hobbies, List<Double> AHPPreference, List<List<Boolean>> freeTime) {
        this.FCMToken = FCMToken;
        this.userName = userName;
        this.hobbies = hobbies;
        this.AHPPreference = AHPPreference;
        this.freeTime = freeTime;
    }

    public User(String userId, String userName, List<String> hobbies, List<Double> AHPPreference, List<Float> barValue, List<List<Boolean>> freeTime) {
        this.userId = userId;
        this.userName = userName;
        this.hobbies = hobbies;
        this.AHPPreference = AHPPreference;
        this.barValue = barValue;
        this.freeTime = freeTime;
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

    public List<List<Boolean>> getFreeTime() {
        return freeTime;
    }

    public List<Float> getBarValue() {
        return barValue;
    }
}
