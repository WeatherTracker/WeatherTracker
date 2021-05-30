package com.example.weathertracker.retrofit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Event {
    private Boolean isAuth;
    private String eventName, eventId, hostRemark, startTime, endTime, staticHobbyClass, staticHobbyTag;
    private double latitude, longitude;
    private List<String> participants, hosts, dynamicTags;
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

    public Boolean isAuth() {
        return isAuth;
    }

//    public Boolean isHost(String user_id) {
//        return hosts.contains(user_id);
//    }

    public List<String> getDynamicTags() {
        return dynamicTags;
    }

    public String[] strSplit(String s) {
        return s.split(" ");
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public static Boolean isTimeValid(String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            //進行轉換
            Date nowDate = new Date(System.currentTimeMillis());
            Date sdate = sdf.parse(startDate);
            Date edate = sdf.parse(endDate);
            if (sdate.compareTo(edate) < 0) {//開始時間要比結束時間早
                return nowDate.compareTo(sdate) < 0;//未來的活動
            }
            return false;
        } catch (Exception e) {
            System.out.println("isTimeValid exception happened");
            return false;
        }
    }
}
