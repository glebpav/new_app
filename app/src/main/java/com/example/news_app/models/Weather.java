package com.example.news_app.models;

public class Weather {

    private String temperature;
    private String weatherDesc;
    private String iconUrl;

    public Weather(String temperature, String weatherDesc, String iconUrl) {
        this.temperature = temperature;
        this.weatherDesc = weatherDesc;
        this.iconUrl = iconUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
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
