package com.example.weathertracker.retrofit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class timeTest {
    private String dateTime;
    private Date date;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public String getDateTime() {
        return dateTime;
    }

    public Date getDate() throws ParseException {
        date = df.parse(dateTime);
        return date;
    }
}
