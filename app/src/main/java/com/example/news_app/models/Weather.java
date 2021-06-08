package com.example.news_app.models;

public class Weather {

    private String temperature;
    private String weatherDesc;

    public Weather(String temperature, String weatherDesc) {
        this.temperature = temperature;
        this.weatherDesc = weatherDesc;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWeatherDesc() {
        return weatherDesc;
    }

    public void setWeatherDesc(String weatherDesc) {
        this.weatherDesc = weatherDesc;
    }
}
