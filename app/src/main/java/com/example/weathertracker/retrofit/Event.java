package com.example.weathertracker.retrofit;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    private String eventName, eventId, hostRemark;
    private double latitude, longitude;
    private Date startTime, endTime;
    private ArrayList<String> participants, hosts, staticTags;

    public Event(String eventName, String hostRemark, double latitude, double longitude, Date startTime, Date endTime, ArrayList<String> staticTags) {
        this.eventName = eventName;
        this.hostRemark = hostRemark;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startTime = startTime;
        this.endTime = endTime;
        this.staticTags = staticTags;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventId() {
        return eventId;
    }

    public String getHostRemark() {
        return hostRemark;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public ArrayList<String> getHosts() {
        return hosts;
    }

    public ArrayList<String> getStaticTags() {
        return staticTags;
    }

    public Boolean checkAuth(String user_id) {
        return hosts.contains(user_id);
    }
}
