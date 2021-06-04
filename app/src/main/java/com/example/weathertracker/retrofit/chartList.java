package com.example.weathertracker.retrofit;

import java.util.List;

public class chartList {
    private List<XYPlot> temperature;
    private List<XYPlot> humidity;
    private List<XYPlot> windSpeed;
    private List<XYPlot> UV;
    private List<XYPlot> AQI;
    private List<XYPlot> POP;
    private String city, area, siteName;

    public class XYPlot {
        private String time;
        private Double value;

        public String getTime() {
            return time;
        }

        public Double getValue() {
            return value;
        }
    }


    public List<XYPlot> getTemperature() {
        return temperature;
    }

    public List<XYPlot> getHumidity() {
        return humidity;
    }

    public List<XYPlot> getWindSpeed() {
        return windSpeed;
    }

    public List<XYPlot> getUV() {
        return UV;
    }

    public List<XYPlot> getAQI() {
        return AQI;
    }

    public List<XYPlot> getPOP() {
        return POP;
    }

    public String getCity() {
        return city;
    }

    public String getArea() {
        return area;
    }

    public String getSiteName() {
        return siteName;
    }
}

