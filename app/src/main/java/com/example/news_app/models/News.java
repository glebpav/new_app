package com.example.news_app.models;

public class News {

    private String title;
    private String description;
    private String url;
    private String rating;

    public News(String title, String description, String url, String rating) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.rating = rating;
    }

    public News() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
