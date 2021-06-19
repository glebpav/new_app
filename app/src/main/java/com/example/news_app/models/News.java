package com.example.news_app.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@Entity(tableName = "hotNews")
public class News {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "url")
    private String url;
    @ColumnInfo(name = "rating")
    private String rating;

    @Ignore
    public News(String title, String description, String url, String rating) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.rating = rating;
    }

    @Ignore
    public News() {}

    public News(int id, String title, String description, String url, String rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.url = url;
        this.rating = rating;
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

    static public ArrayList<News> serializeNews(String news) {
        ArrayList<News> news_list = new ArrayList<>();
        try {
            JSONArray news_array = new JSONArray(news);
            for (int i = 0; i < news_array.length(); i++) {
                JSONObject obj1 = (JSONObject) news_array.get(i);
                News news1 = new News(
                        obj1.getString("title"),
                        obj1.getString("body"),
                        obj1.getString("url"),
                        obj1.getString("rating")
                );
                news_list.add(news1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return news_list;
    }

    @NonNull
    @Override
    public String toString() {

        String response = " { ";
        response += title + " } ";
        return response;
    }
}
