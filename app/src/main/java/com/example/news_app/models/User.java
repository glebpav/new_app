package com.example.news_app.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class User {

    private int id;
    private String name;
    private String login;
    private String password;
    private String history;
    private String themes;

    private String sites;

    public User(int id, String name, String login, String password, String history, String themes, String sites) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.password = password;
        this.history = history;
        this.themes = themes;
        this.sites = sites;
    }

    public User() {
    }

    public String getSites() {
        return sites;
    }

    public void setSites(String sites) {
        this.sites = sites;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getThemes() {
        return themes;
    }

    public void setThemes(String themes) {
        this.themes = themes;
    }

    static public User serializeUser(String jsonString) {
        try {
            JSONObject obj = new JSONObject(jsonString);
            return new User(Integer.parseInt(obj.getString("id")),
                    obj.getString("name"),
                    obj.getString("login"),
                    obj.getString("password"),
                    obj.getString("history"),
                    obj.getString("themes"),
                    obj.getString("sites"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
