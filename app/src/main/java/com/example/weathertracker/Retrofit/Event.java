package com.example.weathertracker.Retrofit;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    private String event_name, event_id, host_remark;
    private double latitude, longitude;
    private Date start_time, end_time;
    private ArrayList<String> participants, hosts, static_tags;

    public Event(String event_name, String host_remark, double latitude, double longitude, Date start_time, Date end_time, ArrayList<String> static_tags) {
        this.event_name = event_name;
        this.host_remark = host_remark;
        this.latitude = latitude;
        this.longitude = longitude;
        this.start_time = start_time;
        this.end_time = end_time;
        this.static_tags = static_tags;
    }

    public String getEvent_name() {
        return event_name;
    }

    public String getEvent_id() {
        return event_id;
    }

    public String getHost_remark() {
        return host_remark;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Date getStart_time() {
        return start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public ArrayList<String> getHosts() {
        return hosts;
    }

    public ArrayList<String> getStatic_tags() {
        return static_tags;
    }

    public Boolean checkAuth(String user_id) {
        return hosts.contains(user_id);
    }
}
