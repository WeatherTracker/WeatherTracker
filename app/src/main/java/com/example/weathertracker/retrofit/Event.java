package com.example.weathertracker.retrofit;

import java.util.List;

public class Event {
    private String eventName, eventId, hostRemark, startTime, endTime, staticHobbyClass, staticHobbyTag;
    private double latitude, longitude;
    private List<String> participants, hosts;
    private Boolean isPublic, isOutDoor;

    public Event(String eventName, String hostRemark, String startTime, String endTime, String staticHobbyClass, String staticHobbyTag, double latitude, double longitude, List<String> hosts, Boolean isPublic, Boolean isOutDoor) {
        this.eventName = eventName;
        this.hostRemark = hostRemark;
        this.startTime = startTime;
        this.endTime = endTime;
        this.staticHobbyClass = staticHobbyClass;
        this.staticHobbyTag = staticHobbyTag;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hosts = hosts;
        this.isPublic = isPublic;
        this.isOutDoor = isOutDoor;
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

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStaticHobbyClass() {
        return staticHobbyClass;
    }

    public String getStaticHobbyTag() {
        return staticHobbyTag;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public Boolean isPublic() {
        return isPublic;
    }

    public Boolean isOutDoor() {
        return isOutDoor;
    }

    public Boolean isHost(String user_id) {
        return hosts.contains(user_id);
    }

    public String[] strSplit() {
        return this.startTime.split(" ");
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventName='" + eventName + '\'' +
                ", eventId='" + eventId + '\'' +
                ", hostRemark='" + hostRemark + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", staticHobbyClass='" + staticHobbyClass + '\'' +
                ", staticHobbyTag='" + staticHobbyTag + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", participants=" + participants +
                ", hosts=" + hosts +
                ", isPublic=" + isPublic +
                ", isOutDoor=" + isOutDoor +
                '}';
    }
//    public Date StrToISO(String ISOString){
//
//    }
//
//    public String ISOToStr(Date ISO){
//
//    }
}
